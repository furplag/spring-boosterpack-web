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
package jp.furplag.spring.booster.web.security.authentication.filter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.Assert;

import jp.furplag.util.commons.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoPasswordPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

  private static final String COOKIE_REMOTE_USER_PARAMETER_NAME = "REMOTE_USER";

  private String userNameParameter = COOKIE_REMOTE_USER_PARAMETER_NAME;

  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    String principal = request.getRemoteUser();
    if (principal == null) {
      principal = request.getHeader(userNameParameter);
      if (principal != null) log.debug("\n  Pre-auth user detected @getHeader.");
    }
    if (principal == null) {
      principal = (String) request.getAttribute(userNameParameter);
      if (principal != null) log.debug("Pre-auth user detected @getAttribute.");
    }
    if (principal == null && request.getCookies() != null) {
      List<Cookie> cookies = Arrays.stream(request.getCookies()).filter(c->userNameParameter.equals(c.getName())).collect(Collectors.toList());
      if (cookies.size() > 0) principal = cookies.get(0).getValue();
      if (principal != null) log.debug("Pre-auth user detected @getCookies.");
    }

    return StringUtils.defaultString(principal);
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return "";
  }

  public void setUserNameParameter(String userNameParameter){
    Assert.hasText(userNameParameter, "\"userNameParameter\" must not empty.");
    this.userNameParameter = userNameParameter;
  }
}
