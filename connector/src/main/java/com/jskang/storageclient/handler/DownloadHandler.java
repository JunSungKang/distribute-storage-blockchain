package com.jskang.storageclient.handler;

import com.jskang.storageclient.file.Download;
import com.jskang.storageclient.http.ResponseApi;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.file.Paths;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadHandler extends HandlerUtils implements Handler {

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
            LOG.error("A file name was not entered.");
            return;
        }
        String fileName = (String) data.get("fileName");
        if (data.get("downloadPath") == null) {
            LOG.error("A download path was not entered.");
            return;
        }
        String downloadPath = (String) data.get("downloadPath");

        // 파일 다운로드
        new Download().excute(downloadPath, fileName);
        new Download().reedSolomonDecoding(Paths.get(downloadPath, fileName).toString());

        // 모든 작업 완료 후 응답
        try {
            String response = String.format("%s bytes download complete.", data.get("fileName"));
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
