package nts.uk.ctx.at.request.dom.applicationreflect.service;

import java.util.List;

import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.SEmpHistImport;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.dailymonthlyprocessing.ExecutionTypeExImport;

/**
 * 申請反映Mgrクラス
 * @author do_dt
 *
 */
public interface AppReflectManager {
	/**
	 * 社員の申請を反映
	 * @param appInfor
	 * @param excLogId 実行ID
	 * @param currentRecord default = 0
	 */
	public void reflectEmployeeOfApp(Application appInfor, List<SEmpHistImport> sEmpHistImport, 
			ExecutionTypeExImport execuTionType, String excLogId, int currentRecord);

	void reflectEmployeeOfAppWithTransaction(Application appInfor, List<SEmpHistImport> sEmpHistImport, ExecutionTypeExImport execuTionType,
			String excLogId);
}
