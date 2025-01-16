-- $BEGIN

INSERT INTO mamba_fact_insurance(admission_date, closing_date, beneficiary_name, household_head_name, family_code,
                                 beneficiary_level, card_number, company_name, age, birth_date, gender, doctor_name,
                                 insurance_name, insurance_id, current_insurance_rate, current_insurance_rate_flat_fee,
                                 hop_service_id, service_bill_quantity, service_bill_unit_price)

SELECT -- DATE_FORMAT(gb.created_date, '%d/%m/%Y') AS admission_date,
       DATE(gb.created_date)               AS admission_date,
       DATE(gb.closing_date)               AS closing_date,
       bps.person_name_long                AS beneficiary_name,
       ben.owner_name                      AS household_head_name,
       ben.owner_code                      AS family_code,
       ben.level                           AS beneficiary_level,
       isp.insurance_card_no               AS card_number,
       ben.company                         AS company_name,
       bps.age                             AS age,
       DATE(bps.birthdate)                 AS birth_date,
       bps.gender                          AS gender,
       gb.closed_by_name                   AS doctor_name,
       ins.name                            AS insurance_name,
       ins.insurance_id                    AS insurance_id,
       ins.current_insurance_rate          AS current_insurance_rate,
       ins.current_insurance_rate_flat_fee AS current_insurance_rate_flat_fee,
       psb.service_id                      AS hop_service_id,
       psb.quantity                        AS service_bill_quantity,
       psb.unit_price                      AS service_bill_unit_price

FROM mamba_dim_patient_service_bill psb
         INNER JOIN mamba_dim_consommation cons ON psb.consommation_id = cons.consommation_id
         INNER JOIN mamba_dim_global_bill gb on cons.global_bill_id = gb.global_bill_id
         INNER JOIN mamba_dim_beneficiary ben on cons.beneficiary_id = ben.beneficiary_id
         INNER JOIN mamba_dim_insurance_policy isp on ben.insurance_policy_id = isp.insurance_policy_id
         INNER JOIN mamba_dim_insurance ins ON ins.insurance_id = isp.insurance_id
         INNER JOIN mamba_dim_person bps ON bps.person_id = ben.patient_id

WHERE gb.closed = 1
  AND psb.voided = 0
-- GROUP BY cons.global_bill_id
-- HAVING MIN(cons.consommation_id)
ORDER BY gb.closing_date ASC
;
-- $END