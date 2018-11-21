package com.terry.securityjpa.config.web.support;

import java.lang.reflect.Method;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.terry.securityjpa.dto.SearchDTO;

import lombok.Getter;
import lombok.Setter;

/**
 * PageableHandlerMethodArgumentResolver 클래스가 Controller의 메소드에서 파라미터로 paging관련 값을 PageRequest 객체로 전달해주는 것과는 다르게
 * 이 클래스는 검색과 관련된 파라미터 값 및 paging 관련 값들이 같이 들어있는 SearchDTO 클래스 객체로 전달해준다
 * @author Terry Chang
 *
 */
@Getter
@Setter
public class CustomPageableHandlerMethodArgumentResolver extends PageableHandlerMethodArgumentResolver {

  private static final String DEFAULT_SEARCHTYPE_PARAMETER = "searchType";
  private static final String DEFAULT_SEARCHWORD_PARAMETER = "searchWord";
  private static final Pageable DEFAULT_PAGE_REQUEST = new SearchDTO(null, null, 0, 20);
  private static final String INVALID_DEFAULT_PAGE_SIZE = "Invalid default page size configured for method %s! Must not be less than one!";

  private Pageable fallbackPageable = DEFAULT_PAGE_REQUEST;
  
  private String searchTypeParameterName = DEFAULT_SEARCHTYPE_PARAMETER;
  private String searchWordParameterName = DEFAULT_SEARCHWORD_PARAMETER;
  
  public CustomPageableHandlerMethodArgumentResolver() {
    super();
  }
  
  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    // TODO Auto-generated method stub
    return SearchDTO.class.equals(parameter.getParameterType());
  }

  @Override
  public Pageable resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
    // TODO Auto-generated method stub
    
    Pageable defaultOrFallback = getDefaultFromAnnotationOrFallback(methodParameter);
    
    String searchType = webRequest.getParameter(getParameterNameToUse(searchTypeParameterName, methodParameter));
    String searchWord = webRequest.getParameter(getParameterNameToUse(searchWordParameterName, methodParameter)); 
    String pageString = webRequest.getParameter(getParameterNameToUse(super.getPageParameterName(), methodParameter));
    String pageSizeString = webRequest.getParameter(getParameterNameToUse(super.getSizeParameterName(), methodParameter));
    
    int page = StringUtils.hasText(pageString) ? parseAndApplyBoundaries(pageString, Integer.MAX_VALUE, true)
        : defaultOrFallback.getPageNumber();
    int pageSize = StringUtils.hasText(pageSizeString) ? parseAndApplyBoundaries(pageSizeString, super.getMaxPageSize(), false)
        : defaultOrFallback.getPageSize();
    
    SearchDTO searchDTO = new SearchDTO(searchType, searchWord, page, pageSize);
    
    return searchDTO;
  }
  
  private Pageable getDefaultFromAnnotationOrFallback(MethodParameter methodParameter) {

    if (methodParameter.hasParameterAnnotation(PageableDefault.class)) {
      return getDefaultPageRequestFrom(methodParameter);
    }

    return fallbackPageable;
  }

  private Pageable getDefaultPageRequestFrom(MethodParameter parameter) {

    PageableDefault defaults = parameter.getParameterAnnotation(PageableDefault.class);
    Integer defaultPageNumber = defaults.page();
    
    /*
     * 아래의 세 줄은 원래 Integer defaultPageSize = getSpecificPropertyOrDefaultFromValue(defaults, "size");를 사용하려 했으나
     * SpringDataAnnotationUtils 클래스의 getSpecificPropertyOrDefaultFromValue 메소드를 사용할 수 없어서
     * 해당 메소드의 코드를 현재 상황에 맞게 바꿔서 구현한 것이다
     */
    Object propertyDefaultValue = AnnotationUtils.getDefaultValue(defaults, "size");
    Object propertyValue = AnnotationUtils.getValue(defaults, "size");
    Integer defaultPageSize = (Integer)(ObjectUtils.nullSafeEquals(propertyDefaultValue, propertyValue) ? AnnotationUtils.getValue(defaults)
        : propertyValue);

    if (defaultPageSize < 1) {
      Method annotatedMethod = parameter.getMethod();
      throw new IllegalStateException(String.format(INVALID_DEFAULT_PAGE_SIZE, annotatedMethod));
    }

    if (defaults.sort().length == 0) {
      return new PageRequest(defaultPageNumber, defaultPageSize);
    }

    return new PageRequest(defaultPageNumber, defaultPageSize, defaults.direction(), defaults.sort());
  }

  /**
   * Tries to parse the given {@link String} into an integer and applies the given boundaries. Will return 0 if the
   * {@link String} cannot be parsed.
   * 
   * @param parameter the parameter value.
   * @param upper the upper bound to be applied.
   * @param shiftIndex whether to shift the index if {@link #oneIndexedParameters} is set to true.
   * @return
   */
  private int parseAndApplyBoundaries(String parameter, int upper, boolean shiftIndex) {

    try {
      int parsed = Integer.parseInt(parameter) - (super.isOneIndexedParameters() && shiftIndex ? 1 : 0);
      return parsed < 0 ? 0 : parsed > upper ? upper : parsed;
    } catch (NumberFormatException e) {
      return 0;
    }
  }

}
