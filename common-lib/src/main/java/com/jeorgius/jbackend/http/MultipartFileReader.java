package com.jeorgius.jbackend.http;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MultipartFileReader extends AbstractHttpMessageConverter<MultipartFile> {
    private final DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();

    public MultipartFileReader() {
        super(MediaType.ALL);
    }

    @Override
    protected boolean canRead(MediaType mediaType) {
        return !mediaType.getSubtype().contains("json");
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz == MultipartFile.class;
    }

    @Override
    protected MultipartFile readInternal(Class<? extends MultipartFile> clazz,
                                         HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        String filename = inputMessage.getHeaders().getContentDisposition().getFilename();
        FileItem fileItem = diskFileItemFactory.createItem("file", null, true, filename);
        try (InputStream is = inputMessage.getBody();
             OutputStream os = fileItem.getOutputStream()) {

            IOUtils.copy(is, os);
            return new CommonsMultipartFile(fileItem);
        }
    }

    @Override
    protected void writeInternal(MultipartFile multipartFile, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        throw new UnsupportedOperationException("Multipart file writing is not supported");
    }
}