package nts.uk.ctx.at.request.app.find.dialog.employmentsystem.holidaysubstitute;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;
import nts.uk.shr.com.context.AppContexts;
/**
 * 代休確認を起動する
 * UKDesign.UniversalK.就業.KDL_ダイアログ.KDL005_代休確認ダイアログ.アルゴリズム.代休確認を起動する
 * @author phongtq
 *
 */
@Stateless
public class HolidaySubstituteFinder {
	
	@Inject
	private EmpEmployeeAdapter empEmployeeAdapter;
	
	@Inject
	private GetInfoRemainSubstituteHoliday substituteHoliday;
	
	public HolidaySubstituteDto findHolidaySubstitute(List<String> sIDs) {
		// 社員ID(List)から個人社員基本情報を取得
		List<EmployeeImport> lstEmp = this.empEmployeeAdapter.findByEmpId(sIDs);
		
		// 画面　＝　単一モード
		int mode = 0;
		
		RemainNumberConfirmDto confirmDto = null;
		// Input．社員IDリストをチェック
		if (sIDs.size() == 1)
			// 社員の代休残数詳細情報を取得 
			confirmDto = substituteHoliday.getSubstituteHoliday(AppContexts.user().companyId(), sIDs.get(0)); 
		else 
			mode = 1; // 画面　＝　複数モード
		
		HolidaySubstituteDto substituteDto = new HolidaySubstituteDto(lstEmp, confirmDto, mode);
		return substituteDto;
	}
}
