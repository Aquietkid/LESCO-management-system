package org.example.server.controller;

import org.example.commons.model.Customer;
import org.example.commons.model.Employee;
import org.json.JSONException;
import org.json.JSONObject;

import javax.naming.AuthenticationException;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {
    /**
     * Operations: Create, Read, Update, Delete
     * Subject: customer, bill, tariff, password, CNICCustomer, billReports, loginStatus
     */
    private static final Map<String, List<String>> VALID_COMBINATIONS = Map.of(
            "read", Arrays.asList("customer", "bill", "tariff", "password", "CNICCustomers", "billReports", "loginStatus"),
            "update", Arrays.asList("customer", "bill", "tariff", "password"),
            "delete", Arrays.asList("customer", "bill"),
            "create", Arrays.asList("customer", "bill")
    );

    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    private static final String REQUEST_DELIMITER = "\r\n\r\n";

    @Override
    public void run() {
        try (InputStream input = clientSocket.getInputStream();
             OutputStream output = clientSocket.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input));
             PrintWriter writer = new PrintWriter(output, true)) {

            StringBuilder requestBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("read: " + line);
                if(line.isEmpty()) break;
                requestBuilder.append(line).append("\n");
            }


            String requestString = requestBuilder.toString();
            System.out.println("Request: " + requestString);


            JSONObject request = new JSONObject(requestString);
            JSONObject response = processRequest(request);

            System.out.println("Response: " + response.toString());

            writer.println(response.toString(4));
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
        try {
            String operation = request.optString("operation", "");
            String subject = request.optString("subject", "");
            String username = request.optString("username", "");
            String password = request.optString("password", "");
            JSONObject parameters = request.optJSONObject("parameters");

            if (!VALID_COMBINATIONS.containsKey(operation)) {
                return createErrorResponse("Invalid operation: " + operation);
            }
            if (!VALID_COMBINATIONS.get(operation).contains(subject)) {
                return createErrorResponse("Invalid subject for operation '" + operation + "': " + subject);
            }

            System.out.println(request);

            return switch (operation) {
                case "read" -> handleRead(subject, parameters, username, password);
                case "create" -> handleCreate(subject, parameters, username, password);
                case "update" -> handleUpdate(subject, parameters, username, password);
                case "delete" -> handleDelete(subject, parameters, username, password);
                default -> createErrorResponse("Unknown operation: " + operation);
            };

        } catch (Exception e) {
            return createErrorResponse("An error occurred: " + e.getMessage());
        }
    }

    private JSONObject handleRead(String subject, JSONObject parameters, String username, String password) {
        try {
            switch (subject) {
                case "customer" -> {
                    return createSuccessResponse("Customer data read successfully.");
                }
                case "bill" -> {
                    return createSuccessResponse("Bill data read successfully.");
                }
                case "CNICCustomers" -> {
                    authenticateEmployee(username, password);
                    EmployeeMenu employeeMenu = new EmployeeMenu(new Employee(username, password));
                    String resultBody = employeeMenu.viewCNICCustomers();
                    return createSuccessResponse(resultBody);
                }
                case "billReports" -> {
                    authenticateEmployee(username, password);
                    EmployeeMenu employeeMenu = new EmployeeMenu(new Employee(username, password));
                    String resultBody = employeeMenu.viewBillReports();
                    return createSuccessResponse(resultBody);
                }
                case "estimateUpcomingBill" -> {
                    authenticateUser(username, password);
                    CustomerMenu customerMenu = new CustomerMenu(EmployeeMenu.getCustomerFromID(username));
                    return createSuccessResponse("Bill estimated!");
                }
                case "loginStatus" -> {
                    UserWrapper userWrapper = new UserWrapper();
                    int loginStatus = userWrapper.getLoginStatus(username, password);
                    return createSuccessResponse(String.valueOf(loginStatus));
                }
                default -> {
                    return createErrorResponse("Invalid subject for read operation: " + subject);
                }
            }
        } catch (AuthenticationException e) {
            return createErrorResponse("Authentication failed: " + e.getMessage(), 401);
        } catch (Exception e) {
            return createErrorResponse("An error occurred: " + e.getMessage());
        }
    }

    private JSONObject handleCreate(String subject, JSONObject parameters, String username, String password) {
        try {
            authenticateEmployee(username, password);

            if ("customer".equals(subject)) {
                Customer newCustomer = createCustomerFromParameters(parameters);
                EmployeeMenu employeeMenu = new EmployeeMenu(new Employee(username, password));
                employeeMenu.addCustomer(newCustomer);
                return createSuccessResponse("Customer created successfully.");
            } else if ("bill".equals(subject)) {
                return createSuccessResponse("Bill created successfully.");
            } else {
                return createErrorResponse("Invalid subject for create operation: " + subject);
            }
        } catch (AuthenticationException e) {
            return createErrorResponse("Authentication failed: " + e.getMessage(), 401);
        } catch (Exception e) {
            return createErrorResponse("Invalid data or missing fields: " + e.getMessage());
        }
    }

    private JSONObject handleUpdate(String subject, JSONObject parameters, String username, String password) {
        try {
            authenticateEmployee(username, password);
            switch (subject) {
                case "CNICExpiry" -> {
                    if (parameters == null || !parameters.has("customerId") || !parameters.has("newCNICExpiryDate")) {
                        return createErrorResponse("Missing parameters for updating CNIC expiry date.");
                    }

                    String customerId = parameters.getString("customerId");
                    String newCNICExpiryDate = parameters.getString("newCNICExpiryDate");

                    CustomerMenu customerMenu = new CustomerMenu(EmployeeMenu.getCustomerFromID(customerId));

                    boolean updateResult = customerMenu.updateCNICExpiry(newCNICExpiryDate);
                    if (Boolean.TRUE.equals(updateResult)) {
                        return createSuccessResponse("CNIC expiry updated successfully.");
                    } else {
                        return createErrorResponse("Failed to update CNIC expiry.");
                    }
                }
                default -> throw new IllegalStateException("Unexpected value: " + subject);
            }
        } catch (AuthenticationException e) {
            return createErrorResponse("Authentication failed: " + e.getMessage(), 401);
        } catch (Exception e) {
            return createErrorResponse("An error occurred: " + e.getMessage());
        }
    }

    private JSONObject handleDelete(String subject, JSONObject parameters, String username, String password) {
        try {
            authenticateEmployee(username, password);
            return createSuccessResponse(subject + " deleted successfully.");
        } catch (AuthenticationException e) {
            return createErrorResponse("Authentication failed: " + e.getMessage(), 401);
        } catch (Exception e) {
            return createErrorResponse("An error occurred: " + e.getMessage());
        }
    }

    private void authenticateEmployee(String username, String password) throws AuthenticationException {
        LoginMenu loginMenu = new LoginMenu();
        if (loginMenu.login(username, password) != LoginMenu.EMPLOYEE_ID) {
            throw new AuthenticationException("Unauthorized User!");
        }
    }

    private void authenticateUser(String username, String password) throws AuthenticationException {
        LoginMenu loginMenu = new LoginMenu();
        if (loginMenu.login(username, password) != LoginMenu.CUSTOMER_ID) {
            throw new AuthenticationException("Unauthorized User!");
        }
    }

    private Customer createCustomerFromParameters(JSONObject parameters) {
        return new Customer(
                parameters.getString("customerID"),
                parameters.getString("cnic"),
                parameters.getString("name"),
                parameters.getString("address"),
                parameters.getString("phone"),
                parameters.getBoolean("isCommercial"),
                parameters.getBoolean("isThreePhase"),
                parameters.getString("connectionDate")
        );
    }

    private JSONObject createSuccessResponse(String message) {
        JSONObject response = new JSONObject();
        response.put("status", "success");
        response.put("returnCode", 200);
        response.put("resultBody", message);
        return response;
    }

    private JSONObject createErrorResponse(String message, int returnCode) {
        JSONObject response = new JSONObject();
        response.put("status", "error");
        response.put("returnCode", returnCode);
        response.put("resultBody", message);
        return response;
    }

    private JSONObject createErrorResponse(String message) {
        return createErrorResponse(message, 500);
    }
}
