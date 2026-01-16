create table queue_provider (
    queue_id int,
    provider_id int,
    primary key (queue_id, provider_id),
    foreign key (queue_id) references queue(id),
    foreign key (provider_id) references provider(id)
);
