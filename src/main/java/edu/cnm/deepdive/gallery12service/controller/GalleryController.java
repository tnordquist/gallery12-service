package edu.cnm.deepdive.gallery12service.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.cnm.deepdive.gallery12service.model.entity.Gallery;
import edu.cnm.deepdive.gallery12service.model.entity.User;
import edu.cnm.deepdive.gallery12service.service.GalleryService;
import edu.cnm.deepdive.gallery12service.service.ImageService;
import edu.cnm.deepdive.gallery12service.view.GalleryViews;
import java.util.UUID;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/galleries")
@ExposesResourceFor(Gallery.class)
public class GalleryController {

  private final GalleryService galleryService;
  private final ImageService imageService;

  public GalleryController(GalleryService galleryService,
      ImageService imageService) {
    this.galleryService = galleryService;
    this.imageService = imageService;
  }

  @JsonView(GalleryViews.Hierarchical.class)
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Gallery> post(@RequestBody Gallery gallery, Authentication auth) {
    gallery = galleryService.newGallery(gallery, (User) auth.getPrincipal());
    return ResponseEntity.created(gallery.getHref()).body(gallery);
  }

  @JsonView(GalleryViews.Flat.class)
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Iterable<Gallery> getAll(Authentication auth) {
    return galleryService.getAll();
  }

  @JsonView(GalleryViews.Hierarchical.class)
  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Gallery get(@PathVariable UUID id, Authentication auth) {
    return galleryService
        .get(id)
        .orElseThrow();
  }

  @PutMapping(value = "/{galleryId}/images/{imageId}",
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public boolean setImageGallery(@PathVariable UUID galleryId, @PathVariable UUID imageId,
      @RequestBody boolean imageInGallery, Authentication auth) {
    return galleryService
        .get(galleryId)
        .flatMap((gallery) ->
            imageService
                .get(imageId)
                .map((image) -> {
                  if (imageInGallery) {
                    image.setGallery(gallery);
                    gallery.getImages().add(image);
                  } else {
                    image.setGallery(null);
                    gallery.getImages().remove(image);
                  }
                  return galleryService.save(gallery);
                })
        )
        .map((gallery) -> imageInGallery)
        .orElseThrow();
  }

}