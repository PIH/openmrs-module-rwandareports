DELIMITER //

DROP PROCEDURE IF EXISTS sp_mamba_fact_thirdparty_report_query;

CREATE PROCEDURE sp_mamba_fact_thirdparty_report_query(
    IN company_name VARCHAR(30),
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

SET @select_stmt = CONCAT('INSERT INTO mamba_fact_thirdparty SELECT
        bill.first_closing_date_id,
        bill.admission_date,
        bill.card_number,
        bill.age,
        bill.gender,
        bill.beneficiary_name,
        bill.company_name, ',
        @insurance_report_columns, ',
        (', @imaging_report_columns, ') AS IMAGING,
        (', @proced_report_columns, ') AS PROCED,
        d.amount AS amount_100_percent,
        (d.amount * c.rate) / 100 AS insurance_amount,
        (d.amount * (100 - c.rate)) / 100 AS third_party_amount
    FROM mamba_fact_patient_service_bill_flat bill
    INNER JOIN mamba_dim_consommation b ON  bill.global_bill_id = b.global_bill_id
    INNER JOIN mamba_dim_patient_bill d ON d.patient_bill_id =b.patient_bill_id
    INNER JOIN mamba_dim_insurance_rate c ON  bill.insurance_id = c.insurance_id
    WHERE bill.company_name = ''', @company_name,'''
      AND bill.admission_date BETWEEN ''', @start_date,
     ''' AND ''', @end_date, ''';'
);

    PREPARE select_stmt FROM @select_stmt;
    EXECUTE select_stmt;
    DEALLOCATE PREPARE select_stmt;

END //

DELIMITER ;