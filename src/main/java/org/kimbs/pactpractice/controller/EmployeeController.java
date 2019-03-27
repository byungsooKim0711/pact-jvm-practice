package org.kimbs.pactpractice.controller;

import java.util.List;
import java.util.Optional;

import org.kimbs.pactpractice.domain.Employee;
import org.kimbs.pactpractice.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping(value = "/emp")
    public ResponseEntity<List<Employee>> list() {
        final List<Employee> employees = employeeService.findAll();

        if (employees.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @GetMapping(value = "/emp/{id}")
    public ResponseEntity<Employee> selectOne(@PathVariable Long id) {
        final Optional<Employee> selected = employeeService.findById(id);
        
        if (!selected.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(selected.get(), HttpStatus.OK);
    }

    @PostMapping(value = "/emp")
    public ResponseEntity<Employee> create(@RequestBody final Employee employee, final UriComponentsBuilder uriBuilder) {
        Employee created = employeeService.create(employee);
        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriBuilder.path("/api/emp/{id}").buildAndExpand(created.getId()).toUri());

        return new ResponseEntity<>(created, headers, HttpStatus.CREATED);
    } 

    @PutMapping(value = "/emp/{id}")
	public ResponseEntity<Employee> update(@PathVariable Long id, @RequestBody final Employee employee) {
		Optional<Employee> updated = employeeService.findById(id);
		if (!updated.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
		employee.setId(updated.get().getId());
        updated = Optional.of(employeeService.create(employee));
        
		return new ResponseEntity<>(updated.get(), HttpStatus.OK);
    }
    
    @DeleteMapping(value = "/emp/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		Optional<Employee> deleted = employeeService.findById(id);
		if (!deleted.isPresent()) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
        employeeService.deleteById(id);
        
        return new ResponseEntity<>(HttpStatus.OK);
	}
}