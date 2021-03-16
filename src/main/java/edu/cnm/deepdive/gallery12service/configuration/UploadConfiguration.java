package edu.cnm.deepdive.gallery12service.configuration;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Declares a hierarchical set of properties, mapping to a corresponding hierarchy of application
 * properties, all with the {@code "upload."} prefix. These properties customize the root path,
 * filename generation, and subdirectory organization used by the {@link
 * edu.cnm.deepdive.gallery12service.service.LocalFilesystemStorageService}.
 */
@Component
@ConfigurationProperties(prefix = "upload")
public class UploadConfiguration {

  private boolean applicationHome;
  private String path;
  private List<String> contentTypes;
  private FilenameProperties filename;

  /**
   * Base directory of the file store, relative to the application home directory (if {@code
   * applicationHome} is {@code true}) or to the current working directory.
   */
  private String directory = "uploads";
  /**
   * Regular expression pattern that (in general) includes one or more capture groups, used for
   * constructing a subdirectory path for any given generated filename.
   */
  private Pattern subdirectoryPattern = Pattern.compile("^(.{4})(.{2})(.{2}).*$");

  /**
   * Set of MIME types permitted for upload into the file store.
   */
  private Set<String> whitelist = new LinkedHashSet<>();

  public boolean isApplicationHome() {
    return applicationHome;
  }

  public void setApplicationHome(boolean applicationHome) {
    this.applicationHome = applicationHome;
  }

  /**
   * Returns the base directory of the file store, relative to the application home directory (if
   * {@link #isApplicationHome()} returns {@code true}) or to the current working directory.
   */
  public String getDirectory() {
    return directory;
  }

  /**
   * Sets the base directory of the file store. If {@code directory} is an absolute path, then it
   * will be used as-is; otherwise, it will be interpreted relative to the application home
   * directory (if {@link #isApplicationHome()} returns {@code true}) or to the current working
   * directory.
   */
  public void setDirectory(String directory) {
    this.directory = directory;
  }

  /**
   * Returns a regular expression pattern that (in general) includes one or more capture groups,
   * used for constructing a subdirectory path for any given generated filename.
   */
  public Pattern getSubdirectoryPattern() {
    return subdirectoryPattern;
  }

  /**
   * Sets the regular expression pattern used to capture the subdirectory path components from a
   * generated filename.
   */
  public void setSubdirectoryPattern(Pattern subdirectoryPattern) {
    this.subdirectoryPattern = subdirectoryPattern;
  }
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public List<String> getContentTypes() {
    return contentTypes;
  }

  public void setContentTypes(List<String> contentTypes) {
    this.contentTypes = contentTypes;
  }

  public FilenameProperties getFilename() {
    return filename;
  }

  public void setFilename(
      FilenameProperties filename) {
    this.filename = filename;
  }
  /**
   * Returns the set of MIME types permitted for upload into the file store.
   */
  public Set<String> getWhitelist() {
    return whitelist;
  }

  /**
   * Sets the set of MIME types permitted for upload into the file store.
   */
  public void setWhitelist(Set<String> whitelist) {
    this.whitelist = whitelist;
  }
  public static class FilenameProperties {

    private String unknown;
    private String format;
    private int randomizerLimit;
    private TimestampProperties timestamp;

    public String getUnknown() {
      return unknown;
    }

    public void setUnknown(String unknown) {
      this.unknown = unknown;
    }

    public String getFormat() {
      return format;
    }

    public void setFormat(String format) {
      this.format = format;
    }

    public int getRandomizerLimit() {
      return randomizerLimit;
    }

    public void setRandomizerLimit(int randomizerLimit) {
      this.randomizerLimit = randomizerLimit;
    }

    public TimestampProperties getTimestamp() {
      return timestamp;
    }

    public void setTimestamp(
        TimestampProperties timestamp) {
      this.timestamp = timestamp;
    }

    public static class TimestampProperties {

      private String format;
      private String timeZone;

      public String getFormat() {
        return format;
      }

      public void setFormat(String format) {
        this.format = format;
      }

      public String getTimeZone() {
        return timeZone;
      }

      public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
      }

    }

  }
}