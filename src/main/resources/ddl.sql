create table member
(
    id         int          not null auto_increment,
    login_id   varchar(32)  not null,
    password   varchar(128) not null,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp,
    primary key (id)
);