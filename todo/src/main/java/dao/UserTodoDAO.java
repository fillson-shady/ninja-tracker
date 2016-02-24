package dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import models.User;
import models.UserTodo;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Singleton
@Transactional
public class UserTodoDAO extends CommonDAO {

    private UserDAO userDAO;

    @Inject
    public UserTodoDAO(UserDAO userDAO, Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
        this.userDAO = userDAO;
    }

    @SuppressWarnings("unchecked")
    public List<UserTodo> listTodo(User user) {
        return baseQuery(UserTodo.class, "WHERE e.user IN (:linked) ORDER BY e.completed, e.created desc")
            .setParameter("linked", userDAO.listAllLinkedUsers(user)).getResultList();
    }

    public UserTodo createTodo(User user, String title) {
        UserTodo userTodo = new UserTodo(user, title);
        getEntityManager().persist(userTodo);
        return userTodo;
    }

    public void editTodo(User user, Long id, String title) {
        getTodoOpt(user, id).ifPresent(todo -> {
            todo.title = title;
            getEntityManager().merge(todo);
        });
    }

    public void completeTodo(User user, Long id) {
        getTodoOpt(user, id).ifPresent(todo -> {
            todo.completed = !todo.completed;
            getEntityManager().merge(todo);
        });
    }

    public void completeAll(User user, Boolean value) {
        listTodo(user).stream().forEach(todo -> {
            todo.completed = value;
            getEntityManager().merge(todo);
        });
    }

    public void removeTodo(User user, Long id) {
        getTodoOpt(user, id).ifPresent(getEntityManager()::remove);
    }

    public void removeCompleted(User user) {
        listTodo(user).stream().filter(todo -> todo.completed).forEach(getEntityManager()::remove);
    }

    private Optional<UserTodo> getTodoOpt(User user, Long id) {
        return Optional.ofNullable((UserTodo) baseQuery(UserTodo.class, "WHERE e.user IN (:linked) AND e.id = :id")
                .setParameter("linked", userDAO.listAllLinkedUsers(user))
                .setParameter("id", id)
                .getSingleResult());
    }
}
