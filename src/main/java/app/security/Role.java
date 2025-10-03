package app.security;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "roles")
public class Role {
    @Id
    @Column(name = "rolename", nullable = false)
    private String rolename;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private Set<User> users = new HashSet<>();

    public Role(){

    }

    public Role(String rolename){
        this.rolename = rolename.toUpperCase(Locale.ROOT);
    }

    public void setRoleName(String rolename){
        this.rolename = rolename.toUpperCase(Locale.ROOT);
    }

}