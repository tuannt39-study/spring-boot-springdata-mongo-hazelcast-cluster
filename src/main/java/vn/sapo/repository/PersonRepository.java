package vn.sapo.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import vn.sapo.domain.Person;

import java.util.List;

@Repository
public interface PersonRepository extends MongoRepository<Person, String> {

    @Cacheable(cacheNames = "personFindByEmail")
    @Query("{'email': ?0}")
    List<Person> findByEmail(String email);

}
