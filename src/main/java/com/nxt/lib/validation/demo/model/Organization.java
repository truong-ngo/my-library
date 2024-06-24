package com.nxt.lib.validation.demo.model;

import lombok.Data;

import java.util.List;

@Data
public class Organization {
    private String orgName;
    private String orgCode;
    private String taxCode;
    private String address;
    private String phoneNumber;
    List<Department> departments;
}
