-- $BEGIN
CALL sp_mamba_dim_third_party_create();
CALL sp_mamba_dim_third_party_insert();
CALL sp_mamba_dim_third_party_update();
-- $END