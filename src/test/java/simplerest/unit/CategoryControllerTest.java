package simplerest.unit;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import simplerest.Application;
import simplerest.category.model.Category;
import simplerest.category.repository.CategoryRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class CategoryControllerTest {
  @Autowired
  private CategoryRepository categoryRepository;

  private Category category;

  @Autowired
  private MockMvc mvc;

  @Before
  public void setUp() throws Exception {
    category = loadCategorySample();
    assertNotNull(category);

    categoryRepository.insert(category);
  }

  @After
  public void cleanUp() throws Exception {
    categoryRepository.delete(category);
  }

  @Test
  public void checkHello() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(content().string(equalTo("Service running!")));
  }

  @Test
  public void checkGetCategoryById() throws Exception {
    MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/category/" + category.getId())
        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

    ObjectMapper mapper = new ObjectMapper();
    Category categoryResult =
        mapper.readValue(result.getResponse().getContentAsString(), Category.class);
    assertEquals(category, categoryResult);
  }

  @Test
  public void checkGetCategoryBySlug() throws Exception {
    MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/category/" + category.getSlug())
        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

    ObjectMapper mapper = new ObjectMapper();
    Category categoryResult =
        mapper.readValue(result.getResponse().getContentAsString(), Category.class);
    assertEquals(category, categoryResult);
  }

  @Test
  public void checkUpdateCategory() throws Exception {
    MvcResult result = mvc.perform(MockMvcRequestBuilders.patch("/category/edit")
        .param("categoryId", category.getId().toString()).param("isVisible", "false")
        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

    category.setIsVisible(false);

    ObjectMapper mapper = new ObjectMapper();
    Category categoryResult =
        mapper.readValue(result.getResponse().getContentAsString(), Category.class);
    assertEquals(category, categoryResult);

    result = mvc.perform(MockMvcRequestBuilders.patch("/category/edit")
        .param("categoryId", category.getId().toString()).param("isVisible", "true")
        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

    category.setIsVisible(true);
    mapper = new ObjectMapper();
    categoryResult = mapper.readValue(result.getResponse().getContentAsString(), Category.class);
    assertEquals(category, categoryResult);
  }

  @Test
  public void checkUpdateCategoryNotFound() throws Exception {
    mvc.perform(MockMvcRequestBuilders.patch("/category/edit")
        .param("categoryId", UUID.randomUUID().toString()).param("isVisible", "false")
        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());

  }

  private Category loadCategorySample() throws IOException {
    FileInputStream testJSONInputStream =
        new FileInputStream("src/test/resources/catalog/valid_category.json");
    ObjectMapper mapper = new ObjectMapper();
    Category category = mapper.readValue(testJSONInputStream, Category.class);
    return category;
  }
}
