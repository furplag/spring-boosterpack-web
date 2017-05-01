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

  @Value("#{T(jp.furplag.util.commons.StringUtils).defaultIfEmpty('${boosterpack.web.security.logout-success-url:}', '/quit')}")
  @Getter
  @Setter
  private String logoutSuccessUrl;

  @Value("#{T(jp.furplag.util.commons.StringUtils).defaultIfEmpty('${boosterpack.web.mvc.locale-change-param-name:}', 'locale')}")
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
