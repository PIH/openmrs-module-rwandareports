-- $BEGIN
CALL sp_mamba_dim_admission_create();
CALL sp_mamba_dim_admission_insert();
CALL sp_mamba_dim_admission_update();
-- $END