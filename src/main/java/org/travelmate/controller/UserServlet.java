package org.travelmate.controller;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.travelmate.repository.UserRepository;
import org.travelmate.service.UserService;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {

    private final UserService userService;
    private final Jsonb jsonb = JsonbBuilder.create();

    public UserServlet() {
        this.userService = new UserService(new UserRepository());
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            String jsonUsers = jsonb.toJson(userService.findAll());
            resp.getWriter().write(jsonUsers);
        }
        else{
            try{
                String idStr = pathInfo.substring(1);
                UUID userId = java.util.UUID.fromString(idStr);
                var userOpt = userService.find(userId);
                if(userOpt.isPresent()){
                    String jsonUser = jsonb.toJson(userOpt.get());
                    resp.getWriter().write(jsonUser);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("User not found");
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

        var user = jsonb.fromJson(req.getReader(), org.travelmate.model.User.class);
        if(user.getId() == null){
            user.setId(UUID.randomUUID());
            userService.create(user);
        }
        else {
            userService.update(user);
        }
        String jsonUser = jsonb.toJson(user);
        resp.getWriter().write(jsonUser);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("User ID must be provided in the URL path.");
            return;
        }

        try {
            UUID userId = UUID.fromString(pathInfo.substring(1));

            var userOpt = userService.find(userId);
            if(userOpt.isPresent()){
                String jsonUser = jsonb.toJson(userOpt.get());
                resp.getWriter().write(jsonUser);
                userService.delete(userOpt.get());
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("User not found");
            }
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
