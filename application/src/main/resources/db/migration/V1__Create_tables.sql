/*
Created		08.11.2022
Modified		04.12.2022
Project
Model
Company
Author
Version
Database		PostgreSQL 8
*/



/* Drop Referential Integrity Triggers */





/* Drop User-Defined Triggers */



/* Drop Domains */



/* Drop Procedures */



/* Drop Views */



/* Drop Indexes */



/* Drop Tables */
Drop table if exists security_user_nac_role Restrict;
Drop table if exists security_user_security_role Restrict;
Drop table if exists security_role Restrict;
Drop table if exists security_user Restrict;
Drop table if exists nac_role_network_resource Restrict;
Drop table if exists network_resource Restrict;
Drop table if exists user_device_alerts Restrict;
Drop table if exists user_device_block_info Restrict;
Drop table if exists nac_role Restrict;
Drop table if exists user_device Restrict;



/* Create Domains */



/* Create User-Defined Types */



/* Create Sequences */



/* Create Tables */


Create table user_device
(
    mac_address Macaddr NOT NULL UNIQUE,
    hostname Varchar,
    ip_address Inet,
    security_user_id  UUID NOT NULL,
    primary key (mac_address)
);

Create table nac_role
(
    role_id Serial NOT NULL UNIQUE,
    name Varchar NOT NULL UNIQUE,
    primary key (role_id)
);

Create table user_device_block_info
(
    mac_address Macaddr NOT NULL UNIQUE,
    user_banned Boolean NOT NULL,
    last_ban_timestamp Timestamp with time zone,
    last_unban_timestamp Timestamp with time zone,
    primary key (mac_address)
);

Create table user_device_alerts
(
    message_id Integer NOT NULL UNIQUE,
    mac_address Macaddr NOT NULL,
    alert_message Varchar NOT NULL,
    primary key (message_id,mac_address)
);

Create table network_resource
(
    resource_port Integer NOT NULL UNIQUE,
    name Varchar NOT NULL,
    primary key (resource_port)
);

Create table nac_role_network_resource
(
    role_id Integer NOT NULL,
    resource_port Integer NOT NULL,
    primary key (role_id,resource_port)
);

Create table security_user
(
    id  UUID NOT NULL UNIQUE,
    username Varchar NOT NULL UNIQUE,
    password Varchar NOT NULL,
    primary key (id)
);

Create table security_role
(
    id Serial NOT NULL UNIQUE,
    name Varchar NOT NULL UNIQUE,
    primary key (id)
);

Create table security_user_security_role
(
    user_id  UUID NOT NULL,
    role_id Integer NOT NULL,
    primary key (user_id,role_id)
);

Create table security_user_nac_role
(
    user_id  UUID NOT NULL,
    role_id Integer NOT NULL,
    primary key (user_id,role_id)
);


/* Create Tab 'Others' for Selected Tables */


/* Create Alternate Keys */



/* Create Indexes */



/* Create Foreign Keys */

Alter table user_device_alerts add  foreign key (mac_address) references user_device (mac_address) on update restrict on delete restrict;

Alter table nac_role_network_resource add  foreign key (role_id) references nac_role (role_id) on update restrict on delete restrict;

Alter table security_user_nac_role add  foreign key (role_id) references nac_role (role_id) on update restrict on delete restrict;

Alter table nac_role_network_resource add  foreign key (resource_port) references network_resource (resource_port) on update restrict on delete restrict;

Alter table security_user_security_role add  foreign key (user_id) references security_user (id) on update restrict on delete restrict;

Alter table user_device add  foreign key (security_user_id) references security_user (id) on update restrict on delete restrict;

Alter table security_user_nac_role add  foreign key (user_id) references security_user (id) on update restrict on delete restrict;

Alter table security_user_security_role add  foreign key (role_id) references security_role (id) on update restrict on delete restrict;



/* Create Procedures */



/* Create Views */



/* Create Referential Integrity Triggers */





/* Create User-Defined Triggers */



/* Create Groups */



/* Add Users To Groups */



/* Create Group Permissions */
/* Group permissions on tables */

/* Group permissions on views */

/* Group permissions on procedures */



/* Create User Permissions */
/* User permissions on tables */

/* User permissions on views */

/* User permissions on procedures */






