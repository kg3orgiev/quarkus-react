package com.k0c3.fullstack.project;

import com.k0c3.fullstack.task.Task;
import com.k0c3.fullstack.user.UserService;
import io.quarkus.security.UnauthorizedException;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hibernate.ObjectNotFoundException;

import java.util.List;

@ApplicationScoped
public class ProjectService {

    private final UserService userService;

    @Inject
    public ProjectService(UserService userService) {
        this.userService = userService;
    }

    public Uni<Project> findById(long id) {
        return userService.getCurrentUser()
                .chain(user -> Project.<Project>findById(id)
                        .onItem().ifNull().failWith(() -> new ObjectNotFoundException(id, "Project"))
                        .onItem().invoke(project -> {
                            if (!user.equals(project.user)) {
                                throw new UnauthorizedException("You are not allowed to see this project");
                            }
                        }));
    }

    public Uni<List<Project>> listForUser() {
        return userService.getCurrentUser()
                .chain(user -> Project.find("user", user).list());
    }

    @Transactional
    public Uni<Project> create(Project project) {
        return userService.getCurrentUser()
                .chain(user -> {
                    project.user = user;
                    return project.persistAndFlush();
                });
    }

    @Transactional
    public Uni<Project> update(Project project) {
        return findById(project.id)
                .chain(p -> Project.getSession())
                .chain(s -> s.merge(project));
    }

    @Transactional
    public Uni<Void> delete(long id) {
        return findById(id)
                .chain(p -> Task.update("project = null where project = ?1", p)
                        .chain(i -> p.delete()));
    }
}
