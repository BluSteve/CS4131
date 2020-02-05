package com.example.lab3.data;

import com.example.lab3.MainActivity;
import com.example.lab3.User;
import com.example.lab3.data.model.LoggedInUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    String oriUsername;
    public Result<LoggedInUser> login(String username, String password) {

        try {
            oriUsername = username;
            username = username.toLowerCase();
            File file = new File(MainActivity.getAppContext().getExternalFilesDir(null),
                    "login.txt");
            file.createNewFile();

//            PrintWriter out1 = new PrintWriter(new FileWriter(file));
//            out1.write("");
//            out1.flush();

            PrintWriter out = new PrintWriter(new FileWriter(file,true));
            InputStream in = new FileInputStream(file);
            Scanner s = new Scanner(in);

            ArrayList<User> users = new ArrayList<User>();
            while (s.hasNextLine()) {
                String[] nextline = s.nextLine().split(",");
                String tempusername = nextline[0];
                String temppassword = nextline[1];
                users.add(new User(tempusername, temppassword));
            }

            for (User u : users) {
                if (u.isUser(username, password)) {
                    return new Result.Success<>(new LoggedInUser(oriUsername,oriUsername));
                } else if (u.getUsername().equals(username) && !u.getPassword().equals(password)) {
                    return null;
                }
            }

            users.add(new User(username, password));
            out.println(new User(username, password).toString());
            out.close();
            return new Result.Success<>(new LoggedInUser(oriUsername,oriUsername));
        } catch (Exception e) {
            e.printStackTrace();
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
