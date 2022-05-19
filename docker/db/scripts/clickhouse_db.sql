
create table USER_FLOW_DATA
(
    MAC_ADDRESS            String,
    HOSTNAME               String,
    NETFLOW_VERSION        String,
    SOURCE_IP_ADDRESS      String,
    DESTINATION_IP_ADDRESS String,
    SOURCE_PORT            Int32,
    DESTINATION_PORT       Int32,
    PROTOCOL_TYPE          String,
    TIMESTAMP              DateTime,
    TCP_FLAGS              String
)
    engine = MergeTree ORDER BY MAC_ADDRESS;

create table SECURITY_USER
(
    ID       UUID default generateUUIDv4(),
    USERNAME String,
    PASSWORD String,
    ROLES    Array(String)
)
    engine = MergeTree ORDER BY ID;

create table NAC_USER
(
    MAC_ADDRESS    String,
    HOSTNAME       Nullable(String) default 'UNDEFINED',
    IS_BLACKLISTED Int32            default 0,
    IP_ADDRESS     String,
    PORTS          Array(Int32) default [],
    ALERTS         Array(String) default []
)
    engine = MergeTree ORDER BY MAC_ADDRESS