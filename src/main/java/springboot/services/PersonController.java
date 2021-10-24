package springboot.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.model.Person;
import springboot.model.Session;
import springboot.model.User;
import springboot.repository.PeopleRepository;
import springboot.repository.SessionRepository;
import springboot.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
public class PersonController {
    private static final Logger logger = LogManager.getLogger();

    private PeopleRepository peopleRepository;
    private UserRepository userRepository;
    private SessionRepository sessionRepository;

    public PersonController(PeopleRepository peopleRepository, UserRepository userRepository, SessionRepository sessionRepository) {
        this.peopleRepository = peopleRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        User newUser = userRepository.findUserByLoginAndPassword(user.getLogin(), user.getPassword());
        if (newUser == null)
            return new ResponseEntity<>(null, HttpStatus.valueOf(404));

        Session session = new Session();
        session.setUserId(newUser);
        session.setToken(UUID.randomUUID().toString());
        sessionRepository.save(session);

        JSONObject token = new JSONObject();
        token.put("token", session.getToken());

        return new ResponseEntity<>(token.toString(), HttpStatus.valueOf(200));
    }

    @GetMapping("/people")
    public ResponseEntity<ArrayList<Person>> fetchPeople(@RequestHeader Map<String, String> headers) {
        if (!headers.containsKey("authorization") || !sessionRepository.findById(headers.get("authorization")).isPresent())
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        ArrayList<Person> people = (ArrayList<Person>) peopleRepository.findAll();
        return new ResponseEntity<>(people, HttpStatus.valueOf(200));
    }

    @PostMapping("/people")
    public ResponseEntity<String> insertPerson(@RequestHeader Map<String, String> headers, @RequestBody Map<String, String> details) {
        if (!headers.containsKey("authorization") || !sessionRepository.findById(headers.get("authorization")).isPresent())
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));

        JSONArray errors = new JSONArray();

        if (!details.containsKey("firstName") || !details.containsKey("lastName") || !details.containsKey("birthDate"))
            errors.put("One or more fields are empty or misspelled");
        if (details.containsKey("firstName") && details.get("firstName").length() > 100)
            errors.put("First name must be under 100 character");
        if (details.containsKey("lastName") && details.get("lastName").length() > 100)
            errors.put("Last name must be under 100 character");
        if (LocalDate.parse(details.get("birthDate")).isAfter(LocalDate.now()))
            errors.put("Date of birth must be before current date");

        if (errors.length() > 0)
            return new ResponseEntity<>(errors.toString(), HttpStatus.valueOf(400));

        Person person = new Person();
        person.setFirstName(details.get("firstName"));
        person.setLastName(details.get("lastName"));
        person.setBirthDate(LocalDate.parse(details.get("birthDate")));
        peopleRepository.save(person);
        return new ResponseEntity<>(null, HttpStatus.valueOf(200));
    }

    @PutMapping("/people/{id}")
    public ResponseEntity<String> updatePerson(@RequestHeader Map<String, String> headers, @PathVariable("id") long id, @RequestBody Map<String, String> updates) {
        if (!headers.containsKey("authorization") || !sessionRepository.findById(headers.get("authorization")).isPresent())
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        Optional<Person> person = peopleRepository.findById(id);
        if (!person.isPresent())
            return new ResponseEntity<>(null, HttpStatus.valueOf(404));

        JSONArray errors = new JSONArray();

        if (updates.containsKey("firstName")) {
            if (updates.get("firstName").length() > 100)
                errors.put("First name must be less than 100 characters");
            person.get().setFirstName(updates.get("firstName"));
            updates.remove("firstName");
        }
        if (updates.containsKey("lastName")) {
            if (updates.get("lastName").length() > 100)
                errors.put("Last name must be less than 100 characters");
            person.get().setLastName(updates.get("lastName"));
            updates.remove("lastName");
        }
        if (updates.containsKey("birthDate")) {
            if(LocalDate.parse(updates.get("birthDate")).isAfter(LocalDate.now()))
                errors.put("Date of birth must be before current date");
            person.get().setBirthDate(LocalDate.parse(updates.get("birthDate")));
            updates.remove("birthDate");
        }

        if(updates.size() > 0) {
            for(String key: updates.keySet())
                errors.put("Unable to identify key: " + key);
        }

        if (errors.length() > 0)
            return new ResponseEntity<>(errors.toString(), HttpStatus.valueOf(400));

        peopleRepository.save(person.get());
        return new ResponseEntity<>(null, HttpStatus.valueOf(200));
    }

    @DeleteMapping("/people/{id}")
    public ResponseEntity<String> deletePerson(@RequestHeader Map<String, String> headers, @PathVariable("id") long id) {
        if (!headers.containsKey("authorization") || !sessionRepository.findById(headers.get("authorization")).isPresent())
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        Optional<Person> person = peopleRepository.findById(id);
        if (!person.isPresent())
            return new ResponseEntity<>(null, HttpStatus.valueOf(404));

        peopleRepository.delete(person.get());
        return new ResponseEntity<>(null, HttpStatus.valueOf(200));
    }

    @GetMapping("/people/{id}")
    public ResponseEntity<Person> fetchPerson(@RequestHeader Map<String, String> headers, @PathVariable("id") long id) {
        if (!headers.containsKey("authorization") || !sessionRepository.findById(headers.get("authorization")).isPresent())
            return new ResponseEntity<>(null, HttpStatus.valueOf(401));
        Optional<Person> person = peopleRepository.findById(id);
        if (!person.isPresent())
            return new ResponseEntity<>(null, HttpStatus.valueOf(404));
        return new ResponseEntity<>(person.get(), HttpStatus.valueOf(200));
    }
}
