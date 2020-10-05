package ru.job4j.auth.controller;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.job4j.auth.domain.Employee;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    private final RestTemplate rest;
    private final EmployeeRepository repository;

    private static final String API = "http://localhost:8080/person/";
    private static final String API_ID = "http://localhost:8080/person/{id}";


    public EmployeeController(RestTemplate rest, EmployeeRepository repository) {
        this.rest = rest;
        this.repository = repository;
    }

    @GetMapping
    public Collection<Employee> findAll() {
        Map<Integer, Employee> rsl = StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .collect(Collectors.toMap(Employee::getId, employee -> employee));

        List<Person> persons = rest.exchange(
                API, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<Person>>() {
                }).getBody();
        for (Person person : persons) {
            rsl.get(person.getEmployeeId()).getAccounts().add(person);
        }
        return rsl.values();
    }

    @PostMapping
    public ResponseEntity<Person> create(@RequestBody Person person) {
        person.setEmployeeId(1);
        Person rsl = rest.postForObject(API, person, Person.class);
        return new ResponseEntity<>(rsl, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody Person person) {
        person.setEmployeeId(1);
        rest.put(API, person);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        rest.delete(API_ID, id);
        return ResponseEntity.ok().build();
    }
}
