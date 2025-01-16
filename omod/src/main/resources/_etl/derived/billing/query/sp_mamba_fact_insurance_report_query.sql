DELIMITER //

DROP PROCEDURE IF EXISTS sp_mamba_fact_insurance_report_query;

CREATE PROCEDURE sp_mamba_fact_insurance_report_query(
    IN insurance_id INT,
    IN start_date DATETIME,
    IN end_date DATETIME)

BEGIN

    SET session group_concat_max_len = 20000;
    SET @insurance_report_columns := NULL;
    SET @imaging_report_columns := NULL;
    SET @proced_report_columns := NULL;

    SELECT GROUP_CONCAT(DISTINCT CONCAT('IFNULL (bill.`', hop_service_id, '`, 0) AS ', '`', column_name, '`') ORDER BY
                        id ASC SEPARATOR ', ')
    INTO @insurance_report_columns
    FROM mamba_dim_billing_report_columns
    WHERE report_type = 'INSURANCE'
      AND group_column_name = 'INSURANCE';

    -- Imaging Columns TODO: improve this area
    SELECT (GROUP_CONCAT(DISTINCT CONCAT('IFNULL (bill.`', hop_service_id, '`, 0)') ORDER BY
                         id ASC SEPARATOR ' + ')) AS 'group_column_name'
    INTO @imaging_report_columns
    FROM mamba_dim_billing_report_columns
    WHERE report_type = 'INSURANCE'
      AND group_column_name = 'IMAGING';

    -- Proceed Columns TODO: improve this area
    SELECT GROUP_CONCAT(DISTINCT CONCAT('IFNULL (bill.`', hop_service_id, '`, 0)') ORDER BY
                        id ASC SEPARATOR ' + ')
    INTO @proced_report_columns
    FROM mamba_dim_billing_report_columns
    WHERE report_type = 'INSURANCE'
      AND group_column_name = 'PROCED.';

    SET @select_stmt = CONCAT('SELECT bill.first_closing_date_id,
           bill.admission_date,
           bill.closing_date,
           bill.beneficiary_name,
           bill.household_head_name,
           bill.family_code,
           bill.beneficiary_level,
           bill.card_number,
           bill.company_name,
           bill.age,
           bill.birth_date,
           bill.gender,
           bill.doctor_name,
           bill.insurance_id,
           bill.global_bill_id,
           bill.global_bill_identifier,
           ', @insurance_report_columns, ',
           (', @imaging_report_columns, ') AS `IMAGING`,
           (', @proced_report_columns, ') AS `PROCED.`
        FROM mamba_fact_patient_service_bill_flat bill
    WHERE bill.insurance_id = ', insurance_id,
                              ' AND bill.admission_date BETWEEN ''', start_date, ''' AND ''', end_date, ''';');

    PREPARE select_stmt FROM @select_stmt;
    EXECUTE select_stmt;
    DEALLOCATE PREPARE select_stmt;

END //

DELIMITER ;