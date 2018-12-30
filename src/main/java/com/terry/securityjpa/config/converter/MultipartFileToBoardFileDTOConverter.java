package com.terry.securityjpa.config.converter;

import com.terry.securityjpa.dto.BoardFileDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일이 업로드 될때 업로드된 파일을 조작하기 위해 BoardFileDTO 클래스 객체로 변환하는 Converter 인터페이스 구현 클래스이다
 */
public class MultipartFileToBoardFileDTOConverter implements Converter<MultipartFile, BoardFileDTO> {

  @Override
  public BoardFileDTO convert(MultipartFile multipartFile) {
    BoardFileDTO result = null;
    if(multipartFile != null) {
      String orgFileName = multipartFile.getOriginalFilename();
      String extension = null;
      int idx = orgFileName.lastIndexOf(".");
      if(idx != -1) {
        extension = orgFileName.substring(idx + 1);
      }

      result = BoardFileDTO.builder()
          .orgFileName(orgFileName)
          .extension(extension)
          .size(multipartFile.getSize())
          .multipartFile(multipartFile).build();

    }
    return result;
  }
}
