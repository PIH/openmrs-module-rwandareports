-- $BEGIN
CALL sp_mamba_dim_third_party_bill_create();
CALL sp_mamba_dim_third_party_bill_insert();
CALL sp_mamba_dim_third_party_bill_update();
-- $END