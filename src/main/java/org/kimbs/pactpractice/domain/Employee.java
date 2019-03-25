package org.kimbs.pactpractice.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Entity
@Data
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(max = 10)
    @Column(name = "name")
    private String empName;

    @Length(max = 9)
    @Column(name = "job")
    private String job;

    @Column(name = "salary")
    private int salary;
}