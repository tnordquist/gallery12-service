package edu.cnm.deepdive.gallery12service.service;

import edu.cnm.deepdive.gallery12service.configuration.UploadConfiguration;
import edu.cnm.deepdive.gallery12service.configuration.UploadConfiguration.FilenameProperties;
import edu.cnm.deepdive.gallery12service.configuration.UploadConfiguration.FilenameProperties.TimestampProperties;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Simple implementation of {@link StorageService}, storing files in a directory specified in the
 * application properties, further organized into subdirectories by specified portions of the
 * generated filenames.
 */
@Service
@Profile("service")
public class LocalFilesystemStorageService implements StorageService {

  private static final String KEY_PATH_DELIMITER = "/";
  private static final String KEY_PATH_FORMAT = "%s" + KEY_PATH_DELIMITER + "%s";
  private static final String INVALID_MEDIA_FORMAT = "%s is not allowed in this storage service.";

  private final Random rng;
  private final Path uploadDirectory;
  private final Pattern subdirectoryPattern;
  private final Set<String> whitelist;
  private final DateFormat formatter;
  private final String filenameFormat;
  private final int randomizerLimit;
  private final List<MediaType> contentTypes;

  @Autowired
  public LocalFilesystemStorageService(
      Random rng, UploadConfiguration uploadConfiguration, ApplicationHome applicationHome) {
    this.rng = rng;
    FilenameProperties filenameProperties = uploadConfiguration.getFilename();
    TimestampProperties timestampProperties = filenameProperties.getTimestamp();
    String uploadPath = uploadConfiguration.getDirectory();
    uploadDirectory = uploadConfiguration.isApplicationHome()
        ? applicationHome.getDir().toPath().resolve(uploadPath)
        : Path.of(uploadPath);
    //noinspection ResultOfMethodCallIgnored
    uploadDirectory.toFile().mkdirs();
    subdirectoryPattern = uploadConfiguration.getSubdirectoryPattern();
    whitelist = uploadConfiguration.getWhitelist();
    contentTypes = whitelist.stream()
        .map(MediaType::valueOf)
        .collect(Collectors.toList());
    filenameFormat = filenameProperties.getFormat();
    randomizerLimit = filenameProperties.getRandomizerLimit();
    formatter = new SimpleDateFormat(timestampProperties.getFormat());
    formatter.setTimeZone(TimeZone.getTimeZone(timestampProperties.getTimeZone()));
  }

  @Override
  public String store(MultipartFile file) throws IOException, HttpMediaTypeException {
    if (!whitelist.contains(file.getContentType())) {
      throw new HttpMediaTypeException(String.format(INVALID_MEDIA_FORMAT, file.getContentType()),
          (Throwable) contentTypes) {
      };
    }
    String originalFilename = file.getOriginalFilename();
    String newFileName = String.format(filenameFormat, formatter.format(new Date()),
        rng.nextInt(randomizerLimit),
        getExtension((originalFilename != null) ? originalFilename : ""));
    String subdirectory = getSubdirectory(newFileName);
    Path resolvedPath = uploadDirectory.resolve(subdirectory);
    resolvedPath.toFile().mkdirs();
    Files.copy(file.getInputStream(), resolvedPath.resolve(newFileName));
    return String.format(KEY_PATH_FORMAT, subdirectory, newFileName);
  }

  @PostConstruct
  private void initUploads() {
    //noinspection ResultOfMethodCallIgnored
    uploadDirectory.toFile().mkdirs();
  }

  @Override
  public Resource retrieve(String key) throws InvalidPathException, MalformedURLException {
    Path file = uploadDirectory.resolve(key);
    return new UrlResource(file.toUri());
  }

  @Override
  public boolean delete(String key)
      throws IOException, UnsupportedOperationException, SecurityException {
    try {
      File file = uploadDirectory.resolve(key).toFile();
      return file.delete();
    } catch (InvalidPathException e) {
      throw new IOException(e);
    }
  }

  @NonNull
  private String getExtension(@NonNull String filename) {
    int position;
    position = filename.lastIndexOf('.');
    return ((position) >= 0) ? filename.substring(position + 1) : "";
  }

  private String getSubdirectory(@NonNull String filename) {
    String path;
    Matcher matcher = subdirectoryPattern.matcher(filename);
    if (matcher.matches()) {
      path = IntStream.rangeClosed(1, matcher.groupCount())
          .mapToObj(matcher::group)
          .collect(Collectors.joining(KEY_PATH_DELIMITER));
    } else {
      path = "";
    }
    return path;
  }

}