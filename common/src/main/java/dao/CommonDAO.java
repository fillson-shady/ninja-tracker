package dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

@Singleton
@Transactional
public class CommonDAO {

    private Provider<EntityManager> entityManagerProvider;

    @Inject
    public CommonDAO(Provider<EntityManager> entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    protected EntityManager getEntityManager() {
        return entityManagerProvider.get();
    }

    protected <T> Query baseQuery(Class<T> entityClass, String query, Object... params) {
        return baseQuery(entityClass, query, false, params);
    }

    protected <T> Query baseQuery(Class<T> entityClass, String query, boolean count, Object... params) {
        Class<?> clazz = (count ? Long.class : entityClass);
        String baseSelect = count ? "SELECT COUNT(e) FROM" : "SELECT e FROM";
        Query q = getEntityManager().createQuery(baseSelect + " " + entityClass.getSimpleName() + " e " + query, clazz);
        int i = 1;
        for (Object param : params)
            q.setParameter(i++, param);
        return q;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> list(Class<T> entityClass, String query, Object... params) {
        return baseQuery(entityClass, query, false, params).getResultList();
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> unique(Class<T> entityClass, String query, Object... params) {
        return Optional.ofNullable((T)baseQuery(entityClass, query, false, params).getSingleResult());
    }

    public <T> Long count(Class<T> entityClass, String query, Object... params) {
        return (Long)baseQuery(entityClass, query, true, params).getSingleResult();
    }
}
