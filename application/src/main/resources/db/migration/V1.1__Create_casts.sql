DROP CAST IF EXISTS (varchar AS macaddr);
DROP CAST IF EXISTS (varchar AS inet);
CREATE CAST (varchar AS macaddr) WITH INOUT AS IMPLICIT;
CREATE CAST (varchar AS inet) WITH INOUT AS IMPLICIT;