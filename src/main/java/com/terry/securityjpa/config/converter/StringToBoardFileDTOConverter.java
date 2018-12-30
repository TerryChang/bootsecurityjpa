package com.terry.securityjpa.config.converter;

import com.terry.securityjpa.dto.BoardFileDTO;
import org.springframework.core.convert.converter.Converter;

/**
 * 파일이 업로드 되지 않은 상황에서는 MultipartFileToBoardFileDTOConverter 가 동작을 하지 않는다.
 * 파일이 업로드 되지 않을 경우엔 String 타입의 null로 인식되어서 이런 상황이 벌어지는데
 * 이럴 경우를 대비해서 만든 Converter 인터페이스 구현 객체이다다
 */
public class StringToBoardFileDTOConverter implements Converter<String, BoardFileDTO> {
  @Override
  public BoardFileDTO convert(String source) {
    return null;
  }
}
