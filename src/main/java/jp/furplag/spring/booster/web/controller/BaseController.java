package jp.furplag.spring.booster.web.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * A minimal standard mappings for WebApp.
 *
 * @author furplag
 *
 */
public abstract class BaseController {

  @Value("#{T(jp.furplag.util.commons.StringUtils).defaultIfEmpty('${boosterpack.web.security.login-url:}', '/login')}")
  private String loginUrl;

  @Value("#{T(jp.furplag.util.commons.StringUtils).defaultIfEmpty('${boosterpack.web.security.login-success-url:}', '/')}")
  private String loginSuccessUrl;

  @Value("#{T(jp.furplag.util.commons.StringUtils).defaultIfEmpty('${server.error.path:}', '/error')}")
  private String errorPath;

  @Autowired
  MessageSource messageSource;

  /**
   * default: boosterpack-web/src/main/resources/templates/boosterpack/index.html
   *
   * @param request {@link HttpServletRequest}
   * @param principal {@link Principal}
   * @param model {@link Model}
   * @return modelAndView {@link org.springframework.web.servlet.ModelAndView ModelAndView}
   */
  @RequestMapping({"/", "/index"})
  public String index(HttpServletRequest request, Principal principal, Model model) {
    return "boosterpack/index";
  }

  /**
   * default: boosterpack-web/src/main/resources/templates/boosterpack/login/login.html
   *
   * @param request {@link HttpServletRequest}
   * @param principal {@link Principal}
   * @param model {@link Model}
   * @return modelAndView {@link org.springframework.web.servlet.ModelAndView ModelAndView}
   */
  @GetMapping("#{T(jp.furplag.util.commons.StringUtils).defaultIfEmpty('${boosterpack.web.security.login-url:}', '/login')}")
  public String login(HttpServletRequest request, Principal principal, Model model) {
    if (principal != null && !request.isUserInRole("ROLE_ANONYMOUS")) return "redirect:" + loginSuccessUrl;

    return "boosterpack/login/login";
  }

  /**
   * default: boosterpack-web/src/main/resources/templates/boosterpack/index.html
   *
   * @param request {@link HttpServletRequest}
   * @param principal {@link Principal}
   * @param model {@link Model}
   * @return modelAndView {@link org.springframework.web.servlet.ModelAndView ModelAndView}
   */
  @RequestMapping("#{T(jp.furplag.util.commons.StringUtils).defaultIfEmpty('${boosterpack.web.security.login-success-url:}', '/dashboard')}")
  public String dashboard(HttpServletRequest request, Principal principal, Model model) {
    return "boosterpack/index";
  }

  /**
   * default: boosterpack-web/src/main/resources/templates/boosterpack/index.html
   *
   * @param request {@link HttpServletRequest}
   * @param principal {@link Principal}
   * @param model {@link Model}
   * @return modelAndView {@link org.springframework.web.servlet.ModelAndView ModelAndView}
   */
  @GetMapping("#{T(jp.furplag.util.commons.StringUtils).defaultIfEmpty('${boosterpack.web.security.logout-success-url:}', '/quit')}")
  public String logout(HttpServletRequest request, Principal principal, RedirectAttributes attributes) {
    attributes.addFlashAttribute("notificationType", "info");
    attributes.addFlashAttribute("notificationMessage", messageSource.getMessage("message.logout", null, "sign out successfully.", LocaleContextHolder.getLocale()));

    return "redirect:/";
  }

  /**
   * default: json: {"result": true, "message": "sign out successfully."}
   *
   * @param request {@link HttpServletRequest}
   * @param principal {@link Principal}
   * @param model {@link Model}
   * @return json: {"result": true, "message": "sign out successfully."}
   */
  @GetMapping("#{T(jp.furplag.util.commons.StringUtils).defaultIfEmpty('${boosterpack.web.security.logout-success-url:}', '/quit')}quietly")
  @ResponseBody
  public Map<?, ?> logoutQuietly(HttpServletRequest request, HttpServletResponse response, Principal principal) {
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("result", principal == null || request.isUserInRole("ROLE_ANONYMOUS"));
    responseBody.put("message", messageSource.getMessage("message.logout", null, "sign out successfully.", LocaleContextHolder.getLocale()));

    return responseBody;
  }

  /**
   * default: json: {"result": true}
   *
   * @param request {@link HttpServletRequest}
   * @param principal {@link Principal}
   * @param model {@link Model}
   * @return json: {"result": true}
   */
  @GetMapping({"/resuscitate"})
  @ResponseBody
  public Map<?, ?> resuscitate(HttpServletRequest request, HttpServletResponse response, Principal principal) {
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("result", principal != null && !request.isUserInRole("ROLE_ANONYMOUS"));

    return responseBody;
  }

  @RequestMapping({"#{T(jp.furplag.util.commons.StringUtils).defaultIfEmpty('${boosterpack.web.security.login-failure-url:}', '/error/unauthorized')}"})
  public String unauthrized(RedirectAttributes attributes) {
    attributes.addFlashAttribute("unauthorized", true);
//    attributes.addFlashAttribute("notificationType", "danger");
//    attributes.addFlashAttribute("notificationMessage", messageSource.getMessage("error.unauthorized", null, "authentication failure.", LocaleContextHolder.getLocale()));

    return "redirect:" + loginUrl;
  }

  /**
   * default: boosterpack-web/src/main/resources/templates/boosterpack/error/timeout.html
   *
   * @param request {@link HttpServletRequest}
   * @param principal {@link Principal}
   * @param model {@link Model}
   * @return modelAndView {@link org.springframework.web.servlet.ModelAndView ModelAndView}
   */
  @RequestMapping({"#{T(jp.furplag.util.commons.StringUtils).defaultIfEmpty('${boosterpack.web.security.timeout-url:}', '/error/timeout')}"})
  public String timeout(HttpServletRequest request, Principal principal, RedirectAttributes attributes) {
    if (principal != null && !request.isUserInRole("ROLE_ANONYMOUS")) return "redirect:" + loginSuccessUrl;
    attributes.addFlashAttribute("timeout", true);

    return "redirect:" + loginUrl;
  }

  /**
   * default: boosterpack-web/src/main/resources/templates/boosterpack/error/noscript.html
   *
   * @param request {@link HttpServletRequest}
   * @param response {@link HttpServletResponse}
   * @param model {@link Model}
   * @return modelAndView {@link org.springframework.web.servlet.ModelAndView ModelAndView}
   */
  @RequestMapping({"/error/noscript"})
  public String noscript(HttpServletRequest request, HttpServletResponse response, Model model) {
    return "boosterpack/error/noscript";
  }

  /**
   * default: boosterpack-web/src/main/resources/templates/boosterpack/error/nosupport.html
   *
   * @param request {@link HttpServletRequest}
   * @param response {@link HttpServletResponse}
   * @param model {@link Model}
   * @return modelAndView {@link org.springframework.web.servlet.ModelAndView ModelAndView}
   */
  @RequestMapping({"/error/nosupport"})
  public String nosupport(HttpServletRequest request, HttpServletResponse response, Model model) {
    return "boosterpack/error/nosupport";
  }
}
