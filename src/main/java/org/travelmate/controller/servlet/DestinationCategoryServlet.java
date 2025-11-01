package org.travelmate.controller.servlet;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.travelmate.model.DestinationCategory;
import org.travelmate.service.DestinationCategoryService;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/legacy/categories/*")
public class DestinationCategoryServlet extends HttpServlet {

    @Inject
    private DestinationCategoryService categoryService;

    private final Jsonb jsonb = JsonbBuilder.create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String path = req.getPathInfo();

        // GET /api/categories
        if (path == null || path.equals("/")) {
            resp.getWriter().write(jsonb.toJson(categoryService.findAll()));
            return;
        }

        // GET /api/categories/{id}
        try {
            UUID id = UUID.fromString(path.substring(1));
            var categoryOpt = categoryService.find(id);

            if (categoryOpt.isPresent()) {
                resp.getWriter().write(jsonb.toJson(categoryOpt.get()));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("Category not found");
            }
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid category ID");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // CREATE category
        DestinationCategory category;
        try {
            category = jsonb.fromJson(req.getReader(), DestinationCategory.class);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid JSON");
            return;
        }

        category.setId(UUID.randomUUID());
        categoryService.create(category);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write(jsonb.toJson(category));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // UPDATE category
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Category ID required");
            return;
        }

        UUID id;
        try {
            id = UUID.fromString(path.substring(1));
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid Category ID");
            return;
        }

        if (categoryService.find(id).isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("Category not found");
            return;
        }

        DestinationCategory category;
        try {
            category = jsonb.fromJson(req.getReader(), DestinationCategory.class);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid JSON");
            return;
        }

        category.setId(id);
        categoryService.update(category);

        resp.getWriter().write(jsonb.toJson(category));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();

        if (path == null || path.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Category ID required");
            return;
        }

        try {
            UUID id = UUID.fromString(path.substring(1));

            if (categoryService.find(id).isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("Category not found");
                return;
            }

            categoryService.delete(id);

            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid Category ID");
        }
    }
}
