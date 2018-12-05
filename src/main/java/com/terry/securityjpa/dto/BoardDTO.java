package com.terry.securityjpa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class BoardDTO {
  private Long idx;
  private String loginId;
  private String title;
  private String contents;
  private String boardType;
  private LocalDateTime createDT;
  private LocalDateTime updateDT;

  @Builder
  public BoardDTO(Long idx, String loginId, String title, String contents, String boardType, LocalDateTime createDT) {
    this.idx = idx;
    this.loginId = loginId;
    this.title = title;
    this.contents = contents;
    this.boardType = boardType;
    this.createDT = createDT;
  }
}


