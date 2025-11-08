package org.travelmate.controller.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.travelmate.model.Trip;
import org.travelmate.service.DestinationCategoryService;
import org.travelmate.service.TripService;

import java.util.UUID;

@Path("/categories/{categoryId}/trips")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class TripApi {

    @Inject
    private TripService tripService;

    @Inject
    private DestinationCategoryService categoryService;

    @GET
    public Response getAll(@PathParam("categoryId") UUID categoryId) {
        if (categoryService.find(categoryId).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(tripService.findByCategoryId(categoryId)).build();
    }

    @GET
    @Path("/{tripId}")
    public Response getOne(@PathParam("categoryId") UUID categoryId,
                           @PathParam("tripId") UUID tripId) {

        return tripService.find(tripId)
                .filter(t -> t.getCategory() != null && t.getCategory().getId().equals(categoryId))
                .map(t -> Response.ok(t).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response create(@PathParam("categoryId") UUID categoryId, Trip trip) {
        return categoryService.find(categoryId)
                .map(category -> {
                    trip.setId(UUID.randomUUID());
                    trip.setCategory(category);
                    tripService.create(trip);
                    return Response.status(Response.Status.CREATED).entity(trip).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT
    @Path("/{tripId}")
    public Response update(@PathParam("categoryId") UUID categoryId,
                           @PathParam("tripId") UUID tripId,
                           Trip updated) {

        return tripService.find(tripId)
                .filter(t -> t.getCategory() != null && t.getCategory().getId().equals(categoryId))
                .flatMap(existing -> categoryService.find(categoryId)
                        .map(category -> {
                            updated.setId(tripId);
                            updated.setCategory(category);
                            tripService.update(updated);
                            return Response.ok(updated).build();
                        }))
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{tripId}")
    public Response delete(@PathParam("categoryId") UUID categoryId,
                           @PathParam("tripId") UUID tripId) {

        return tripService.find(tripId)
                .filter(t -> t.getCategory() != null && t.getCategory().getId().equals(categoryId))
                .map(existing -> {
                    tripService.delete(existing.getId());
                    return Response.noContent().build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}
