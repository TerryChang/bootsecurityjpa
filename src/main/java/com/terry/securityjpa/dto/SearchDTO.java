package com.terry.securityjpa.dto;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class SearchDTO implements Pageable {
  private String searchType;
  private String searchWord;
  private int pageNo;
  private int pageSize;

  @Override
  public int getPageNumber() {
    // TODO Auto-generated method stub
    // offset 기반으로 페이지번호르 계산할땐 offset / pagesize로 한다
    return pageNo;
  }

  @Override
  public int getPageSize() {
    // TODO Auto-generated method stub
    return pageSize;
  }

  /**
   * Pageable을 구현할때는 getOffset 메소드를 반드시 의미있게 구현해줘야 한다
   * 단순히 page 번호와 pagesize만 넘겨주면 페이징 쿼리를 해주는 Database에서는 문제가 없지만
   * limit 과 offset을 사용하는  페이징 쿼리를 구현하는 Database의 경우 이 offset을 Spring Data가 내부적으로 getPageNumber() * getPageSize()를 하는 것이 아니라
   * getOffset() 메소드를 통해 offset을 구하기 때문에 이 offset도 의미있게 구현해주어야 한다(단순히 return 0 이런식의 무의미한 구현을 하지 말라는 뜻)
   * @return
   */
  @Override
  public int getOffset() {
    // TODO Auto-generated method stub
    return pageNo * pageSize;
  }

  @Override
  public Sort getSort() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Pageable next() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Pageable previousOrFirst() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Pageable first() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean hasPrevious() {
    // TODO Auto-generated method stub
    return false;
  }

}
