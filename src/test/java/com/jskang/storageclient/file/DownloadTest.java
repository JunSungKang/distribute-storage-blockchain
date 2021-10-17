package com.jskang.storageclient.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DownloadTest {

    @Test
    void download() {
        String path = "D:\\jskang\\003-workspace\\005-github-personal-project\\paper-source\\storage-client\\test_bin\\sample.mp4";
        Download download = new Download();
        String rst = download.excute(path);
        Assertions.assertEquals(path, rst);
    }
}
