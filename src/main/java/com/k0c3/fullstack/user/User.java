package com.k0c3.fullstack.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User extends PanacheEntity {

  @Column(unique = true, nullable = false)
  public String name;

  @Column(nullable = false)
  String password;

  @CreationTimestamp
  @Column(updatable = false, nullable = false)
  public ZonedDateTime created;

  @Version
  public int version;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "id"))
  @Column(name = "role")
  public List<String> roles;

  @JsonProperty("password")
  public void setPassword(String password) {
    this.password = password;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;

    User user = (User) o;

    return Objects.equals(id, user.id) &&
            Objects.equals(name, user.name) &&
            Objects.equals(password, user.password) &&
            Objects.equals(created, user.created) &&
            Objects.equals(version, user.version);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, password, created, version, roles);
  }
}
