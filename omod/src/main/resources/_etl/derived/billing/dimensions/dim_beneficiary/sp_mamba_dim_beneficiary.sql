-- $BEGIN
CALL sp_mamba_dim_beneficiary_create();
CALL sp_mamba_dim_beneficiary_insert();
CALL sp_mamba_dim_beneficiary_update();
-- $END