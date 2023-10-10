-- $BEGIN

CREATE TABLE mamba_dim_global_bill
(
    id              INT          NOT NULL AUTO_INCREMENT,
    global_bill_id  INT          NOT NULL,
    admission_id    INT          NOT NULL,
    insurance_id    INT          null,
    bill_identifier varchar(250) NOT NULL,
    global_amount   DECIMAL      NOT NULL,
    closing_date    DATETIME     NULL,
    closed          TINYINT(1)   NOT NULL,
    closed_by_id    INT          NULL,
    closed_by_name  varchar(255) NULL,
    closed_reason   varchar(150) NULL,
    edited_by       INT          NULL,
    edit_reason     varchar(150) NULL,
    created_date    DATETIME     NOT NULL,

    PRIMARY KEY (id)
)
    CHARSET = UTF8MB4;

CREATE INDEX mamba_dim_global_bill_global_bill_id_index
    ON mamba_dim_global_bill (global_bill_id);

CREATE INDEX mamba_dim_global_bill_admission_id_index
    ON mamba_dim_global_bill (admission_id);

CREATE INDEX mamba_dim_global_bill_insurance_id_index
    ON mamba_dim_global_bill (insurance_id);

CREATE INDEX mamba_dim_global_bill_closed_index
    ON mamba_dim_global_bill (closed);

CREATE INDEX mamba_dim_global_bill_closed_by_id_index
    ON mamba_dim_global_bill (closed_by_id);

-- $END