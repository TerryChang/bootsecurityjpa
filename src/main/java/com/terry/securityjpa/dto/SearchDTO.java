package com.terry.securityjpa.dto;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
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

  @Override
  public int getOffset() {
    // TODO Auto-generated method stub
    return 0;
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
