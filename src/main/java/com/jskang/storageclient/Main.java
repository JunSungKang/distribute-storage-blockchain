package com.jskang.storageclient;

import com.jskang.storageclient.file.Download;
import com.jskang.storageclient.file.Upload;
import com.jskang.storageclient.network.NodeInfo;
import com.jskang.storageclient.reedsolomon.ReedSolomonDecoding;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void viewUsing() {
        System.out.println("===================================");
        System.out.println("input 'help' : help.");
        System.out.println("input 'exit' : program exit.");
        System.out.println("input 'upload' : upload.");
        System.out.println("input 'download' : download.");
        System.out.println("===================================");
    }

    public static void main(String[] args) throws IOException {
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.load();

        LOG.info(nodeInfo.getNodeList().toString());

        LOG.info("Client start completed.");
        viewUsing();

        String inputText = "";
        while (!inputText.equals("exit")) {
            Scanner scanner = new Scanner(System.in);
            inputText = scanner.next();

            switch (inputText) {
                case "help":
                    viewUsing();
                    break;
                case "upload":
                    LOG.info("Please enter the file path to upload.");

                    File file = new File(scanner.next());
                    LOG.info(new Upload().excute(file.getAbsolutePath()));
                    break;
                case "download":
                    LOG.info("Please enter the file path to download.");

                    String downloadFile = scanner.next();
                    new Download().excute(downloadFile);
                    ReedSolomonDecoding reedSolomonDecoding = new ReedSolomonDecoding();
                    reedSolomonDecoding.execute("test_merge\\" + downloadFile);
                    break;
            }
        }

    }
}
