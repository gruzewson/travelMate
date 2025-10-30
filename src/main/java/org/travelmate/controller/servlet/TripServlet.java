package org.travelmate.controller.servlet;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.travelmate.model.Trip;
import org.travelmate.service.TripService;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/api/trips/*")
public class TripServlet extends HttpServlet {

    @Inject
    private TripService tripService;
    private final Jsonb jsonb = JsonbBuilder.create();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            String jsonTrips = jsonb.toJson(tripService.findAll());
            resp.getWriter().write(jsonTrips);
        }
        else{
            try{
                String idStr = pathInfo.substring(1);
                UUID tripId = java.util.UUID.fromString(idStr);
                var tripOpt = tripService.find(tripId);
                if(tripOpt.isPresent()){
                    String jsonTrip = jsonb.toJson(tripOpt.get());
                    resp.getWriter().write(jsonTrip);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("trip not found");
                }
            } catch (IllegalArgumentException e){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Invalid UUID format");
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Trip trip;
        try {
            trip = jsonb.fromJson(req.getReader(), Trip.class);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid JSON");
            return;
        }

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // create new trip
            trip.setId(UUID.randomUUID());
            tripService.create(trip);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(jsonb.toJson(trip));

        } else {
            // update existing trip
            try {
                UUID tripIdFromPath = UUID.fromString(pathInfo.substring(1));

                if (tripService.find(tripIdFromPath).isPresent()) {
                    trip.setId(tripIdFromPath);
                    tripService.update(trip);

                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(jsonb.toJson(trip));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("trip not found");
                }
            } catch (IllegalArgumentException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Invalid trip ID format");
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("trip ID must be provided in the URL path.");
            return;
        }

        try {
            UUID tripId = UUID.fromString(pathInfo.substring(1));

            var tripOpt = tripService.find(tripId);
            if(tripOpt.isPresent()){
                String jsonTrip = jsonb.toJson(tripOpt.get());
                resp.getWriter().write(jsonTrip);
                tripService.delete(tripOpt.get());
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("trip not found");
            }
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
