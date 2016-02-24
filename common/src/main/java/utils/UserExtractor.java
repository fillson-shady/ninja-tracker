package utils;

import com.google.inject.Inject;
import dao.UserDAO;
import models.User;
import ninja.Context;
import ninja.params.ArgumentExtractor;

public class UserExtractor implements ArgumentExtractor<User> {

    private UserDAO userDao;

    @Inject
    public UserExtractor(UserDAO userDao) {
        this.userDao = userDao;
    }

    @Override
    public User extract(Context context) {
        return userDao.getAuthUserOpt(context.getSession().get("userId")).orElse(null);
    }

    @Override
    public Class<User> getExtractedType() {
        return User.class;
    }

    @Override
    public String getFieldName() {
        return null;
    }
}
