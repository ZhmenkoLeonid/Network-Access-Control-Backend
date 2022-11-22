create table if not exists USER_FLOW_DATA
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