package edu.cnm.deepdive.gallery12service.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.InvalidPathException;
import org.springframework.core.io.Resource;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

//  StorageReference store(MultipartFile file) throws IOException, ForbiddenMimeTypeException;

  /**
   * Stores the specified file, returning an opaque reference to the file as a {@link String}.
   *
   * @param file Uploaded file.
   * @return Opaque reference, to be used when retrieving or deleting the file from the store.
   * @throws IOException                         If the file cannot be written for any reason.
   * @throws HttpMediaTypeNotAcceptableException If the content-type of {@code file} is not
   *                                             allowed.
   */
  String store(MultipartFile file) throws IOException, HttpMediaTypeNotAcceptableException;

  Resource retrieve(String reference) throws InvalidPathException, MalformedURLException;

  boolean delete(String reference)
      throws InvalidPathException, UnsupportedOperationException, SecurityException;

  class StorageReference {

    private final String filename;
    private final String reference;

    public StorageReference(String filename, String reference) {
      this.filename = filename;
      this.reference = reference;
    }

    public String getFilename() {
      return filename;
    }

    public String getReference() {
      return reference;
    }

  }

  class ForbiddenMimeTypeException extends RuntimeException {

    public ForbiddenMimeTypeException() {
    }

    public ForbiddenMimeTypeException(String message) {
      super(message);
    }

    public ForbiddenMimeTypeException(String message, Throwable cause) {
      super(message, cause);
    }

    public ForbiddenMimeTypeException(Throwable cause) {
      super(cause);
    }

  }

}