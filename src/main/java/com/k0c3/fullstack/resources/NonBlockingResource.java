package com.k0c3.fullstack.resources;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("api/non-blocking-resource")
public class NonBlockingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> sayHello()
    {
        return Uni.createFrom().item("hello")
                .onItem().transform(x-> x + "k0c3");
    }
}
