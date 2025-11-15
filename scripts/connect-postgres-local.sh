#!/bin/bash
# Connect to PostgreSQL in Docker Compose
docker exec -it postgres psql -U postgres -d procurement_auth_db

