package nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.WorkTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerCompanySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyprocess.calc.CalculateOption;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;

/**
 * @author thanh_nx
 *
 *         最大の時間帯でworkを作成
 */
public class CreateWorkMaxTimeZone {

	public static IntegrationOfDaily process(Require require, String cid, IntegrationOfDaily dailyRecord) {

		DailyRecordToAttendanceItemConverter converter = require.createDailyConverter();
		if (!dailyRecord.getWorkInformation().getRecordInfo().getWorkTimeCodeNotNull().isPresent())
			return converter.setData(dailyRecord).toDomain();

		// 日別勤怠(work）の勤務情報をもとに所定時間設定を取得する
		Optional<PredetemineTimeSetting> predeteminate = require.findByWorkTimeCode(cid,
				dailyRecord.getWorkInformation().getRecordInfo().getWorkTimeCode().v());
		if (!predeteminate.isPresent())
			return converter.setData(dailyRecord).toDomain();

		// 日別勤怠(work）の勤務情報をもとに所定時間設定を取得する
		IntegrationOfDaily dailyCalc = converter.setData(dailyRecord).toDomain();

		// 計算日別勤怠(work）の出退勤を削除し、新規作成する
		dailyCalc.getAttendanceLeave().ifPresent(x -> {
			/// 出退勤を削除し
			x.getTimeLeavingWorks().removeIf(y -> y.getWorkNo().v() == 1 || y.getWorkNo().v() == 2);
		});
		/// 新規作成する
		if (!dailyCalc.getAttendanceLeave().isPresent()) {
			List<TimeLeavingWork> lstWork = new ArrayList<>();
			lstWork.add(TimeLeavingWork.createDefaultWithNo(1, TimeChangeMeans.APPLICATION));
			dailyCalc.setAttendanceLeave(Optional.of(new TimeLeavingOfDailyAttd(lstWork, new WorkTimes(1))));
		} else {
			dailyCalc.getAttendanceLeave().get().getTimeLeavingWorks().add(TimeLeavingWork.createDefaultWithNo(1, TimeChangeMeans.APPLICATION));
		}

		// 取得した時間帯を計算日別勤怠(work）の出退勤にセットする
		dailyCalc.getAttendanceLeave().get().getAttendanceLeavingWork(1).ifPresent(x -> {
			x.setAttendanceStamp(
					Optional.of(TimeActualStamp.createByAutomaticSet(predeteminate.get().getStartDateClock())));
			x.setLeaveStamp(Optional.of(TimeActualStamp.createByAutomaticSet(predeteminate.get().getEndDateClock())));
		});

		// 日別実績計算
		return require.calculateForRecord(CalculateOption.asDefault(),
				Arrays.asList(dailyCalc), Optional.empty(), ExecutionType.NORMAL_EXECUTION).get(0);

	}

	public static interface Require {

		// PredetemineTimeSettingRepository
		Optional<PredetemineTimeSetting> findByWorkTimeCode(String companyId, String workTimeCode);

		// DailyRecordConverter
		DailyRecordToAttendanceItemConverter createDailyConverter();
		
		public List<IntegrationOfDaily> calculateForRecord(CalculateOption calcOption,
				List<IntegrationOfDaily> integrationOfDaily, Optional<ManagePerCompanySet> companySet,
				ExecutionType reCalcAtr);
	}
}
