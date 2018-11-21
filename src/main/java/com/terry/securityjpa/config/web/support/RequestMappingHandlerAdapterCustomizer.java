package com.terry.securityjpa.config.web.support;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.terry.securityjpa.config.properties.SpringDataWebProperties;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RequestMappingHandlerAdapter 객체가 초기화가 된 뒤에 해야 할 작업들을 정의하는 객체이다.
 * 여기서 할 일은
 * 1. RequestMappingHandlerAdapter에 등록된 HandlerMethodArgumentResolver 중에서 @EnableSpringDataWebSupport 어노테이션 설정으로 인해
 *    등록된 PageableHandlerMethodArgumentResolver 객체를 찾아 oneIndexedParameters 속성값을 true로 주어 1페이지를 1로 시작하게끔 해준다
 *    (이것과 비슷한 역할을 하는 CustomPageableHandlerMethodArgumentResolver 는 oneIndexedParameters 속성값을 true로 준뒤 WebConfig 클래스에서
 *    addArgumentResolvers 메소드를 통해 등록된다)
 * @author Terry Chang
 *
 */
@AllArgsConstructor
@Slf4j
public class RequestMappingHandlerAdapterCustomizer implements BeanPostProcessor {

  private SpringDataWebProperties springDataWebProperties;
  
  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    // TODO Auto-generated method stub
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    // TODO Auto-generated method stub
    if(bean instanceof RequestMappingHandlerAdapter) {
      RequestMappingHandlerAdapter adapter = (RequestMappingHandlerAdapter)bean;
      List<HandlerMethodArgumentResolver> argumentResolvers = adapter.getArgumentResolvers();
      if(argumentResolvers != null) {
        for(Iterator<HandlerMethodArgumentResolver> iter = argumentResolvers.listIterator(); iter.hasNext();) {
          HandlerMethodArgumentResolver resolver = iter.next();
          logger.debug("resolver's class name is : " + resolver.getClass().getSimpleName());
          if(resolver.getClass() == PageableHandlerMethodArgumentResolver.class) {
            PageableHandlerMethodArgumentResolver pageableHandlerArgumentResolver = (PageableHandlerMethodArgumentResolver)resolver;
            /* 
            oneIndexedParameters 속성값을 바꿀때 WebConfig 클래스의 addArgumentResolvers 메소드에서 
            CustomPageableHandlerMethodArgumentResolver 객체를 등록하는 부분에서도 oneIndexedParameters 속성값을 같은 값으로 설정해주어야 한다
            */
            pageableHandlerArgumentResolver.setMaxPageSize(springDataWebProperties.getPageable().getMaxPageSize());
            pageableHandlerArgumentResolver.setOneIndexedParameters(springDataWebProperties.getPageable().isOneIndexedParameters());
            pageableHandlerArgumentResolver.setPageParameterName(springDataWebProperties.getPageable().getPageParameter());
            pageableHandlerArgumentResolver.setPrefix(springDataWebProperties.getPageable().getPrefix());
            pageableHandlerArgumentResolver.setQualifierDelimiter(springDataWebProperties.getPageable().getQualifierDelimiter());
            pageableHandlerArgumentResolver.setSizeParameterName(springDataWebProperties.getPageable().getSizeParameter());
            break;
          }
        }
      }
    }
    return bean;
  }

}
