insert into version values (1);
create sequence hibernate_sequence start with 1 increment by 1;
create table GitHubUser (jpaId bigint not null, avatar_url varchar(255), events_url varchar(255), followers_url varchar(255), following_url varchar(255), gists_url varchar(255), gravatar_id varchar(255), html_url varchar(255), id bigint, login varchar(255), organizations_url varchar(255), received_events_url varchar(255), repos_url varchar(255), site_admin boolean not null, starred_url varchar(255), subscriptions_url varchar(255), type varchar(255), url varchar(255), primary key (jpaId));
create table Notification (jpaId bigint not null, id bigint, last_read_at timestamp, reason varchar(255), unread boolean not null, updated_at timestamp, url varchar(255), userId varchar(255), repository_jpaId bigint, subject_jpaId bigint, primary key (jpaId));
create table NotificationSubject (jpaId bigint not null, id bigint, latest_comment_url varchar(255), title varchar(255), type varchar(255), url varchar(255), primary key (jpaId));
create table Repository (jpaId bigint not null, description varchar(255), fork boolean not null, full_name varchar(255), html_url varchar(255), id bigint, isPrivate boolean not null, name varchar(255), url varchar(255), owner_jpaId bigint, primary key (jpaId));
alter table Notification add constraint FK5kwjc7d9bp8weijbhe4evjoly foreign key (repository_jpaId) references Repository;
alter table Notification add constraint FK70jp81julgkk51h0nqrh2f48u foreign key (subject_jpaId) references NotificationSubject;
alter table Repository add constraint FK5r01fw32acisnq8bf8vmagg3d foreign key (owner_jpaId) references GitHubUser;

