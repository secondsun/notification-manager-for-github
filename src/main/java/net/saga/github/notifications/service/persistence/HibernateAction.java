package net.saga.github.notifications.service.persistence;

import org.hibernate.Session;

/**
 *
 * @author secon
 */
@FunctionalInterface
public interface HibernateAction {

    public void exec(Session session);
    
}
