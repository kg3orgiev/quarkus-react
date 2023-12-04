package com.k0c3.fullstack.task;

import com.k0c3.fullstack.user.UserService;
import io.quarkus.security.UnauthorizedException;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hibernate.ObjectNotFoundException;

import java.time.ZonedDateTime;
import java.util.List;

@ApplicationScoped
public class TaskService {
  private final UserService userService;

  @Inject
  public TaskService(UserService userService) {
    this.userService = userService;
  }

  public Uni<Task> findById(long id) {
    return userService
        .getCurrentUser()
        .chain(
            user ->
                Task.<Task>findById(id)
                    .onItem()
                    .ifNull()
                    .failWith(() -> new ObjectNotFoundException(id, "Task"))
                    .onItem()
                    .invoke(
                        task -> {
                          if (!user.equals(task.user)) {
                            throw new UnauthorizedException("You are not allowed to see this task");
                          }
                        }));
  }

  public Uni<List<Task>> listForUser() {
    return userService.getCurrentUser().chain(user -> Task.find("user", user).list());
  }

  @Transactional
  public Uni<Task> create(Task task) {
    return userService
        .getCurrentUser()
        .chain(
            user -> {
              task.user = user;
              return task.persistAndFlush();
            });
  }

  @Transactional
  public Uni<Task> update(Task task) {
    return findById(task.id).chain(t -> Task.getSession()).chain(s -> s.merge(task));
  }

  @Transactional
  public Uni<Void> delete(long id) {
    return findById(id).chain(Task::delete);
  }

  @Transactional
  public Uni<Boolean> setComplete(long id, boolean complete) {
    return findById(id)
        .chain(
            task -> {
              task.complete = complete ? ZonedDateTime.now() : null;
              return task.persistAndFlush();
            })
        .chain(task -> Uni.createFrom().item(complete));
  }
}
