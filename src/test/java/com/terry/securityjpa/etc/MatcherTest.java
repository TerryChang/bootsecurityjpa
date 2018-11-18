package com.terry.securityjpa.etc;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MatcherTest {

  /**
   * 준회원 정보를 get 방식으로 넘겨주는 방식의 url에 대한 정규표현식 패턴 테스트
   */
  @Test
  public void associateUrlTest() {
    String url1 = "/board/list.html?type=associate";
    String url2 = "/board/list.html?abc=def&type=associate";
    String url3 = "/board/list.html?abc=def&hij=lmn1&type=associate";
    String url4 = "/board/list.html?abc=def&hij=lmn1&type=associate&myparam=kkk";
    String url5 = "/board/list.html?abc=def&hij=lmn1&type=associate&myparam1=kkk&myparam2=hhh";

    // 정화원 등급 정보를 넘겨줘서 패턴이 틀리게 된 것을 확인
    String url6 = "/board/list.html?abc=def&hij=lmn1&type=regular&myparam1=kkk&myparam2=hhh";

    String pattern = "\\/board\\/list\\.html((\\?type=associate(\\&[a-zA-Z0-9_]+\\=[a-zA-Z0-9_]+)*)|"
        + "(\\?[a-zA-Z0-9_]+\\=[a-zA-Z0-9_]+(\\&[a-zA-Z0-9_]+\\=[a-zA-Z0-9_]+)*\\&type=associate"
        + "(\\&[a-zA-Z0-9_]+\\=[a-zA-Z0-9_]+)*))";
    assertThat(url1.matches(pattern)).isEqualTo(true);
    assertThat(url2.matches(pattern)).isEqualTo(true);
    assertThat(url3.matches(pattern)).isEqualTo(true);
    assertThat(url4.matches(pattern)).isEqualTo(true);
    assertThat(url5.matches(pattern)).isEqualTo(true);
    assertThat(url6.matches(pattern)).isEqualTo(false);
  }
}
