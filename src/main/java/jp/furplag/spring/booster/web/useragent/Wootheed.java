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
package jp.furplag.spring.booster.web.useragent;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import is.tagomor.woothee.Classifier;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Wootheed implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(Wootheed.class);

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
    this.category = Objects.toString(classified.getOrDefault("category", "unknown"), "unknown").toLowerCase().replaceAll("\\s", "");
    this.name = Objects.toString(classified.getOrDefault("name", "unknown"), "unknown").toLowerCase().replaceAll("\\s", "");
    this.version = Objects.toString(classified.getOrDefault("version", "unknown"), "unknown").toLowerCase().replaceAll("\\s", "");
    this.os = Objects.toString(classified.getOrDefault("os", "unknown"), "unknown").toLowerCase().replaceAll("\\s", "");
    this.vendor = Objects.toString(classified.getOrDefault("vendor", "unknown"), "unknown").toLowerCase().replaceAll("\\s", "");
    this.osVersion = Objects.toString(classified.getOrDefault("os_version", "unknown"), "unknown").toLowerCase().replaceAll("\\s", "");
    this.crawler = "crawler".equalsIgnoreCase(category);
    this.ie = "internetexplorer".equalsIgnoreCase(name);
    this.versionNumber = versionNumber(version);
    this.osVersionNumber = versionNumber(osVersion);
    this.outdated = !crawler && isObsolete();

    logger.debug(ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE));
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
   * @return isObsolete returns true if outdated browser.
   */
  private final boolean isObsolete() {
    if (crawler) return false;
    if (ie && versionNumber < 9) return true;
    if (name.contains("edge") && versionNumber < 12) return true;
    if (name.contains("firefox") && versionNumber < 38) return true;
    if (name.contains("chrome") && versionNumber < 35) return true;
    if (name.contains("safari") && versionNumber < 8) return true;
    if (name.contains("opera") && versionNumber < 12) return true;
    if (name.contains("vivaldi") && versionNumber < 1) return true;
    if (name.contains("android") && versionNumber < 4.44) return true;
    if (userAgent.contains("opera mini")) return true;

    return false;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }
}
