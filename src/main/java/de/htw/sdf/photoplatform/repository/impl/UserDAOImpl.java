/*
 *
 * Copyright (C) 2014
 *
 */

package de.htw.sdf.photoplatform.repository.impl;

import de.htw.sdf.photoplatform.persistence.model.Role;
import de.htw.sdf.photoplatform.persistence.model.User;
import de.htw.sdf.photoplatform.repository.UserDAO;
import de.htw.sdf.photoplatform.repository.common.GenericDAOImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

/**
 * repository methods for users.
 *
 * @author <a href="mailto:s0541962@htw-berlin.de">Vincent Schwarzer</a>
 */
@Repository
@Transactional
public class UserDAOImpl extends GenericDAOImpl<User> implements UserDAO,
        UserDetailsService {
    /**
     * User DAO constructor.
     */
    public UserDAOImpl() {
        super();
        setClazz(User.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> find(Integer start, Integer count) {
        StringBuilder queryBuilder = initSelectQuery();
        queryBuilder.append("WHERE user.enabled = true ");
        queryBuilder.append("ORDER BY user.id ");
        Query query = createQuery(queryBuilder.toString());
        query.setFirstResult(start.intValue());
        query.setMaxResults(count.intValue());
        return (List<User>) query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findByEmail(String email) {
        Query query = createQuery("SELECT u FROM User u WHERE u.email = :email");
        query.setParameter("email", email);

        try {
            return (User) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserByUsername(final String username) {
        String queryString = "SELECT user FROM User user "
                + "WHERE user.email = :email";

        Query query = createQuery(queryString);
        query.setParameter("email", username);

        return (User) query.getSingleResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> findByRole(final Role role) {
        return findByRoleId(role.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> findByRoleId(final Long roleId) {
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT user FROM User user ");
        queryBuilder.append("LEFT JOIN FETCH user.userRoles userRoles ");
        queryBuilder.append("WHERE userRoles.role.id = ?1");

        Query query = createQuery(queryBuilder.toString());
        query.setParameter(1, roleId);
        return (List<User>) query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> findAllNotAdminUsers() {
        StringBuilder queryBuilder = initSelectQuery();
        queryBuilder.append("WHERE userRoles.role.id != ?1");

        Query query = createQuery(queryBuilder.toString());
        query.setParameter(1, Role.ADMIN_ID);
        return (List<User>) query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> findByEnabled(boolean enabled) {
        StringBuilder queryBuilder = initSelectQuery();
        queryBuilder.append("WHERE user.enabled = ?1");

        Query query = createQuery(queryBuilder.toString());
        query.setParameter(1, enabled);
        return (List<User>) query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<User> findByRoleAndEnabledFilter(String roleName, boolean enabled) {
        StringBuilder sb = new StringBuilder("SELECT u FROM User u");
        sb.append(" LEFT JOIN FETCH u.userRoles userRoles");
        sb.append(" WHERE userRoles.role.name = :roleName ");
        sb.append(" AND u.enabled = :enabled");

        Query query = createQuery(sb.toString());
        query.setParameter("roleName", roleName);
        query.setParameter("enabled", enabled);

        return (List<User>) query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> findByAccountLocked(boolean locked) {
        StringBuilder queryBuilder = initSelectQuery();
        queryBuilder.append("WHERE user.accountNonLocked = ?1");

        Query query = createQuery(queryBuilder.toString());
        query.setParameter(1, locked);
        return (List<User>) query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findById(Long id) {
        StringBuilder queryBuilder = initSelectQuery();
        queryBuilder.append("WHERE user.id = ?1");
        Query query = createQuery(queryBuilder.toString());
        query.setParameter(1, id);
        return (User) query.getSingleResult();
    }

    private StringBuilder initSelectQuery() {
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT DISTINCT(user) FROM User user ");
        queryBuilder.append("LEFT JOIN FETCH user.userRoles userRoles ");

        return queryBuilder;
    }

    @Override
    public EntityManager getEntityManager() {
        return super.getEntityManager();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<User> findPhotographersToActivate() {
        Query query = createQuery("SELECT u FROM User u WHERE u.userRole.role.name = :roleName");
        query.setParameter("roleName", Role.BECOME_PHOTOGRAPHER);

        return (List<User>) query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findByPasswordResetToken(final String passwordResetToken) {
        Query query = createQuery("SELECT u FROM User u WHERE u.passwordResetToken = :token");
        query.setParameter("token", passwordResetToken);

        try {
            return (User) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}
