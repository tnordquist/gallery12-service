package edu.cnm.deepdive.gallery12service.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import com.fasterxml.jackson.annotation.JsonView;
import edu.cnm.deepdive.gallery12service.model.entity.Image;
import edu.cnm.deepdive.gallery12service.model.entity.User;
import edu.cnm.deepdive.gallery12service.service.UserService;
import edu.cnm.deepdive.gallery12service.view.ImageViews;
import java.util.UUID;
import java.util.function.Function;
import org.springframework.context.annotation.Profile;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@ExposesResourceFor(User.class)
@Profile("service")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @RequestMapping(method = GET, value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
  public User me(Authentication auth) {
    return (User) auth.getPrincipal();
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public User get(@PathVariable UUID id, Authentication auth) {
    return userService
        .get(id)
        .orElseThrow();
  }

  @GetMapping(value = "/{id}/images", produces = MediaType.APPLICATION_JSON_VALUE)
  @JsonView(ImageViews.Flat.class)
  public Iterable<Image> getImages(@PathVariable UUID id, Authentication auth) {
    return userService.get(id)
        .map(User::getImages)
        .orElseThrow();
  }
}