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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    private final RestTemplate rest;
    private final EmployeeRepository repository;

    private static final String API = "http://localhost:8080/person/";
    private static final String API_EMP = "http://localhost:8080/employee/";

    private static final String API_ID = "http://localhost:8080/person/{id}";
    private static final String API_EMP_ID = "http://localhost:8080/employee/{id}";



    public EmployeeController(RestTemplate rest, EmployeeRepository repository) {
        this.rest = rest;
        this.repository = repository;
    }

//curl -i http://localhost:8080/employee/
    @RequestMapping(value = "/")
    public Collection<Employee> findAll() {
        Map<Integer, Employee> employeeMap = StreamSupport.stream(
                repository.findAll().spliterator(), false
        ).collect(Collectors.toMap(Employee::getId, empl2 -> empl2));
        List<Person> persons = rest.exchange(
                API,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Person>>() { }
        ).getBody();
        for (Person item: Objects.requireNonNull(persons)) {
            employeeMap.get(item.getEmployeeId()).getAccounts().add(item);
        }
        return employeeMap.values();
    }

    //curl -H 'Content-Type: application/json' -X POST -d '{"name":"Roman","surname":"Vokhmin","inn":"555","hired":"2020-10-05T18:29:40.177+00:00"}' http://localhost:8080/employee/
    @PostMapping("/")
    public ResponseEntity<Employee> createEmp(@RequestBody Employee employee) {
        return new ResponseEntity<Employee>(
                this.repository.save(employee),
                HttpStatus.CREATED);
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
