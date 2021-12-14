package nts.uk.ctx.at.record.dom.applicationcancel.getreflect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition.DailyAfterAppReflectResult;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition.RCCreateDailyAfterApplicationeReflect;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ChangeDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerCompanySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyprocess.calc.CalculateOption;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;

/**
 * @author thanh_nx
 *
 *         申請反映結果を取得
 */
public class GetApplicationReflectionResult {

	public static Optional<IntegrationOfDaily> getReflectResult(Require require, String companyId, ApplicationShare application,
			GeneralDate baseDate, Optional<IntegrationOfDaily> dailyData) {

		// input.実績=emptyの場合は、実績を取得
		if (!dailyData.isPresent()) {
			dailyData = require.findDaily(application.getEmployeeID(), application.getAppDate().getApplicationDate());
		}

		if (!dailyData.isPresent())
			return dailyData;
		// 申請反映後の日別勤怠(work）を作成する（勤務実績）
		DailyAfterAppReflectResult dailyAppReflect = RCCreateDailyAfterApplicationeReflect.process(require, companyId, application,
				new DailyRecordOfApplication(new ArrayList<>(), ScheduleRecordClassifi.RECORD, dailyData.get()),
				baseDate);

		// 勤怠変更後の補正（日別実績の補正処理）
		IntegrationOfDaily dailyCorrect = require.correction(dailyAppReflect.getDomainDaily(),
				ChangeDailyAttendance.createChangeDailyAtt(dailyAppReflect.getLstItemId(), ScheduleRecordClassifi.RECORD));

		// 日別実績の修正からの計算
		List<IntegrationOfDaily> lstDailyCalc = require.calculateForRecord(CalculateOption.asDefault(),
				Arrays.asList(dailyCorrect), Optional.empty(), ExecutionType.NORMAL_EXECUTION);

		// 日別勤怠(work)を日別実績に変換する
		return lstDailyCalc.stream().findFirst();

	}

	public static interface Require extends RCCreateDailyAfterApplicationeReflect.Require{

		// DailyRecordShareFinder
		public Optional<IntegrationOfDaily> findDaily(String employeeId, GeneralDate date);

		// ICorrectionAttendanceRule
		public IntegrationOfDaily correction(IntegrationOfDaily domainDaily, ChangeDailyAttendance changeAtt);

		// CalculateDailyRecordServiceCenter
		public List<IntegrationOfDaily> calculateForRecord(CalculateOption calcOption,
				List<IntegrationOfDaily> integrationOfDaily, Optional<ManagePerCompanySet> companySet,
				ExecutionType reCalcAtr);

	}

}
