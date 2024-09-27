DROP TABLE IF EXISTS widgets;

CREATE TABLE widgets
(
    id      SERIAL NOT NULL PRIMARY KEY,
    name    TEXT,
    purpose TEXT
);