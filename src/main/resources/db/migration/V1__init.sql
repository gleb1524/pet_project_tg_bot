create table users
(
    id      bigserial primary key,
    name    varchar(36) not null,
    surname varchar(56)
);

create table currency
(
    iso_code    bigserial primary key UNIQUE,
    code        varchar,
    rate_to_rub decimal,
    created_at  timestamp default current_timestamp,
    updated_at  timestamp default current_timestamp,
    CONSTRAINT id UNIQUE (iso_code)
);

create table user_options
(
    id          bigserial primary key,
    user_id     bigserial references users (id),
    currency_id bigserial references currency (iso_code),
    created_at  timestamp default current_timestamp,
    updated_at  timestamp default current_timestamp
);

create table currency_rate
(
    target_currency_iso_code bigserial,
    source_currency_iso_code bigserial,
    id                       varchar primary key unique not null,
    rate                     decimal,
    created_at               timestamp default current_timestamp,
    updated_at               timestamp default current_timestamp
);

create table currency_rate_order
(
    id           bigserial primary key,
    user_id      bigserial references users (id),
    currency_id  bigserial references currency (iso_code),
    desired_rate decimal
);

ALTER TABLE currency_rate
    ADD CONSTRAINT FK_TARGET_CURRENCY_ON_CR FOREIGN KEY (target_currency_iso_code) REFERENCES currency (iso_code);

ALTER TABLE currency_rate
    ADD CONSTRAINT FK_SOURCE_CURRENCY_ON_CR FOREIGN KEY (source_currency_iso_code) REFERENCES currency (iso_code);
