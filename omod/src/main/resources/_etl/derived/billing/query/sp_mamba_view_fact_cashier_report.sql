
DELIMITER //

DROP PROCEDURE IF EXISTS sp_mamba_view_fact_cashier_report;

CREATE PROCEDURE sp_mamba_view_fact_cashier_report()

BEGIN

SET session group_concat_max_len = 20000;
SET @insurance_report_columns := NULL;


SELECT GROUP_CONCAT(DISTINCT CONCAT('IFNULL (cashier.`', hop_service_id, '`, 0) AS ', '`', column_name, '`') ORDER BY
                        id ASC SEPARATOR ', ')
INTO @insurance_report_columns
FROM mamba_dim_billing_report_columns;


SET @select_stmt = CONCAT('CREATE OR REPLACE VIEW mamba_view_fact_cashier_report AS
        SELECT cashier.first_date_id,
           cashier.date,
           cashier.bill_payment_id,
           cashier.patient_bill_id,
           cashier.patient_name,
           cashier.global_bill_id,
           ', @insurance_report_columns, '
        FROM mamba_fact_cashier_report_flat cashier;');

PREPARE select_stmt FROM @select_stmt;
EXECUTE select_stmt;
DEALLOCATE PREPARE select_stmt;

END //

DELIMITER ;
