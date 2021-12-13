package nts.uk.ctx.at.request.app.find.dialog.annualholiday;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.app.find.dialog.annualholiday.dto.AnnualHolidaysDto;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;

/**
 * 
 * @author phongtq
 * UKDesign.UniversalK.就業.KDL_ダイアログ.KDL020_年休ダイアログ.アルゴリズム.年休確認を起動する
 * 年休確認を起動する
 *
 */
@Stateless
public class AnnualHolidaysFinder {

	@Inject
	private EmpEmployeeAdapter empEmployeeAdapter;
	
	@Inject
	private GetInforRemainAnnualHoliday annualHoliday;

	public AnnualHolidaysDto findAnnualHolidays(List<String> sIDs) {
		// 社員ID(List)から個人社員基本情報を取得
		List<EmployeeImport> lstEmp = this.empEmployeeAdapter.findByEmpId(sIDs);

		// 画面 ＝ 単一モード
		int mode = 0;

		AnnualHolidaysDto suspenHoliday = null; 
		InforAnnualHolidaysAccHolidayDto confirmDto = null;
		// Input．社員IDリストをチェック
		// 社員の代休残数詳細情報を取得
		confirmDto = annualHoliday.getGetInforRemainAnnualHoliday(GeneralDate.today(), sIDs.get(0));
		
		if (sIDs.size() != 1)
		mode = 1; // 画面 ＝ 複数モード
		
		suspenHoliday = new AnnualHolidaysDto(lstEmp, confirmDto, mode);
		return suspenHoliday;
	}
}


