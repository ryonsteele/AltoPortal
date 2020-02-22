package com.alto.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by fan.jin on 2016-11-03.
 */

@Entity
@Table(name="USER_AUTHORITY")
public class UserAuthority implements Serializable {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name="user_id")
    Long userid;

    @Column(name="authority_id")
    Long authorityid;

    public UserAuthority(){}

    public UserAuthority(Long id, Long auth){
        this.userid = id;
        this.authorityid = auth;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Long getAuthorityid() {
        return authorityid;
    }

    public void setAuthorityid(Long authorityid) {
        this.authorityid = authorityid;
    }
}
