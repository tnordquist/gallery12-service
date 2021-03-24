package edu.cnm.deepdive.gallery12service.model.dao;

import edu.cnm.deepdive.gallery12service.model.entity.Image;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, UUID> {

  Iterable<Image> getAllByOrderByCreatedDesc();

}
