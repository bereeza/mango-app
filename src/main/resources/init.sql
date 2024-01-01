CREATE DATABASE mango_app;

\c mango_app;

-- delete tables
delete
from users;
delete
from posts;
delete
from comments;

SELECT *
FROM users;

SELECT *
FROM posts;

SELECT *
FROM comments;

CREATE TABLE IF NOT EXISTS users
(
    id       SERIAL PRIMARY KEY,
    email    VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(255) NOT NULL,
    UNIQUE (id, email, nickname)
);

CREATE TABLE IF NOT EXISTS posts
(
    id          SERIAL PRIMARY KEY UNIQUE,
    user_id     INTEGER REFERENCES users (id),
    description TEXT NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS comments
(
    id         SERIAL PRIMARY KEY UNIQUE,
    user_id    INTEGER REFERENCES users (id),
    post_id    INTEGER REFERENCES posts (id),
    comment    TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);