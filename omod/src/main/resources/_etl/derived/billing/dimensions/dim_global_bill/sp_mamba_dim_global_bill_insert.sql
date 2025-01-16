-- $BEGIN

INSERT INTO mamba_dim_global_bill (global_bill_id,
                                   admission_id,
                                   insurance_id,
                                   bill_identifier,
                                   global_amount,
                                   closing_date,
                                   closed,
                                   closed_by_id,
                                   closed_reason,
                                   edited_by,
                                   edit_reason,
                                   created_date)
SELECT global_bill_id,
       admission_id,
       insurance_id,
       bill_identifier,
       global_amount,
       closing_date,
       closed,
       closed_by as closed_by_id,
       closed_reason,
       edited_by,
       edit_reason,
       created_date
FROM mamba_source_db.moh_bill_global_bill;

-- $END

-- $END