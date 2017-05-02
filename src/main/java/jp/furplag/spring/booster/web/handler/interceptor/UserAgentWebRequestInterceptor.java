/**
 * Copyright (C) 2016+ furplag (https://github.com/furplag)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
