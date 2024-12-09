package org.example.client.controller;

import org.example.client.ServerParams;
import org.example.commons.model.Customer;
import org.example.commons.model.Employee;
import org.example.commons.model.User;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class UserWrapper {
    User myUser;

    public UserWrapper() {
        this.myUser = null;
    }

    public User getMyUser() {
        return myUser;
    }

    public void setMyUser(User myUser) {
        this.myUser = myUser;
    }

    public int getLoginStatus(String username, String password) throws IOException {

        JSONObject request = new JSONObject();
        request.put("username", username);
        request.put("password", password);
        request.put("operation", "read");
        request.put("subject", "loginStatus");
        request.put("parameters", new JSONObject());

        int result = LoginMenu.UNKNOWN_ID;

        try (Socket socket = new Socket(ServerParams.ServerIP, ServerParams.ServerPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println(request);
            out.println(request.toString(4) + "\n");

            StringBuilder responseBuilder = new StringBuilder();
            String responseLine;
            while ((responseLine = in.readLine()) != null && !responseLine.isEmpty()) {
                responseBuilder.append(responseLine);
            }
            JSONObject response = new JSONObject(responseBuilder.toString());

            if ("success".equalsIgnoreCase(response.getString("status")) && response.getInt("returnCode") == 200) {
                return response.getInt("resultBody");
            } else {
                return LoginMenu.UNKNOWN_ID;
            }

        }

    }

}
