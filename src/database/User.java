/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

/**
 *
 * @author Will
 */
public class User {
    private String username;
    private String password;
    private int libSize;

    public User(String username, String password, int libSize) {
        this.username = username;
        this.password = password;
        this.libSize = libSize;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getLibSize() {
        return libSize;
    }
    
}
