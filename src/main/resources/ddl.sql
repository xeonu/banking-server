create table member
(
    id         int          not null auto_increment,
    login_id   varchar(32)  not null,
    password   varchar(128) not null,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp,
    primary key (id)
);

create table account
(
    id         int not null auto_increment,
    owner_id   int not null,
    number     varchar(32),
    balance    bigint,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp,
    primary key (id),
    foreign key (owner_id) references member (id)
);

create table account_transfer
(
    id                  int not null auto_increment,
    sender_account_id   int,
    receiver_account_id int,
    amount              bigint,
    created_at          datetime default current_timestamp,
    updated_at          datetime default current_timestamp,
    primary key (id),
    foreign key (sender_account_id) references account (id),
    foreign key (receiver_account_id) references account (id)
);

create table friend_info
(
    id         int not null auto_increment,
    member_id  int,
    friend_id  int,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp,
    primary key (id),
    foreign key (member_id) references member (id),
    foreign key (friend_id) references member (id)
);

create table friend_request
(
    id          int not null auto_increment,
    sender_id   int,
    receiver_id int,
    created_at  datetime default current_timestamp,
    updated_at  datetime default current_timestamp,
    primary key (id),
    foreign key (sender_id) references member (id),
    foreign key (receiver_id) references member (id)
)