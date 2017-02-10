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
package jp.furplag.spring.booster.web.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.csrf.MissingCsrfTokenException;

import jp.furplag.spring.booster.web.security.authentication.SessionizedLoginUrlAuthenticationEntryPoint;
import jp.furplag.util.commons.StringUtils;
import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties("webstarter.security")
public abstract class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final Log log = LogFactory.getLog(WebSecurityConfigurerAdapter.class);

  @Getter
  @Setter
  private String loginUrl;

  @Getter
  @Setter
  private String loginSuccessUrl;

  @Getter
  @Setter
  private String loginFailureUrl;

  @Getter
  @Setter
  private String logoutUrl;

  @Getter
  @Setter
  private String logoutSuccessUrl;

  @Getter
  @Setter
  private String timeoutUrl;

  @Getter
  @Setter
  private List<String> ignored = new ArrayList<>();

  @PostConstruct
  public void init() {
    loginUrl = StringUtils.defaultString(loginUrl, "/login");
    if (StringUtils.isBlank(loginSuccessUrl)) {
      loginSuccessUrl = "/";
    }
    if (StringUtils.isBlank(loginFailureUrl)) {
      loginFailureUrl = loginUrl + (loginUrl.contains("?") ? "&" : "?") + "error";
    }
    if (StringUtils.isBlank(logoutUrl)) {
      logoutUrl = loginUrl + (loginUrl.contains("?") ? "&" : "?") + "logout";
    }
    if (StringUtils.isBlank(logoutSuccessUrl)) {
      logoutSuccessUrl = "/";
    }
    if (StringUtils.isBlank(timeoutUrl)) {
      timeoutUrl = loginUrl + (loginUrl.contains("?") ? "&" : "?") + "timeout";
    }
    if (ignored == null) ignored = new ArrayList<>();
    ignored.addAll(Arrays.asList(loginUrl, loginFailureUrl, logoutSuccessUrl, timeoutUrl));
    ignored = ignored.stream().sorted(Comparator.naturalOrder()).distinct().collect(Collectors.toList());

    log.debug(ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE));
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint() {
    return new SessionizedLoginUrlAuthenticationEntryPoint(loginUrl);
  }

  @Bean
  protected AccessDeniedHandler accessDeniedHandler() {
    return new AccessDeniedHandler() {
      @Override
      public void handle(HttpServletRequest request, HttpServletResponse response,
          AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if (accessDeniedException instanceof MissingCsrfTokenException) {
          authenticationEntryPoint().commence(request, response, null);
        } else {
          new AccessDeniedHandlerImpl().handle(request, response, accessDeniedException);
        }
      }
    };
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    // @formatter:off
    web.ignoring().antMatchers("/favicon.ico", "/css/**", "/js/**", "/img/**", "/libs/**", "/boosterpack/**")
    ;
    // @formatter:on
  }

  protected CsrfTokenRepository csrfTokenRepository() {
    HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
    repository.setHeaderName("X-XSRF-TOKEN");

    return repository;
  }
}
