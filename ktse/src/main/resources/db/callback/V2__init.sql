alter table provider
    add user_id int null;

alter table provider
    add constraint provider_user_id_fk
        foreign key (user_id) references user (id);

