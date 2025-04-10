-- $BEGIN

CREATE TABLE IF NOT EXISTS mamba_dim_bill_payment
(
    id              INT            NOT NULL AUTO_INCREMENT,
    bill_payment_id INT            NOT NULL,
    patient_bill_id int            not null,
    amount_paid     decimal(20, 2) not null,
    date_received   DATETIME       null,
    collector       int            not null,
    created_date    DATETIME       null DEFAULT '1900-01-01 00:00:00',

    PRIMARY KEY (id)
)
    CHARSET = UTF8MB4;

CREATE INDEX mamba_dim_bill_payment_bill_payment_id_index
    ON mamba_dim_bill_payment (bill_payment_id);

CREATE INDEX mamba_dim_bill_payment_patient_bill_id_index
    ON mamba_dim_bill_payment (patient_bill_id);

-- $END