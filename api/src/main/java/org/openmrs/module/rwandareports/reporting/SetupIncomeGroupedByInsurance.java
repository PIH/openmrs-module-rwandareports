package org.openmrs.module.rwandareports.reporting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.OrderType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;

import java.util.Date;

public class SetupIncomeGroupedByInsurance {
		
	protected final static Log log = LogFactory.getLog(SetupIncomeGroupedByInsurance.class);
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();

		
	public void setup() throws Exception {		
		 
		ReportDefinition rd = createReportDefinition();
		ReportDesign designCSV = Helper.createCsvReportDesign(rd,"Billing - Grouped Insurance Report.csv_");
		Helper.saveReportDesign(designCSV);
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("Billing - Grouped Insurance Report.csv_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		Helper.purgeReportDefinition("Billing - Grouped Insurance Report");
	}
	
	private ReportDefinition createReportDefinition() {
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("Billing - Grouped Insurance Report");
		reportDefinition.addParameter(new Parameter("startDate", "From:", Date.class));
		reportDefinition.addParameter(new Parameter("endDate", "To:", Date.class));

		createDataSetDefinition(reportDefinition);

		Helper.saveReportDefinition(reportDefinition);
		
		return reportDefinition;
	}

	private void createDataSetDefinition(ReportDefinition reportDefinition) {

		SqlDataSetDefinition sqldsd =new SqlDataSetDefinition();
		sqldsd.setSqlQuery("SELECT " +
				"(SELECT group_concat(created_date) FROM moh_bill_consommation where global_bill_id=gb.global_bill_id) as Bill_Date,\n" +
				"(SELECT group_concat('[',consommation_id,']') FROM moh_bill_consommation where global_bill_id=gb.global_bill_id) as Bill_No,\n" +
				"pi.identifier as 'Patient ID',\n" +
				"concat(COALESCE(pn.given_name,''),\" \",COALESCE(pn.middle_name,''),\" \",COALESCE(pn.family_name,'')) as 'Patient Names',\n" +
				"i.name as 'Insurance Name',\n" +
				"concat(ir.rate,'%') as Percent,\n" +
				"gb.global_amount as Due_Amount,\n" +
				"-- sum(gb.global_amount) as Due_amount,\n" +
				"sum(bp.amount_paid) as Paid_Amount,\n" +
				"round(((gb.global_amount)*(ir.rate/100)),0) as 'Insurance Amount',\n" +
				"concat(COALESCE(c.given_name,''),\" \",COALESCE(c.middle_name,''),\" \",COALESCE(c.family_name,'')) as Doctor,\n" +
				"concat(COALESCE(np.given_name,''),\" \",COALESCE(np.middle_name,''),\" \",COALESCE(np.family_name,'')) as Cashier\n" +
				"FROM moh_bill_patient_bill pb\n" +
				"left join moh_bill_payment bp on pb.patient_bill_id=bp.patient_bill_id\n" +
				"left join moh_bill_consommation cn on pb.patient_bill_id=cn.patient_bill_id\n" +
				"left join moh_bill_beneficiary bn on cn.beneficiary_id=bn.beneficiary_id\n" +
				"left join moh_bill_global_bill gb on cn.global_bill_id=gb.global_bill_id\n" +
				"left join moh_bill_insurance_policy e on bn.insurance_policy_id = e.insurance_policy_id \n" +
				"left join moh_bill_insurance i on e.insurance_id = i.insurance_id\n" +
				"left join moh_bill_department dp on cn.department_id=dp.department_id\n" +
				"left join moh_bill_insurance_rate ir on i.insurance_id = ir.insurance_id\n" +
				"left join patient_identifier pi on bn.patient_id = pi.patient_id \n" +
				"left join person p on bn.patient_id = p.person_id\n" +
				"left join users b on pb.creator = b.user_id \n" +
				"left join person_name c on b.person_id = c.person_id\n" +
				"left join person_name pn on bn.patient_id = pn.person_id\n" +
				"left join users u on bp.creator = u.user_id\n" +
				"left join person_name np on u.person_id = np.person_id\n" +
				"WHERE pb.voided=0\n" +
				"and ir.retired=0\n" +
				"and DATE(pb.created_date) between :startDate  and :endDate\n" +
				"group by gb.global_bill_id WITH rollup\n" +
				"order by i.name ;");
		sqldsd.addParameter(new Parameter("startDate", "From:", Date.class));
		sqldsd.addParameter(new Parameter("endDate", "To:", Date.class));

		reportDefinition.addDataSetDefinition("dsd",Mapped.mapStraightThrough(sqldsd));


	}


}

