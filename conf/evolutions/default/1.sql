# --- !Ups

create table file_info(
       id    bigserial,
       name  varchar(255) not null,
       hash  varchar(64) not null,
       data  bytea not null
);

# --- !Downs

drop table file_info;
