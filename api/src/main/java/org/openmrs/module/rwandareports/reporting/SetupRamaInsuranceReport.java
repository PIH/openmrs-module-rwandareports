package org.openmrs.module.rwandareports.reporting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;

import java.util.Date;

public class SetupRamaInsuranceReport {

    protected final static Log log = LogFactory.getLog(SetupRamaInsuranceReport.class);
    GlobalPropertiesManagement gp = new GlobalPropertiesManagement();


    public void setup() throws Exception {

        ReportDefinition rd = createReportDefinition();
        ReportDesign designCSV = Helper.createCsvReportDesign(rd,"Billing - Insurance Report.csv_");
        Helper.saveReportDesign(designCSV);
    }

    public void delete() {
        ReportService rs = Context.getService(ReportService.class);
        for (ReportDesign rd : rs.getAllReportDesigns(false)) {
            if ("Billing - Insurance Report.csv_".equals(rd.getName())) {
                rs.purgeReportDesign(rd);
            }
        }
        Helper.purgeReportDefinition("Billing - Insurance Report");
    }

    private ReportDefinition createReportDefinition() {

        ReportDefinition reportDefinition = new ReportDefinition();
        reportDefinition.setName("Billing - Insurance Report");
        reportDefinition.addParameter(new Parameter("startDate", "From:", Date.class));
        reportDefinition.addParameter(new Parameter("endDate", "To:", Date.class));
        reportDefinition.addParameter(new Parameter("insuranceName", "Insurance Name:", String.class));

        createDataSetDefinition(reportDefinition);

        Helper.saveReportDefinition(reportDefinition);

        return reportDefinition;
    }

