-- $BEGIN
CALL sp_mamba_dim_insurance_create();
CALL sp_mamba_dim_insurance_insert();
CALL sp_mamba_dim_insurance_update();
-- $END