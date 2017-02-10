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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import jp.furplag.spring.booster.web.resolver.UserAgentHandler;
import jp.furplag.util.commons.StringUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * An implementation of {@link WebMvcConfigurer} with empty methods allowing
 * subclasses to override only the methods they're interested in.
 *
 * @author furplag
 *
 */
@ConfigurationProperties("webstarter.mvc")
public abstract class WebMvcConfigurerAdapter implements WebMvcConfigurer {

  private final Log log = LogFactory.getLog(WebMvcConfigurerAdapter.class);

  @Getter
  @Setter
  protected String validationMessagePrefix;

  @Getter
  @Setter
  protected String localeChangeParamName;

  @Getter
  @Setter
  protected List<String> localeChangeHttpMethods = new ArrayList<>();

  @Getter
  @Setter
  protected Boolean localeChangeignoreInvalid;

  @Autowired
  protected MessageSource messageSource;

  @PostConstruct
  public void init() {
    if (localeChangeHttpMethods == null) localeChangeHttpMethods = new ArrayList<>();
    if (localeChangeHttpMethods.size() < 1) localeChangeHttpMethods.addAll(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    localeChangeignoreInvalid = localeChangeignoreInvalid == null || localeChangeignoreInvalid;
    localeChangeParamName = StringUtils.defaultString(localeChangeParamName, "locale");
    validationMessagePrefix = StringUtils.defaultString(validationMessagePrefix);

    log.debug(ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE));
  }

  /**
   * Enable to i18n requesting.
   *
   * @return localeResolver {@link SessionLocaleResolver}
   */
  @Bean
  public LocaleResolver localeResolver() {
    SessionLocaleResolver localeResolver = new SessionLocaleResolver();
    localeResolver.setDefaultLocale(Locale.ROOT);

    return localeResolver;
  }

  /**
   * Enable to i18n requesting.
   *
   * @return localeChangeInterceptor　{@link LocaleChangeInterceptor}
   */
  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
    LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
    localeChangeInterceptor.setIgnoreInvalidLocale(localeChangeignoreInvalid);
    localeChangeInterceptor.setHttpMethods(localeChangeHttpMethods.toArray(new String[]{}));
    localeChangeInterceptor.setParamName(localeChangeParamName);

    return localeChangeInterceptor;
  }

  /**
   * LocalValidatorFactoryBean for return localized messages in validation.
   *
   * @return {@link LocalValidatorFactoryBean}
   */
  @Bean
  public LocalValidatorFactoryBean validator() {
    LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
    localValidatorFactoryBean.setValidationMessageSource(messageSource);

    return localValidatorFactoryBean;
  }

  /**
   *
   *
   * @return {@link DefaultMessageCodesResolver}
   */
  @Bean
  public DefaultMessageCodesResolver  messageCodesResolver() {
    DefaultMessageCodesResolver defaultMessageCodesResolver = new DefaultMessageCodesResolver();
    defaultMessageCodesResolver.setPrefix(validationMessagePrefix);

    return defaultMessageCodesResolver;
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation is empty.
   */
  @Override
  public void configurePathMatch(PathMatchConfigurer configurer) {}

  /**
   * {@inheritDoc}
   * <p>
   * This implementation is empty.
   */
  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {}

  /**
   * {@inheritDoc}
   * <p>
   * This implementation is empty.
   */
  @Override
  public void configureAsyncSupport(AsyncSupportConfigurer configurer) {}

  /**
   * {@inheritDoc}
   * <p>
   * This implementation is empty.
   */
  @Override
  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {}

  /**
   * {@inheritDoc}
   * <p>
   * This implementation is empty.
   */
  @Override
  public void addFormatters(FormatterRegistry registry) {}

  /**
   * {@inheritDoc}
   * <p>
   * This implementation is empty.
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(localeChangeInterceptor());
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation is empty.
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {}


  /**
   * {@inheritDoc}
   * <p>
   * This implementation is empty.
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    // @formatter:off
    /*
    registry.addMapping("/**")
      .allowCredentials(false)
      .allowedOrigins("*")
      .allowedMethods("GET", "POST", "PUT", "DELETE","OPTIONS")
      .allowedHeaders("Origin", "X-Requested-With", "Content-Type", "Accept")
      .maxAge(3600);
    */
    // @formatter:on
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation is empty.
   */
  @Override
  public void addViewControllers(ViewControllerRegistry registry) {}

  /**
   * {@inheritDoc}
   * <p>
   * This implementation is empty.
   */
  @Override
  public void configureViewResolvers(ViewResolverRegistry registry) {}

  /**
   * {@inheritDoc}
   * <p>
   * This implementation provides classified UA (a.k.a. Woothee) .
   */
  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    argumentResolvers.add(new UserAgentHandler());
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation is empty.
   */
  @Override
  public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {}

  /**
   * {@inheritDoc}
   * <p>
   * This implementation is empty.
   */
  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {}

  /**
   * {@inheritDoc}
   * <p>
   * This implementation is empty.
   */
  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {}

  /**
   * {@inheritDoc}
   * <p>
   * This implementation is empty.
   */
  @Override
  public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {}

  /**
   * {@inheritDoc}
   * <p>
   * This implementation is empty.
   */
  @Override
  public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {}

  /**
   * {@inheritDoc}
   * @see {@link jp.furplag.spring.boot.starter.webstarter.config.WebMvcConfigurerAdapter#validator() validator()}
   */
  @Override
  public Validator getValidator() {
    return validator();
  }

  /**
   * {@inheritDoc}
   * @see {@link jp.furplag.spring.boot.starter.webstarter.config.WebMvcConfigurerAdapter#messageCodesResolver() messageCodesResolver()}
   */
  @Override
  public MessageCodesResolver getMessageCodesResolver() {
    return messageCodesResolver();
  }
}
