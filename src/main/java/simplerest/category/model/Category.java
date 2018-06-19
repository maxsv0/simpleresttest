package simplerest.category.model;

import java.util.List;
import java.util.UUID;
import org.springframework.data.annotation.Transient;

public class Category {
  UUID id;
  String name;
  String slug;
  Category parentCategory;
  @Transient
  List<Category> childCategory;
  Boolean isVisible;

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

  public Category getParentCategory() {
    return parentCategory;
  }

  public void setParentCategory(Category parentCategory) {
    this.parentCategory = parentCategory;
  }

  public Boolean getIsVisible() {
    return isVisible;
  }

  public void setIsVisible(Boolean isVisible) {
    this.isVisible = isVisible;
  }

  public List<Category> getChildCategory() {
    return childCategory;
  }

  public void setChildCategory(List<Category> childCategory) {
    this.childCategory = childCategory;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Category other = (Category) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (isVisible == null) {
      if (other.isVisible != null)
        return false;
    } else if (!isVisible.equals(other.isVisible))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (parentCategory == null) {
      if (other.parentCategory != null)
        return false;
    } else if (!parentCategory.equals(other.parentCategory))
      return false;
    if (slug == null) {
      if (other.slug != null)
        return false;
    } else if (!slug.equals(other.slug))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Category [id=" + id + ", name=" + name + ", slug=" + slug + ", parentCategory="
        + parentCategory + ", isVisible=" + isVisible + "]";
  }

}
