DROP TABLE IF EXISTS users;
CREATE TABLE users (
                     user_name VARCHAR(256) PRIMARY KEY,
                     name VARCHAR(256) NOT NULL,
                     email VARCHAR(256) NOT NULL,
                     gender VARCHAR(256) NOT NULL,
                     picture_url VARCHAR(256) NOT NULL
);