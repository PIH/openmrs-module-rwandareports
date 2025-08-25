DELIMITER //

DROP PROCEDURE IF EXISTS sp_mamba_fact_service_revenue_report_insert;

CREATE PROCEDURE sp_mamba_fact_service_revenue_report_insert()

BEGIN

SET session group_concat_max_len = 20000;


SET @select_stmt = CONCAT('INSERT INTO mamba_fact_service_revenue
    SELECT
        mdpsb.patient_service_bill_id AS patient_service_bill_id,
        mdhs.service_id AS service_id,
        mdpsb.service_date AS service_date,
        mdsc.name AS `service`,
        SUM(CASE WHEN mdhs.name = ''AMBULANCE'' THEN (mdpsb.paid_quantity * mdpsb.unit_price) ELSE 0 END) AS `AMBULANCE`,
        SUM(CASE WHEN mdhs.name = ''APPAREILLAGE ORTHOPEDIQUE'' THEN (mdpsb.paid_quantity * mdpsb.unit_price) ELSE 0 END) AS `APPAREILLAGE ORTHOPEDIQUE`,
        SUM(CASE WHEN mdhs.name = ''CHIRURGIE'' THEN (mdpsb.paid_quantity * mdpsb.unit_price) ELSE 0 END) AS `CHIRURGIE`,
        SUM(CASE WHEN mdhs.name = ''CONSOMMABLES'' THEN (mdpsb.paid_quantity * mdpsb.unit_price) ELSE 0 END) AS `CONSOMMABLES`,
        SUM(CASE WHEN mdhs.name = ''DCP ANESTHESIE'' THEN (mdpsb.paid_quantity * mdpsb.unit_price) ELSE 0 END) AS `DCP ANESTHESIE`,
        SUM(CASE WHEN mdhs.name = ''DCP CONSULTATION'' THEN (mdpsb.paid_quantity * mdpsb.unit_price) ELSE 0 END) AS `DCP CONSULTATION`,
        SUM(CASE WHEN mdhs.name = ''DCP DERMATOLOGY'' THEN (mdpsb.paid_quantity * mdpsb.unit_price) ELSE 0 END) AS `DCP DERMATOLOGY`,
        SUM(CASE WHEN mdhs.name = ''DCP INTERNAL MEDECINE'' THEN (mdpsb.paid_quantity * mdpsb.unit_price) ELSE 0 END) AS `DCP INTERNAL MEDECINE`,
        SUM(CASE WHEN mdhs.name = ''DCP OBSTETRICS-GYNECOLOGY'' THEN (mdpsb.paid_quantity * mdpsb.unit_price) ELSE 0 END) AS `DCP OBSTETRICS-GYNECOLOGY`,
        SUM(CASE WHEN mdhs.name = ''DCP OPHTHALMOLOGY'' THEN (mdpsb.paid_quantity * mdpsb.unit_price) ELSE 0 END) AS `DCP OPHTHALMOLOGY`,
        SUM(CASE WHEN mdhs.name = ''DCP OTORHINOLARYNGOLOGIE (ENT/ORL)'' THEN (mdpsb.paid_quantity * mdpsb.unit_price) ELSE 0 END) AS `DCP OTORHINOLARYNGOLOGIE (ENT/ORL)`,
        SUM(CASE WHEN mdhs.name = ''DCP PEDIATRICS'' THEN (mdpsb.paid_quantity * mdpsb.unit_price) ELSE 0 END) AS `DCP PEDIATRICS`,
        SUM(CASE WHEN mdhs.name = ''DCP STOMATOLOGY'' THEN (mdpsb.paid_quantity * mdpsb.unit_price) ELSE 0 END) AS `DCP STOMATOLOGY`,
        SUM(CASE WHEN mdhs.name = ''DCP SURGERY'' THEN (mdpsb.paid_quantity * mdpsb.unit_price) ELSE 0 END) AS `DCP SURGERY`,
        SUM(CASE WHEN mdhs.name = ''ECHOGRAPHIE'' THEN (mdpsb.paid_quantity * mdpsb.unit_price) ELSE 0 END) AS `ECHOGRAPHIE`,
        SUM(CASE WHEN mdhs.name = ''LABORATOIRE'' THEN (mdpsb.paid_quantity * mdpsb.unit_price) ELSE 0 END) AS `LABORATOIRE`,
        SUM(CASE WHEN mdhs.name = ''MEDICAMENTS'' THEN (mdpsb.paid_quantity * mdpsb.unit_price) ELSE 0 END) AS `MEDICAMENTS`,
        SUM(CASE WHEN mdhs.name = ''OPHTALMOLOGIE'' THEN (mdpsb.paid_quantity * mdpsb.unit_price) ELSE 0 END) AS `OPHTALMOLOGIE`
    FROM mamba_dim_patient_service_bill mdpsb
        LEFT JOIN mamba_dim_hop_service mdhs
            ON mdpsb.service_id = mdhs.service_id
        LEFT JOIN mamba_dim_billable_service mdbs
            ON mdbs.billable_service_id = mdpsb.billable_service_id
        LEFT JOIN mamba_dim_service_category mdsc
            ON mdsc.service_category_id = mdbs.service_category_id
    WHERE mdhs.name <> ''ALL''
      AND mdsc.name IS NOT NULL
    GROUP BY
        mdsc.name,
        mdpsb.patient_service_bill_id,
        mdhs.service_id,
        mdpsb.service_date'
);

PREPARE select_stmt FROM @select_stmt;
EXECUTE select_stmt;
DEALLOCATE PREPARE select_stmt;

END //

DELIMITER ;