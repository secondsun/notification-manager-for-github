package net.saga.github.notifications.manager.persistence;

import org.hibernate.Session;

/**
 *
 * @author secon
 */
@FunctionalInterface
public interface HibernateAction {

    public void exec(Session session);
    
}
