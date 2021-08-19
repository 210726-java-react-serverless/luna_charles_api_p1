package com.revature.registration.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.revature.registration.models.Student;
import com.revature.registration.services.UserServices;
import com.revature.registration.util.exceptions.DataSourceException;
import com.revature.registration.util.exceptions.InvalidInformationException;
import com.revature.registration.web.dtos.ErrorResponse;
import com.revature.registration.web.dtos.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class StudentServlet extends HttpServlet {

    private final UserServices userServices;
    private final ObjectMapper objectMapper;

    public StudentServlet(UserServices userServices, ObjectMapper objectMapper) {
        this.userServices = userServices;
        this.objectMapper = objectMapper;
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(req.getMethod() + " received from client.");
        resp.getWriter().write("<h1>Student Page</h1>");
    }

    //For student registration
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        PrintWriter printWriter = resp.getWriter();
        resp.setContentType("application/json");

        try {
            Student newStudent = objectMapper.readValue(req.getInputStream(), Student.class);
            Principal principal = new Principal(userServices.registerStudent(newStudent));
            String payload = objectMapper.writeValueAsString(principal);
            printWriter.write(payload);
            resp.setStatus(201);
        } catch (InvalidInformationException | MismatchedInputException e) {
            resp.setStatus(400);
            ErrorResponse errorResponse = new ErrorResponse(400, e.getMessage());
            printWriter.write(objectMapper.writeValueAsString(errorResponse));
        } catch (DataSourceException dre) {
            resp.setStatus(409);
            ErrorResponse errorResponse = new ErrorResponse(409, dre.getMessage());
            printWriter.write(objectMapper.writeValueAsString(errorResponse));
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
        }
    }
}
