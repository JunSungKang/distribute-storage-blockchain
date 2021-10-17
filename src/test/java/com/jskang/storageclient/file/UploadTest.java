package com.jskang.storageclient.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UploadTest {

    @Test
    void upload() {
        String path = "D:\\jskang\\003-workspace\\005-github-personal-project\\paper-source\\storage-client\\test_bin\\sample.mp4";
        Upload upload = new Upload();
        String rst = upload.excute(path);
        Assertions.assertEquals(path, rst);
    }
}
