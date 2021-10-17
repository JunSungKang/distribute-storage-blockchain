package com.jskang.storageclient.file;

import com.jskang.storageclient.reedsolomon.ReedSolomonCommon;
import com.jskang.storageclient.reedsolomon.ReedSolomonEncoding;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Upload implements FileUpDown{

    private Logger LOG = LoggerFactory.getLogger(this.getClass());
    private ReedSolomonCommon reedSolomonCommon = null;

    /**
     * Reed-Solomon encoding processing.
     * @param filePath Absolute file path.
     * @return filepath.
     */
    public String reedSolomonEncoding(String filePath) {
        LOG.info("[" + filePath + "] ReedSolomon encoding start.");

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
            reedSolomonCommon.execute(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePath;
    }

    /**
     * File upload.
     * @param filePath Absolute path to file to upload.
     * @return
     */
    public String excute(String filePath) {
        LOG.info("[" + filePath + "] upload start.");
        return this.reedSolomonEncoding(filePath);
    }
}
