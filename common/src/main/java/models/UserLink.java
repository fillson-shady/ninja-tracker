package models;

import javax.persistence.*;

@Entity
@Table(name="\"UserLink\"")
public class UserLink {

    @Id
    @GeneratedValue
    public Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "\"userId\"")
    public User user;
    @ManyToOne(optional = false)
    @JoinColumn(name = "\"linkedUserId\"")
    public User linkedUser;

    public UserLink() {}

    public UserLink(User user, User linkedUser) {
        this.user = user;
        this.linkedUser = linkedUser;
    }
}
