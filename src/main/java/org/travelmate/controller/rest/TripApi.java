package org.travelmate.controller.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.travelmate.model.Trip;
import org.travelmate.model.User;
import org.travelmate.service.DestinationCategoryService;
import org.travelmate.service.TripService;
import org.travelmate.service.UserService;

import java.util.List;
import java.util.UUID;

@Path("/categories/{categoryId}/trips")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TripApi {

    @Inject
    private TripService tripService;

    @Inject
    private DestinationCategoryService categoryService;

    @Inject
    private UserService userService;

    @Context
    private SecurityContext securityContext;

    @GET
    @RolesAllowed({"ADMIN", "USER"})
    public Response getAll(@PathParam("categoryId") UUID categoryId) {
        if (categoryService.find(categoryId).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<Trip> trips;
        if (securityContext.isUserInRole("ADMIN")) {
            // Admin can see all trips
            trips = tripService.findByCategoryId(categoryId);
        } else {
            // Regular user can see only their own trips
            String login = securityContext.getUserPrincipal().getName();
            User currentUser = userService.findByLogin(login)
                    .orElseThrow(() -> new ForbiddenException("User not found"));
            trips = tripService.findByCategoryIdAndUserId(categoryId, currentUser.getId());
        }

        return Response.ok(trips).build();
    }

    @GET
    @Path("/{tripId}")
    @RolesAllowed({"ADMIN", "USER"})
    public Response getOne(@PathParam("categoryId") UUID categoryId,
                           @PathParam("tripId") UUID tripId) {

        return tripService.find(tripId)
                .filter(t -> t.getCategory() != null && t.getCategory().getId().equals(categoryId))
                .map(trip -> {
                    // Check if user has permission to view this trip
                    if (!securityContext.isUserInRole("ADMIN")) {
                        String login = securityContext.getUserPrincipal().getName();
                        User currentUser = userService.findByLogin(login)
                                .orElseThrow(() -> new ForbiddenException("User not found"));

                        if (trip.getUser() == null || !trip.getUser().getId().equals(currentUser.getId())) {
                            throw new ForbiddenException("You can only view your own trips");
                        }
                    }
                    return Response.ok(trip).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @RolesAllowed({"ADMIN", "USER"})
    public Response create(@PathParam("categoryId") UUID categoryId, Trip trip) {
        return categoryService.find(categoryId)
                .map(category -> {
                    trip.setId(UUID.randomUUID());
                    trip.setCategory(category);

                    // Automatically set owner for regular users
                    if (!securityContext.isUserInRole("ADMIN")) {
                        String login = securityContext.getUserPrincipal().getName();
                        User currentUser = userService.findByLogin(login)
                                .orElseThrow(() -> new ForbiddenException("User not found"));
                        trip.setUser(currentUser);
                    } else {
                        // Admin can specify user in request, or it defaults to admin
                        if (trip.getUser() != null && trip.getUser().getId() != null) {
                            // Verify that the specified user exists
                            User specifiedUser = userService.find(trip.getUser().getId())
                                    .orElseThrow(() -> new BadRequestException("Specified user not found"));
                            trip.setUser(specifiedUser);
                        } else {
                            // If no user specified, set admin as owner
                            String login = securityContext.getUserPrincipal().getName();
                            User currentUser = userService.findByLogin(login)
                                    .orElseThrow(() -> new ForbiddenException("User not found"));
                            trip.setUser(currentUser);
                        }
                    }

                    tripService.create(trip);
                    return Response.status(Response.Status.CREATED).entity(trip).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT
    @Path("/{tripId}")
    @RolesAllowed({"ADMIN", "USER"})
    public Response update(@PathParam("categoryId") UUID categoryId,
                           @PathParam("tripId") UUID tripId,
                           Trip updated) {

        return tripService.find(tripId)
                .filter(t -> t.getCategory() != null && t.getCategory().getId().equals(categoryId))
                .flatMap(existing -> {
                    if (!securityContext.isUserInRole("ADMIN")) {
                        String login = securityContext.getUserPrincipal().getName();
                        User currentUser = userService.findByLogin(login)
                                .orElseThrow(() -> new ForbiddenException("User not found"));

                        if (existing.getUser() == null || !existing.getUser().getId().equals(currentUser.getId())) {
                            throw new ForbiddenException("You can only edit your own trips");
                        }
                    }

                    return categoryService.find(categoryId)
                            .map(category -> {
                                existing.setTitle(updated.getTitle());
                                existing.setStartDate(updated.getStartDate());
                                existing.setEndDate(updated.getEndDate());
                                existing.setEstimatedCost(updated.getEstimatedCost());
                                existing.setStatus(updated.getStatus());
                                existing.setCategory(category);

                                if (securityContext.isUserInRole("ADMIN") && updated.getUser() != null) {
                                    existing.setUser(updated.getUser());
                                }

                                tripService.update(existing);
                                return Response.ok(existing).build();
                            });
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{tripId}")
    @RolesAllowed({"ADMIN", "USER"})
    public Response delete(@PathParam("categoryId") UUID categoryId,
                           @PathParam("tripId") UUID tripId) {

        return tripService.find(tripId)
                .filter(t -> t.getCategory() != null && t.getCategory().getId().equals(categoryId))
                .map(existing -> {
                    // Check ownership for regular users
                    if (!securityContext.isUserInRole("ADMIN")) {
                        String login = securityContext.getUserPrincipal().getName();
                        User currentUser = userService.findByLogin(login)
                                .orElseThrow(() -> new ForbiddenException("User not found"));

                        if (existing.getUser() == null || !existing.getUser().getId().equals(currentUser.getId())) {
                            throw new ForbiddenException("You can only delete your own trips");
                        }
                    }

                    tripService.delete(existing.getId());
                    return Response.noContent().build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}