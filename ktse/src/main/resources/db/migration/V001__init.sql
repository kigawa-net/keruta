create table if not exists provider
(
    id      integer auto_increment,
    name    varchar(50),
    issuer  varchar(50),
    setting text,
    create_at timestamp,
    primary key (id)
);
create table if not exists queue
(
    id          integer auto_increment,
    name        varchar(50),
    provider_id integer,
    setting     text,
    create_at timestamp,
    primary key (id),
    foreign key (provider_id) references provider (id)
);
create table if not exists user(
    id   integer auto_increment,
    create_at timestamp,
    primary key (id)
);
create table if not exists queue_user(
    queue_id integer,
    user_id  integer,
    create_at timestamp,
    foreign key (queue_id) references queue (id),
    foreign key (user_id) references user (id)
);
create table if not exists task(
    id   integer auto_increment,
    user_id integer,
    queue_id integer,
    create_at timestamp,
    primary key (id),
    foreign key (user_id) references user (id),
    foreign key (queue_id) references queue (id)
);
create table if not exists user_idp(
    user_id integer,
    issuer varchar(50),
    subject varchar(50),
    create_at timestamp,
    primary key (user_id, issuer),
    foreign key (user_id) references user (id)
)
