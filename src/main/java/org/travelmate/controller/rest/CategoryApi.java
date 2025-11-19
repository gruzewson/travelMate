package org.travelmate.controller.rest;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
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
public class CategoryApi {

    @Inject
    private DestinationCategoryService categoryService;

    @GET
    @RolesAllowed({"ADMIN", "USER"})
    public Response getAll() {
        return Response.ok(categoryService.findAll()).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "USER"})
    public Response getOne(@PathParam("id") UUID id) {
        return categoryService.find(id)
                .map(c -> Response.ok(c).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @RolesAllowed("ADMIN")
    public Response create(DestinationCategory category) {
        category.setId(UUID.randomUUID());
        categoryService.create(category);
        return Response.status(Response.Status.CREATED).entity(category).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response update(@PathParam("id") UUID id, DestinationCategory updated) {
        return categoryService.find(id)
                .map(existing -> {
                    existing.setName(updated.getName());
                    existing.setDescription(updated.getDescription());
                    categoryService.update(existing);
                    return Response.ok(existing).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response delete(@PathParam("id") UUID id) {
        if (categoryService.find(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        categoryService.delete(id);
        return Response.noContent().build();
    }
}
