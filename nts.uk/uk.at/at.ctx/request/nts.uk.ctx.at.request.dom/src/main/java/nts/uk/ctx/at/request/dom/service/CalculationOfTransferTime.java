package nts.uk.ctx.at.request.dom.service;

import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.DayoffChangeAtr;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CheckDateForManageCmpLeaveService;
import nts.uk.ctx.at.shared.dom.worktime.common.CompensatoryOccurrenceDivision;
import nts.uk.ctx.at.shared.dom.worktime.common.GetSubHolOccurrenceSetting;
import nts.uk.ctx.at.shared.dom.worktime.common.SubHolTransferSet;

/**
 * @author thanh_nx
 *
 *         振替時間の計算
 */
public class CalculationOfTransferTime {

	//処理
	public static Optional<CalculationOfTransferTimeResult> process(Require require, String companyId, String employeeId,
			GeneralDate date, Optional<String> workTypeCode,
			CalculationOfTransferTimeResult beforeTransTime, DayoffChangeAtr atr) {

		// 代休を管理する年月日かどうかを判断する
		boolean check = CheckDateForManageCmpLeaveService.check(require, companyId, employeeId, date);
		if (!check) {
			return Optional.empty();
		}

		// 該当日の就業時間帯を取得する
		Optional<String> getWorkTimeCode = GetWorkTimeOfDay.getWorkInfoFromSetting(require, companyId,
				employeeId, date, workTypeCode, beforeTransTime.getWorkTimeCode());

		Optional<SubHolTransferSet> setting = GetSubHolOccurrenceSetting.process(require, companyId, getWorkTimeCode,
				CompensatoryOccurrenceDivision.valueOf(atr.value));
		if (!setting.isPresent()) {
			return Optional.empty();
		}

		AttendanceTime tranferTime = setting.get()
				.getTransferTime(atr == DayoffChangeAtr.BREAKTIME
						? beforeTransTime.getHolidayTransTime().orElse(new AttendanceTime(0))
						: beforeTransTime.getOverTransTime().orElse(new AttendanceTime(0)));

		return Optional.of(atr == DayoffChangeAtr.BREAKTIME
				? new CalculationOfTransferTimeResult(getWorkTimeCode, Optional.of(tranferTime), Optional.empty())
				: new CalculationOfTransferTimeResult(getWorkTimeCode, Optional.empty(), Optional.of(tranferTime)));
	}

	public static interface Require extends CheckDateForManageCmpLeaveService.Require,
			GetSubHolOccurrenceSetting.Require, GetWorkTimeOfDay.Require {
	}

}
