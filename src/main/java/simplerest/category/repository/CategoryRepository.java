package simplerest.category.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import simplerest.category.model.Category;

public interface CategoryRepository extends MongoRepository<Category, UUID> {
  Optional<Category> findById(UUID id);
  
  Category findBySlug(String slug);
  
  
}