package edu.cnm.deepdive.gallery12service.service;

import edu.cnm.deepdive.gallery12service.model.dao.GalleryRepository;
import edu.cnm.deepdive.gallery12service.model.entity.Gallery;
import edu.cnm.deepdive.gallery12service.model.entity.User;
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
}
