package controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.UserDAO;
import dto.BooleanResponse;
import models.User;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.Router;
import ninja.params.Param;

@Singleton
public class LoginController {

    private UserDAO userDao;
    private Router router;

    @Inject
    public LoginController(UserDAO userDao, Router router) {
        this.userDao = userDao;
        this.router = router;
    }

    public Result login() {
        return Results.html();
    }

    public Result loginPost(@Param("email") String email,
                            @Param("name") String name,
                            @Param("password") String password,
                            Context context) {

        return userDao.getAuthUserOpt(email, password).map(user -> authenticate(user, context)).orElseGet(() ->
                userDao.addUserOpt(email, name, password).map(user -> authenticate(user, context)).orElseGet(() -> {
                    context.getFlashScope().put("email", email);
                    context.getFlashScope().error("flash.auth.incorrectCredentials");
                    return Results.redirect(router.getReverseRoute(LoginController.class, "login"));
                })) ;
    }

    public Result logout(Context context) {
        context.getSession().clear();
        context.getSession().put("clear", "true");
        context.getFlashScope().success("flash.auth.successfullyLoggedOut");
        return Results.redirect(router.getReverseRoute(ApplicationController.class, "index"));
    }

    public Result checkFreeEmail(@Param("email") String email) {
        return Results.json().render(new BooleanResponse(userDao.isEmailFree(email)));
    }

    private Result authenticate(User user, Context context) {
        context.getSession().put("email", user.email);
        context.getSession().put("name", user.name);
        context.getSession().put("userId", user.id.toString());
        context.getFlashScope().success("flash.auth.successfullyLoggedIn");
        return Results.redirect(router.getReverseRoute(ApplicationController.class, "index"));
    }
}
