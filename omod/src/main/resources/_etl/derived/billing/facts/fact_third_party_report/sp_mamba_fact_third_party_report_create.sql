DELIMITER //

DROP PROCEDURE IF EXISTS sp_mamba_fact_third_party_report_create;

CREATE PROCEDURE sp_mamba_fact_third_party_report_create()
BEGIN

SET session group_concat_max_len = 20000;
SET @service_columns := NULL;

SELECT GROUP_CONCAT(DISTINCT CONCAT('`', hop_service_id, '` DECIMAL(20, 2)'))
INTO @service_columns
FROM mamba_fact_patient_service_bill;

IF @service_columns IS NULL THEN
        SET @create_table = CONCAT(
                'CREATE TABLE mamba_fact_thirdparty (
                        admission_date DATETIME NULL,
                        card_number VARCHAR(255) NULL,
                        age INT NULL,
                        gender CHAR NULL,
                        beneficiary_name TEXT NULL,
                        company_name VARCHAR(255) NULL,
                        MEDICAMENTS DECIMAL (20,2) NULL,
                        CONSULTATION DECIMAL (20,2) NULL,
                        HOSPITALISATION DECIMAL (20,2) NULL,
                        LABORATOIRE DECIMAL (20,2) NULL,
                        FORMALITES_ADMINISTRATIVES DECIMAL (20,2) NULL,
                        AMBULANCE DECIMAL (20,2) NULL,
                        CONSOMMABLES DECIMAL (20,2) NULL,
                        OXYGENOTHERAPIE DECIMAL (20,2) NULL,
                        IMAGING DECIMAL (21,2) NULL,
                        PROCED DECIMAL (25,2) NULL,
                        amount_100_percent DECIMAL (20,2) NULL,
                        insurance_amount DOUBLE NULL,
                        third_party_amount DOUBLE NULL,

                    -- Unique constraints
                    -- constraint first_closing_date_id unique (first_closing_date_id),

                    -- Indexes
                    INDEX mamba_fact_thirdparty_card_number_index (card_number))'
                   -- INDEX mamba_fact_thirdparty_first_closing_date_id_index (first_closing_date_id))'
            );

ELSE
        SET @create_table = CONCAT(
                 'CREATE TABLE mamba_fact_thirdparty (
                        admission_date DATETIME NULL,
                        card_number VARCHAR(255) NULL,
                        age INT NULL,
                        gender CHAR NULL,
                        beneficiary_name TEXT NULL,
                        company_name VARCHAR(255) NULL,
                        MEDICAMENTS DECIMAL (20,2) NULL,
                        CONSULTATION DECIMAL (20,2) NULL,
                        HOSPITALISATION DECIMAL (20,2) NULL,
                        LABORATOIRE DECIMAL (20,2) NULL,
                        FORMALITES_ADMINISTRATIVES DECIMAL (20,2) NULL,
                        AMBULANCE DECIMAL (20,2) NULL,
                        CONSOMMABLES DECIMAL (20,2) NULL,
                        OXYGENOTHERAPIE DECIMAL (20,2) NULL,
                        IMAGING DECIMAL (21,2) NULL,
                        PROCED DECIMAL (25,2) NULL,
                        amount_100_percent DECIMAL (20,2) NULL,
                        insurance_amount DOUBLE NULL,
                        third_party_amount DOUBLE NULL,

                    -- Unique constraints
                    -- constraint first_closing_date_id unique (first_closing_date_id),

                    -- Indexes
                    INDEX mamba_fact_thirdparty_card_number_index (card_number))'
                    --INDEX mamba_fact_thirdparty_first_closing_date_id_index (first_closing_date_id))'
            );
END IF;

DROP TABLE IF EXISTS `mamba_fact_thirdparty`;

PREPARE createtb FROM @create_table;
EXECUTE createtb;
DEALLOCATE PREPARE createtb;

END //

DELIMITER ;