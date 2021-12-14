package nts.uk.ctx.at.shared.dom.adapter.generalinfo.dtoimport;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.employeeworkway.businesstype.employee.BusinessTypeOfEmployeeHis;

/**
 * 特定期間の社員情報
 */
@NoArgsConstructor
@Getter
@Setter
public class EmployeeGeneralInfoImport {
	
	/** 雇用履歴一覧 */
	List<ExEmploymentHistoryImport> employmentHistoryImports = new ArrayList<>(); 
	
	/** 分類履歴一覧 */
	List<ExClassificationHistoryImport> exClassificationHistoryImports= new ArrayList<>();
	
	/** 職位履歴一覧 */
	List<ExJobTitleHistoryImport> exJobTitleHistoryImports= new ArrayList<>();
	
	/** 職場履歴一覧 */
	List<ExWorkPlaceHistoryImport> exWorkPlaceHistoryImports= new ArrayList<>();

	/** 勤務種別一覧 */
	List<BusinessTypeOfEmployeeHis> exWorkTypeHistoryImports= new ArrayList<>();
	
	/** 部門履歴一覧 */
	List<ExDepartmentHistoryImport> exDepartmentHistoryImports= new ArrayList<>();

	public EmployeeGeneralInfoImport(List<ExEmploymentHistoryImport> employmentHistoryImports,
			List<ExClassificationHistoryImport> exClassificationHistoryImports,
			List<ExJobTitleHistoryImport> exJobTitleHistoryImports,
			List<ExWorkPlaceHistoryImport> exWorkPlaceHistoryImports) {
		super();
		this.employmentHistoryImports = employmentHistoryImports;
		this.exClassificationHistoryImports = exClassificationHistoryImports;
		this.exJobTitleHistoryImports = exJobTitleHistoryImports;
		this.exWorkPlaceHistoryImports = exWorkPlaceHistoryImports;
		this.exWorkTypeHistoryImports = new ArrayList<>();
	}

	public EmployeeGeneralInfoImport(List<ExEmploymentHistoryImport> employmentHistoryImports,
			List<ExClassificationHistoryImport> exClassificationHistoryImports,
			List<ExJobTitleHistoryImport> exJobTitleHistoryImports,
			List<ExWorkPlaceHistoryImport> exWorkPlaceHistoryImports,
			List<BusinessTypeOfEmployeeHis> exWorkTypeHistoryImports) {
		super();
		this.employmentHistoryImports = employmentHistoryImports;
		this.exClassificationHistoryImports = exClassificationHistoryImports;
		this.exJobTitleHistoryImports = exJobTitleHistoryImports;
		this.exWorkPlaceHistoryImports = exWorkPlaceHistoryImports;
		this.exWorkTypeHistoryImports = exWorkTypeHistoryImports;
	}
	
}
