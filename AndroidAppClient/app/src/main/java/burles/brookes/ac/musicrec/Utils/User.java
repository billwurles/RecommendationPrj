package burles.brookes.ac.musicrec.Utils;

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

    @Override
    public String toString() {
        return username +", "+libSize;
    }
}
