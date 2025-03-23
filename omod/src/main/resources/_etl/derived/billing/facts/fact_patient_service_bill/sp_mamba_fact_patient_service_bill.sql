-- $BEGIN
CALL sp_mamba_fact_patient_service_bill_create();
CALL sp_mamba_fact_patient_service_bill_insert();
CALL sp_mamba_fact_patient_service_bill_update();
-- $END