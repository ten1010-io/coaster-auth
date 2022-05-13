drop table if exists hibernate_sequence;

drop table if exists user;

create table hibernate_sequence
(
    next_val bigint
) engine = InnoDB;

insert into hibernate_sequence
values (1);

create table user
(
    id                 bigint       not null,
    creation_timestamp datetime(6),
    version            integer,
    department         varchar(255),
    email              varchar(255),
    password           varchar(60)  not null,
    phone_number       varchar(255),
    user_id            varchar(255) not null,
    korean_name           varchar(255),
    primary key (id)
) engine = InnoDB;

alter table user
    add constraint UKdnhomwlbtpmj040yo66fnx01r unique (user_id);
