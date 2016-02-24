package filters;

import com.google.inject.Inject;
import controllers.LoginController;
import ninja.*;

public class AuthFilter implements Filter {

    private Router router;

    @Inject
    public AuthFilter(Router router) {
        this.router = router;
    }

    @Override
    public Result filter(FilterChain filterChain, Context context) {
        if(context.getSession() == null || context.getSession().get("userId") == null)
            return Results.redirect(router.getReverseRoute(LoginController.class, "login"));
        else
            return filterChain.next(context);
    }
}
