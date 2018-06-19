package simplerest.integration;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import com.fasterxml.jackson.databind.ObjectMapper;
import simplerest.Application;
import simplerest.category.model.Category;
import simplerest.category.repository.CategoryRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = Application.class)
public class CategoryControllerIT {
  @LocalServerPort
  private int port;

  private static final Logger log = LoggerFactory.getLogger(CategoryControllerIT.class);

  @Autowired
  private CategoryRepository categoryRepository;

  private URL base;

  @Autowired
  private TestRestTemplate template;

  @Before
  public void setUp() throws Exception {
    this.base = new URL("http://localhost:" + port + "/");
  }

  @Test
  public void checkHello() throws Exception {
    ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
    assertThat(response.getBody(), equalTo("Service running!"));
  }

  @Test
  public void checkSaveCategory() throws Exception {
    Category category = loadCategorySample();
    assertNotNull(category);

    ResponseEntity<Category> response =
        template.postForEntity(base.toString() + "category/new", category, Category.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(category, response.getBody());

    Optional<Category> categoryDb = categoryRepository.findById(category.getId());
    assertEquals(category, categoryDb.get());

    categoryRepository.delete(category);
  }

  @Test
  public void checkSaveCategoryErrorSame() throws Exception {
    Category category = loadCategorySample();
    assertNotNull(category);

    ResponseEntity<Category> response =
        template.postForEntity(base.toString() + "category/new", category, Category.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(category, response.getBody());

    response = template.postForEntity(base.toString() + "category/new", category, Category.class);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    categoryRepository.delete(category);
  }

  @Test
  public void checkSaveCategoryError() throws Exception {
    Category category = loadCategorySample();
    assertNotNull(category);

    UUID categoryId = category.getId();

    category.setId(null);
    ResponseEntity<Category> response =
        template.postForEntity(base.toString() + "category/new", category, Category.class);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    category.setId(categoryId);

    category.setSlug(null);
    response = template.postForEntity(base.toString() + "category/new", category, Category.class);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    categoryRepository.delete(category);
  }

  @Test
  public void checkGetCategoryById() throws Exception {
    Category category = loadCategorySample();
    assertNotNull(category);

    categoryRepository.insert(category);

    ResponseEntity<Category> response =
        template.getForEntity(base.toString() + "category/" + category.getId(), Category.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(category, response.getBody());

    categoryRepository.delete(category);
  }

  @Test
  public void checkGetCategoryByIdNotFound() throws Exception {
    Category category = loadCategorySample();
    assertNotNull(category);

    categoryRepository.insert(category);

    ResponseEntity<Category> response =
        template.getForEntity(base.toString() + "category/" + UUID.randomUUID(), Category.class);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    ResponseEntity<Category> response2 = template
        .getForEntity(base.toString() + "category/test" + UUID.randomUUID(), Category.class);
    assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());

    categoryRepository.delete(category);
  }

  @Test
  public void checkGetCategoryBySlug() throws Exception {
    Category category = loadCategorySample();
    assertNotNull(category);

    categoryRepository.insert(category);

    ResponseEntity<Category> response =
        template.getForEntity(base.toString() + "category/" + category.getSlug(), Category.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(category, response.getBody());

    categoryRepository.delete(category);
  }

  @Test
  public void checkGetCategoryByIdNotVisible() throws Exception {
    Category category = loadCategorySample();
    assertNotNull(category);

    category.setIsVisible(false);
    categoryRepository.insert(category);

    ResponseEntity<Category> response =
        template.getForEntity(base.toString() + "category/" + category.getId(), Category.class);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    categoryRepository.delete(category);
  }

  @Test
  public void checkSaveCategoryWithParent() throws Exception {
    Category category = loadCategoryWithParentSample();
    assertNotNull(category);
    assertNotNull(category.getParentCategory());

    ResponseEntity<Category> response =
        template.postForEntity(base.toString() + "category/new", category, Category.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(category, response.getBody());

    response = template.getForEntity(
        base.toString() + "category/" + category.getId(), Category.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    log.info("category parent REST = {}", response.getBody());
    assertEquals(category, response.getBody());

    Optional<Category> categoryDb = categoryRepository.findById(category.getParentCategory().getId());
    log.info("category parent DB = {}", categoryDb);
    assertTrue(categoryDb.isPresent());
    assertEquals(category.getParentCategory(), categoryDb.get());

    categoryRepository.delete(category);
    categoryRepository.deleteById(category.getParentCategory().getId());
  }

  @Test
  public void checkSaveCategoryWithSameParent() throws Exception {
    Category categorySample = loadCategoryWithParentSample();
    assertNotNull(categorySample);
    assertNotNull(categorySample.getParentCategory());

    ResponseEntity<Category> response =
        template.postForEntity(base.toString() + "category/new", categorySample, Category.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(categorySample, response.getBody());

    UUID categoryFirstId = categorySample.getId();

    Category category = new Category();
    category.setId(UUID.randomUUID());
    category.setSlug("test");
    category.setName("Test");
    category.setIsVisible(true);
    category.setParentCategory(categorySample.getParentCategory());

    response = template.postForEntity(base.toString() + "category/new", category, Category.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(category, response.getBody());

    Optional<Category> categoryDb = categoryRepository.findById(category.getId());
    log.debug("categoryDb = {}", categoryDb);
    assertTrue(categoryDb.isPresent());
    assertEquals(category, categoryDb.get());

    response = template.getForEntity(
        base.toString() + "category/" + categorySample.getParentCategory().getId(), Category.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    log.info("category parent REST = {}", response.getBody());

    assertEquals(2, response.getBody().getChildCategory().size());
    assertEquals(categorySample, response.getBody().getChildCategory().get(0));
    assertEquals(category, response.getBody().getChildCategory().get(1));

    categoryRepository.delete(category);
    categoryRepository.deleteById(categoryFirstId);
    categoryRepository.deleteById(category.getParentCategory().getId());
  }

  private Category loadCategorySample() throws IOException {
    FileInputStream testJSONInputStream =
        new FileInputStream("src/test/resources/catalog/valid_category.json");
    ObjectMapper mapper = new ObjectMapper();
    Category category = mapper.readValue(testJSONInputStream, Category.class);
    return category;
  }

  private Category loadCategoryWithParentSample() throws IOException {
    FileInputStream testJSONInputStream =
        new FileInputStream("src/test/resources/catalog/valid_category_with_parent.json");
    ObjectMapper mapper = new ObjectMapper();
    Category category = mapper.readValue(testJSONInputStream, Category.class);
    return category;
  }
}
