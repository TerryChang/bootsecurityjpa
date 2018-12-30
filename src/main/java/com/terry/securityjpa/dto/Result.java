package com.terry.securityjpa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 작업에 대한 결과를 코드와 메시지 형태로 보내고자 할때 사용되는 DTO 클래스
 */
@Data
@Builder
@AllArgsConstructor
public class Result implements Serializable {

  private static final long serialVersionUID = -9096514988010795608L;

  private final String code;
  private final String message;

}
