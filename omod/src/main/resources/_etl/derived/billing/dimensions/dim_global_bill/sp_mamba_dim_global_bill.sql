-- $BEGIN
CALL sp_mamba_dim_global_bill_create();
CALL sp_mamba_dim_global_bill_insert();
CALL sp_mamba_dim_global_bill_update();
-- $END