package controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.UserDAO;
import filters.AuthFilter;
import models.User;
import ninja.*;
import ninja.params.Param;
import utils.AuthUser;

import java.net.HttpURLConnection;
import java.net.URL;

@Singleton
public class ApplicationController {

    private UserDAO userDao;
    private Router router;

    @Inject
    public ApplicationController(UserDAO userDao, Router router) {
        this.userDao = userDao;
        this.router = router;
    }

    public Result index() {
        return Results.html();
    }

    @FilterWith(AuthFilter.class)
    public Result userProfile(@AuthUser User user) {
        return Results.html().render("links", userDao.listLinkedNames(user));
    }

    @FilterWith(AuthFilter.class)
    public Result createUserLinkPost(@Param("linkEmail") String linkEmail,
                                     @Param("linkPassword") String linkPassword,
                                     @AuthUser User user,
                                     Context context) {

        return userDao.getAuthUserOpt(linkEmail, linkPassword).map(linkedUser -> {
            if (userDao.linkExists(user, linkedUser))
                context.getFlashScope().error("flash.sync.alreadyCreated");
            else {
                userDao.createLink(user, linkedUser);
                context.getFlashScope().success("flash.sync.successfullyCreated");
            }
            return Results.redirect(router.getReverseRoute(ApplicationController.class, "userProfile"));
        }).orElseGet(() -> {
            context.getFlashScope().error("flash.sync.incorrectCredentials");
            return Results.redirect(router.getReverseRoute(ApplicationController.class, "userProfile"));
        });
    }

    public Result ping(@Param("callback") String callback) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(callback).openConnection();
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(10_000);
            connection.setRequestMethod("GET");
            System.out.println(connection.getResponseCode());
        } catch (Exception ignored) {}
        return Results.noContent();
    }
}
