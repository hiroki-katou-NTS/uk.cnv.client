package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.hdworkapply;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.at.shared.dom.scherec.application.holidayworktime.AppHolidayWorkShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.ReflectAppDestination;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.BreakApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.algorithm.reflectbreak.ReflectApplicationTime;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.algorithm.subtransfer.TranferHdWorkCompensatoryApp;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition.DailyAfterAppReflectResult;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition.ReflectAttendance;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition.ReflectDirectBounceClassifi;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition.ReflectStartEndWork;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition.ReflectWorkInformation;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.workchangeapp.ReflectWorkChangeApp.WorkInfoDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * refactor 4 refactor4 事前休日出勤申請の反映
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BeforeHdWorkAppReflect extends DomainObject {
	/**
	 * 休日出勤時間を実績項目へ反映する
	 */
	private NotUseAtr reflectActualHolidayWorkAtr;

	/**
	 * @author thanh_nx
	 *
	 *         事前休日出勤申請の反映（勤務実績）
	 */
	public DailyAfterAppReflectResult process(Require require, String cid, AppHolidayWorkShare holidayApp,
			DailyRecordOfApplication dailyApp) {

		List<Integer> lstId = new ArrayList<Integer>();
		// 勤務情報を勤務情報DTOへセット
		WorkInfoDto workInfoDto = new WorkInfoDto(
				Optional.ofNullable(holidayApp.getWorkInformation().getWorkTypeCode()),
				holidayApp.getWorkInformation().getWorkTimeCodeNotNull());

		// 勤務情報の反映
		lstId.addAll(ReflectWorkInformation.reflectInfo(require, cid, workInfoDto, dailyApp, Optional.of(true),
				Optional.of(true)));

		// 始業終業の反映
		lstId.addAll(
				ReflectStartEndWork.reflect(require, cid, dailyApp, holidayApp.getWorkingTimeList()));

		// 事前休出時間の反映
		ReflectApplicationTime.process(holidayApp.getApplicationTime().getApplicationTime(), dailyApp,
				Optional.of(ReflectAppDestination.SCHEDULE));
		
		//直行直帰区分の反映
		lstId.addAll(ReflectDirectBounceClassifi.reflect(dailyApp, holidayApp.getBackHomeAtr(), holidayApp.getGoWorkAtr()));

		// [休日出勤時間を実績項目へ反映する]をチェック
		if (this.getReflectActualHolidayWorkAtr() == NotUseAtr.USE) {
			// 休出時間の反映
			ReflectApplicationTime.process(holidayApp.getApplicationTime().getApplicationTime(), dailyApp,
					Optional.of(ReflectAppDestination.RECORD));
			//休日出勤時間の代休振替(申請用)
			TranferHdWorkCompensatoryApp.process(require, cid, dailyApp.getDomain());
		}
		//ドメイン「休憩の申請反映」を作成する
		//休憩の申請反映
		new BreakApplication(NotUseAtr.USE).process(holidayApp.getBreakTimeList(), dailyApp);
		return new DailyAfterAppReflectResult(dailyApp, lstId);
	}

	public static interface Require extends ReflectWorkInformation.Require,ReflectStartEndWork.Require, TranferHdWorkCompensatoryApp.Require {

	}

	/**
	 * @author thanh_nx
	 *
	 *
	 *         事前休日出勤申請の反映(勤務予定）
	 */

	public DailyAfterAppReflectResult processSC(Require require, String cid, AppHolidayWorkShare holidayApp,
			DailyRecordOfApplication dailyApp) {

		List<Integer> lstId = new ArrayList<Integer>();
		// 勤務情報を勤務情報DTOへセット
		WorkInfoDto workInfoDto = new WorkInfoDto(
				Optional.ofNullable(holidayApp.getWorkInformation().getWorkTypeCode()),
				holidayApp.getWorkInformation().getWorkTimeCodeNotNull());

		// 勤務情報の反映
		lstId.addAll(ReflectWorkInformation.reflectInfo(require, cid, workInfoDto, dailyApp, Optional.of(true),
				Optional.of(true)));

		// 出退勤の反映
		lstId.addAll(ReflectAttendance.reflect(require, cid, holidayApp.getWorkingTimeList(), ScheduleRecordClassifi.RECORD, dailyApp,
				Optional.of(true), Optional.of(true), Optional.of(TimeChangeMeans.APPLICATION)));
		
		//直行直帰区分の反映
		lstId.addAll(ReflectDirectBounceClassifi.reflect(dailyApp, holidayApp.getBackHomeAtr(), holidayApp.getGoWorkAtr()));

		// ドメイン「休憩の申請反映」を作成する
		// 休憩・外出の申請反映
		new BreakApplication(NotUseAtr.USE).process(holidayApp.getBreakTimeList(), dailyApp);
		return new DailyAfterAppReflectResult(dailyApp, lstId);
	}

}
