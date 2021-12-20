package nts.uk.ctx.at.request.app.find.dialog.suspensionholiday;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.request.app.find.dialog.annualholiday.InforAnnualHolidaysAccHolidayDto;
import nts.uk.ctx.at.request.app.find.dialog.annualholiday.dto.AnnualHolidaysDto;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;
import nts.uk.shr.com.context.AppContexts;
/**
 * 積休確認ダイアログを起動する
 * UKDesign.UniversalK.就業.KDL_ダイアログ.KDL029_積休ダイアログ.アルゴリズム.積休確認ダイアログを起動する
 * @author phongtq
 *
 */
@Stateless
public class SuspensionHolidayFinder {
	@Inject
	private EmpEmployeeAdapter empEmployeeAdapter;
	
	@Inject
	private GetInforNumberRemainEmployees remainDays;

	public AnnualHolidaysDto findSuspensionHoliday(List<String> sIDs) {
		// 社員ID(List)から個人社員基本情報を取得
		List<EmployeeImport> lstEmp = this.empEmployeeAdapter.findByEmpId(sIDs);

		// 画面 ＝ 単一モード
		int mode = 0;
		
		AnnualHolidaysDto suspenHoliday = null; 
		InforAnnualHolidaysAccHolidayDto confirmDto = null;
		// Input．社員IDリストをチェック
		
			// 社員の積休残数詳細情報を取得
			confirmDto = remainDays.getInforNumberRemainEmployees(AppContexts.user().companyId(), sIDs.get(0));
			if (sIDs.size() > 1)
			mode = 1; // 画面 ＝ 複数モード
		
		suspenHoliday = new AnnualHolidaysDto(lstEmp, confirmDto, mode);
		return suspenHoliday;
	}
}
