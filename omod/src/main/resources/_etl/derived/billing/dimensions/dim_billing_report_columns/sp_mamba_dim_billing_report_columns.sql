-- $BEGIN
CALL sp_mamba_dim_billing_report_columns_create();
CALL sp_mamba_dim_billing_report_columns_insert();
CALL sp_mamba_dim_billing_report_columns_update();
-- $END