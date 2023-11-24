package com.k0c3.fullstack.user;

import com.k0c3.fullstack.project.Project;
import com.k0c3.fullstack.task.Task;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.hibernate.ObjectNotFoundException;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class UserService {
  private final JsonWebToken jsonWebToken;

  @Inject
  public UserService(JsonWebToken jsonWebToken) {
    this.jsonWebToken = jsonWebToken;
  }
  public Uni<User> findById(long id) {
    return User.<User>findById(id)
        .onItem()
        .ifNull()
        .failWith(() -> new ObjectNotFoundException(id, "User"));
  }
  @WithSession
  public Uni<User> findByName(String name) {
    return User.find("name", name).firstResult();
  }

  public Uni<List<User>> list() {
    return User.listAll();
  }

  @Transactional
  public Uni<User> create(User user) {
    user.password = BcryptUtil.bcryptHash(user.password);
    return user.persistAndFlush();
  }

  @Transactional
  public Uni<User> update(User user) {
    return findById(user.id).chain(u -> User.getSession()).chain(s -> s.merge(user));
  }

  @Transactional
  public Uni<Void> delete(long id) {
    return findById(id)
        .chain(
            u ->
                Uni.combine()
                    .all()
                    .unis(Task.delete("user.id", u.id), Project.delete("user.id", u.id))
                    .asTuple()
                    .chain(t -> u.delete()));
  }

  public Uni<User> getCurrentUser() {
    return findByName(jsonWebToken.getName());
  }

  public static boolean matches(User user, String password) {
    return BcryptUtil.matches(password, user.password);
  }

  @Transactional
  public Uni<User> changePassword(String currentPassword, String newPassword) {
    return getCurrentUser()
            .chain(u -> {
              if (!matches(u, currentPassword)) {
                throw new ClientErrorException("Current password does not match", Response.Status.CONFLICT);
              }
              u.setPassword(BcryptUtil.bcryptHash(newPassword));
              return u.persist();
            });
  }
}
