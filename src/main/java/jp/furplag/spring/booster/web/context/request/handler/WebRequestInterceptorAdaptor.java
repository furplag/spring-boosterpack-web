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
