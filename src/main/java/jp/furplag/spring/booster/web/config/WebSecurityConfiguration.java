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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jp.furplag.spring.booster.web.security.authentication.GentlyLogoutSuccessHandler;
import jp.furplag.spring.booster.web.security.authentication.SessionizedLoginUrlAuthenticationEntryPoint;
import jp.furplag.spring.booster.web.security.filter.CsrfHeaderFilter;
import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties("boosterpack.web.security")
public abstract class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final Logger logger = LoggerFactory.getLogger(WebSecurityConfigurerAdapter.class);

  @Getter
  @Setter
  private Boolean gatewayEnabled = true;

  @Getter
  @Setter
  protected String loginUrl;

  @Getter
  @Setter
  private String loginFailureUrl;

  @Getter
  @Setter
  private String loginSuccessUrl;

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
  private List<String> allowedAnonymous = new ArrayList<>();

  @Getter
  @Setter
  private Boolean sessionResuscitate = false;

  @PostConstruct
  public void init() {
    if (allowedAnonymous == null) allowedAnonymous = new ArrayList<>();

    loginUrl = StringUtils.defaultIfBlank(loginUrl, "/login");
    loginFailureUrl = StringUtils.defaultIfBlank(loginFailureUrl, "/error/unauthorized");
    loginSuccessUrl = StringUtils.defaultIfBlank(loginSuccessUrl, "/dashboard");
    logoutUrl = StringUtils.defaultIfBlank(logoutUrl, "/logout");
    logoutSuccessUrl = StringUtils.defaultIfBlank(logoutSuccessUrl, "/quit");
    timeoutUrl = StringUtils.defaultIfBlank(timeoutUrl, "/error/timeout");
    gatewayEnabled = gatewayEnabled == null || gatewayEnabled;

    if (gatewayEnabled) allowedAnonymous.addAll(Arrays.asList("/", "/index"));
    allowedAnonymous.addAll(Arrays.asList(loginUrl, loginFailureUrl, logoutUrl, logoutSuccessUrl, timeoutUrl, "/error/**", "/resuscitate"));
    allowedAnonymous = allowedAnonymous.stream().sorted(Comparator.naturalOrder()).distinct().collect(Collectors.toList());
    sessionResuscitate = sessionResuscitate != null || sessionResuscitate;

    logger.debug("  Boosterpack\n-----\n{}-----", ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE));
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
  public LogoutSuccessHandler gentlyLogoutSuccessHandler() {
    GentlyLogoutSuccessHandler gentlyLogoutSuccessHandler = new GentlyLogoutSuccessHandler();

    return gentlyLogoutSuccessHandler;
  }

  /**
   *
   *
   * @return {@link AccessDeniedHandler}
   */
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

  /**
   * Inject XSRF-TOKEN to HttpHeader.
   *
   * @return {@link CsrfTokenRepository}
   */
  @Bean
  protected CsrfTokenRepository csrfTokenRepository() {
    HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
    repository.setHeaderName("X-XSRF-TOKEN");

    return repository;
  }

  /**
   * Override this method to configure the {@link HttpSecurity}. Typically subclasses
   * should not invoke this method by calling super as it may override their
   * configuration. The default configuration is:
   *
   * <pre>
   * http.authorizeRequests().anyRequest().authenticated().and().formLogin().and().httpBasic();
   * </pre>
   *
   * @param http the {@link HttpSecurity} to modify
   * @throws Exception if an error occurs
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http
    .authorizeRequests()
        .antMatchers(allowedAnonymous.toArray(new String[]{})).permitAll()
        .anyRequest().authenticated()
      .and().formLogin()
        .loginPage(getLoginUrl()).permitAll()
        .loginProcessingUrl(getLoginUrl()).permitAll()
        .failureUrl(getLoginFailureUrl()).permitAll()
        .defaultSuccessUrl(getLoginSuccessUrl()).permitAll()
      .and().rememberMe()
      .and().logout()
        .logoutRequestMatcher(new AntPathRequestMatcher(getLogoutUrl(), HttpMethod.POST.toString()))
        .logoutSuccessHandler(gentlyLogoutSuccessHandler())
        .deleteCookies("JSESSIONID", "jsessionid")
        .invalidateHttpSession(true).permitAll()
      .and().sessionManagement()
        .maximumSessions(-1).expiredUrl(getTimeoutUrl())
        .and().sessionFixation().newSession()
      .and().csrf()
        .ignoringAntMatchers(allowedAnonymous.toArray(new String[]{}))
        .csrfTokenRepository(csrfTokenRepository())
      .and().exceptionHandling()
        .authenticationEntryPoint(authenticationEntryPoint())
        .accessDeniedHandler(accessDeniedHandler())
      .and().addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class)
    ;
    // @formatter:on
  }

  /**
   * {@inheritDoc}
   * <p>
   * Provides to access static resources before authentication. The default configuration is:
   * <pre>favicon, /css, /js, /img, /webjars/**, and /boosterpack/**.</pre>
   * </p>
   */
  @Override
  public void configure(WebSecurity web) throws Exception {
    // @formatter:off
    web.ignoring().antMatchers("/favicon.ico", "/css/**", "/js/**", "/img/**", "/libs/**", "/webjars/**", "/boosterpack/**");
    // @formatter:on
  }
}
