package nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.service;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationTypeShare;
import nts.uk.ctx.at.shared.dom.scherec.application.common.PrePostAtrShare;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;

/**
 * DS: 勤務種類から促す申請を判断する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.申請承認.設定.会社別.申請承認設定.アルゴリズム.勤務種類から促す申請を判断する
 * 
 * @author chungnt
 *
 */

public class JudgingApplication {

	/**
	 * [1] 判断する
	 * 
	 * @param require
	 * @param cid
	 *            会社ID
	 * @param sid
	 *            社員ID
	 * @param date
	 *            年月日
	 * @param workTypeCode
	 *            勤務種類コード
	 */
	public static Optional<ApplicationTypeShare> toDecide(Require require, String cid, String sid, GeneralDate date,
			String workTypeCode) {

		// $勤務種類 = require.勤務種類を取得する(会社ID,勤務種類コード)
		Optional<WorkType> workType = require.findByPK(cid, workTypeCode);

		// if $勤務種類.isEmpty
		// return Optional.Empty
		if (workType.isPresent()) {
			return Optional.empty();
		}

		ApplicationTypeShare result = null;

		if (workType.get().isHolidayWork() || (workType.get().getDailyWork().getWorkTypeUnit().isOneDay()
				&& workType.get().getDailyWork().getOneDay().isHoliday())) {
			result = ApplicationTypeShare.HOLIDAY_WORK_APPLICATION;
		} else if (!workType.get().chechAttendanceDay().isHoliday()) {
			result = ApplicationTypeShare.OVER_TIME_APPLICATION;
		} else {
			return Optional.empty();
		}

		if (ApplicationAvailable.get(require, cid, sid, date, result)) {
			List<Application> applications = require.getAllApplicationByAppTypeAndPrePostAtr(sid, result.value, date,
					PrePostAtrShare.POSTERIOR.value);
			if (applications.isEmpty()) {
				return Optional.of(result);
			}
		}

		return Optional.empty();

	}

	public static interface Require extends ApplicationAvailable.Require {
		/**
		 * [R-1] 勤務種類を取得する
		 */
		Optional<WorkType> findByPK(String companyId, String workTypeCd);

		/**
		 * [R-2] 申請を取得する
		 */
		List<Application> getAllApplicationByAppTypeAndPrePostAtr(String employeeID, int appType, GeneralDate appDate,
				int prePostAtr);

	}

}
