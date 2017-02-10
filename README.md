# spring-boosterpack-web



usual configuration for [Spring Boot](https://github.com/spring-projects/spring-boot) webapp (war) faster, easier.

## Feature

### WebMvcConfig scaffolds.
More easier to i18n support.

#### Usage
```WebMvcConfiguration.java
@Configuration
public class WebMvcConfiguration extends jp.furplag.spring.booster.web.config.WebMvcConfigurerAdapter {
  // Add LocaleChangeInterceptor.
  // Enable to use i18n validation messages.
  ...
}
```

### WebSecurityConfig scaffolds.
* Inject CSRF token to HTTP header.
* Static resources permitted all ( css, js ... ) .

#### Usage
```WebSecurityConfiguration.java
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends jp.furplag.spring.booster.web.config.WebSecurityConfiguration {
  // Add CsrfHeaderFilter.
  // Regist Static resource path to IgnoredRequestConfigurer.
  ...
}
```

### Enable to detect UserAgent more easily ( [Woothee](https://github.com/woothee/woothee-java) provides ) .
#### Usage
```SomeController.java
  @GetMapping("/")
  public String index(Wootheed wootheed) {
    logger.info(ToStringBuilder.reflectionToString(wootheed, ToStringStyle.MULTI_LINE_STYLE));
    ...
  }
```

## Author
Furplag

## License
Code is under the [Apache Licence 2.0](LICENCE).
