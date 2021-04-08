package com.study.jrest.domain;

import lombok.Data;

import javax.ws.rs.FormParam;

/**
 * @author jiayq
 * @Date 2021-04-01
 */
@Data
public class Student {
    @FormParam("id")
    private Long id;
    @FormParam("name")
    private String name;
    @FormParam("age")
    private Integer age;
}
