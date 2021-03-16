package edu.cnm.deepdive.gallery12service.service;

import edu.cnm.deepdive.gallery12service.model.dao.GalleryRepository;
import edu.cnm.deepdive.gallery12service.model.entity.Gallery;
import edu.cnm.deepdive.gallery12service.model.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GalleryService {

  private final GalleryRepository galleryRepository;

  public GalleryService(
      GalleryRepository galleryRepository) {
    this.galleryRepository = galleryRepository;
  }

  public Gallery newGallery(Gallery gallery, User creator) {
    gallery.setCreator(creator);
    return galleryRepository.save(gallery);
  }

  public Optional<Gallery> get(UUID id) {
    return galleryRepository.findById(id);
  }

  public Iterable<Gallery> getAll() {
    return galleryRepository.getAllByOrderByTitleAsc();
  }
}
