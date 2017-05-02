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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import jp.furplag.spring.booster.web.useragent.Wootheed;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserAgentRequestInterceptor extends HandlerInterceptorAdapter {

  private final Logger logger = LoggerFactory.getLogger(UserAgentRequestInterceptor.class);

  private final boolean wootheeEnabled;

  private final String attributeName;

  @PostConstruct
  public void init() {
    if (wootheeEnabled) logger.info("\n  Boosterpack:\n    Injecting Woothee as named \"{}\" in every views.", attributeName);
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    if (!wootheeEnabled || modelAndView == null || ((HandlerMethod)handler).hasMethodAnnotation(ResponseBody.class)) return;

    modelAndView.getModelMap().addAttribute(attributeName, new Wootheed(request.getHeader("User-Agent")));
  }
}
