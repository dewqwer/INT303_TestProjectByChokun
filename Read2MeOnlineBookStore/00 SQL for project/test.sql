ALTER DATABASE read2me CHARACTER SET utf8 COLLATE utf8_general_ci;
CREATE TABLE address (
    addressid     BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    addressno     VARCHAR(20) NOT NULL,
    street        VARCHAR(40) NOT NULL,
    alley         VARCHAR(40) NOT NULL,
    subdistrict   VARCHAR(40) NOT NULL,
    district      VARCHAR(40) NOT NULL,
    province      VARCHAR(40) NOT NULL,
    postcode      VARCHAR(20) NOT NULL,
    customerid    BIGINT NOT NULL
    
);
ALTER TABLE address ADD CONSTRAINT address_pk PRIMARY KEY ( addressid );
ALTER TABLE address.* CHARACTER SET utf8;

CREATE TABLE book (
    isbn              BIGINT NOT NULL ,
    title             VARCHAR(40) NOT NULL UNIQUE,
    author            VARCHAR(40) NOT NULL,
    description       VARCHAR(100) NOT NULL,
    publisher         VARCHAR(40) NOT NULL,
    category          VARCHAR(100) NOT NULL,
    unitpriceperone   BIGINT NOT NULL,
    salegroup         VARCHAR(60)
);
ALTER TABLE book ADD CONSTRAINT book_pk PRIMARY KEY ( isbn );

CREATE TABLE customer (
    customerid   BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    firstname    VARCHAR(40) NOT NULL,
    lastname     VARCHAR(40) NOT NULL,
    phoneno      VARCHAR(15) NOT NULL,
    email        VARCHAR(60) NOT NULL UNIQUE,
    password     VARCHAR(30) NOT NULL
);
ALTER TABLE customer ADD CONSTRAINT customer_pk PRIMARY KEY ( customerid );

CREATE TABLE lineitem (
    quantity             INTEGER NOT NULL,
    totalpricelineitem   BIGINT NOT NULL,
    orderid              BIGINT NOT NULL ,
    isbn                 BIGINT NOT NULL
);
ALTER TABLE lineitem ADD CONSTRAINT lineitem_pk PRIMARY KEY ( orderid , isbn );

CREATE TABLE "ORDERS" (
    orderid       BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    ordereddate   DATE NOT NULL,
    orderstatus   VARCHAR(30) NOT NULL,
    customerid    BIGINT NOT NULL 
);
ALTER TABLE "ORDERS" ADD CONSTRAINT order_pk PRIMARY KEY ( orderid );

CREATE TABLE payment (
    paymentid    BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    "method"     VARCHAR(30) NOT NULL,
    totalprice   BIGINT NOT NULL,
    orderid      BIGINT NOT NULL UNIQUE
);
ALTER TABLE payment ADD CONSTRAINT payment_pk PRIMARY KEY ( paymentid );

CREATE TABLE productreview (
    reviewid    BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    comment     VARCHAR(50),
    rating      INTEGER NOT NULL,
    isbn        BIGINT NOT NULL,
    customerid    BIGINT NOT NULL
);
ALTER TABLE productreview ADD CONSTRAINT productreview_pk PRIMARY KEY ( reviewid );

CREATE TABLE shipping (
    shippingid     BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    shippingcost   INTEGER NOT NULL,
    shippingdate   DATE NOT NULL,
    status         VARCHAR(30) NOT NULL,
    orderid        BIGINT NOT NULL UNIQUE ,
    addressid      BIGINT NOT NULL
);
ALTER TABLE shipping ADD CONSTRAINT shipping_pk PRIMARY KEY ( shippingid );

ALTER TABLE address
    ADD CONSTRAINT address_customer_fk FOREIGN KEY ( customerid )
        REFERENCES customer ( customerid );

ALTER TABLE lineitem
    ADD CONSTRAINT lineitem_book_fk FOREIGN KEY ( isbn )
        REFERENCES book ( isbn );

ALTER TABLE lineitem
    ADD CONSTRAINT lineitem_order_fk FOREIGN KEY ( orderid )
        REFERENCES "ORDERS" ( orderid );

ALTER TABLE "ORDERS"
    ADD CONSTRAINT order_customer_fk FOREIGN KEY ( customerid )
        REFERENCES customer ( customerid );

ALTER TABLE payment
    ADD CONSTRAINT payment_order_fk FOREIGN KEY ( orderid )
        REFERENCES "ORDERS" ( orderid );

ALTER TABLE productreview
    ADD CONSTRAINT productreview_book_fk FOREIGN KEY ( isbn )
        REFERENCES book ( isbn );

ALTER TABLE productreview
    ADD CONSTRAINT productreview_customer_fk FOREIGN KEY ( customerid )
        REFERENCES customer ( customerid );

ALTER TABLE shipping
    ADD CONSTRAINT shipping_address_fk FOREIGN KEY ( addressid )
        REFERENCES address ( addressid );

ALTER TABLE shipping
    ADD CONSTRAINT shipping_order_fk FOREIGN KEY ( orderid )
        REFERENCES "ORDERS" ( orderid );



-- Oracle SQL Developer Data Modeler Summary Report: 
-- 
-- CREATE TABLE                             9
-- CREATE INDEX                             6
-- ALTER TABLE                             24
-- CREATE VIEW                              0
-- ALTER VIEW                               0
-- CREATE PACKAGE                           0
-- CREATE PACKAGE BODY                      0
-- CREATE PROCEDURE                         0
-- CREATE FUNCTION                          0
-- CREATE TRIGGER                           0
-- ALTER TRIGGER                            0
-- CREATE COLLECTION TYPE                   0
-- CREATE STRUCTURED TYPE                   0
-- CREATE STRUCTURED TYPE BODY              0
-- CREATE CLUSTER                           0
-- CREATE CONTEXT                           0
-- CREATE DATABASE                          0
-- CREATE DIMENSION                         0
-- CREATE DIRECTORY                         0
-- CREATE DISK GROUP                        0
-- CREATE ROLE                              0
-- CREATE ROLLBACK SEGMENT                  0
-- CREATE SEQUENCE                          0
-- CREATE MATERIALIZED VIEW                 0
-- CREATE SYNONYM                           0
-- CREATE TABLESPACE                        0
-- CREATE USER                              0
-- 
-- DROP TABLESPACE                          0
-- DROP DATABASE                            0
-- 
-- REDACTION POLICY                         0
-- 
-- ORDS DROP SCHEMA                         0
-- ORDS ENABLE SCHEMA                       0
-- ORDS ENABLE OBJECT                       0
-- 
-- ERRORS                                   0
-- WARNINGS                                 0
