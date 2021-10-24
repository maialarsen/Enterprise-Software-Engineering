package springboot.model;

import javax.persistence.*;

@Entity
@Table(name = "session")
public class Session {
    @Id
    @Column(name = "token", nullable = false, length = 100)
    private String token;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="user_id")
    private User userId;

    public Session() {}

    //accessors
    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return token.toString();
    }
}
