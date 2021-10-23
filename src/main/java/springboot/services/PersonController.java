package springboot.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class PersonController {
    private static final Logger logger = LogManager.getLogger();

    @PostMapping("/login")
    public ResponseEntity<String> login() {
        return new ResponseEntity<>("login", HttpStatus.valueOf(200));
    }

    @GetMapping("/people")
    public ResponseEntity<String> fetchPeople() {
        return new ResponseEntity<>("fetch people", HttpStatus.valueOf(200));
    }

    @PostMapping("/people")
    public ResponseEntity<String> insertPerson() {
        return new ResponseEntity<>("insert person", HttpStatus.valueOf(200));
    }

    @PutMapping("/people/{id}")
    public ResponseEntity<String> updatePerson() {
        return new ResponseEntity<>("update person", HttpStatus.valueOf(200));
    }

    @DeleteMapping("/people/{id}")
    public ResponseEntity<String> deletePerson() {
        return new ResponseEntity<>("delete person", HttpStatus.valueOf(200));
    }

    @GetMapping("/people/{id}")
    public ResponseEntity<String> fetchPerson() {
        return new ResponseEntity<>("fetch person", HttpStatus.valueOf(200));
    }
}
