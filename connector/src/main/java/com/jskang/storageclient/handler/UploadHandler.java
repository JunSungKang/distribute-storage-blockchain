package com.jskang.storageclient.handler;

import com.jskang.storageclient.file.Upload;
import com.jskang.storageclient.http.ResponseApi;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadHandler extends HandlerUtils implements Handler {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run(HttpExchange exchange) {
        byte[] buff = new byte[65535];
        InputStream bin = exchange.getRequestBody();

        try {
            bin.readNBytes(buff, 0, buff.length);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            return;
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return;
        }

        // 클라이언트 요청데이터 체크
        Map<String, Object> data = getBodyParser(buff);
        if (data.get("fileName") == null) {
            String errorMsg = "A file name was not entered.";
            LOG.error(errorMsg);
            try {
                ResponseApi.outputStreamData(HttpURLConnection.HTTP_NOT_FOUND, exchange, errorMsg);
            } catch (IOException e) {
                LOG.error(e.getMessage());
                return;
            } catch (Exception e) {
                LOG.error(e.getMessage());
                return;
            }
            return;
        }
        String fileName = (String) data.get("fileName");
        if (data.get("uploadPath") == null) {
            String errorMsg = "A upload path was not entered.";
            LOG.error("A upload path was not entered.");
            try {
                ResponseApi.outputStreamData(HttpURLConnection.HTTP_NOT_FOUND, exchange, errorMsg);
            } catch (IOException e) {
                LOG.error(e.getMessage());
                return;
            } catch (Exception e) {
                LOG.error(e.getMessage());
                return;
            }
            return;
        }
        String uploadPath = (String) data.get("uploadPath");

        // 파일 업로드
        String resultData = new Upload().excute(uploadPath, fileName);
        LOG.info("upload success.");
        LOG.debug(resultData);

        // 모든 작업 완료 후 응답
        try {
            String response = String.format("%s bytes upload complete.", data.get("fileName"));
            ResponseApi.outputStreamData(HttpURLConnection.HTTP_OK, exchange, response);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            return;
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return;
        }
    }
}
