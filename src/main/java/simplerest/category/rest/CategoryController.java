package simplerest.category.rest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import simplerest.category.model.Category;
import simplerest.category.repository.CategoryRepository;

@RestController
public class CategoryController {
  private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

  private CategoryRepository categoryRepository;

  @Autowired
  public CategoryController(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @RequestMapping("/")
  public String index() {
    return "Service running!";
  }

  @RequestMapping(value = "/category/{categoryName}", method = RequestMethod.GET)
  public ResponseEntity<Category> getCategory(@PathVariable String categoryName) {
    log.info("Get request recieved. categoryName = {}", categoryName);

    Category category = getCategoryByUrl(categoryName);

    if (category == null) {
      return new ResponseEntity<Category>(HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity<Category>(category, HttpStatus.OK);
    }
  }

  @RequestMapping(value = "/category/edit", method = RequestMethod.PATCH)
  public ResponseEntity<Category> patchCategory(@RequestParam String categoryId,
      @RequestParam String isVisible) {
    log.info("Patch Request recieved. categoryId = {}, isVisible = {}", categoryId, isVisible);

    Category category = getCategoryById(categoryId, false);

    if (category == null) {
      return new ResponseEntity<Category>(HttpStatus.NOT_FOUND);
    } else {
      category.setIsVisible(Boolean.valueOf(isVisible));
      Category categorySaved = categoryRepository.save(category);

      return new ResponseEntity<Category>(categorySaved, HttpStatus.OK);
    }
  }

  @RequestMapping(value = "/category/new", method = RequestMethod.POST)
  public ResponseEntity<Category> saveCategory(@RequestBody Category category) {
    log.info("Save Category = {}", category);

    if (category.getId() == null || category.getSlug() == null) {
      return new ResponseEntity<Category>(HttpStatus.BAD_REQUEST);
    }

    try {
      Category categoryResult = saveCategoryNew(category);
      return new ResponseEntity<Category>(categoryResult, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<Category>(HttpStatus.BAD_REQUEST);
    }
  }

  private Category getCategoryById(String caregoryId, Boolean isVisible) {
    try {
      UUID caregoryUuid = UUID.fromString(caregoryId);
      Optional<Category> category;

      if (isVisible) {
        category = categoryRepository.findByIdAndIsVisibleIsTrue(caregoryUuid);
      } else {
        category = categoryRepository.findById(caregoryUuid);
      }

      if (category.isPresent()) {
        return category.get();
      } else {
        return null;
      }
    } catch (IllegalArgumentException exception) {
      return null;
    }
  }

  private Category getCategoryByUrl(String categoryName) {
    Category category = getCategoryById(categoryName, true);
    log.info("Search by UUID result = {}", category);

    if (category == null) {
      category = getCategoryBySlug(categoryName);
      log.info("Search by Slug result = {}", category);
    }

    if (category != null) {
      List<Category> childCategory =
          categoryRepository.findByParentCategoryIdAndIsVisibleIsTrue(category.getId());
      log.info("childCategory = {}", childCategory);
      
      if (!childCategory.isEmpty()) {
        category.setChildCategory(childCategory);
      }
    }

    return category;
  }

  private Category getCategoryBySlug(String slug) {
    return categoryRepository.findBySlugAndIsVisibleIsTrue(slug);
  }

  private Category saveCategoryNew(Category category) {
    if (category.getParentCategory() != null) {
      Optional<Category> categoryParent =
          categoryRepository.findById(category.getParentCategory().getId());
      if (!categoryParent.isPresent()) {
        saveCategoryNew(category.getParentCategory());
      }
    }

    return categoryRepository.insert(category);
  }
}
