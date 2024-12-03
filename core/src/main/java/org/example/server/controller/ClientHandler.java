package org.example.server.controller;

import org.example.commons.model.Employee;
import org.json.JSONObject;

import javax.naming.AuthenticationException;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {
    private static final Map<String, List<String>> VALID_COMBINATIONS = Map.of("read", Arrays.asList("customer", "bill", "tariff", "password", "CNICCustomers"), "update", Arrays.asList("customer", "bill", "tariff", "password"), "delete", Arrays.asList("customer", "bill"), "create", Arrays.asList("customer", "bill"));

    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (InputStream input = clientSocket.getInputStream(); OutputStream output = clientSocket.getOutputStream(); BufferedReader reader = new BufferedReader(new InputStreamReader(input)); PrintWriter writer = new PrintWriter(output, true)) {
            StringBuilder requestBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if ("END".equals(line)) {
                    break; // End of request
                }
                requestBuilder.append(line);
            }

            System.out.println(requestBuilder.toString());

            JSONObject request = new JSONObject(requestBuilder.toString());
            JSONObject response = processRequest(request);
            writer.println(response.toString(4)); // Send response to the client

        } catch (IOException e) {
            System.err.println("Error handling client request: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private JSONObject processRequest(JSONObject request) {
        JSONObject response = null;

        try {
            // Extract fields from request
            String operation = request.optString("operation", "");
            String subject = request.optString("subject", "");
            String username = request.optString("username", "");
            String password = request.optString("password", "");
            JSONObject parameters = request.optJSONObject("parameters");

            // Validate operation and subject
            if (!VALID_COMBINATIONS.containsKey(operation)) {
                return createErrorResponse("Invalid operation: " + operation);
            }
            if (!VALID_COMBINATIONS.get(operation).contains(subject)) {
                return createErrorResponse("Invalid subject for operation '" + operation + "': " + subject);
            }

            // Process the request based on operation and subject
            switch (operation) {
                case "read":
                    response = handleRead(subject, parameters, username, password);
                    break;
                case "create":
                    response = handleCreate(subject, parameters, username, password);
                    break;
                case "update":
                    response = handleUpdate(subject, parameters, username, password);
                    break;
                case "delete":
                    response = handleDelete(subject, parameters, username, password);
                    break;
                default:
                    return createErrorResponse("Unknown operation: " + operation);
            }

        } catch (Exception e) {
            response = createErrorResponse("An error occurred: " + e.getMessage());
        }

        return response;
    }

    private JSONObject handleRead(String subject, JSONObject parameters, String username, String password) {
        JSONObject response = new JSONObject();

        try {

            // Authenticate employee
            authenticateEmployee(username, password);

            // Check if subject is valid
            // TODO: add all operations to check for validity
            if (!"CNICCustomers".equals(subject)) {
                response.put("status", "error");
                response.put("returnCode", 400); // Bad Request
                response.put("resultBody", "Invalid subject for read operation: " + subject);
                return response;
            }

            // Initialize EmployeeMenu and perform read operation
            EmployeeMenu employeeMenu = new EmployeeMenu(new Employee(username, password));
            String resultBody = employeeMenu.viewCNICCustomers();

            // Success response
            prepareSuccessResponse(response, resultBody);

        } catch (AuthenticationException e) {
            // Handle authentication failure
            response.put("status", "error");
            response.put("returnCode", 401); // Unauthorized
            response.put("resultBody", "Authentication failed: " + e.getMessage());

        } catch (Exception e) {
            // Handle other exceptions
            response.put("status", "error");
            response.put("returnCode", 500); // Internal Server Error
            response.put("resultBody", "An error occurred: " + e.getMessage());
        }

        return response;
    }

    private static void prepareSuccessResponse(JSONObject response, String resultBody) {
        response.put("status", "success");
        response.put("returnCode", 200); // OK
        response.put("resultBody", resultBody);
    }

    private void authenticateEmployee(String username, String password) throws AuthenticationException {
        LoginMenu loginMenu = new LoginMenu();
        if (loginMenu.login(username, password) != LoginMenu.EMPLOYEE_ID) {
            throw new AuthenticationException("Unauthorized User!");
        }
    }

    private void authenticateCustomer(String username, String password) throws AuthenticationException {
        LoginMenu loginMenu = new LoginMenu();
        if (loginMenu.login(username, password) != LoginMenu.CUSTOMER_ID) {
            throw new AuthenticationException("Unauthorized User!");
        }
    }

    private JSONObject handleCreate(String subject, JSONObject parameters, String username, String password) {
        JSONObject response = new JSONObject();
        response.put("status", "success");
        response.put("message", subject + " created successfully.");
        return response;
    }

    private JSONObject handleUpdate(String subject, JSONObject parameters, String username, String password) {
        JSONObject response = new JSONObject();
        response.put("status", "success");
        response.put("message", subject + " updated successfully.");
        return response;
    }

    private JSONObject handleDelete(String subject, JSONObject parameters, String username, String password) {
        JSONObject response = new JSONObject();
        response.put("status", "success");
        response.put("message", subject + " deleted successfully.");
        return response;
    }

    private JSONObject createErrorResponse(String message) {
        JSONObject response = new JSONObject();
        response.put("status", "error");
        response.put("message", message);
        return response;
    }
}
