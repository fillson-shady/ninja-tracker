package models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name="\"UserTodo\"")
public class UserTodo {

    @Id
    @GeneratedValue
    public Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "\"userId\"")
    public User user;
    @Column(nullable = false)
    public String title;
    @Column(nullable = false)
    public boolean completed = false;
    @Column(nullable = false)
    public Timestamp created = new Timestamp(new Date().getTime());

    public UserTodo() {}

    public UserTodo(User user, String title) {
        this.user = user;
        this.title = title;
    }

    public Repr toRepr() {
        return new Repr(id, title, completed);
    }

    public final class Repr {
        public Long id;
        public String title;
        public Boolean completed;
        public Repr(Long id, String title, Boolean completed) {
            this.id = id;
            this.title = title;
            this.completed = completed;
        }
    }
}
