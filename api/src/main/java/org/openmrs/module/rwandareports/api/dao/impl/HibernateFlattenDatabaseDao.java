package org.openmrs.module.rwandareports.api.dao.impl;

import org.openmrs.Obs;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrimambacore.db.AnalysisDbSessionFactory;
import org.openmrs.module.rwandareports.api.dao.FlattenDatabaseDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author smallGod
 * @date: 01/03/2023
 */
public class HibernateFlattenDatabaseDao implements FlattenDatabaseDao {
	
	@Autowired
	private AnalysisDbSessionFactory sessionFactory;
	
	@Override
	public void executeFlatteningScript() {
		sessionFactory.getCurrentSession().createSQLQuery("CALL sp_mamba_data_processing_etl()").executeUpdate();
	}
	
	public AnalysisDbSessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void setSessionFactory(AnalysisDbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
