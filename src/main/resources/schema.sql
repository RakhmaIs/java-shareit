CREATE TABLE IF NOT EXISTS users
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_name VARCHAR(255)        NOT NULL,
    email     VARCHAR(512) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS items
(
    id               BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    item_name        VARCHAR(255)                                   NOT NULL,
    owner_id         BIGINT REFERENCES users (id) ON DELETE CASCADE NOT NULL,
    item_description VARCHAR(512)                                   NOT NULL,
    available        BOOLEAN,
    request_id       BIGINT
);
CREATE TABLE IF NOT EXISTS item_requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description  VARCHAR                     NOT NULL,
    requester_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL
    );

CREATE TABLE IF NOT EXISTS bookings
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_time     TIMESTAMP WITHOUT TIME ZONE                    NOT NULL,
    end_time       TIMESTAMP WITHOUT TIME ZONE                    NOT NULL,
    item_id        BIGINT REFERENCES items (id) ON DELETE CASCADE NOT NULL,
    booker_id      BIGINT REFERENCES users (id) ON DELETE CASCADE NOT NULL,
    booking_status VARCHAR(10)                                    NOT NULL
);

CREATE TABLE IF NOT EXISTS comments
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text_comment VARCHAR(1012)                                  NOT NULL,
    item_id      BIGINT REFERENCES items (id) ON DELETE CASCADE NOT NULL,
    user_id      BIGINT REFERENCES users (id) ON DELETE CASCADE NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE  DEFAULT NOW()     NOT NULL
);