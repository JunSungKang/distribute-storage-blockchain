package com.jskang.storageclient;

import com.jskang.storageclient.handler.DownloadHandler;
import com.jskang.storageclient.handler.UploadHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        int port = 8080;

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/upload", exchange -> {
            LOG.info("Upload start...");
            new UploadHandler().run(exchange);
        });
        server.createContext("/download", exchange -> {
            LOG.info("API Download start...");
            new DownloadHandler().run(exchange);
        });
        server.setExecutor(null);
        server.start();

        String startMsg = String.format("Storage Client Server Port {%d} Start Completed.", port);
        LOG.info(startMsg);
    }
}
