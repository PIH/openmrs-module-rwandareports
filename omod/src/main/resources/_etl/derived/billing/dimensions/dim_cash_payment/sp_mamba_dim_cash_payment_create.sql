-- $BEGIN

CREATE TABLE IF NOT EXISTS mamba_dim_cash_payment
(
    cash_payment_id INT             NOT NULL AUTO_INCREMENT,
    creator         INT             NOT NULL,
    created_date    DATETIME        NOT NULL,
    voided          SMALLINT        NOT NULL,
    voided_date     DATETIME        NULL,
    void_reason     VARCHAR(250)    NULL,
    voided_by       INT             NULL

    PRIMARY KEY (cash_payment_id)
    )
    CHARSET = UTF8MB4;

CREATE INDEX  mamba_dim_cash_payment_cash_payment_id_index
    ON  mamba_dim_cash_payment (cash_payment_id);

-- $END