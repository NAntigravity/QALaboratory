package qa.concurrency.laboratory.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_name", nullable = false)
    private String userName;

    @Getter
    @Setter
    public Integer visitsAmount;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public User(String userName) {
        this.userName = userName;
        visitsAmount = 1;
    }

    public User() {

    }
}
