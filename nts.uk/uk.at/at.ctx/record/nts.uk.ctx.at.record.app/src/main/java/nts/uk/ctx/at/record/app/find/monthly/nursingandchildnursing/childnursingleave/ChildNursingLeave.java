package nts.uk.ctx.at.record.app.find.monthly.nursingandchildnursing.childnursingleave;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.app.find.monthly.nursingandchildnursing.getdeitalinfonursingbyemps.GetDeitalInfoNursingByEmp;
import nts.uk.ctx.at.record.app.find.monthly.nursingandchildnursing.getdeitalinfonursingbyemps.NursingAndChildNursingRemainDto;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingCategory;

/**
 * 子の看護休暇ダイアログを起動
 * UKDesign.UniversalK.就業.KDL_ダイアログ.KDL051_子の看護休暇ダイアログ.アルゴリズム.子の看護休暇ダイアログを起動
 * 
 * @author tutk
 *
 */
@Stateless
public class ChildNursingLeave {

	@Inject
	private EmpEmployeeAdapter empEmployeeAdapter;

	@Inject
	private GetDeitalInfoNursingByEmp getDeitalInfoNursingByEmps;

	/**
	 * 
	 * @param companyId 会社ID
	 * @param listEmp   List＜社員ID＞
	 */
	public ChildNursingLeaveDto get(String companyId, List<String> listEmpId) {

		// 社員ID(List)から個人社員基本情報を取得
		List<EmployeeImport> lstEmployee = this.empEmployeeAdapter.findByEmpId(listEmpId);

		NursingAndChildNursingRemainDto nursingAndChildNursingRemainDto = null;
		if (listEmpId.size() == 1) {
			// 社員の子の看護介護残数詳細情報を取得
			nursingAndChildNursingRemainDto = getDeitalInfoNursingByEmps.get(companyId, listEmpId.get(0),
					NursingCategory.ChildNursing);
		}
		//取得したデータを返す
		return new ChildNursingLeaveDto(lstEmployee, nursingAndChildNursingRemainDto);
	}

}
