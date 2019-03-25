package org.kimbs.pactpractice.service;

import java.util.List;
import java.util.Optional;

import org.kimbs.pactpractice.domain.Employee;
import org.kimbs.pactpractice.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee create(Employee employee) {
        Employee created = employeeRepository.saveAndFlush(employee);
        return created;
    }

    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public void deleteById(Long id) {
        employeeRepository.deleteById(id);;
    }
}