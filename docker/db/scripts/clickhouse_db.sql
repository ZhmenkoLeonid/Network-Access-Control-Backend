create table USER
(
    IP_ADDRESS String
)
    engine = MergeTree ORDER BY IP_ADDRESS;
	
create table USER_BLACKLIST
(
    IP_ADDRESS String
)
    engine = MergeTree ORDER BY IP_ADDRESS;
	
create table USER_FLOW_DATA
(
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
    engine = MergeTree ORDER BY SOURCE_IP_ADDRESS;

create table USER_OPENED_PORT
(
    USER_ID String,
    PORT int
)
    engine = MergeTree ORDER BY USER_ID;