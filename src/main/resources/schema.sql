DROP TABLE IF EXISTS widgets;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS authors;

CREATE TABLE widgets
(
    id      SERIAL NOT NULL PRIMARY KEY,
    name    TEXT,
    purpose TEXT
);

CREATE TABLE authors
(
    id   SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    age  INTEGER
);

CREATE TABLE books
(
    isbn      TEXT PRIMARY KEY,
    title     TEXT                            NOT NULL,
    author_id INTEGER REFERENCES authors (id) NOT NULL
);

