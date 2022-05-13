drop table if exists user CASCADE;

drop sequence if exists hibernate_sequence;

create sequence hibernate_sequence start with 1 increment by 1;

create table user (
    id bigint not null,
    creation_timestamp timestamp,
    version integer,
    department varchar(255),
    email varchar(255),
    password varchar(60) not null,
    phone_number varchar(255),
    user_id varchar(255) not null,
    korean_name varchar(255),
    primary key (id)
);

alter table user add constraint UKdnhomwlbtpmj040yo66fnx01r unique (user_id);
