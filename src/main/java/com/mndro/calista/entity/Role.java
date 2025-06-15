package com.mndro.calista.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

/**
 * The {@code Role} class represents a user role in the application.
 */
@Entity
@Table(name="roles")
public class Role implements GrantedAuthority {

    @Getter
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    @Column(name="role_id")
    private UUID roleId;

    /**
     * -- SETTER --
     *  Set the authority associated with the role.
     *
     * @param authority The authority string.
     */
    @Setter
    private String authority;

    /**
     * Default constructor for the {@code Role} class.
     */
    public Role(){
        super();
    }

    /**
     * Parameterized constructor for the {@code Role} class.
     *
     * @param authority The authority associated with the role.
     */
    public Role(String authority){
        this.authority = authority;
    }

    /**
     * Parameterized constructor for the {@code Role} class.
     *
     * @param roleId    The ID of the role.
     * @param authority The authority associated with the role.
     */
    public Role(UUID roleId, String authority){
        this.roleId = roleId;
        this.authority = authority;
    }

    /**
     * Get the authority associated with the role.
     *
     * @return The authority string.
     */
    @Override
    public String getAuthority() {
        return this.authority;
    }

    /**
     * Set the ID of the role.
     *
     * @param roleId The role ID.
     */
    public void setRoleId(UUID roleId){
        this.roleId = roleId;
    }
}
