package com.jskang.storageclient.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Download {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    public String Download(String fileName) {
        LOG.info("download start.");
        return fileName;
    }

}
