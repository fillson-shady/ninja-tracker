package models;

import javax.persistence.*;

@Entity
@Table(name="\"User\"")
public class User {

    @Id
    @GeneratedValue
    public Long id;
    @Column(nullable = false)
    public String name;
    @Column(nullable = false, unique = true)
    public String email;
    @Column(nullable = false)
    public byte[] passwordHash;
    @Column(nullable = false)
    public byte[] salt;

    public User() {}

    public User(String name, String email, byte[] passwordHash, byte[] salt) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }
}
