-- $BEGIN

-- Fetch and set property values from global_props table
SET @insurance_property_value = (SELECT property_value
                 FROM mamba_source_db.global_property
                 WHERE property = 'mohbilling.insuranceReportColumns');
SET @imaging_property_value = (SELECT property_value
                FROM mamba_source_db.global_property
                WHERE property = 'mohbilling.IMAGING');
SET @procedures_property_value = (SELECT property_value
                 FROM mamba_source_db.global_property
                 WHERE property = 'mohbilling.PROCEDURES');

-- Create a temporary table to store the service_ids and their group names
CREATE TEMPORARY TABLE temp_service_groups AS
SELECT service_id,
    CASE
      WHEN FIND_IN_SET(service_id, @insurance_property_value) THEN 'INSURANCE'
      WHEN FIND_IN_SET(service_id, @imaging_property_value) THEN 'IMAGING'
      WHEN FIND_IN_SET(service_id, @procedures_property_value) THEN 'PROCED.'
      END AS group_name
FROM mamba_dim_hop_service
WHERE FIND_IN_SET(service_id, @insurance_property_value)
  OR FIND_IN_SET(service_id, @imaging_property_value)
  OR FIND_IN_SET(service_id, @procedures_property_value);
-- preserves order of insertion (use on small datasets)


INSERT INTO mamba_dim_billing_report_columns (report_type, hop_service_id, column_name, group_column_name)
SELECT 'INSURANCE'  AS report_type,
    h.service_id  AS hop_service_id,
    h.name     AS column_name,
    tsg.group_name AS group_column_name
FROM mamba_dim_hop_service h
     JOIN temp_service_groups tsg ON h.service_id = tsg.service_id
ORDER BY FIND_IN_SET(hop_service_id,
           CONCAT_WS(',', @insurance_property_value, @imaging_property_value, @procedures_property_value));

DROP TEMPORARY TABLE IF EXISTS temp_service_groups;


-- insert another type here

-- $END