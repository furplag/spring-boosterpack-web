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
package jp.furplag.spring.booster.web.context.request.handler;

import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.AsyncWebRequestInterceptor;
import org.springframework.web.context.request.WebRequest;

/**
 * Abstract adapter class for the {@link AsyncWebRequestInterceptor} interface,
 * for simplified implementation of pre-only/post-only interceptors.
 *
 * @author furplag
 *
 */
public abstract class WebRequestInterceptorAdaptor implements AsyncWebRequestInterceptor {

  /**
   * {@inheritDoc}
   * <p>
   * This implementation is empty.
   */
  @Override
  public void preHandle(WebRequest request) throws Exception {}

  /**
   * {@inheritDoc}
   * <p>
   * This implementation is empty.
   */
  @Override
  public void postHandle(WebRequest request, ModelMap model) throws Exception {}

  /**
   * {@inheritDoc}
   * <p>
   * This implementation is empty.
   */
  @Override
  public void afterCompletion(WebRequest request, Exception ex) throws Exception {}

  /**
   * {@inheritDoc}
   * <p>
   * This implementation is empty.
   */
  @Override
  public void afterConcurrentHandlingStarted(WebRequest request) {}

}
