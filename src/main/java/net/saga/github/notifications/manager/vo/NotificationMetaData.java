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

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;


/**
 *
 * @author summers
 */
@Entity
public class NotificationMetaData implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private ApplicationAccount applicationAccount;

    private int rateLimit;
    private int rateLimitRemaining;
    private int rateLimitReset;
    private String eTag;
    private String lastModified;
    private ZonedDateTime lastTested = ZonedDateTime.now().minus(1, ChronoUnit.YEARS);

    protected NotificationMetaData() {
    }

    public NotificationMetaData(ApplicationAccount account) {
        this.applicationAccount = account;
        this.rateLimit = 60;
        this.rateLimitRemaining = 60;
        this.rateLimitReset = 0;
        this.eTag = "";
        this.lastModified = "";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApplicationAccount getApplicationAccount() {
        return applicationAccount;
    }

    public void setApplicationAccount(ApplicationAccount applicationAccount) {
        this.applicationAccount = applicationAccount;
    }

    public int getRateLimit() {
        return rateLimit;
    }

    public void setRateLimit(int rateLimit) {
        this.rateLimit = rateLimit;
    }

    public int getRateLimitRemaining() {
        return rateLimitRemaining;
    }

    public void setRateLimitRemaining(int rateLimitRemaining) {
        this.rateLimitRemaining = rateLimitRemaining;
    }

    public int getRateLimitReset() {
        return rateLimitReset;
    }

    public ZonedDateTime getRateLimitResetAsZonedDateTime() {
        Instant i = Instant.ofEpochSecond(rateLimitReset);
        return ZonedDateTime.ofInstant(i, ZoneOffset.UTC.normalized());
    }

    public void setRateLimitReset(int rateLimitReset) {
        this.rateLimitReset = rateLimitReset;
    }

    public String geteTag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public ZonedDateTime getLastTested() {
        return lastTested;
    }

    public void setLastTested(ZonedDateTime lastTested) {
        this.lastTested = lastTested;
    }

    
    
}
