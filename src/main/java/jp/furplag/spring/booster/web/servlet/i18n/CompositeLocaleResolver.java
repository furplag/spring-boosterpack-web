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
package jp.furplag.spring.booster.web.servlet.i18n;

import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import jp.furplag.util.Localizer;
import jp.furplag.util.commons.StringUtils;
import lombok.Setter;

/**
 * HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
DefaultHttpClient client = (DefaultHttpClient) factory.getHttpClient();
client.getCredentialsProvider().setCredentials(
    new AuthScope(hostName, portNumber),
    new UsernamePasswordCredentials(username, password));
RestTemplate restTemplate = new RestTemplate(factory);
 */

/**
 * Allows users with cookies disabled enable to change a custom Locale.
 *
 *
 * @author furplag
 *
 */
@ConfigurationProperties("boosterpack.web.i18n")
public class CompositeLocaleResolver extends CookieLocaleResolver {

  private final static Logger logger = LoggerFactory.getLogger(CompositeLocaleResolver.class);

  @Setter
  private String defaultLocaleName;

  @Setter
  private String defaultZoneId;

  private SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();

  @PostConstruct
  private void init() {
    Locale dafaultLocale = Localizer.getAvailableLocale(StringUtils.defaultString(defaultLocaleName, ""));
    TimeZone defaultTimeZone = TimeZone.getTimeZone(Localizer.getZoneId(StringUtils.defaultIfEmpty(defaultZoneId, null)));
    logger.debug("\n  Boosterpack:\n    Initialize {Locale: {}, TimeZone: {}}", StringUtils.defaultIfEmpty(dafaultLocale.getDisplayName(), "ROOT"), defaultTimeZone.getDisplayName());

    setDefaultLocale(dafaultLocale);
    setDefaultTimeZone(defaultTimeZone);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Locale determineDefaultLocale(HttpServletRequest request) {
      return sessionLocaleResolver.resolveLocale(request);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
      super.setLocale(request, response, locale);
      sessionLocaleResolver.setLocale(request, response, locale);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDefaultLocale(Locale defaultLocale) {
      sessionLocaleResolver.setDefaultLocale(defaultLocale);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDefaultTimeZone(TimeZone timeZone) {
      sessionLocaleResolver.setDefaultTimeZone(timeZone);
  }
}
