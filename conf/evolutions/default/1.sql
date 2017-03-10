# --- !Ups

create table file_info(
       id    bigserial,
       name  varchar(255) not null,
       hash  varchar(64) not null,
       fecha timestamp not null default current_timestamp,
       data  binary not null
);

# --- !Downs

drop table file_info;
