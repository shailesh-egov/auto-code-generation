#!/bin/bash

# Script to migrate database schema for Bank Account Service

set -e

if [ -z "$DB_URL" ]; then
    echo "DB_URL environment variable is not set"
    exit 1
fi

if [ -z "$DB_USER" ]; then
    echo "DB_USER environment variable is not set"
    exit 1
fi

if [ -z "$DB_PASSWORD" ]; then
    echo "DB_PASSWORD environment variable is not set"
    exit 1
fi

echo "Starting database migration for Bank Account Service..."

flyway -url="$DB_URL" -user="$DB_USER" -password="$DB_PASSWORD" -locations=filesystem:/flyway/sql migrate

echo "Database migration completed successfully!"