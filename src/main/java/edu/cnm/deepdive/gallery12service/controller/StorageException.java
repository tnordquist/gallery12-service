package edu.cnm.deepdive.gallery12service.controller;

import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class StorageException extends ResponseStatusException {

  private static final String NOT_FOUND_REASON = "User not found";

  public StorageException(IOException ex) {
    super(HttpStatus.NOT_FOUND, NOT_FOUND_REASON, ex);
  }
}
