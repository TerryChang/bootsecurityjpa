package com.terry.securityjpa.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class BoardDTO {
  private Long idx;
  private String loginId;
  private String title;
  private String contents;
  private String boardType;
  private LocalDateTime createDT;
  private LocalDateTime updateDT;

  @Singular("boardFileDTOList")
  private List<BoardFileDTO> boardFileDTOList;

}


