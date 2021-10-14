package com.jskang.storageclient.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Upload {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    public String upload(String filePath) {
        LOG.info("upload start.");
        return filePath;
    }

}
