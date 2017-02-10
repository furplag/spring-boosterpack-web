package jp.furplag.spring.booster.web.useragent;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.math.NumberUtils;

import is.tagomor.woothee.Classifier;
import jp.furplag.util.commons.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Wootheed implements Serializable {

  private final String category;
  private final String name;
  private final String version;
  private final String os;
  private final String vendor;
  private final String osVersion;
  private final String userAgent;

  private final boolean crawler;
  private final boolean ie;
  private final double versionNumber;
  private final double osVersionNumber;
  private final boolean outdated;

  public Wootheed(String userAgent) {
    this.userAgent = StringUtils.defaultString(userAgent);
    Map<String, String> classified = Classifier.parse(this.userAgent);
    this.category = StringUtils.flatten(classified.getOrDefault("category", "unknown"));
    this.name = StringUtils.flatten(classified.getOrDefault("name", "unknown"));
    this.version = StringUtils.flatten(classified.getOrDefault("version", "unknown"));
    this.os = StringUtils.flatten(classified.getOrDefault("os", "unknown"));
    this.vendor = StringUtils.flatten(classified.getOrDefault("vendor", "unknown"));
    this.osVersion = StringUtils.flatten(classified.getOrDefault("os_version", "unknown"));
    this.crawler = "crawler".equalsIgnoreCase(category);
    this.ie = "internetexplorer".equalsIgnoreCase(name);
    this.versionNumber = versionNumber(version);
    this.osVersionNumber = versionNumber(osVersion);
    this.outdated = !crawler && isObsolute();
    System.out.println(this.toString());

  }

  /**
   * version to numericaly.
   *
   * @param version version string
   * @return versionNumber version number
   */
  private static double versionNumber(final String version) {
    if (NumberUtils.isCreatable(version.toString())) return NumberUtils.toDouble(version);
    String versionString = Arrays.stream(version.split("\\.", 2)).map(s->s.replaceAll("[^0-9]", "")).collect(Collectors.joining("."));

    return NumberUtils.toDouble(versionString);
  }

  /**
   * lazy browser detection for presentation.
   *
   * @return isObsolute returns true if outdated browser.
   */
  private final boolean isObsolute() {
    if (crawler) return false;
    if (ie && versionNumber < 9) return true;
    if (name.contains("firefox") && versionNumber < 38) return true;
    if (name.contains("chrome") && versionNumber < 38) return true;
    if (name.contains("safari") && os.contains("windows") && versionNumber < 6) return true;
    if (name.contains("safari") && os.contains("windows") && versionNumber < 6) return true;

    return false;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
