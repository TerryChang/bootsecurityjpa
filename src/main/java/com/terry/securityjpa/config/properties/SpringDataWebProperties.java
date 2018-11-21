package com.terry.securityjpa.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * application.yml에 설정되어 있는 spring.data.web 프로퍼티를 읽어들이는 클래스
 * (이 소스는 Spring Boot 2.X에 있는 SpringDataWebProperties.class 소스를 그대로 사용했다)
 * @author Terry Chang
 *
 */
@Configuration
@ConfigurationProperties("spring.data.web")
public class SpringDataWebProperties {
  
  private final Pageable pageable = new Pageable();

  private final Sort sort = new Sort();

  public Pageable getPageable() {
    return this.pageable;
  }

  public Sort getSort() {
    return this.sort;
  }

  /**
   * Pageable properties.
   */
  public static class Pageable {

    /**
     * Page index parameter name.
     */
    private String pageParameter = "page";

    /**
     * Page size parameter name.
     */
    private String sizeParameter = "size";

    /**
     * Whether to expose and assume 1-based page number indexes. Defaults to "false",
     * meaning a page number of 0 in the request equals the first page.
     */
    private boolean oneIndexedParameters = false;

    /**
     * General prefix to be prepended to the page number and page size parameters.
     */
    private String prefix = "";

    /**
     * Delimiter to be used between the qualifier and the actual page number and size
     * properties.
     */
    private String qualifierDelimiter = "_";

    /**
     * Default page size.
     */
    private int defaultPageSize = 20;

    /**
     * Maximum page size to be accepted.
     */
    private int maxPageSize = 2000;

    public String getPageParameter() {
      return this.pageParameter;
    }

    public void setPageParameter(String pageParameter) {
      this.pageParameter = pageParameter;
    }

    public String getSizeParameter() {
      return this.sizeParameter;
    }

    public void setSizeParameter(String sizeParameter) {
      this.sizeParameter = sizeParameter;
    }

    public boolean isOneIndexedParameters() {
      return this.oneIndexedParameters;
    }

    public void setOneIndexedParameters(boolean oneIndexedParameters) {
      this.oneIndexedParameters = oneIndexedParameters;
    }

    public String getPrefix() {
      return this.prefix;
    }

    public void setPrefix(String prefix) {
      this.prefix = prefix;
    }

    public String getQualifierDelimiter() {
      return this.qualifierDelimiter;
    }

    public void setQualifierDelimiter(String qualifierDelimiter) {
      this.qualifierDelimiter = qualifierDelimiter;
    }

    public int getDefaultPageSize() {
      return this.defaultPageSize;
    }

    public void setDefaultPageSize(int defaultPageSize) {
      this.defaultPageSize = defaultPageSize;
    }

    public int getMaxPageSize() {
      return this.maxPageSize;
    }

    public void setMaxPageSize(int maxPageSize) {
      this.maxPageSize = maxPageSize;
    }

  }

  /**
   * Sort properties.
   */
  public static class Sort {

    /**
     * Sort parameter name.
     */
    private String sortParameter = "sort";

    public String getSortParameter() {
      return this.sortParameter;
    }

    public void setSortParameter(String sortParameter) {
      this.sortParameter = sortParameter;
    }

  }

}
