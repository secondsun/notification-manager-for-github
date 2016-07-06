/*
 * Copyright (C) 2016 summers.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package net.saga.github.notifications.manager.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author summers
 */
@Entity
@Table(indexes = {@Index(name = "notification_id", unique = true, columnList = "id, userId")})
public class Notification implements Serializable, Comparable<Notification> {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long jpaId;

    private Long id;

    @ManyToOne
    private Repository repository;

    @Embedded
    private NotificationSubject subject;

    private String reason;
    private boolean unread;
    private ZonedDateTime updated_at;
    private ZonedDateTime last_read_at;

    private String userId;

    private URL url;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJpaId() {
        return jpaId;
    }

    public void setJpaId(Long jpaId) {
        this.jpaId = jpaId;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public NotificationSubject getSubject() {
        return subject;
    }

    public void setSubject(NotificationSubject subject) {
        this.subject = subject;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public ZonedDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(ZonedDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public ZonedDateTime getLast_read_at() {
        return last_read_at;
    }

    public void setLast_read_at(ZonedDateTime last_read_at) {
        this.last_read_at = last_read_at;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static Notification shallowClone(Notification notification) {
        Notification toReturn = new Notification();
        toReturn.id = notification.id;
        toReturn.reason = notification.reason;
        toReturn.unread = notification.unread;
        toReturn.userId = notification.userId;
        toReturn.subject = notification.subject;
        return toReturn;
    }

    @Override
    public int compareTo(Notification o) {
        if (!updated_at.equals(o.updated_at)) {
            return updated_at.compareTo(o.updated_at);
        } return repository.getName().compareTo(o.repository.getName());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Notification other = (Notification) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
    
    
}

