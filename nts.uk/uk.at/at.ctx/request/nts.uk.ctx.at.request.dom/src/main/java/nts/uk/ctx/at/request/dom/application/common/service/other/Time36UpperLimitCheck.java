package nts.uk.ctx.at.request.dom.application.common.service.other;

import java.util.Optional;

import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.overtimerestappcommon.Time36AgreeCheckRegister;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.Time36ErrorInforList;
import nts.uk.shr.com.enumcommon.NotUseAtr;

public interface Time36UpperLimitCheck {
	
	/**
	 * UKDesign.UniversalK.就業.KAF_申請.共通アルゴリズム."18.３６時間の上限チェック(新規登録)_NEW".18.３６時間の上限チェック(新規登録)_NEW
	 * @param companyID 会社ID
	 * @param employeeID 社員ID
	 * @param employmentCD 雇用コード
	 * @param application 申請
	 * @param opAppOverTime optional＜残業申請＞
	 * @param opAppHolidayWork optional＜休日出勤申請＞
	 * @param extratimeExcessAtr 時間外超過区分
	 * @param extratimeDisplayAtr 時間外表示区分
	 * @return
	 */
	public Time36ErrorInforList checkRegister(String companyID, String employeeID, String employmentCD, Application application,
			Optional<AppOverTime> opAppOverTime, Optional<AppHolidayWork> opAppHolidayWork,
			Time36AgreeCheckRegister extratimeExcessAtr, NotUseAtr extratimeDisplayAtr);
}
