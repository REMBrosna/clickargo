package com.acleda.company.student.administrator.model;

import com.acleda.company.student.infrastructure.domain.AbstractPersistableCustom;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
@Entity
@Table(name = "T_APP_USER")
public class TAppUser extends AbstractPersistableCustom implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Column(name = "USERNAME", nullable = false, length = 100)
    private String username;

    @Column(name = "FIRSTNAME", nullable = false, length = 100)
    private String firstname;

    @Column(name = "LASTNAME", nullable = false, length = 100)
    private String lastname;
    @Column(name = "GENDER", nullable = false, length = 100)
    private String gender;

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_OF_BIRTH")
    private Date dtOfBirth;

    @Column(name = "ADDRESS")
    private String address;

    @Pattern(regexp = "\\d{9,15}", message = "Phone number must be 9 to 15 digits")
    @Column(name = "PHONE_NUMBER")
    private String conNumber;

    @JsonIgnore
    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "ACCOUNT_NON_EXPIRED", nullable = false)
    private boolean accountNonExpired = true;

    @Column(name = "ACCOUNT_NON_LOCKED", nullable = false)
    private boolean accountNonLocked = true;

    @Column(name = "CREDENTIALS_NON_EXPIRED", nullable = false)
    private boolean credentialsNonExpired = true;

    @Column(name = "FIRST_TIME_LOGIN_REMAINING", nullable = false)
    private boolean firstTimeLoginRemaining = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "APPUSER_ROLE",
            joinColumns = @JoinColumn(name = "APPUSER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    private Set<Role> roles = new HashSet<>();

    @Column(name = "LAST_TIME_PASSWORD_UPDATED")
    private Date lastTimePasswordUpdated;

    @Column(name = "PASSWORD_NEVER_EXPIRES", nullable = false)
    private boolean passwordNeverExpires;

    @Column(name = "IS_SELF_SERVICE_USER", nullable = false)
    private boolean isSelfServiceUser;

    @Column(name = "STATUS")
    private Character status;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "USR_DT_CREATE", updatable = false)
    private Date usrDtCreate;

    @Column(name = "USR_UID_CREATE")
    private String usrUidCreate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "USR_DT_LUPD")
    private Date usrDtLupd;

    @Column(name = "USR_UID_LUPD")
    private String usrUidLupd;

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return populateGrantedAuthorities();
    }

    private List<GrantedAuthority> populateGrantedAuthorities() {
        final List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (final Role role : this.roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return grantedAuthorities;
    }

    @JsonIgnore
    public List<String> getStrAuthorities() {
        final List<String> grantedAuthorities = new ArrayList<>();
        for (final Role role : this.roles) {
            grantedAuthorities.add(role.getName());
        }
        return grantedAuthorities;
    }

    public void setStrRoles(List<String> roles) {
        for (final String role : roles) {
            this.roles.add(Role.builder().name(role).build());
        }
    }

    @Override
    public String toString() {
        return "AppUser [username=" + this.username + ", getId()=" + this.getId() + "]";
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }

    @Override
    public Long getId() {
        return super.getId();
    }
}
