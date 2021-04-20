package edu.cnm.deepdive.gallery12service.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.cnm.deepdive.gallery12service.model.entity.Gallery;
import edu.cnm.deepdive.gallery12service.model.entity.Image;
import edu.cnm.deepdive.gallery12service.model.entity.User;
import edu.cnm.deepdive.gallery12service.service.GalleryService;
import edu.cnm.deepdive.gallery12service.service.ImageService;
import edu.cnm.deepdive.gallery12service.view.ImageViews;
import java.io.IOException;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
@ExposesResourceFor(Image.class)
@Profile("service")
public class ImageController {

  private final ImageService imageService;
  private final GalleryService galleryService;

  private static final String ATTACHMENT_DISPOSITION_FORMAT = "attachment: filename=\"%s\"";

  public ImageController(ImageService imageService,
      GalleryService galleryService) {
    this.imageService = imageService;
    this.galleryService = galleryService;
  }

  @JsonView(ImageViews.Hierarchical.class)
  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Image get(@PathVariable UUID id, Authentication auth) {
    return imageService
        .get(id)
        .orElseThrow();
  }

  @GetMapping("/{id}/content")
  public ResponseEntity<Resource> getContent(@PathVariable UUID id, Authentication auth) {
    return imageService
        .get(id)
        .map((image) -> {
          try {
            Resource resource = imageService.getContent(image);
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    String.format(ATTACHMENT_DISPOSITION_FORMAT, image.getName()))
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength()))
                .header(HttpHeaders.CONTENT_TYPE, image.getContentType())
                .body(resource);
          } catch (IOException e) {
            throw new RuntimeException(e); // FIXME
          }
        })
        .orElseThrow();
  }

  @JsonView(ImageViews.Flat.class)
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Iterable<Image> list(Authentication auth) {
    return imageService.list();
  }


}