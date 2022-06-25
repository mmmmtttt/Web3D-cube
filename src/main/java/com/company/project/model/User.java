package com.company.project.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String username;

    private String password;

    private Integer sex;

    @Column(name = "register_date")
    private Date registerDate;

    @Column(name = "portrait_id")
    private Integer portraitId;

    private Integer pants;
    private Integer jacket;

    @Transient
    private List<? extends GrantedAuthority> authorities;

    public User(String username, String password, Integer sex, Date registerDate,
                Integer portraitId, Integer jacket, Integer pants) {
        this.password = password;
        this.username = username;
        this.sex = sex;
        this.registerDate = registerDate;
        this.jacket = jacket;
        this.pants = pants;
        this.portraitId = portraitId;
    }

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return username
     */
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * @return password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return sex
     */
    public Integer getSex() {
        return sex;
    }

    /**
     * @param sex
     */
    public void setSex(Integer sex) {
        this.sex = sex;
    }

    /**
     * @return register_date
     */
    public Date getRegisterDate() {
        return registerDate;
    }

    /**
     * @param registerDate
     */
    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Integer getPortraitId() {
        return portraitId;
    }

    public void setPortraitId(Integer portraitId) {
        this.portraitId = portraitId;
    }

    public Integer getJacket() {
        return jacket;
    }

    public void setJacket(Integer jacket) {
        this.jacket = jacket;
    }

    public Integer getPants() {
        return pants;
    }

    public void setPants(Integer pants) {
        this.pants = pants;
    }

    public void setAuthorities(List<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }
}