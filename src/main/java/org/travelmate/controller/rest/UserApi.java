package org.travelmate.controller.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.travelmate.model.User;
import org.travelmate.service.TripService;
import org.travelmate.service.UserService;

import java.util.UUID;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class UserApi {

    @Inject
    private UserService userService;
    @Inject
    private TripService tripService;


    @GET
    public Response getAll() {
        return Response.ok(userService.findAll()).build();
    }

    @GET
    @Path("/{id}")
    public Response getOne(@PathParam("id") UUID id) {
        return userService.find(id)
                .map(u -> Response.ok(u).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
    @GET
    @Path("/{id}/trips")
    public Response getUserTrips(@PathParam("id") UUID id) {
        if (userService.find(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(tripService.findByUserId(id)).build();
    }


    @POST
    public Response create(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        userService.create(user);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") UUID id, User updated) {
        return userService.find(id)
                .map(existing -> {
                    updated.setId(id);
                    userService.update(updated);
                    return Response.ok(updated).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        if (userService.find(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        userService.delete(id);
        return Response.noContent().build();
    }
}

