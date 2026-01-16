alter table provider
    change create_at created_at timestamp default CURRENT_TIMESTAMP not null;
alter table queue
    change create_at created_at timestamp default CURRENT_TIMESTAMP not null;
alter table queue_provider
    add column created_at timestamp default CURRENT_TIMESTAMP not null;
alter table queue_user
    change create_at created_at timestamp default CURRENT_TIMESTAMP not null;
alter table task
    change create_at created_at timestamp default CURRENT_TIMESTAMP not null;
alter table user
    change create_at created_at timestamp default CURRENT_TIMESTAMP not null;
alter table user_idp
    change create_at created_at timestamp default CURRENT_TIMESTAMP not null;
