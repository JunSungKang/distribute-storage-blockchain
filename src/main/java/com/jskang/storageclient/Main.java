package com.jskang.storageclient;

import com.jskang.storageclient.file.Download;
import com.jskang.storageclient.file.Upload;
import java.io.File;
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

    public static void main(String[] args) {
        LOG.info("Client start completed.");
        viewUsing();

        String inputText = "";
        while (!inputText.equals("exit")) {
            Scanner scanner = new Scanner(System.in);
            inputText = scanner.next();

            File file = null;
            switch (inputText) {
                case "help":
                    viewUsing();
                    break;
                case "upload":
                    System.out.println("Please enter the file path to upload.");

                    file = new File(scanner.next());
                    System.out.println(new Upload().excute(file.getAbsolutePath()));
                    break;
                case "download":
                    System.out.println("Please enter the file path to download.");

                    file = new File(scanner.next());
                    System.out.println(new Download().excute(file.getAbsolutePath()));
                    break;
            }
        }

    }
}
