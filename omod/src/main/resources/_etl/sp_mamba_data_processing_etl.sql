-- $BEGIN

-- add base folder SP here if any --

-- Call the ETL process
CALL sp_mamba_data_processing_derived_billing();

-- $END