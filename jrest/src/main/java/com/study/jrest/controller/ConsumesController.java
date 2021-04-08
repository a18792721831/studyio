package com.study.jrest.controller;

import com.study.jrest.domain.Student;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author jiayq
 * @Date 2021-04-03
 */
@Path("consumes")
public class ConsumesController {

    @GET
    @Consumes(MediaType.WILDCARD)
    public String consumeGet(@QueryParam("name") String name) {
        return "consume get , " + name;
    }

    @GET
    @Path("{name}")
    public String consumeGetPath(@PathParam("name") String name) {
        return "consume get path , " + name;
    }

    @POST
    public String consumePost(@MatrixParam("name") String name) {
        return "consume post , " + name;
    }

    @POST
    @Path("/post/{name}")
    public String consumePostPath(@PathParam("name") String name) {
        return "consume post path , " + name;
    }

    @Path("stu")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String consumeStu(Student student) {
        return "consume student , " + student;
    }

    @Path("stu/bean")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String consumeStuBean(@BeanParam Student student) {
        return "consume student bean , " + student;
    }

}
