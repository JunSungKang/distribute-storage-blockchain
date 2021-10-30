package com.jskang.storageclient.file;

import com.jskang.storageclient.reedsolomon.ReedSolomonCommon;
import com.jskang.storageclient.reedsolomon.ReedSolomonDecoding;
import com.jskang.storageclient.reedsolomon.ReedSolomonEncoding;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Download implements FileUpDown{

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    /**
     * Reed-Solomon decoding processing.
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
                LOG.info("Create distribute file merging: " +file.getName());
                // 디렉토리인 경우, 이미 처리된 파일 생략
                if (file.isDirectory() || !file.getName().endsWith("0")) {
                    continue;
                }

                ReedSolomonDecoding reedSolomon = new ReedSolomonDecoding();
                byte[] partFileData = reedSolomon.execute(filePath);
                byte[] fullFile = new byte[partFileData.length];
                System.arraycopy(partFileData, 0, fullFile, 0, partFileData.length);

                // Prevent creation with file size 0byte
                if (out == null){
                    out = new FileOutputStream(outputFile, false);
                }
                out.write(fullFile);
            }

            if (out != null){
                out.close();
            }

            if (directory.listFiles().length < ReedSolomonCommon.DATA_SHARDS){
                throw new FileNotFoundException("Not enough shards present: We need at least DATA_SHARDS to be able to reconstruct the file.");
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
     * @param filePath Absolute path to file to download
     * @return
     */
    @Override
    public String excute(String filePath) {
        LOG.info("download start.");
        return this.reedSolomonDecoding(filePath);
    }

}
