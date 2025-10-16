package org.travelmate.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.travelmate.repository.AvatarRepository;
import org.travelmate.service.AvatarService;

import java.io.*;
import java.nio.file.*;

@WebServlet("/api/avatars/*")
public class AvatarServlet extends HttpServlet {

    private AvatarService avatarService;

    @Override
    public void init() throws ServletException {
        String avatarDir = getServletContext().getInitParameter("avatars.dir");
        avatarService = new AvatarService(new AvatarRepository(avatarDir));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "No file name provided");
            return;
        }

        String filename = pathInfo.substring(1); // remove leading "/"
        Path avatarPath = avatarService.getAvatar(filename);

        if (avatarPath == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, "Avatar not found");
            return;
        }

        // Set response headers
        res.setContentType(Files.probeContentType(avatarPath));
        res.setContentLengthLong(Files.size(avatarPath));

        try (InputStream in = Files.newInputStream(avatarPath);
             OutputStream out = res.getOutputStream()) {
            in.transferTo(out);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String pathInfo = req.getPathInfo();

        String filename = pathInfo.substring(1);

        try {
            avatarService.deleteAvatar(filename);
            res.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (IOException e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting avatar");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "No file name provided");
            return;
        }

        String filename = pathInfo.substring(1);

        try (InputStream in = req.getInputStream()) {
            avatarService.uploadAvatar(filename, in);
            res.setStatus(HttpServletResponse.SC_CREATED);
        } catch (IOException e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error uploading avatar");
        }
    }

}
