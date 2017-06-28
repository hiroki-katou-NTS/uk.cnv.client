package nts.uk.ctx.at.record.dom.standardtime.repository;

import java.util.List;

import nts.uk.ctx.at.record.dom.standardtime.AgreementYearSetting;

public interface AgreementYearSettingRepository {
	
	List<AgreementYearSetting> find(String employeeId);
	
	void add(AgreementYearSetting agreementYearSetting);
	
	void update(AgreementYearSetting agreementYearSetting);
	
	void delete(String employeeId, int yearvalue);
}
