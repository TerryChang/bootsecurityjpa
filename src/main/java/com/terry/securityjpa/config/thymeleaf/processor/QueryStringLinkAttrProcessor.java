package com.terry.securityjpa.config.thymeleaf.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.EngineEventUtils;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring4.naming.SpringContextVariableNames;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.LinkExpression;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.StringUtils;
import org.unbescape.html.HtmlEscape;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class QueryStringLinkAttrProcessor extends AbstractAttributeTagProcessor {

  public static final int ATTR_PRECEDENCE = 1300;
  public static final String ATTR_NAME = "queryStringLink";
  private static final char PARAMS_START_CHAR = '(';
  private static final char PARAMS_END_CHAR = ')';
  private static final char EXPRESSION_END_CHAR = '}';
  private String charset = "UTF-8";

  public QueryStringLinkAttrProcessor(TemplateMode templateMode, String dialectPrefix, String charset) {
    super(templateMode, dialectPrefix,  null, false, ATTR_NAME, true, ATTR_PRECEDENCE, true);
    this.charset = charset;

  }

  @Override
  protected void doProcess(ITemplateContext context
      , IProcessableElementTag tag
      , AttributeName attributeName                       // thymeleaf 속성인 th:queryStringLink
      , String attributeValue                             // th:queryStringLink 속성에 설정한 값
      , IElementTagStructureHandler structureHandler) {

    final RequestContext requestContext = (RequestContext)context.getVariable(SpringContextVariableNames.SPRING_REQUEST_CONTEXT);   // URI를 구하기 위한 Request Context
    final LinkExpression linkExpression;
    final Object expressionResult;

    logger.info("attributeName.getAttributeName() : " + attributeName.getAttributeName());
    logger.info("attributeValue :" + attributeValue);

    if(StringUtils.isEmptyOrWhitespace(attributeValue)) {       // queryString 속성에 값이 없을 경우 null로 return 한다
      expressionResult = null;
    } else {
      // queryString 속성의 값을 Thyemeleaf에서 제공하는 링크를 표현한 객체로 변환한다
      // 예를 들어 attributeValue에 @{/board/ist.html(page=1)가 있으면 이를 LinkExpression 객체로 변환할 경우 /board/list.html 과 page=1을 각각 분리해서 보관한다
      // 즉 LinkExpression 객체의 base 속성엔 /board/list.html을, parameters 속성엔 page=1을 갖게 된다
      linkExpression = (LinkExpression)StandardExpressions.getExpressionParser(context.getConfiguration()).parseExpression(context, attributeValue);

      if(linkExpression == null) {
        expressionResult = null;
      } else {
        if(requestContext.getQueryString() == null) {               // Request Cnntext에서 QueryString이 없으면 QueryString을 붙일 필요가 없어지기 때문에 사용자가 queryString 속성에 입력한 값만을 가지고 작업하면 된다
          expressionResult = linkExpression.execute(context);
        } else {
          // QueryString을 key와 value로 분리해내서 각각 저장한다

          URI uri = null;
          List<NameValuePair> nvp = null;

          try {
            uri = new URI(requestContext.getRequestUri() + "?" + requestContext.getQueryString());
            nvp = URLEncodedUtils.parse(uri, Charset.forName(charset));
          } catch(URISyntaxException urise) {
            logger.error("Passed URI Syntax Invalid : " + uri, urise);
          }

          AssignationSequence assignationSequence = linkExpression.getParameters();

          // 사용자가 queryString 속성에 넣은 값에 파라미터가 있을 경우 QueryString 에서 사용자가 넣은 파라미터와 동일한 값이 있는지 찾아보고
          // 이것이 있을 경우 이를 제거한뒤에 사용자가 queryString 속성에 넣은 파라미터 값으로 대체해서 넣는다
          // 예를 들어 Request QueryString에 check=name&page=3이란 값이 있고 queryString 속성에 넣은 값으로 @{/board/list.html(page=1)} 이렇게 있으면
          // page=3과 page=1이 같은 파라미터 이름으로 중복되기 때문에 page=3을 지우고 page=1로 대체하는 작업을 진행한다
          // 대체하는 방법은 해당 파라미터를 key로 하고 있는 NameValuePair 객체를 List에서 찾아서 이를 지운뒤에 page=1을 NameValuePair 객체로 생성해서
          // 이를 List에 넣는다
          if(assignationSequence != null) {
            for(Assignation assignation : assignationSequence) {
              nvp.removeIf(e -> assignation.getLeft().getStringRepresentation().equals(e.getName()));
              nvp.add(new BasicNameValuePair(assignation.getLeft().getStringRepresentation(), assignation.getRight().getStringRepresentation()));
            }
          }

          // 최종 작업이 완료된 QueryString 이 들어있는 List 객체안의 값들을 key=value 형태의 문자열로 만든뒤에 이것을 각각 ,로 결합한다
          // 위에서 사용한 예를 가지고 표현하게 되면 check=name,page=1 이렇게 된다
          // 이렇게 만든 문자열을 이따가 Thymeleaf에서 queryString 속성에 넣은 값의 파라미터로 다시 재구성을 하게 된다
          // 즉 @{/board/list.html(page=1)}을 @{/board/list.html(check=name,page=1)} 으로 바꾸는 것이다
          // 아래의 expressionResult 까지가 그 과정이다
          final String parameters = nvp.stream()
              .map(nv -> nv.getName() + "=" + nv.getValue() + "")
              .collect(Collectors.joining(","));

          final StringBuilder stringBuilder = new StringBuilder();

          if(linkExpression.hasParameters()) {
            stringBuilder.append(attributeValue.split("\\(", 2)[0]);
          } else {
            stringBuilder.append(attributeValue.substring(0, attributeValue.lastIndexOf(EXPRESSION_END_CHAR)));
          }

          stringBuilder.append(PARAMS_START_CHAR)
              .append(parameters)
              .append(PARAMS_END_CHAR)
              .append(EXPRESSION_END_CHAR);

          attributeValue = stringBuilder.toString();
          expressionResult = EngineEventUtils.computeAttributeExpression(context, tag, attributeName, attributeValue).execute(context);
        }
      }
    }

    // 최종 작업이 완료된 expressionResult 변수에 저장된 값을 이용해서 href 속성에 HTML Link 형태의 값으로 변환한 값을 설정하게 된다
    structureHandler.setAttribute("href", HtmlEscape.escapeHtml4Xml(expressionResult == null ? null : expressionResult.toString()));
  }
}
