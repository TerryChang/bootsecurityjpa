package com.terry.securityjpa.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardFileDTO implements Serializable {

  private static final long serialVersionUID = 7070845014288980944L;

  private Long idx;                           // DB에 저장되어 있는 key 값
  private String savePath;                    // 파일이 저장되는 경로
  private String orgFileName;                 // 원래 파일명(파일이 업로드 될 당시의 파일이름)
  private String realFileName;                // 실제 파일명(서버에 저장되는 파일 이름)
  private String extension;                   // 확장자
  private Long size;                          // 업로드 된 파일 크기
  private LocalDateTime createDT;             // 파일 등록 일시
  private Long boardIdx;                      // 해당 파일과 연관되는 Board key 값

  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private File file;                          // 업로드 된 MultipartFile 객체가 실제 Java File 객체로 저장된 파일
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private MultipartFile multipartFile;        // 사용자가 업로드된 MultipartFile

  /**
   * savePath와 realFileName 을 이용해서 MultipartFile을 File로 복사한다
   * @throws IllegalStateException
   * @throws IOException
   */
  public void transferFile () throws IllegalStateException, IOException {
    File fileSavePath = new File(savePath);
    if(!fileSavePath.exists()) {
      fileSavePath.mkdirs();
    }
    transferFile(this.savePath + realFileName);
  }

  /**
   * 파일명이 포함된 특정 경로 문자열을 입력받아 MultipartFile을 File로 복사한다
   * 이 메소드를 사용할 경우 파일이 저장되는 디렉토리는 외부에서 만들어주어야 한다
   * @param filePath
   * @throws IllegalStateException
   * @throws IOException
   */
  public void transferFile (String filePath) throws IllegalStateException, IOException {
    file = new File(filePath);
    multipartFile.transferTo(file);
  }
}
