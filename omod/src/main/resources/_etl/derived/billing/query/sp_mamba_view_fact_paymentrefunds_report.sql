DELIMITER //

DROP PROCEDURE IF EXISTS sp_mamba_view_fact_paymentrefunds_report;

CREATE PROCEDURE sp_mamba_view_fact_paymentrefunds_report()
BEGIN

    SET SESSION group_concat_max_len = 20000;
    -- Create or Replace View
    SET @select_stmt =
        'CREATE OR REPLACE VIEW mamba_view_fact_paymentrefunds_report AS
        SELECT DISTINCT mdpr.refund_id,
                bill_payment_id payment_id,
                concat(mdpn.given_name, '''', mdpn.family_name)cashier_names,
                mdpr.created_date submitted_on,
                concat(mdpn1.given_name, '''', mdpn1.family_name)approved_by,
                concat(mdpn.given_name, '''', mdpn.family_name)confirmed_by,
                refund_reason
        FROM mamba_dim_payment_refund mdpr
        LEFT JOIN mamba_dim_person_name mdpn  ON mdpn.person_id = mdpr.refunded_by
        LEFT JOIN mamba_dim_paid_service_bill_refund mdpsbr  ON mdpsbr.refund_id = mdpr.refund_id
        LEFT JOIN mamba_dim_person_name mdpn1  ON mdpn1.person_id = mdpsbr.approved_by;';

    -- Execute the dynamic SQL statement
    PREPARE select_stmt FROM @select_stmt;
    EXECUTE select_stmt;
    DEALLOCATE PREPARE select_stmt;

END //

DELIMITER ;