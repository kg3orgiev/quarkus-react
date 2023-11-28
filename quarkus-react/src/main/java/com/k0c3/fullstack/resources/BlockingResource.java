package com.k0c3.fullstack.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("api/blocking-resource")
public class BlockingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayHello()
    {
        return "hello k0c3";
    }
}
