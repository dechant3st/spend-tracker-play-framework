# --- First database schema

# --- !Ups

create table spend (
    id                  bigint not null primary key,
    name                varchar(255),
    spent_date                timestamp,
    spent_value               decimal(12, 2)
);

create sequence spend_seq start with 1000;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists spend;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists spend_seq;