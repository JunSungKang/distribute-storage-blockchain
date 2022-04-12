package com.jskang.storageclient.file;

import com.jskang.storageclient.http.RequestApi;
import com.jskang.storageclient.reedsolomon.ReedSolomonCommon;
import com.jskang.storageclient.reedsolomon.ReedSolomonEncoding;
import com.jskang.storageclient.response.ResponseData;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Upload implements FileUpDown {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());
    private ReedSolomonEncoding reedSolomonCommon = null;
    private RequestApi requestApi = new RequestApi();

    /**
     * Reed-Solomon encoding processing.
     *
     * @param filePath Absolute file path.
     * @return filepath.
     */
    public List<String> reedSolomonEncoding(String filePath) {
        LOG.info("(" + filePath + ") ReedSolomon encoding start.");

        // 리드솔로몬 처리할 파일 읽기
        File file = Paths.get(filePath).toFile();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // 읽은 파일 byte로 변환
        int fileSize = (int) file.length();
        final int storedSize = fileSize + ReedSolomonCommon.BYTES_IN_INT;
        final int shardSize =
            (storedSize + ReedSolomonCommon.DATA_SHARDS - 1) / ReedSolomonCommon.DATA_SHARDS;
        final int bufferSize = shardSize * ReedSolomonCommon.DATA_SHARDS;

        byte[] allBytes = new byte[bufferSize];
        try {
            fileInputStream.read(allBytes, 0, fileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ReedSolomon Encoding 수행
        reedSolomonCommon = new ReedSolomonEncoding(allBytes);
        try {
            return reedSolomonCommon.execute(filePath);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * File upload.
     *
     * @param path Absolute path parent.
     * @param fileName Upload file name.
     * @return
     */
    @Override
    public String excute(String path, String fileName) {
        String filePath = Paths.get(path, fileName).toAbsolutePath().toString();
        LOG.info("(" + filePath + ") upload start.");
        List<String> outputFiles = this.reedSolomonEncoding(filePath);

        outputFiles.stream().forEach(downloadFile -> {
            try {
                ResponseData result = requestApi
                    .fileUpload("127.0.0.1:20040/file/upload?fileName=" + fileName, Path.of(downloadFile));
                LOG.info(result.toString());
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage());
            }
        });
        return null;
    }
}
