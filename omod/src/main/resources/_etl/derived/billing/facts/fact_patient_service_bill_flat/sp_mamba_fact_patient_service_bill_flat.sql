-- $BEGIN
CALL sp_mamba_fact_patient_service_bill_flat_create();
CALL sp_mamba_fact_patient_service_bill_flat_insert();
CALL sp_mamba_fact_patient_service_bill_flat_update();
-- $END