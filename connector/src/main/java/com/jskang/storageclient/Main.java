package com.jskang.storageclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jskang.storageclient.common.Converter;
import com.jskang.storageclient.file.Download;
import com.jskang.storageclient.file.Upload;
import com.jskang.storageclient.reedsolomon.ReedSolomonDecoding;
import com.sun.net.httpserver.HttpServer;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/upload", exchange -> {
            LOG.info("Upload start...");

            String response = "<h3>fucheng!welcome!</h3>";
            exchange.sendResponseHeaders(404, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });
        server.createContext("/download", exchange -> {
            LOG.info("API Download start...");

            byte[] buff = new byte[65535];
            InputStream bin = exchange.getRequestBody();

            bin.readNBytes(buff, 0, buff.length);

            // 클라이언트 요청데이터 체크
            Map<String, Object> data = getBodyParser(buff);
            if (data.get("fileName") == null) {
                LOG.error("A file name was not entered.");
                return;
            }
            String fileName = (String)data.get("fileName");
            if (data.get("downloadPath") == null) {
                LOG.error("A download path was not entered.");
                return;
            }
            String downloadPath = (String)data.get("downloadPath");

            // 파일 다운로드
            new Download().excute(downloadPath, fileName);
            new Download().reedSolomonDecoding(Paths.get(downloadPath, fileName).toString());

            // 모든 작업 완료 후 응답
            String response = String.format("%s bytes download complete.", data.get("fileName"));
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });
        server.setExecutor(null);
        server.start();

        LOG.info("Client start completed.");

        /*if (type.equals("upload")) {
            LOG.info("Please enter the file path to upload.");

            File file = new File(path);
            LOG.info(new Upload().excute(file.getAbsolutePath()));
        } else if (type.equals("download")) {
            LOG.info("Please enter the file path to download.");

            String downloadFile = path;
            new Download().excute(downloadFile);
            ReedSolomonDecoding reedSolomonDecoding = new ReedSolomonDecoding();
            reedSolomonDecoding.execute("test_merge\\" + downloadFile);
        } else {
            LOG.error("The type(-t) option is unknown.");
        }*/
    }

    private static Map<String, Object> getBodyParser(byte[] bytes) {
        return Converter.jsonToMap(new String(bytes));
    }

    private static Map<String, Object> getQueryParser(String query) {
        if (query == null) {
            return new HashMap<>();
        }

        Map<String, Object> paramMap = new HashMap<>();

        String[] params = query.split("&");
        for (int i=0; i<params.length; i++) {
            String[] param = params[i].split("=");
            if (param.length < 2) {
                continue;
            }

            paramMap.put(param[0], param[1]);
        }

        return paramMap;
    }
}
