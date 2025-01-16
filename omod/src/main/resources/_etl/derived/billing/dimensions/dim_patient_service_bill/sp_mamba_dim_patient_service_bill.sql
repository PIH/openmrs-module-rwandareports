-- $BEGIN
CALL sp_mamba_dim_patient_service_bill_create();
CALL sp_mamba_dim_patient_service_bill_insert();
CALL sp_mamba_dim_patient_service_bill_update();
-- $END