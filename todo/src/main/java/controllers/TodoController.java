package controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.UserTodoDAO;
import filters.AuthFilter;
import models.User;
import models.UserTodo;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.PathParam;
import utils.AuthUser;

import java.util.stream.Collectors;

@Singleton
@FilterWith(AuthFilter.class)
public class TodoController {

    private UserTodoDAO todoDAO;

    @Inject
    public TodoController(UserTodoDAO todoDAO) {
        this.todoDAO = todoDAO;
    }

    public Result todoList() {
        return Results.html();
    }

    public Result todoListPost(@AuthUser User user) {
        return Results.json().render("data", todoDAO.listTodo(user)
                .stream().map(UserTodo::toRepr).collect(Collectors.toList()));
    }

    public Result addTodoPost(@Param("title") String title,
                              @AuthUser User user) {
        Long id = todoDAO.createTodo(user, title).id;
        return Results.json().render("id", id);
    }

    public Result editTodoPost(@PathParam("id") Long id,
                               @Param("title") String title,
                               @AuthUser User user) {
        todoDAO.editTodo(user, id, title);
        return Results.noContent();
    }

    public Result completeTodoPost(@PathParam("id") Long id,
                                   @AuthUser User user) {
        todoDAO.completeTodo(user, id);
        return Results.noContent();
    }

    public Result completeAllTodoPost(@Param("value") Boolean value,
                                      @AuthUser User user) {
        todoDAO.completeAll(user, value);
        return Results.noContent();
    }

    public Result removeTodoPost(@PathParam("id") Long id,
                                 @AuthUser User user) {
        todoDAO.removeTodo(user, id);
        return Results.noContent();
    }

    public Result removeCompletedTodoPost(@AuthUser User user) {
        todoDAO.removeCompleted(user);
        return Results.noContent();
    }
}
