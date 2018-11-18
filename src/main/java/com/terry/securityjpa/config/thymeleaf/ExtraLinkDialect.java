package com.terry.securityjpa.config.thymeleaf;

import com.terry.securityjpa.config.thymeleaf.processor.QueryStringLinkAttrProcessor;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
public class ExtraLinkDialect extends AbstractProcessorDialect {

  public static final String NAME = "ExtraLink";
  public static final String DEFAULT_PREFIX = "th";
  public static final int PROCESSOR_PRECEDENCE = 800; // 현재 dialect의 우선순위를 정하는 변수(spring xml 설정시 order 속성을 생각하면 됨)
  private String charset = "UTF-8";

  public ExtraLinkDialect() {
    super(NAME, DEFAULT_PREFIX, PROCESSOR_PRECEDENCE);
  }

  public ExtraLinkDialect(String charset) {
    this();
    this.charset = charset;
  }

  @Override
  public Set<IProcessor> getProcessors(String dialectPrefix) {
    final Set<IProcessor> processors = new LinkedHashSet<>();

    processors.add(new QueryStringLinkAttrProcessor(TemplateMode.HTML, dialectPrefix, charset));
    return processors;
  }
}
