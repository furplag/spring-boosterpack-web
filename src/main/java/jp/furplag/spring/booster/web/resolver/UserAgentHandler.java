package jp.furplag.spring.booster.web.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jp.furplag.spring.booster.web.useragent.Wootheed;

/**
 * Enable to use UserAgent more easily.
 *
 * @author furplag
 *
 */
public class UserAgentHandler implements HandlerMethodArgumentResolver {

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return Wootheed.class.isAssignableFrom(parameter.getParameterType());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    return new Wootheed(webRequest.getHeader("User-Agent"));
  }
}
