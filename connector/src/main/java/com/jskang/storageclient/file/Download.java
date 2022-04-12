package com.jskang.storageclient.file;

import com.jskang.storageclient.http.RequestApi;
import com.jskang.storageclient.reedsolomon.ReedSolomonCommon;
import com.jskang.storageclient.reedsolomon.ReedSolomonDecoding;
import com.jskang.storageclient.reedsolomon.ReedSolomonEncoding;
import com.jskang.storageclient.response.ResponseData;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Download implements FileUpDown {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());
    private ReedSolomonEncoding reedSolomonCommon = null;
    private RequestApi requestApi = new RequestApi();

    /**
     * Reed-Solomon decoding processing.
     *
     * @param filePath Absolute file path.
     * @return filepath.
     */
    public String reedSolomonDecoding(String filePath) {
        LOG.info("(" + filePath + ") ReedSolomon decoding start.");

        // Reed-Solomon decoding processing.
        // Write out the resulting files.
        File outputFile = new File(filePath);
        FileOutputStream out = null;

        try {
            final File directory = new File(filePath).getParentFile();
            for (File file : directory.listFiles()) {
                LOG.info("Create distribute file merging: " + file.getName());
                // 디렉토리인 경우, 이미 처리된 파일 생략
                if (file.isDirectory() || !file.getName().endsWith("0")) {
                    continue;
                }

                ReedSolomonDecoding reedSolomon = new ReedSolomonDecoding();
                byte[] partFileData = reedSolomon.execute(filePath);
                byte[] fullFile = new byte[partFileData.length];
                System.arraycopy(partFileData, 0, fullFile, 0, partFileData.length);

                // Prevent creation with file size 0byte
                if (out == null) {
                    out = new FileOutputStream(outputFile, false);
                }
                out.write(fullFile);
            }

            if (out != null) {
                out.close();
            }

            if (directory.listFiles().length < ReedSolomonCommon.DATA_SHARDS) {
                throw new FileNotFoundException(
                    "Not enough shards present: We need at least DATA_SHARDS to be able to reconstruct the file.");
            }
            LOG.info("Create distribute file completed.");
        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage());
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

        return filePath;
    }

    /**
     * File download.
     *
     * @param path Absolute file path.
     * @param fileName Absolute path to file to download
     * @return
     */
    @Override
    public String excute(String path, String fileName) {
        LOG.info("download start.");
        ResponseData responseData = requestApi
            .get("127.0.0.1:20040/file/position_info?fileName=" + fileName);

        List<String> positions = (List<String>)responseData.getBody();
        for (int i = 0; i < positions.size(); i++) {
            // TODO: 파일 경로가 아니라 IP주소로 변경하는 구조를 해야함
            try {
                String distributeFileName = positions.get(i);
                int idx = distributeFileName.lastIndexOf(File.separator);
                distributeFileName = distributeFileName.substring(idx+1, distributeFileName.length());
                requestApi.fileDownload(
                    "127.0.0.1:20040/file/download",
                    path, distributeFileName);
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }
        return "COMPLETE";
    }

}
