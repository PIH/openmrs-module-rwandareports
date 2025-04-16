-- $BEGIN
CALL sp_mamba_dim_recovery_create();
CALL sp_mamba_dim_recovery_insert();
CALL sp_mamba_dim_recovery_update();
-- $END