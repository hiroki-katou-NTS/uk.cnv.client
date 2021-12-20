package nts.uk.ctx.at.request.app.find.dialog.employmentsystem.startholidayconfirmation;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.request.app.find.dialog.employmentsystem.holidaysubstitute.RemainNumberConfirmDto;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;

/**
 * 振休確認ダイアログを起動する
 * UKDesign.UniversalK.就業.KDL_ダイアログ.KDL009_振休確認ダイアログ.アルゴリズム.振休確認ダイアログを起動する
 * @author tutk
 *
 */
@Stateless
public class StartHolidayConfirmation {
	
	@Inject
	private EmpEmployeeAdapter empEmployeeAdapter;
	
	@Inject
	private GetDetailInfoEmpRemainHoliday getDetailInfoEmpRemainHoliday;
	
	public StartHolidayConfDto get(List<String> listEmployee) {
		StartHolidayConfDto result = new StartHolidayConfDto();
		// 社員ID(List)から個人社員基本情報を取得
		List<EmployeeImport> lstEmp = this.empEmployeeAdapter.findByEmpId(listEmployee);
		result.setListEmployeeImport(lstEmp);
		//Input．社員IDリストをチェック
		if(listEmployee.size()!=1) {
			//画面　＝　複数モード
			result.setMode(1);
			return result;
		}
		//社員の振休残数詳細情報を取得
		RemainNumberConfirmDto remainNumConfirmDto = getDetailInfoEmpRemainHoliday.getByEmpId(listEmployee.get(0)); 
		result.setRemainNumConfirmDto(remainNumConfirmDto);
		//画面　＝　単一モード
		result.setMode(0);
		return result;
	}

}
