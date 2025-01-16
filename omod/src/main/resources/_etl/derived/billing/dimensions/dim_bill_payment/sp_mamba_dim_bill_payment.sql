-- $BEGIN
CALL sp_mamba_dim_bill_payment_create();
CALL sp_mamba_dim_bill_payment_insert();
CALL sp_mamba_dim_bill_payment_update();
-- $END