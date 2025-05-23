DELIMITER //

DROP PROCEDURE IF EXISTS sp_mamba_view_fact_service_report;

CREATE PROCEDURE sp_mamba_view_fact_service_report()
BEGIN

    SET SESSION group_concat_max_len = 20000;
    -- Create or Replace View
    SET @select_stmt =
            'CREATE OR REPLACE VIEW mamba_view_fact_service_report AS
                SELECT
                    DISTINCT  patient_service_bill_id,
                    mdhs.service_id,service_date,
                    mdhs.name service,
                    paid_quantity * unit_price Amount
                FROM mamba_dim_patient_service_bill mdpsb
                INNER JOIN mamba_dim_hop_service mdhs ON mdpsb.service_id = mdhs.service_id
                LEFT JOIN mamba_dim_service_category mdsc  ON mdsc.service_id = mdhs.service_id;';

        -- Execute the dynamic SQL statement
    PREPARE select_stmt FROM @select_stmt;
    EXECUTE select_stmt;
    DEALLOCATE PREPARE select_stmt;

END //

DELIMITER ;