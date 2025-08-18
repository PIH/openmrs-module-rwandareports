DELIMITER //

DROP PROCEDURE IF EXISTS sp_mamba_fact_service_revenue_report_create;

CREATE PROCEDURE sp_mamba_fact_service_revenue_report_create()
BEGIN

SET session group_concat_max_len = 20000;
SET @service_columns := NULL;

SELECT GROUP_CONCAT(DISTINCT CONCAT('`', hop_service_id, '` DECIMAL(20, 2)'))
INTO @service_columns
FROM mamba_fact_patient_service_bill;

IF @service_columns IS NULL THEN
        SET @create_table = CONCAT(
                'CREATE TABLE mamba_fact_service_revenue (
                        patient_service_bill_id INT NOT NULL,
                        service_id INT NULL,
                        service_date DATE NULL,
                        service VARCHAR(150) NULL,
                        AMBULANCE DECIMAL(25,2),
                        APPAREILLAGE_ORTHOPEDIQUE DECIMAL(25,2),
                        CHIRURGIE  DECIMAL(25,2),
                        CONSOMMABLES  DECIMAL(25,2),
                        ANESTHESIE  DECIMAL(25,2),
                        CONSULTATION  DECIMAL(25,2),
                        DERMATOLOGY  DECIMAL(25,2),
                        INTERNAL_MEDECINE  DECIMAL(25,2),
                        OBSTETRICS_GYNECOLOGY  DECIMAL(25,2),
                        OPHTHALMOLOGY  DECIMAL(25,2),
                        OTORHINOLARYNGOLOGIE  DECIMAL(25,2),
                        PEDIATRICS  DECIMAL(25,2),
                        STOMATOLOGY  DECIMAL(25,2),
                        SURGERY  DECIMAL(25,2),
                        ECHOGRAPHIE  DECIMAL(25,2),
                        LABORATOIRE  DECIMAL(25,2),
                        MEDICAMENTS  DECIMAL(25,2),
                        OPHTALMOLOGIE DECIMAL(25,2)

                    -- Unique constraints
                    -- constraint first_closing_date_id unique (first_closing_date_id),

                    -- Indexes
                    -- INDEX mamba_fact_service_revenue_patient_service_bill_id_index (patient_service_bill_id)),
                    -- INDEX mamba_fact_service_revenue_service_id_index (service_id)
                )'
            );

ELSE
        SET @create_table = CONCAT(
                 'CREATE TABLE mamba_fact_service_revenue (
                        patient_service_bill_id INT NOT NULL,
                        service_id INT NULL,
                        service_date DATE NULL,
                        service VARCHAR(150) NULL,
                        AMBULANCE DECIMAL(25,2),
                        APPAREILLAGE_ORTHOPEDIQUE DECIMAL(25,2),
                        CHIRURGIE  DECIMAL(25,2),
                        CONSOMMABLES  DECIMAL(25,2),
                        ANESTHESIE  DECIMAL(25,2),
                        CONSULTATION  DECIMAL(25,2),
                        DERMATOLOGY  DECIMAL(25,2),
                        INTERNAL_MEDECINE  DECIMAL(25,2),
                        OBSTETRICS_GYNECOLOGY  DECIMAL(25,2),
                        OPHTHALMOLOGY  DECIMAL(25,2),
                        OTORHINOLARYNGOLOGIE  DECIMAL(25,2),
                        PEDIATRICS  DECIMAL(25,2),
                        STOMATOLOGY  DECIMAL(25,2),
                        SURGERY  DECIMAL(25,2),
                        ECHOGRAPHIE  DECIMAL(25,2),
                        LABORATOIRE  DECIMAL(25,2),
                        MEDICAMENTS  DECIMAL(25,2),
                        OPHTALMOLOGIE DECIMAL(25,2)

                    -- Unique constraints
                    -- constraint patient_service_bill_id unique (patient_service_bill_id),

                    -- Indexes
                    -- INDEX mamba_fact_service_revenue_patient_service_bill_id_index (patient_service_bill_id)),
                    -- INDEX mamba_fact_service_revenue_service_id_index (service_id)
                )'
            );
END IF;

DROP TABLE IF EXISTS mamba_fact_service_revenue;

PREPARE createtb FROM @create_table;
EXECUTE createtb;
DEALLOCATE PREPARE createtb;

END //

DELIMITER ;