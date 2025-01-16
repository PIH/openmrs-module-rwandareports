-- $BEGIN

INSERT INTO mamba_dim_facility_service_price (facility_service_price_id,
                                              location_id,
                                              concept_id,
                                              name,
                                              short_name,
                                              description,
                                              category,
                                              full_price,
                                              start_date,
                                              end_date,
                                              item_type,
                                              hide_item,
                                              created_date)
SELECT facility_service_price_id,
       location_id,
       concept_id,
       name,
       short_name,
       description,
       category,
       full_price,
       start_date,
       end_date,
       item_type,
       hide_item,
       created_date
FROM mamba_source_db.moh_bill_facility_service_price;

-- $END