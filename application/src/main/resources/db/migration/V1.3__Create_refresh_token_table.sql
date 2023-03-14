Create table refresh_token
(
    id Integer NOT NULL primary key,
    user_id uuid NOT NULL,
    token varchar NOT NULL,
    expiry_date timestamp NOT NULL
);

Alter table refresh_token add  foreign key (user_id) references security_user (id) on update restrict on delete restrict;