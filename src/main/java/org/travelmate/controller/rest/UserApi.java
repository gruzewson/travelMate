package org.travelmate.controller.rest;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.travelmate.controller.rest.dto.UserRegistrationRequestDTO;
import org.travelmate.model.User;
import org.travelmate.model.enums.UserRole;
import org.travelmate.service.TripService;
import org.travelmate.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserApi {

    @Inject
    private UserService userService;
    @Inject
    private TripService tripService;
    @Inject
    private Pbkdf2PasswordHash passwordHash;

    @Context
    private SecurityContext securityContext;

    @GET
    @RolesAllowed("ADMIN")
    public Response getAll() {
        return Response.ok(userService.findAll()).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "USER"})
    public Response getOne(@PathParam("id") UUID id) {
        return userService.find(id)
                .map(user -> {
                    // Check if user has permission to view this user
                    if (!securityContext.isUserInRole("ADMIN")) {
                        String login = securityContext.getUserPrincipal().getName();
                        User currentUser = userService.findByLogin(login)
                                .orElseThrow(() -> new ForbiddenException("User not found"));

                        if (!user.getId().equals(currentUser.getId())) {
                            throw new ForbiddenException("You can only view your own profile");
                        }
                    }
                    return Response.ok(user).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/{id}/trips")
    @RolesAllowed({"ADMIN", "USER"})
    public Response getUserTrips(@PathParam("id") UUID id) {
        if (userService.find(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Check if user has permission to view trips for this user
        if (!securityContext.isUserInRole("ADMIN")) {
            String login = securityContext.getUserPrincipal().getName();
            User currentUser = userService.findByLogin(login)
                    .orElseThrow(() -> new ForbiddenException("User not found"));

            if (!id.equals(currentUser.getId())) {
                throw new ForbiddenException("You can only view your own trips");
            }
        }

        return Response.ok(tripService.findByUserId(id)).build();
    }

    @POST
    @Path("/register")
    @PermitAll
    public Response register(UserRegistrationRequestDTO request) {
        if (request.getLogin() == null || request.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Login and password are required")
                    .build();
        }

        if (userService.findByLogin(request.getLogin()).isPresent()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("User with this login already exists")
                    .build();
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("Pbkdf2PasswordHash.Iterations", "210000");
        parameters.put("Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA256");
        parameters.put("Pbkdf2PasswordHash.SaltSizeBytes", "32");
        passwordHash.initialize(parameters);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setLogin(request.getLogin());
        user.setPassword(passwordHash.generate(request.getPassword().toCharArray()));
        user.setRole(UserRole.USER);
        user.setDateOfBirth(request.getDateOfBirth());

        userService.create(user);

        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @POST
    @RolesAllowed("ADMIN")
    public Response create(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        userService.create(user);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response update(@PathParam("id") UUID id, User updated) {
        return userService.find(id)
                .map(existing -> {
                    existing.setLogin(updated.getLogin());
                    existing.setDateOfBirth(updated.getDateOfBirth());

                    if (updated.getPassword() != null && !updated.getPassword().isEmpty()) {
                        existing.setPassword(updated.getPassword());
                    }
                    if (updated.getRole() != null) {
                        existing.setRole(updated.getRole());
                    }

                    userService.update(existing);
                    return Response.ok(existing).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response delete(@PathParam("id") UUID id) {
        if (userService.find(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        userService.delete(id);
        return Response.noContent().build();
    }
}
