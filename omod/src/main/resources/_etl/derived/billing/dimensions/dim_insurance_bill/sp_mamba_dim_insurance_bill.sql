-- $BEGIN
CALL sp_mamba_dim_insurance_bill_create();
CALL sp_mamba_dim_insurance_bill_insert();
CALL sp_mamba_dim_insurance_bill_update();
-- $END