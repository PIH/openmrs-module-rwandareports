-- $BEGIN

CREATE TABLE IF NOT EXISTS mamba_dim_billing_report_columns
(
    id                INT         NOT NULL AUTO_INCREMENT,
    report_type       VARCHAR(50) NOT NULL,
    hop_service_id    INT         NOT NULL,
    column_name       VARCHAR(50) NOT NULL,
    group_column_name VARCHAR(50) DEFAULT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (`hop_service_id`) REFERENCES `mamba_dim_hop_service` (`service_id`)
)
    CHARSET = UTF8MB4;

-- $END