package edu.cnm.deepdive.gallery12service.service;

import edu.cnm.deepdive.gallery12service.model.dao.ImageRepository;
import edu.cnm.deepdive.gallery12service.model.entity.Gallery;
import edu.cnm.deepdive.gallery12service.model.entity.Image;
import edu.cnm.deepdive.gallery12service.model.entity.User;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.multipart.MultipartFile;

@Service
@Profile("service")
public class ImageService {

  private static final String UNTITLED_FILENAME = "untitled";

  private final ImageRepository imageRepository;
  private final StorageService storageService;


  public ImageService(ImageRepository imageRepository,
      StorageService storageService) {
    this.imageRepository = imageRepository;
    this.storageService = storageService;
  }

  /**
   * Selects and returns a {@link Image} with the specified {@code id}, as the content of an {@link
   * Optional Optional&lt;Image&gt;}. If no such instance exists, the {@link Optional} is empty.
   *
   * @param id Unique identifier of the {@link Image}.
   * @return {@link Optional Optional&lt;Image&gt;} containing the selected image.
   */
  public Optional<Image> get(@NonNull UUID id) {
    return imageRepository.findById(id);
  }

  /**
   * Selects and returns all images
   * @return images
   */
  public Iterable<Image> list() {
    return imageRepository.getAllByOrderByCreatedDesc();
  }

  public void delete(@NonNull Image image) throws IOException {
    storageService.delete(image.getKey());
    imageRepository.delete(image);
  }

  public Image save(@NonNull Image image, User contributor) {
    image.setContributor(contributor);
    return imageRepository.save(image);
  }

  public Resource getContent(@NonNull Image image) throws IOException {
    return storageService.retrieve(image.getKey());
  }

  public Image store(@NonNull MultipartFile file, String title, String description, @NonNull User contributor, Gallery gallery)
      throws IOException, HttpMediaTypeException {
    String originalFilename = file.getOriginalFilename();
    String contentType = file.getContentType();
    String key = storageService.store(file);
    Image image = new Image();
    image.setTitle(title);
    image.setDescription(description);
    image.setContributor(contributor);
    image.setName((originalFilename != null) ? originalFilename : UNTITLED_FILENAME);
    image.setContentType((contentType != null) ? contentType: MediaType.APPLICATION_OCTET_STREAM_VALUE);
    image.setKey(key);
    image.setGallery(gallery);
    return imageRepository.save(image);
  }

}