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

@WebServlet("/api/categories/*")
public class DestinationCategoryServlet extends HttpServlet {

    @Inject
    private DestinationCategoryService categoryService;
    private final Jsonb jsonb = JsonbBuilder.create();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            String jsonCategories = jsonb.toJson(categoryService.findAll());
            resp.getWriter().write(jsonCategories);
        }
        else{
            try{
                String idStr = pathInfo.substring(1);
                UUID categoryId = java.util.UUID.fromString(idStr);
                var categoryOpt = categoryService.find(categoryId);
                if(categoryOpt.isPresent()){
                    String jsonCategories = jsonb.toJson(categoryOpt.get());
                    resp.getWriter().write(jsonCategories);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("category not found");
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

        DestinationCategory category;
        try {
            category = jsonb.fromJson(req.getReader(), DestinationCategory.class);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid JSON");
            return;
        }

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // create new category
            category.setId(UUID.randomUUID());
            categoryService.create(category);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(jsonb.toJson(category));

        } else {
            // update existing category
            try {
                UUID categoryIdFromPath = UUID.fromString(pathInfo.substring(1));

                if (categoryService.find(categoryIdFromPath).isPresent()) {
                    category.setId(categoryIdFromPath);
                    categoryService.update(category);

                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(jsonb.toJson(category));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("category not found");
                }
            } catch (IllegalArgumentException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Invalid category ID format");
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("category ID must be provided in the URL path.");
            return;
        }

        try {
            UUID categoryId = UUID.fromString(pathInfo.substring(1));

            var categoryOpt = categoryService.find(categoryId);
            if(categoryOpt.isPresent()){
                String jsonCategories = jsonb.toJson(categoryOpt.get());
                resp.getWriter().write(jsonCategories);
                categoryService.delete(categoryOpt.get());
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("category not found");
            }
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}

