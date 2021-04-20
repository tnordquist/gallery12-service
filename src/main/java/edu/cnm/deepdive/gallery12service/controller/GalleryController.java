package edu.cnm.deepdive.gallery12service.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.cnm.deepdive.gallery12service.model.entity.Gallery;
import edu.cnm.deepdive.gallery12service.model.entity.Image;
import edu.cnm.deepdive.gallery12service.model.entity.User;
import edu.cnm.deepdive.gallery12service.service.GalleryService;
import edu.cnm.deepdive.gallery12service.service.ImageService;
import edu.cnm.deepdive.gallery12service.view.GalleryViews;
import edu.cnm.deepdive.gallery12service.view.ImageViews;
import java.io.IOException;
import java.util.UUID;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

  /**
   * Stores uploaded file content along with a new {@link Image} instance referencing the content.
   *
   * @param title       Summary of uploaded content.
   * @param description Detailed description of uploaded content.
   * @param file        MIME content of single file upload.
   * @param auth        Authentication token with {@link User} principal.
   * @return Instance of {@link Image} created &amp; persisted for the uploaded content.
   */
  @JsonView(GalleryViews.Hierarchical.class)
  @PostMapping(value = "/{galleryId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Image> post(
      @PathVariable(required = false) UUID galleryId,
      @RequestParam MultipartFile file,
      @RequestParam(required = false) String title,
      @RequestParam(required = false) String description,
      Authentication auth) throws IOException, HttpMediaTypeException {
    return galleryService.get(galleryId)
        .map((gallery) -> securePost(file, (User) auth.getPrincipal(), gallery, title, description))
        .orElseThrow(ImageNotFoundException::new);
  }

  private ResponseEntity<Image> securePost(MultipartFile file, User user, Gallery gallery,
      String title, String description) {
    try {
      Image image = imageService.store(file, title, description, user, gallery);
      return ResponseEntity.created(image.getHref()).body(image);
    } catch (IOException e) {
      throw new StorageException(e);
    } catch (HttpMediaTypeException e) {
      throw new MimeTypeNotAllowedException();
    }
  }

}