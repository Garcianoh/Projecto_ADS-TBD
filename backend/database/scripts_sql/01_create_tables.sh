#!/bin/bash
sqlplus boleia/boleia@FREEPDB1 << EOF
$(cat /docker-entrypoint-initdb.d/01_create_tables.sql)
EOF