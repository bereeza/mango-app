CREATE TABLE IF NOT EXISTS _user
(
    userId   SERIAL PRIMARY KEY,
    email    VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255)        NOT NULL,
    avatar   VARCHAR(255)
);