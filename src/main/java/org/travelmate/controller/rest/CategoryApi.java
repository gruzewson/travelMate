package org.travelmate.controller.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.travelmate.model.DestinationCategory;
import org.travelmate.service.DestinationCategoryService;

import java.util.UUID;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CategoryApi {

    @Inject
    private DestinationCategoryService categoryService;

    @GET
    public Response getAll() {
        return Response.ok(categoryService.findAll()).build();
    }

    @GET
    @Path("/{id}")
    public Response getOne(@PathParam("id") UUID id) {
        return categoryService.find(id)
                .map(c -> Response.ok(c).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response create(DestinationCategory category) {
        category.setId(UUID.randomUUID());
        categoryService.create(category);
        return Response.status(Response.Status.CREATED).entity(category).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") UUID id, DestinationCategory updated) {
        return categoryService.find(id)
                .map(existing -> {
                    updated.setId(id);
                    categoryService.update(updated);
                    return Response.ok(updated).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        if (categoryService.find(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        categoryService.delete(id);
        return Response.noContent().build();
    }
}
