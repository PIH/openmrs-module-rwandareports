-- $BEGIN

CREATE TABLE IF NOT EXISTS mamba_dim_beneficiary
(
    id                  INT          NOT NULL AUTO_INCREMENT,
    beneficiary_id      INT          NOT NULL,
    patient_id          INT          NOT NULL,
    insurance_policy_id INT          NOT NULL,
    policy_id_number    VARCHAR(250) NULL,
    created_date        DATE         NOT NULL,
    creator             INT          NOT NULL,
    owner_name          VARCHAR(150) NULL,
    owner_code          VARCHAR(150) NULL,
    level               INT          NULL,
    company             VARCHAR(100) NULL,

    PRIMARY KEY (id)
)
    CHARSET = UTF8MB4;

CREATE INDEX mamba_dim_beneficiary_beneficiary_id_index
    ON mamba_dim_beneficiary (beneficiary_id);

CREATE INDEX mamba_dim_beneficiary_patient_id_index
    ON mamba_dim_beneficiary (patient_id);

CREATE INDEX mamba_dim_beneficiary_insurance_policy_id_index
    ON mamba_dim_beneficiary (insurance_policy_id);

CREATE INDEX mamba_dim_beneficiary_policy_id_number_index
    ON mamba_dim_beneficiary (policy_id_number);

-- $END