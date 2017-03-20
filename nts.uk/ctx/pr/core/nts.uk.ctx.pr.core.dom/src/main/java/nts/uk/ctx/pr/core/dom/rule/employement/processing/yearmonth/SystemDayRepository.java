package nts.uk.ctx.pr.core.dom.rule.employement.processing.yearmonth;

import java.util.List;

import nts.uk.ctx.pr.core.dom.rule.employement.processing.yearmonth.systemday.SystemDay;


public interface SystemDayRepository {

	List<SystemDay> findAll(String companyCode, int processingNo);
	
	SystemDay findOne(String companyCode, int processingNo);

	void insert(SystemDay domain);
	
	void update1(SystemDay domain);
	
	void delete(SystemDay domain);
}
