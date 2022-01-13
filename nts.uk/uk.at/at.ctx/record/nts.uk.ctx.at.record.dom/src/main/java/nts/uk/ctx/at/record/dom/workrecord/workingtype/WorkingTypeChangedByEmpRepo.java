/**
 * 
 */
package nts.uk.ctx.at.record.dom.workrecord.workingtype;

import java.util.List;

import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;

/**
 * @author danpv
 *
 */
public interface WorkingTypeChangedByEmpRepo {

	public WorkingTypeChangedByEmployment get(CompanyId companyId, EmploymentCode empCode);
	
	public List<String> checkSetting(String companyId, List<String> empCode);

	public void save(WorkingTypeChangedByEmployment workingType);
	
	void copyEmployment(String companyId, WorkingTypeChangedByEmployment sourceData, List<String> targetEmploymentCodes);
	
	public void deleteEmploymentSetting(String companyId, String empCode);
	
	public List<String> getDistinctEmpCodeByCompanyId(String companyId);

}
