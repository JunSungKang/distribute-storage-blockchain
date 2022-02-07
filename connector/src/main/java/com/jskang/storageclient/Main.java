package com.jskang.storageclient;

import com.jskang.storageclient.file.Download;
import com.jskang.storageclient.file.Upload;
import com.jskang.storageclient.reedsolomon.ReedSolomonDecoding;
import java.io.File;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, ParseException {
        // Create options object
        Options options = new Options();
        options.addOption("t", "type", true, "upload | download");
        options.addOption("f", "file", true, "file path");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        // Select type option Upload or Download.
        String type = "";
        if (!cmd.hasOption("t")) {
            LOG.error("The type(-t) option is missing.");
            return;
        }
        type = cmd.getOptionValue("t");

        // Select type option Upload or Download.
        String path = "";
        if (!cmd.hasOption("f")) {
            LOG.error("The file(-f) option is missing.");
            return;
        }
        path = cmd.getOptionValue("f");

        LOG.info("Client start completed.");

        if (type.equals("upload")) {
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
        }
    }
}
