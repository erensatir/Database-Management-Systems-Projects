create table if not exists sample(
    id serial PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    data text,
    value int default 0
);

create table if not exists company(
    name VARCHAR(64) PRIMARY KEY,
    country VARCHAR(64),
    zip INT,
    city VARCHAR(64),
    streetInfo text,
    phoneNumber VARCHAR(64) UNIQUE NOT NULL
);

create table if not exists emails(
    name VARCHAR(64),
    email VARCHAR(64),
    PRIMARY KEY (name, email),
    FOREIGN KEY (name) REFERENCES company(name)
);

create table if not exists product(
    id serial PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    brandName VARCHAR(64),
    description VARCHAR(64)
);

create table if not exists produce(
    id serial,
    name VARCHAR(64) NOT NULL,
    product_id INT NOT NULL,
    capacity INT,
    PRIMARY KEY (id),
    FOREIGN KEY (name) REFERENCES company(name),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

create table if not exists transaction(
    id serial,
    name VARCHAR(64) NOT NULL,
    product_id INT NOT NULL,
    amount INT,
    createdDate DATE,
    PRIMARY KEY (id),
    FOREIGN KEY (name) REFERENCES company(name),
    FOREIGN KEY (product_id) REFERENCES product(id)
);


create FUNCTION sample_trigger() RETURNS TRIGGER AS
'
    BEGIN
        IF (SELECT value FROM sample where id = NEW.id ) > 1000
           THEN
           RAISE SQLSTATE ''23503'';
           END IF;
        RETURN NEW;
    END;
' LANGUAGE plpgsql;

create TRIGGER sample_value AFTER insert ON sample
    FOR EACH ROW EXECUTE PROCEDURE sample_trigger();

create FUNCTION transaction_trigger() RETURNS TRIGGER AS
'
 BEGIN
    IF(SELECT SUM(amount) FROM transaction WHERE name= NEW.name AND product_id=NEW.product_id) >
        (SELECT capacity FROM produce WHERE name=NEW.name AND product_id=NEW.product_id)
        THEN
        RAISE SQLSTATE ''23503'';
        END IF;
        RETURN NEW;
    END;

'LANGUAGE plpgsql;

create TRIGGER order_value AFTER insert ON transaction
    FOR EACH ROW EXECUTE PROCEDURE transaction_trigger()


