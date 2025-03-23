-- $BEGIN
CALL sp_mamba_dim_consommation_create();
CALL sp_mamba_dim_consommation_insert();
CALL sp_mamba_dim_consommation_update();
-- $END