/*
 *
 * Copyright (C) 2014
 *
 */

package de.htw.sdf.photoplatform.repository.impl;

import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import de.htw.sdf.photoplatform.persistence.models.UserProfile;
import de.htw.sdf.photoplatform.repository.UserProfileDAO;
import de.htw.sdf.photoplatform.repository.common.GenericDAOImpl;

/**
 * Created by Sergej Meister.
 */
@Repository
@Transactional
public class UserProfileDAOImpl extends GenericDAOImpl<UserProfile> implements
        UserProfileDAO {

    /**
     * User Profile dao constructor.
     */
    public UserProfileDAOImpl() {
        super();
        setClazz(UserProfile.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserProfile findByUserId(final Long id) {
        String queryString = "SELECT DISTINCT(userProfile) FROM UserProfile userProfile "
                + "LEFT JOIN FETCH userProfile.user user "
                + "WHERE user.id = ?1";

        Query query = createQuery(queryString);
        query.setParameter(1, id);
        return (UserProfile) query.getSingleResult();
    }
}
