package com.jskang.storageclient;

import com.jskang.storageclient.http.HttpMethod;
import com.jskang.storageclient.handler.DownloadHandler;
import com.jskang.storageclient.handler.UploadHandler;
import com.jskang.storageclient.http.ResponseApi;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        int port = 8080;
        try {
            port = Integer.valueOf(args[0]);
        } catch (NumberFormatException e) {
            LOG.warn("An incorrect value was entered. The port defaults to 8080.");
            LOG.debug(e.getMessage());
        } catch (Exception e) {
            LOG.warn("An error occurred while converting a number. The port defaults to 8080.");
            LOG.debug(e.getMessage());
        }

        // TODO: 404 응답 처리 해야함
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/upload", exchange -> {
            LOG.info("Upload start...");
            if (exchange.getRequestMethod().equals(HttpMethod.POST)) {
                new UploadHandler().run(exchange);
            } else {
                String response = String.format("[%s] /upload - This service is not supported.", exchange.getRequestMethod());
                LOG.error(response);
                ResponseApi.outputStreamData(HttpURLConnection.HTTP_BAD_METHOD, exchange, response);
            }
        });
        server.createContext("/download", exchange -> {
            LOG.info("API Download start...");
            if (exchange.getRequestMethod().equals(HttpMethod.POST)) {
                new DownloadHandler().run(exchange);
            } else {
                String response = String.format("[%s] /upload - This service is not supported.", exchange.getRequestMethod());
                LOG.error(response);
                ResponseApi.outputStreamData(HttpURLConnection.HTTP_BAD_METHOD, exchange, response);
            }
        });

        server.setExecutor(null);
        server.start();

        String startMsg = String.format("Storage Client Server Port {%d} Start Completed.", port);
        LOG.info(startMsg);
    }
}
