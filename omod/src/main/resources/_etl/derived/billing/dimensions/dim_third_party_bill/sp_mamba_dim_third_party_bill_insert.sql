-- $BEGIN

INSERT INTO mamba_dim_third_party_bill (third_party_bill_id,
                                        amount,
                                        created_date)
SELECT third_party_bill_id,
       amount,
       created_date
FROM moh_bill_third_party_bill;

-- $END