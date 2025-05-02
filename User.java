// taskmanager/User.java

import java.util.Objects;

/**
 * Represents a user in the Task Manager system.
 */
public class User {
    private final String username;
    private final String passwordHash; // stored via simple hash table in TaskManager

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User other = (User) o;
        return Objects.equals(username, other.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
