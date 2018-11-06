package com.terry.securityjpa.config.converter.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileHttpMessageConverter implements HttpMessageConverter<File> {

  private List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();

  public FileHttpMessageConverter(){
    this.supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
  }

  @Override
  public boolean canRead(Class<?> clazz, MediaType mediaType) {
    return false;
  }

  @Override
  public boolean canWrite(Class<?> clazz, MediaType mediaType) {
    return File.class.equals(clazz);
  }

  @Override
  public List<MediaType> getSupportedMediaTypes() {
    return supportedMediaTypes;
  }

  @Override
  public File read(Class<? extends File> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
    return null;
  }

  @Override
  public void write(File file, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

    if (contentType == null) {
      contentType = MediaType.APPLICATION_OCTET_STREAM;
    }

    HttpHeaders headers = outputMessage.getHeaders();
    // response.setHeader("Content-Disposition", "attachment;fileName=\"" + org_file_name + "\";");
    headers.setContentType(contentType);
    headers.set("Content-Length", Long.toString(file.length()));
    headers.set("Content-Transfer-Encoding", "binary");

    FileInputStream fis = new FileInputStream(file);

    FileCopyUtils.copy(fis, outputMessage.getBody());

  }
}