    private void createDataSetDefinition(ReportDefinition reportDefinition) {

        SqlDataSetDefinition sqldsd =new SqlDataSetDefinition();
        sqldsd.setSqlQuery("SELECT \n" +
                "    DATE(gb.created_date) AS `Date`,\n" +
                "    pi.identifier AS `Patient ID`,\n" +
                "    bn.policy_id_number AS `BENEFICIARY'S AFFILIATION NUMBER`,\n" +
                "    FLOOR(DATEDIFF(CURDATE(), p.birthdate) / 365.25) AS `BENEFICIARY'S AGE`,\n" +
                "    p.gender AS `BENEFICIARY'S SEX`,\n" +
                "    CONCAT(COALESCE(pn.given_name, ''), ' ', COALESCE(pn.middle_name, ''), ' ', COALESCE(pn.family_name, '')) AS `BENEFICIARY'S NAMES`,\n" +
                "    bn.owner_name AS `AFFILIATE'S NAMES`,\n" +
                "    bn.company AS `AFFILIATE'S AFFECTATION`,\n" +
                "    i.name AS `Insurance Name`,\n" +
                "    \n" +
                "    -- Cost calculations with GROUP_CONCAT\n" +
                "    COALESCE(\n" +
                "        CONCAT('=', GROUP_CONCAT(\n" +
                "            CASE WHEN hs.name = 'Consultation' THEN ROUND(psb.unit_price * psb.quantity, 0) END\n" +
                "            SEPARATOR '+'\n" +
                "        )),\n" +
                "        '0'\n" +
                "    ) AS `Cost for Consultation`,\n" +
                "    \n" +
                "    COALESCE(\n" +
                "        CONCAT('=', GROUP_CONCAT(\n" +
                "            CASE WHEN hs.name = 'Laboratory' THEN ROUND(psb.unit_price * psb.quantity, 0) END\n" +
                "            SEPARATOR '+'\n" +
                "        )),\n" +
                "        '0'\n" +
                "    ) AS `Cost for Laboratory Tests`,\n" +
                "    \n" +
                "    COALESCE(\n" +
                "        CONCAT('=', GROUP_CONCAT(\n" +
                "            CASE WHEN FIND_IN_SET(\n" +
                "                    hs.service_id,\n" +
                "                    (SELECT property_value\n" +
                "                    FROM global_property\n" +
                "                    WHERE property = 'mohbilling.IMAGING')\n" +
                "            ) > 0\n" +
                "            THEN ROUND(psb.unit_price * psb.quantity, 0)\n" +
                "            ELSE NULL\n" +
                "            END SEPARATOR '+'\n" +
                "        )),\n" +
                "        '0'\n" +
                "    ) AS `Cost for Medical Imaging`,\n" +
                "    \n" +
                "    COALESCE(\n" +
                "        CONCAT('=', GROUP_CONCAT(\n" +
                "            CASE WHEN hs.name = 'Hospitalization' THEN ROUND(psb.unit_price * psb.quantity, 0) END\n" +
                "            SEPARATOR '+'\n" +
                "        )),\n" +
                "        '0'\n" +
                "    ) AS `Cost for Hospitalization`,\n" +
                "    \n" +
                "    COALESCE(\n" +
                "        CONCAT('=', GROUP_CONCAT(\n" +
                "            CASE WHEN FIND_IN_SET(\n" +
                "                    hs.service_id,\n" +
                "                    (SELECT property_value\n" +
                "                    FROM global_property\n" +
                "                    WHERE property = 'mohbilling.PROCEDURES')\n" +
                "            ) > 0\n" +
                "            THEN ROUND(psb.unit_price * psb.quantity, 0)\n" +
                "            ELSE NULL\n" +
                "            END SEPARATOR '+'\n" +
                "        )),\n" +
                "        '0'\n" +
                "    ) AS `Cost for Procedures and Materials`,\n" +
                "    \n" +
                "    COALESCE(\n" +
                "        CONCAT('=', GROUP_CONCAT(\n" +
                "            CASE WHEN FIND_IN_SET(\n" +
                "                    hs.service_id,\n" +
                "                    (SELECT property_value\n" +
                "                    FROM global_property\n" +
                "                    WHERE property = 'mohbilling.MEDECINES')\n" +
                "            ) > 0\n" +
                "            THEN ROUND(psb.unit_price * psb.quantity, 0)\n" +
                "            ELSE NULL\n" +
                "            END SEPARATOR '+'\n" +
                "        )),\n" +
                "        '0'\n" +
                "    ) AS `Cost for Medicines`,\n" +
                "    \n" +
                "   COALESCE(\n" +
                "        CONCAT('=', GROUP_CONCAT(\n" +
                "            CASE WHEN FIND_IN_SET(\n" +
                "                    hs.service_id,\n" +
                "                    (SELECT property_value\n" +
                "                    FROM global_property\n" +
                "                    WHERE property = 'mohbilling.CONSUMABLES')\n" +
                "            ) > 0\n" +
                "            THEN ROUND(psb.unit_price * psb.quantity, 0)\n" +
                "            ELSE NULL\n" +
                "            END SEPARATOR '+'\n" +
                "        )),\n" +
                "        '0'\n" +
                "    ) AS `Cost for Other Consumables`,\n" +
                "    \n" +
                "    ROUND(gb.global_amount, 0) AS `Total Amount 100%`,\n" +
                "    ROUND(gb.global_amount * 0.15, 0) AS `Patient Paid 15%`,\n" +
                "    ROUND(gb.global_amount * 0.85, 0) AS `Insurance Share 85%`\n" +
                "    \n" +
                "FROM moh_bill_patient_bill pb\n" +
                "INNER JOIN moh_bill_consommation mc ON pb.patient_bill_id = mc.patient_bill_id\n" +
                "INNER JOIN moh_bill_patient_service_bill psb ON mc.consommation_id = psb.consommation_id\n" +
                "LEFT JOIN moh_bill_hop_service hs ON psb.service_id = hs.service_id\n" +
                "INNER JOIN moh_bill_beneficiary bn ON mc.beneficiary_id = bn.beneficiary_id\n" +
                "INNER JOIN moh_bill_global_bill gb ON mc.global_bill_id = gb.global_bill_id\n" +
                "INNER JOIN moh_bill_insurance_policy e ON bn.insurance_policy_id = e.insurance_policy_id \n" +
                "INNER JOIN moh_bill_insurance i ON e.insurance_id = i.insurance_id\n" +
                "INNER JOIN moh_bill_department dp ON mc.department_id = dp.department_id\n" +
                "INNER JOIN moh_bill_insurance_rate ir ON i.insurance_id = ir.insurance_id\n" +
                "INNER JOIN patient_identifier pi ON bn.patient_id = pi.patient_id\n" +
                "INNER JOIN person p ON bn.patient_id = p.person_id\n" +
                "INNER JOIN person_name pn ON bn.patient_id = pn.person_id\n" +
                "\n" +
                "WHERE pb.voided = 0\n" +
                "  AND ir.retired = 0\n" +
                "  AND mc.voided = 0\n" +
                "  AND psb.voided = 0\n" +
                "  AND i.name LIKE CONCAT('%', :insuranceName, '%')\n" +
                "  AND gb.closed = 1\n" +
                "  AND DATE(gb.created_date) BETWEEN :startDate AND :endDate\n" +
                "  \n" +
                "GROUP BY gb.global_bill_id;");
        sqldsd.addParameter(new Parameter("startDate", "From:", Date.class));
        sqldsd.addParameter(new Parameter("endDate", "To:", Date.class));
        sqldsd.addParameter(new Parameter("insuranceName", "Insurance Name:", String.class));

        reportDefinition.addDataSetDefinition("dsd",Mapped.mapStraightThrough(sqldsd));


    }


}