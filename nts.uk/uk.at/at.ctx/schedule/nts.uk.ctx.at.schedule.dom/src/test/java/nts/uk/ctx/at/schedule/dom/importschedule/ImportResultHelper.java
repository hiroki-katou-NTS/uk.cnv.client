package nts.uk.ctx.at.schedule.dom.importschedule;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterImportCode;

public class ImportResultHelper {

	/**
	 * 任意の取り込み状態を持つ1件分の取り込み結果を作成する
	 * @param employeeId 社員ID
	 * @param date 年月日
	 * @param importCode 取り込みコード
	 * @param status 取り込み状態
	 * @return 1件分の取り込み結果
	 */
	public static ImportResultDetail createDetail(String employeeId, GeneralDate date, String importCode, ImportStatus status) {
		return new ImportResultDetail(
						new EmployeeId(employeeId)
					,	date
					,	new ShiftMasterImportCode(importCode)
					,	status
				);
	}

}
