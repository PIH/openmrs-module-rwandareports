-- $BEGIN
    INSERT INTO mamba_fact_cashier_report(
        bill_payment_id,
        patient_bill_id,
        date,
        patient_name,
        amount_paid,
        hop_service_id,
        global_bill_id,
        hop_service_name )

        SELECT DISTINCT
            pay.bill_payment_id,
            pay.patient_bill_id,
            pay.date_received date,
            bps.person_name_short  AS patient_name,
            pay.amount_paid,
            psb.service_id        AS hop_service_id,
            gb.global_bill_id,
            hp.name               AS hop_service_name
        FROM mamba_dim_bill_payment pay
        INNER JOIN mamba_dim_paid_service_bill bill ON bill.bill_payment_id = pay.bill_payment_id
        INNER JOIN mamba_dim_patient_service_bill psb ON bill.patient_service_bill_id = psb.patient_service_bill_id
        INNER JOIN mamba_dim_consommation cons ON psb.consommation_id = cons.consommation_id
        INNER JOIN mamba_dim_global_bill gb on cons.global_bill_id = gb.global_bill_id
        INNER JOIN mamba_dim_beneficiary ben on cons.beneficiary_id = ben.beneficiary_id
        INNER JOIN mamba_dim_person bps ON bps.person_id = ben.patient_id
        INNER JOIN mamba_dim_hop_service hp on hp.service_id = psb.service_id
    WHERE gb.closed = 1
      AND psb.voided = 0
    ORDER BY pay.date_received ASC
;
-- $END