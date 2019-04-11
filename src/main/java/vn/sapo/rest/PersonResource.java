package vn.sapo.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.*;
import vn.sapo.domain.Person;
import vn.sapo.repository.PersonRepository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/persons")
public class PersonResource {

    private final Logger log = LoggerFactory.getLogger(PersonResource.class);

    private final PersonRepository personRepository;

    private final CacheManager cacheManager;

    public PersonResource(PersonRepository personRepository, CacheManager cacheManager) {
        this.personRepository = personRepository;
        this.cacheManager = cacheManager;
    }

    @PostConstruct
    public void init() {
        log.info("Cache manager: " + cacheManager);
        log.info("Cache manager names: " + cacheManager.getCacheNames());
    }

    @GetMapping
    public List<Person> contact() {
        return personRepository.findAll();
    }

    @PostMapping
    public Person save(@RequestBody Person contact) {
        personRepository.save(contact);
        return contact;
    }

    @GetMapping("/{id}")
    public Optional<Person> show(@PathVariable String id) {
        return personRepository.findById(id);
    }

    @GetMapping("/find")
    public List<Person> findByEmail(@RequestParam String email) {
        return personRepository.findByEmail(email);
    }

    @PutMapping("/{id}")
    public Person update(@PathVariable String id, @RequestBody Person person) {
        Optional<Person> optPerson = personRepository.findById(id);
        Person c = optPerson.get();
        if (person.getFirstName() != null) {
            c.setFirstName(person.getFirstName());
        }
        if (person.getLastName() != null) {
            c.setLastName(person.getLastName());
        }
        if (person.getAge() != null) {
            c.setAge(person.getAge());
        }
        if (person.getEmail() != null) {
            c.setEmail(person.getEmail());
        }
        log.info("update: " + id);
        personRepository.save(c);
        return c;
    }

    @DeleteMapping("/{id}")
    public Person delete(@PathVariable String id) {
        Optional<Person> optPerson = personRepository.findById(id);
        Person person = optPerson.get();
        log.info("delete: " + id);
        personRepository.delete(person);
        return person;
    }

}
