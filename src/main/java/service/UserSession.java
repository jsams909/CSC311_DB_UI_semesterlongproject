package service;

import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

public class UserSession {

    private static volatile UserSession instance;

    private String userName;
    private String password;
    private String privileges;

   /** Helps the program pinpoint what variables it is using**/
    private UserSession(String userName, String password, String privileges) {
        this.userName = userName;
        this.password = password;
        this.privileges = privileges;
        Preferences userPreferences = Preferences.userRoot().node(getClass().getName());
        userPreferences.put("USERNAME", userName);
        userPreferences.put("PASSWORD", password);
        userPreferences.put("PRIVILEGES", privileges);
    }

    /**Checks locks for thread safety**/
    public static UserSession getInstance(String userName, String password, String privileges) {
        if (instance == null) {
            synchronized (UserSession.class) {
                if (instance == null) {
                    instance = new UserSession(userName, password, privileges);
                }
            }
        }
        return instance;
    }

    /**Gets the username and password it requires**/
    public static UserSession getInstance(String userName, String password) {
        return getInstance(userName, password, "NONE");
    }

    /** Saves the user information**/
    public void saveUserSession() {
        Preferences userPreferences = Preferences.userRoot().node(getClass().getName());
        userPreferences.put("USERNAME", this.userName);
        userPreferences.put("PASSWORD", this.password);
        userPreferences.put("PRIVILEGES", this.privileges);
    }

    public String getUserName() {
        return this.userName;
    }

    public String getPassword() {
        return this.password;
    }

    public String getPrivileges() {
        return this.privileges;
    }

    /**Clears data**/
    public synchronized void cleanUserSession() {
        this.userName = "";
        this.password = "";
        this.privileges = "";
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "userName='" + this.userName + '\'' +
                ", privileges='" + this.privileges + '\'' +
                '}';
    }
}