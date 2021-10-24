package springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.model.Person;

public interface PeopleRepository extends JpaRepository<Person, Long> {
}
