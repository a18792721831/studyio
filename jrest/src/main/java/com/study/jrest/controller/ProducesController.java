package com.study.jrest.controller;

import com.study.jrest.domain.Student;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

/**
 * @author jiayq
 * @Date 2021-04-03
 */
@Path("produces")
public class ProducesController {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String produceGet(@QueryParam("name") String name) {
        return "produces get query param , " + name;
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    public String producePost(@MatrixParam("name") String name) {
        return "produces post query param , <b>" + name + "</b>";
    }

    @GET
    @Path("get/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> produceGetPath(@PathParam("name") String name) {
        return Map.of("result", "produces get path", "param", name);
    }

    @POST
    @Path("post")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Student producesGetStudent(@FormParam("name") String name) {
        Student student = new Student();
        student.setId(0L);
        student.setName(name);
        student.setAge(0);
        return student;
    }

    @GET
    @Path("harder")
    @Produces(MediaType.APPLICATION_JSON)
    public Student produceHeaderParam(@HeaderParam("name") String name) {
        Student student = new Student();
        student.setId(0L);
        student.setName(name);
        student.setAge(0);
        return student;
    }

    @Path("cookie")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Student produceCookieParam(@CookieParam("name") String name) {
        Student student = new Student();
        student.setId(0L);
        student.setName(name);
        student.setAge(0);
        return student;
    }

}
