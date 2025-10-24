package com.tuk.mina.util;

import org.springframework.core.io.InputStreamResource;
import java.io.IOException;
import java.io.InputStream;

public class MultipartInputStreamFileResource extends InputStreamResource {
    private final String filename;

    public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
        super(inputStream);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public long contentLength() throws IOException {
        return -1; // 알 수 없을 때 -1
    }
}
