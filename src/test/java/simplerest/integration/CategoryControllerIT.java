package simplerest.integration;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
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
  private static final Logger log = LoggerFactory.getLogger(CategoryControllerIT.class);

  @LocalServerPort
  private int port;
  
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
    FileInputStream testJSONInputStream =
        new FileInputStream("src/test/resources/catalog/valid_category.json");
    ObjectMapper mapper = new ObjectMapper();
    Category category = mapper.readValue(testJSONInputStream, Category.class);
    assertNotNull(category);

    ResponseEntity<Category> response =
        template.postForEntity(base.toString() + "category/new", category, Category.class);
    assertEquals(category, response.getBody());
    
    Optional<Category> categoryDb = categoryRepository.findById(category.getId());
    assertEquals(category, categoryDb.get());
  }
}
