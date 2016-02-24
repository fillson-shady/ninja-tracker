package dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import models.User;
import models.UserLink;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import utils.PasswordUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
@Transactional
public class UserDAO extends CommonDAO {

    @Inject
    public UserDAO(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    public Optional<User> addUserOpt(String email, String name, String password) {
        if (isEmailFree(email) && EmailValidator.getInstance().isValid(email) && StringUtils.isNoneEmpty(password)) {
            byte[] salt = PasswordUtils.generateSalt();
            byte[] encryptedPwd = PasswordUtils.getEncryptedPassword(password, salt);
            User user = new User(name, email, encryptedPwd, salt);
            getEntityManager().persist(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public Optional<User> getAuthUserOpt(String email, String password) {
        if (StringUtils.isNoneEmpty(email) && StringUtils.isNoneEmpty(password)) {

            List users = list(User.class, "WHERE e.email = ?", email);
            if (users.size() != 1) return Optional.empty();

            User user = (User)users.get(0);
            if (user != null && PasswordUtils.authenticate(password, user.passwordHash, user.salt))
                return Optional.of(user);
        }
        return Optional.empty();
    }

    public Optional<User> getAuthUserOpt(String id) {
        return unique(User.class, "WHERE e.id = ?", Long.parseLong(id));
    }

    public boolean isEmailFree(String email) {
        return StringUtils.isNoneEmpty(email) && 0L == count(User.class, "WHERE e.email = ?", email);
    }

    public UserLink createLink(User user, User linkedUser) {
        UserLink userLink = new UserLink(user, linkedUser);
        getEntityManager().persist(userLink);
        return userLink;
    }

    public boolean linkExists(User user, User linkedUser) {
        return unique(UserLink.class, "WHERE e.user = ? and e.linkedUser = ?", user, linkedUser).isPresent();
    }

    public List<User> listAllLinkedUsers(User user) {
        List<User> res = list(UserLink.class, "WHERE e.user = ? or e.linkedUser = ?", user, user).stream()
                .map(link -> link.user.id.equals(user.id) ? link.linkedUser : link.user).collect(Collectors.toList());
        res.add(user);
        return res;
    }

    public List<String> listLinkedNames(User user) {
        return list(UserLink.class, "WHERE e.user = ? or e.linkedUser = ?", user, user).stream()
                .map(link -> link.user.id.equals(user.id) ? link.linkedUser.name : link.user.name).collect(Collectors.toList());
    }
}
