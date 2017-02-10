/**
 * Copyright (C) 2016+ furplag (https://github.com/furplag/)
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

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import jp.furplag.util.commons.StringUtils;
import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties("webstarter.security")
public class SessionizedLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

  @Getter
  @Setter
  private String loginUrl;

  @Getter
  @Setter
  private String loginFailureUrl;

  @Getter
  @Setter
  private String logoutUrl;

  @Getter
  @Setter
  private String timeoutUrl;

  /**
   *
   * @param loginFormUrl URL where the login page can be found. Should either be
   * relative to the web-app context path (include a leading {@code /}) or an absolute
   * URL.
   */
  public SessionizedLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
    super(loginFormUrl);
  }

  @PostConstruct
  public void init() {
    loginUrl = StringUtils.defaultString(loginUrl, "/login");
    if (StringUtils.isBlank(loginFailureUrl)) {
      loginFailureUrl = loginUrl + (loginUrl.contains("?") ? "&" : "?") + "error";
    }
    if (StringUtils.isBlank(logoutUrl)) {
      logoutUrl = loginUrl + (loginUrl.contains("?") ? "&" : "?") + "logout";
    }
    if (StringUtils.isBlank(timeoutUrl)) {
      timeoutUrl = loginUrl + (loginUrl.contains("?") ? "&" : "?") + "timeout";
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
    if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

      return;
    }

    super.commence(request, response, authException);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String buildRedirectUrlToLoginPage(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
    if (request.getRequestedSessionId() != null && !request.isRequestedSessionIdValid()) {
      return timeoutUrl;
    }

    return super.buildRedirectUrlToLoginPage(request, response, authException);
  }
}
