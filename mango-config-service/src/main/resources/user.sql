CREATE SCHEMA IF NOT EXISTS mango;
SET search_path TO mango;

CREATE TABLE IF NOT EXISTS users
(
    id         SERIAL PRIMARY KEY  NOT NULL,
    email      VARCHAR(128) UNIQUE NOT NULL,
    first_name VARCHAR(64)         NOT NULL,
    last_name  VARCHAR(64)         NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    avatar     VARCHAR(255),
    cv         VARCHAR(255),
    about      VARCHAR(2000),
    reputation BIGINT DEFAULT 0,
    links      VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS company
(
    id          SERIAL PRIMARY KEY NOT NULL,
    name        VARCHAR(255)       NOT NULL,
    description VARCHAR(2000),
    website     VARCHAR(255),
    logo        VARCHAR(255),
    created_at  TIMESTAMP,
    ceo_id      INT                NOT NULL,
    FOREIGN KEY (ceo_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS company_employees
(
    id         SERIAL PRIMARY KEY NOT NULL,
    company_id INT                NOT NULL,
    user_id    INT                NOT NULL,
    user_role  VARCHAR(255)       NOT NULL,
    FOREIGN KEY (company_id) REFERENCES company (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS vacancy
(
    id           SERIAL PRIMARY KEY NOT NULL,
    user_id      INT                NOT NULL,
    is_anonymous BOOLEAN   DEFAULT FALSE,
    company_id   INT                NOT NULL,
    title        VARCHAR(255)       NOT NULL,
    description  VARCHAR(5000)      NOT NULL,
    type         VARCHAR(15)        NOT NULL,
    salary       DECIMAL(10, 2),
    max_limit    INT                NOT NULL,
    created_at   TIMESTAMP,
    closed_at    TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES company (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS vacancy_applicants
(
    id               SERIAL PRIMARY KEY NOT NULL,
    vacancy_id       INT                NOT NULL,
    user_id          INT                NOT NULL,
    cover_letter     VARCHAR(100),
    application_date TIMESTAMP,
    FOREIGN KEY (vacancy_id) REFERENCES vacancy (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS vacancy_statistic
(
    id           SERIAL PRIMARY KEY NOT NULL,
    vacancy_id   INT UNIQUE         NOT NULL,
    views        BIGINT DEFAULT 0,
    applications BIGINT DEFAULT 0,
    FOREIGN KEY (vacancy_id) REFERENCES vacancy (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS post
(
    id         SERIAL PRIMARY KEY NOT NULL,
    user_id    INT                NOT NULL,
    text       VARCHAR(2000),
    photo_link VARCHAR(255),
    reputation BIGINT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comment
(
    id                SERIAL PRIMARY KEY NOT NULL,
    post_id           INT                NOT NULL,
    author_first_name VARCHAR(255)       NOT NULL,
    author_last_name  VARCHAR(255)       NOT NULL,
    author_avatar     VARCHAR(255)       NOT NULL,
    comment           VARCHAR(1000)      NOT NULL,
    created_at        TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE
);
