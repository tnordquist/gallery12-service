package edu.cnm.deepdive.gallery12service.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import edu.cnm.deepdive.gallery12service.view.GalleryViews;
import edu.cnm.deepdive.gallery12service.view.ImageViews;
import java.net.URI;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(
    name = "user_profile",
    indexes = {
        @Index(columnList = "created"),
        @Index(columnList = "connected")
    }
)
@Component
@JsonView({GalleryViews.Hierarchical.class, ImageViews.Hierarchical.class})
public class User {

  private static EntityLinks entityLinks;

  @NonNull
  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(name = "user_id", nullable = false, updatable = false, columnDefinition = "CHAR(16) FOR BIT DATA")
  private UUID id;

  @NonNull
  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date created;

  @NonNull
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date connected;

  @NonNull
  @JsonIgnore
  @Column(nullable = false, updatable = false, unique = true)
  private String oauthKey;

  @NonNull
  @Column(nullable = false, unique = true)
  private String displayName;

  @JsonIgnore
  @NonNull
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "contributor", cascade = CascadeType.ALL)
  @OrderBy("created DESC")
  private final List<Image> images = new LinkedList<>();

  @JsonIgnore
  @NonNull
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade = CascadeType.ALL)
  @OrderBy("created DESC")
  private final List<Gallery> galleries = new LinkedList<>();

  @NonNull
  public UUID getId() {
    return id;
  }

  @NonNull
  public Date getCreated() {
    return created;
  }

  @NonNull
  public Date getConnected() {
    return connected;
  }

  public void setConnected(@NonNull Date connected) {
    this.connected = connected;
  }

  @NonNull
  public String getOauthKey() {
    return oauthKey;
  }

  public void setOauthKey(@NonNull String oauthKey) {
    this.oauthKey = oauthKey;
  }

  @NonNull
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(@NonNull String displayName) {
    this.displayName = displayName;
  }

  @NonNull
  public List<Image> getImages() {
    return images;
  }

  @NonNull
  public List<Gallery> getGalleries() {
    return galleries;
  }

  public URI getHref() {
    //noinspection ConstantConditions
    return (id != null) ? entityLinks.linkForItemResource(User.class, id).toUri() : null;
  }

  @PostConstruct
  private void initHateoas() {
    //noinspection ResultOfMethodCallIgnored
    entityLinks.toString();
  }

  @Autowired
  public void setEntityLinks(
      @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") EntityLinks entityLinks) {
    User.entityLinks = entityLinks;
  }
}