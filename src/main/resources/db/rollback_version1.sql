alter table Repository drop constraint FK5r01fw32acisnq8bf8vmagg3d;
alter table Notification drop constraint FK70jp81julgkk51h0nqrh2f48u;
alter table Notification drop constraint FK5kwjc7d9bp8weijbhe4evjoly;
drop table Repository;
drop table NotificationSubject ;
drop table Notification; 
drop table GitHubUser;
drop sequence hibernate_sequence RESTRICT; 
delete from version where version >= 1;
