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
package jp.furplag.spring.booster.web.security.authentication;

import java.io.IOException;
import java.util.StringJoiner;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import lombok.Getter;
import lombok.Setter;

/**
 *
 *
 * @author furplag
 *
 */
public class GentlyLogoutSuccessHandler implements LogoutSuccessHandler {

  private static final Logger logger = LoggerFactory.getLogger(GentlyLogoutSuccessHandler.class);

  @Value("#{T(org.apache.commons.lang3.StringUtils).defaultIfEmpty('${boosterpack.web.security.logout-success-url:}', '/quit')}")
  @Getter
  @Setter
  private String logoutSuccessUrl;

  @Value("#{T(org.apache.commons.lang3.StringUtils).defaultIfEmpty('${boosterpack.web.mvc.locale-change-param-name:}', 'locale')}")
  @Getter
  @Setter
  private String localeChangeParamName;


  @PostConstruct
  private void init() {}

  /**
   * {@inheritDoc}
   */
  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

    response.setStatus(HttpStatus.OK.value());
    final String logoutSuccessUrl = new StringJoiner("")
      .add(request.getContextPath())
      .add(this.logoutSuccessUrl)
      .add("GET".equals(request.getMethod()) ? "quietly" : "")
      .add("?").add(localeChangeParamName).add("=")
      .add(LocaleContextHolder.getLocale().getLanguage())
      .toString();
    logger.debug("User \"{}\" logged out. redirect to \"{}\".", authentication != null ? authentication.getName() : "Anonymous", logoutSuccessUrl);

    response.sendRedirect(logoutSuccessUrl);
  }
}
