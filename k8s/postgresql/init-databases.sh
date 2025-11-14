#!/bin/bash
set -e

PGPASSWORD=$POSTGRES_PASSWORD psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE IF NOT EXISTS procurement_auth_db;
    CREATE DATABASE IF NOT EXISTS procurement_procurement_db;
    CREATE DATABASE IF NOT EXISTS procurement_quotation_db;
    CREATE DATABASE IF NOT EXISTS procurement_po_db;
    CREATE DATABASE IF NOT EXISTS procurement_inventory_db;
EOSQL

