package com.example.lab3.data;

import android.renderscript.ScriptGroup;
import android.util.Log;

import com.example.lab3.MainActivity;
import com.example.lab3.MyApplication;
import com.example.lab3.User;
import com.example.lab3.data.model.LoggedInUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            Log.d("login", "asdf3");
            File file = new File(MyApplication.getAppContext().getExternalFilesDir(null),"login.txt");
            Log.d("login", "die " + file.isFile());


            PrintWriter out = new PrintWriter(file);

//            if(!s.hasNextLine()) {
//                out.write("Steve,qwerty\nBill,qweddd\n");
//                out.flush();
//            }
            InputStream in = new FileInputStream(file);
            Scanner s = new Scanner(in);
            ArrayList<User> users = new ArrayList<User>();
            do {
                String[] nextline = s.nextLine().split(",");
                String tempusername = nextline[0];
                String temppassword = nextline[1];
                users.add(new User(tempusername, temppassword));
            } while (s.hasNextLine());

            Log.d("login", users.toString());
            for (User u: users) {
                if (u.isUser(username, password)) {
                    Log.d("login", users.toString()+"dasdfasdf");
                    return new Result.Success<>(new LoggedInUser(username, username));
                }
                else if (u.getUsername().equals(username) && !u.getPassword().equals(password)){
                    Log.d("login", users.toString()+"dasdfasdf");
                    return null;
                }
            }

            users.add(new User(username, password));
//            String towrite = "";
//            for (User u: users) {
//                towrite += u.toString() + "\n";
//            }
            out.append(new User(username, password).toString());
            out.close();
//            Scanner s = new Scanner();

//                    LoggedInUser fakeUser =
//                    new LoggedInUser(
//                            java.util.UUID.randomUUID().toString(),
//                            "Jane Doe");
//            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            Log.d("login", "asdf");
            e.printStackTrace();
            return new Result.Error(new IOException("Error logging in", e));
        }
        return null;
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
