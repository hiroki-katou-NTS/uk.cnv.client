package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.otworkapply;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.scherec.application.overtime.AppOverTimeShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.ReflectAppDestination;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.BreakApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.ReflectFlexTime;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.algorithm.reflectbreak.ReflectApplicationTime;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.algorithm.subtransfer.TranferOvertimeCompensatoryApp;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition.ReflectAttendance;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition.ReflectWorkInformation;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 事前残業申請の反映
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BeforeOtWorkAppReflect {

	/**
	 * 休憩・外出を申請反映する
	 */
	private BreakApplication breakLeaveApplication;
	/**
	 * 【削除予定】始業終業を反映する
	 */
	private NotUseAtr reflectWorkInfoAtr;

	/**
	 * 残業時間を実績項目へ反映する
	 */
	private NotUseAtr reflectActualOvertimeHourAtr;

	public static BeforeOtWorkAppReflect create(int reflectWorkInfo, int reflectActualOvertimeHour,
			int reflectBeforeBreak) {
		return new BeforeOtWorkAppReflect(
				new BreakApplication(EnumAdaptor.valueOf(reflectBeforeBreak, NotUseAtr.class)),
				EnumAdaptor.valueOf(reflectWorkInfo, NotUseAtr.class),
				EnumAdaptor.valueOf(reflectActualOvertimeHour, NotUseAtr.class));
	}

	/**
	 * @author thanh_nx
	 *
	 *         事前残業申請の反映（勤務実績）
	 */

	public void processRC(Require require,  String cid, AppOverTimeShare overTimeApp, DailyRecordOfApplication dailyApp) {

		// 事前残業時間の反映
		ReflectApplicationTime.process(overTimeApp.getApplicationTime().getApplicationTime(), dailyApp,
				Optional.of(ReflectAppDestination.SCHEDULE));

		// [残業時間を実績項目へ反映する]をチェック
		if (this.getReflectActualOvertimeHourAtr() == NotUseAtr.USE) {
			// 残業時間の反映
			ReflectApplicationTime.process(overTimeApp.getApplicationTime().getApplicationTime(), dailyApp,
					Optional.of(ReflectAppDestination.RECORD));
			// 残業時間の代休振替(申請用)
			TranferOvertimeCompensatoryApp.process(require, cid, dailyApp.getDomain());
		}
		
		// 事前フレックス時間を反映する
		if(overTimeApp.getApplicationTime().getFlexOverTime().isPresent()) {
			ReflectFlexTime.beforeReflectFlex(dailyApp, overTimeApp.getApplicationTime().getFlexOverTime().get());
		}

		// 休憩の申請反映
		this.getBreakLeaveApplication().process(overTimeApp.getBreakTimeOp(), dailyApp);

	}

	public static interface Require extends ReflectWorkInformation.Require, ReflectAttendance.Require, TranferOvertimeCompensatoryApp.Require {

	}
}
