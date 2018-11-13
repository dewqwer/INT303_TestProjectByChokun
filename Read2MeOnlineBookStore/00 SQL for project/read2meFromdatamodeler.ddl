-- Generated by Oracle SQL Developer Data Modeler 18.1.0.082.1035
--   at:        2018-11-05 21:15:11 ICT
--   site:      Oracle Database 11g
--   type:      Oracle Database 11g



CREATE TABLE address (
    addressid     INTEGER NOT NULL,
    addressno     VARCHAR2(20 CHAR) NOT NULL,
    street        VARCHAR2(40 CHAR) NOT NULL,
    alley         VARCHAR2(40 CHAR) NOT NULL,
    subdistrict   VARCHAR2(40 CHAR) NOT NULL,
    district      VARCHAR2(40 CHAR) NOT NULL,
    province      VARCHAR2(40 CHAR) NOT NULL,
    postcode      VARCHAR2(20 CHAR) NOT NULL,
    customerid    INTEGER NOT NULL
);

ALTER TABLE address ADD CONSTRAINT address_pk PRIMARY KEY ( addressid );

CREATE TABLE book (
    isbn              INTEGER NOT NULL,
    title             VARCHAR2(40 CHAR) NOT NULL,
    author            VARCHAR2(40 CHAR) NOT NULL,
    description       VARCHAR2(100 CHAR) NOT NULL,
    publisher         VARCHAR2(40 CHAR) NOT NULL,
    category          VARCHAR2(100 CHAR) NOT NULL,
    unitpriceperone   INTEGER NOT NULL,
    salegroup         VARCHAR2(60 CHAR)
);

ALTER TABLE book ADD CONSTRAINT book_pk PRIMARY KEY ( isbn );

ALTER TABLE book ADD CONSTRAINT book_title_un UNIQUE ( title );

CREATE TABLE customer (
    customerid   INTEGER NOT NULL,
    firstname    VARCHAR2(40 CHAR) NOT NULL,
    lastname     VARCHAR2(40 CHAR) NOT NULL,
    phoneno      VARCHAR2(15 CHAR) NOT NULL,
    email        VARCHAR2(60 CHAR) NOT NULL,
    password     VARCHAR2(30 CHAR) NOT NULL
);

ALTER TABLE customer ADD CONSTRAINT customer_pk PRIMARY KEY ( customerid );

ALTER TABLE customer ADD CONSTRAINT customer_email_un UNIQUE ( email );

CREATE TABLE lineitem (
    quantity             INTEGER NOT NULL,
    totalpricelineitem   INTEGER  NOT NULL,
    orderid              INTEGER NOT NULL,
    isbn                 INTEGER NOT NULL
);

ALTER TABLE lineitem ADD CONSTRAINT lineitem_pk PRIMARY KEY ( orderid,
                                                              isbn );

CREATE TABLE "ORDER" (
    orderid       INTEGER NOT NULL,
    ordereddate   DATE NOT NULL,
    orderstatus   VARCHAR2(30 CHAR) NOT NULL,
    customerid    INTEGER NOT NULL,
);
ALTER TABLE "ORDER" ADD CONSTRAINT order_pk PRIMARY KEY ( orderid );

CREATE TABLE payment (
    paymentid    INTEGER NOT NULL,
    method       VARCHAR2(30 CHAR) NOT NULL,
    totalprice   INTEGER NOT NULL,
    orderid      INTEGER NOT NULL
);
ALTER TABLE payment ADD CONSTRAINT payment_pk PRIMARY KEY ( paymentid );
ALTER TABLE payment ADD CONSTRAINT payment_orderid_un UNIQUE ( orderid );

CREATE TABLE productreview (
    reviewid    INTEGER NOT NULL,
    "comment"   VARCHAR2(50 CHAR),
    rating      INTEGER NOT NULL,
    isbn        INTEGER NOT NULL,
    customerid  INTEGER NOT NULL
);

ALTER TABLE productreview ADD CONSTRAINT productreview_pk PRIMARY KEY ( reviewid );

CREATE TABLE shipping (
    shippingid     INTEGER NOT NULL,
    shippingcost   INTEGER NOT NULL,
    shippingdate   DATE NOT NULL,
    status         VARCHAR2(30 CHAR) NOT NULL,
    orderid        INTEGER NOT NULL,
    addressid      INTEGER NOT NULL
);
ALTER TABLE shipping ADD CONSTRAINT shipping_pk PRIMARY KEY ( shippingid );
ALTER TABLE shipping ADD CONSTRAINT shipping_orderid_un UNIQUE ( orderid );

ALTER TABLE address
    ADD CONSTRAINT address_customer_fk FOREIGN KEY ( customerid )
        REFERENCES customer ( customerid );

ALTER TABLE lineitem
    ADD CONSTRAINT lineitem_book_fk FOREIGN KEY ( isbn )
        REFERENCES book ( isbn );

ALTER TABLE lineitem
    ADD CONSTRAINT lineitem_order_fk FOREIGN KEY ( orderid )
        REFERENCES "ORDER" ( orderid );

ALTER TABLE "ORDER"
    ADD CONSTRAINT order_customer_fk FOREIGN KEY ( customerid )
        REFERENCES customer ( customerid );

ALTER TABLE payment
    ADD CONSTRAINT payment_order_fk FOREIGN KEY ( orderid )
        REFERENCES "ORDER" ( orderid );

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
        REFERENCES "ORDER" ( orderid );



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