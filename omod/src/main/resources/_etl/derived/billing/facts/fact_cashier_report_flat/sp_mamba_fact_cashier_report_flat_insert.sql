DELIMITER //

DROP PROCEDURE IF EXISTS sp_mamba_fact_cashier_report_flat_insert;

CREATE PROCEDURE sp_mamba_fact_cashier_report_flat_insert()
BEGIN

SET session group_concat_max_len = 20000;
SET @service_columns_case := NULL;

SELECT GROUP_CONCAT(DISTINCT
      CONCAT(
        'SUM(CASE WHEN hop_service_id = "', hop_service_id,
        '" THEN amount_paid ELSE 0 END) AS `', hop_service_id, '`'
     ) SEPARATOR ', ')
INTO @service_columns_case
FROM mamba_fact_cashier_report;

-- TODO: see if first inserting into a sub-query table helps before joining. But also other reports that use hte service revenues but have different base columns can benefit from having a shared services revenue table instead of just sub-querying it here. It can just be a table of its own to be joined on
SET @insert_stmt = CONCAT('INSERT INTO mamba_fact_cashier_report_flat
  SELECT
    b.bill_payment_id,
    b.patient_bill_id,
    date,
    patient_name,
    s.*
  FROM (
    SELECT MIN(id) as first_date_id, global_bill_id, ', @service_columns_case, '
    FROM mamba_fact_cashier_report
    GROUP BY global_bill_id
 ) AS s
  LEFT JOIN mamba_fact_cashier_report b
    on s.global_bill_id = b.global_bill_id AND s.first_date_id = b.id;');

PREPARE inserttbl FROM @insert_stmt;
EXECUTE inserttbl;
DEALLOCATE PREPARE inserttbl;

END //

DELIMITER ;