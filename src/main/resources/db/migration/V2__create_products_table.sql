CREATE TABLE categories
(
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    name       VARCHAR(50) NOT NULL,
    parent_id  BIGINT,
    created_at DATETIME    NOT NULL,
    updated_at DATETIME    NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (parent_id) REFERENCES categories (id)
);

CREATE TABLE products
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    price       INT          NOT NULL,
    description TEXT,
    image_url   VARCHAR(500),
    category_id BIGINT,
    status      VARCHAR(20)  NOT NULL,
    created_at  DATETIME     NOT NULL,
    updated_at  DATETIME     NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (category_id) REFERENCES categories (id)
);
