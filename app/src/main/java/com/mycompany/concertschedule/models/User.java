package com.mycompany.concertschedule.models;

public class User {
    private String id, email, login, pass;
    private int admin, moderator;

    public User() {}


    public User(String id, String email, String login, String pass) {
        this.admin = 0;
        this.moderator = 0;
        this.id = id;
        this.email = email;
        this.login = login;
        this.pass = pass;
    }

    public User(String id, String email, String login, String pass, int admin, int moderator) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.pass = pass;
        this.admin = admin;
        this.moderator = moderator;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }

    public int getModerator() {
        return moderator;
    }

    public void setModerator(int moderator) {
        this.moderator = moderator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
