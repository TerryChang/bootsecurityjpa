package com.terry.securityjpa.etc;

import com.terry.securityjpa.dto.BoardDTO;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LombokTest {

  @Test
  public void Builder_기능_테스트() {
    BoardDTO boardDTO1 = BoardDTO.builder().title("테스트").build();
    BoardDTO boardDTO2 = new BoardDTO();
    boardDTO2.setTitle("테스트");
    assertThat(boardDTO1).isEqualTo(boardDTO2);
  }
}
