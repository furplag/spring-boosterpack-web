package jp.furplag.spring.booster.web.security.authentication.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

/**
 * Processes an authentication pre authorized by SSO Agent.
 *
 * @author furplag
 *
 */
@Slf4j
public class NoPasswdAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  private static final String COOKIE_REMOTE_USER_PARAMETER_NAME = "REMOTE_USER";

  private String userNameParameter = COOKIE_REMOTE_USER_PARAMETER_NAME;

  public NoPasswdAuthenticationFilter() {
    this("/login", RequestMethod.POST.toString());
  }

  public NoPasswdAuthenticationFilter(AntPathRequestMatcher antPathRequestMatcher) {
    super(antPathRequestMatcher);
  }

  public NoPasswdAuthenticationFilter(String pattern, String httpMethod) {
    super(new AntPathRequestMatcher(pattern, httpMethod));
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
    String principal = request.getRemoteUser();
    if (principal == null) {
      principal = request.getHeader(userNameParameter);
      if (principal != null) log.debug("\n  Pre-auth user detected @getHeader.");
    }
    if (principal == null) {
      principal = (String) request.getAttribute(userNameParameter);
      if (principal != null) log.debug("Pre-auth user detected @getAttribute.");
    }
    if (principal == null) {
      List<Cookie> cookies = Arrays.stream(request.getCookies()).filter(c->userNameParameter.equals(c.getName())).collect(Collectors.toList());
      if (cookies.size() > 0) principal = cookies.get(0).getValue();
      if (principal != null) log.debug("Pre-auth user detected @getCookies.");
    }
    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(principal, principal);
    authRequest.setDetails(authenticationDetailsSource.buildDetails(request));

    return this.getAuthenticationManager().authenticate(authRequest);
  }

  public void setUserNameParameter(String userNameParameter){
    Assert.hasText(userNameParameter, "\"userNameParameter\" must not empty.");
    this.userNameParameter = userNameParameter;
  }
}
