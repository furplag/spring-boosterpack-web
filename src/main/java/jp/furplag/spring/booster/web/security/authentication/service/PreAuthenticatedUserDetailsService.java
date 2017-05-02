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
package jp.furplag.spring.booster.web.security.authentication.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public abstract class PreAuthenticatedUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException;

  protected String getPrincipal(final PreAuthenticatedAuthenticationToken token) {
    return StringUtils.defaultString(token == null ? null : (token.getPrincipal() == null ? null : token.getPrincipal().toString()));
  }

  protected String getCredencial(final PreAuthenticatedAuthenticationToken token) {
    return StringUtils.defaultString(token == null ? null : (token.getCredentials() == null ? null : token.getCredentials().toString()));
  }
}
