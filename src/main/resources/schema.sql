-- This schema.sql file is used to initialize the database if needed
-- It will only run if spring.sql.init.mode=always is set in application.properties

-- Check if the users table exists, if not create it
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(120) NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    role VARCHAR(20) NOT NULL
);

-- Create sequence for user_id if it doesn't exist
CREATE SEQUENCE IF NOT EXISTS users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Add a comment to verify the script ran
COMMENT ON TABLE users IS 'Table for storing user information in the LifeLink system';