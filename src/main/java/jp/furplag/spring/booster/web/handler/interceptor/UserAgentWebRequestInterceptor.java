package jp.furplag.spring.booster.web.handler.interceptor;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;

import jp.furplag.spring.booster.web.context.request.handler.WebRequestInterceptorAdaptor;
import jp.furplag.spring.booster.web.useragent.Wootheed;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserAgentWebRequestInterceptor extends WebRequestInterceptorAdaptor {

  private final Log log = LogFactory.getLog(UserAgentWebRequestInterceptor.class);

  private final boolean enabled;

  private final String attributeName;

  @PostConstruct
  public void init() {
    if (enabled) log.info("Injecting Woothee as named \"" + attributeName + "\" in every views.");
  }

  @Override
  public void postHandle(WebRequest request, ModelMap model) throws Exception {
    if (enabled && !model.containsAttribute(attributeName)) model.addAttribute(attributeName, new Wootheed(request.getHeader("User-Agent")));
  }
}
