package com.study.jrest.controller;

import com.study.jrest.domain.Student;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;

/**
 * @author jiayq
 * @Date 2021-03-27
 */
@Path("path")
public class PathController {

    @GET
    public String defaultGetMethod() {
        return "default method for get path";
    }

    @POST
    public String defaultPostMethod() {
        return "default method for post path";
    }

    @PUT
    public String defaultPutMethod() {
        return "default method for put path";
    }

    @DELETE
    public String defaultDeleteMethod() {
        return "default method for delete path";
    }

    @GET
    @Path("hello/{param}")
    public String getHello(@PathParam("param") @NotNull Integer param) {
        if(param >= 0) {
            return "> 0";
        }
        return "hello method for get path + sadw";
    }

    @POST
    @Path("hello")
    public String postHello() {
        return "hello method for post path";
    }

    @PUT
    @Path("hello")
    public String putHello() {
        return "hello method for put path";
    }

    @DELETE
    @Path("hello")
    public String deleteHello() {
        return "hello method for delete path";
    }

}
