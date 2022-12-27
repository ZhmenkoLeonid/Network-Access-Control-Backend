create sequence user_device_alerts_id_seq
    start with 1
    increment by 1;

ALTER TABLE user_device_alerts
ALTER COLUMN message_id set default nextval('user_device_alerts_id_seq');

ALTER SEQUENCE user_device_alerts_id_seq OWNED BY user_device_alerts.message_id;