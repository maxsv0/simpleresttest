package simplerest.category.model;

import java.util.UUID;

public class Category {
  UUID id;
  String name;
  String slug;
  String parentCaregory;
  String isVisible;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public String getParentCaregory() {
    return parentCaregory;
  }

  public void setParentCaregory(String parentCaregory) {
    this.parentCaregory = parentCaregory;
  }

  public String getIsVisible() {
    return isVisible;
  }

  public void setIsVisible(String isVisible) {
    this.isVisible = isVisible;
  }
}
