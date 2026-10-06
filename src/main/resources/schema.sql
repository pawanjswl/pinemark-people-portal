CREATE TABLE IF NOT EXISTS employee (
    id           BIGINT PRIMARY KEY,
    display_name VARCHAR(120) NOT NULL,
    department   VARCHAR(80)  NOT NULL,
    work_email   VARCHAR(160) NOT NULL
);
