package jp.furplag.spring.booster.web.handler.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jp.furplag.spring.booster.web.useragent.Wootheed;

public class UserAgentMethodArgumentResolver implements HandlerMethodArgumentResolver {

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
      NativeWebRequest request, WebDataBinderFactory binderFactory) throws Exception {
    return new Wootheed(request.getHeader("User-Agent"));
  }
}
