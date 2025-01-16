-- $BEGIN
CALL sp_mamba_dim_insurance_policy_create();
CALL sp_mamba_dim_insurance_policy_insert();
CALL sp_mamba_dim_insurance_policy_update();
-- $END