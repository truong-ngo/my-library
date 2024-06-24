package com.nxt.lib.validation.demo.model;

import lombok.Data;

import java.util.List;

@Data
public class Department {
    private String name;
    private String description;
    private Integer maxSize;
    private List<Employee> employees;
}
