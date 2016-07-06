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
import java.net.URL;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author summers
 */
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"login"})}, indexes = {
    @Index(unique = true, columnList = "login")})
public class GitHubUser implements Serializable {

    @Id
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "varchar(255) UNIQUE")
    private String login;
    private URL avatar_url;
    private String gravatar_id;
    private URL url;
    private URL html_url;
    private URL followers_url;
    private URL following_url;
    private URL gists_url;
    private URL starred_url;
    private URL subscriptions_url;
    private URL organizations_url;
    private URL repos_url;
    private URL events_url;
    private URL received_events_url;
    private String type;
    private boolean site_admin;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public URL getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(URL avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getGravatar_id() {
        return gravatar_id;
    }

    public void setGravatar_id(String gravatar_id) {
        this.gravatar_id = gravatar_id;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public URL getHtml_url() {
        return html_url;
    }

    public void setHtml_url(URL html_url) {
        this.html_url = html_url;
    }

    public URL getFollowers_url() {
        return followers_url;
    }

    public void setFollowers_url(URL followers_url) {
        this.followers_url = followers_url;
    }

    public URL getFollowing_url() {
        return following_url;
    }

    public void setFollowing_url(URL following_url) {
        this.following_url = following_url;
    }

    public URL getGists_url() {
        return gists_url;
    }

    public void setGists_url(URL gists_url) {
        this.gists_url = gists_url;
    }

    public URL getStarred_url() {
        return starred_url;
    }

    public void setStarred_url(URL starred_url) {
        this.starred_url = starred_url;
    }

    public URL getSubscriptions_url() {
        return subscriptions_url;
    }

    public void setSubscriptions_url(URL subscriptions_url) {
        this.subscriptions_url = subscriptions_url;
    }

    public URL getOrganizations_url() {
        return organizations_url;
    }

    public void setOrganizations_url(URL organizations_url) {
        this.organizations_url = organizations_url;
    }

    public URL getRepos_url() {
        return repos_url;
    }

    public void setRepos_url(URL repos_url) {
        this.repos_url = repos_url;
    }

    public URL getEvents_url() {
        return events_url;
    }

    public void setEvents_url(URL events_url) {
        this.events_url = events_url;
    }

    public URL getReceived_events_url() {
        return received_events_url;
    }

    public void setReceived_events_url(URL received_events_url) {
        this.received_events_url = received_events_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSite_admin() {
        return site_admin;
    }

    public void setSite_admin(boolean site_admin) {
        this.site_admin = site_admin;
    }

}
