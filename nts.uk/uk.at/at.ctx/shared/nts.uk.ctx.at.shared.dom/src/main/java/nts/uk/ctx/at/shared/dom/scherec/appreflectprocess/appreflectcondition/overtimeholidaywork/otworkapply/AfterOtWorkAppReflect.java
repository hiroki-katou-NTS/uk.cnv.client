package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.otworkapply;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.scherec.application.overtime.AppOverTimeShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.ReflectAppDestination;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.BreakApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.OthersReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.ReflectFlexTime;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.algorithm.reflectbreak.ReflectApplicationTime;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.algorithm.subtransfer.TranferOvertimeCompensatoryApp;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition.ReflectAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 事後残業申請の反映
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AfterOtWorkAppReflect {
	/**
	 * その他項目を反映する
	 */
	private OthersReflect othersReflect;

	/**
	 * 休憩・外出を申請反映する
	 */
	private BreakApplication breakLeaveApplication;

	/**
	 * 出退勤を反映する
	 */
	private NotUseAtr workReflect;

	public static AfterOtWorkAppReflect create(int workReflect, int reflectPaytime, int reflectDivergence,
			int reflectBreakOuting) {
		return new AfterOtWorkAppReflect(new OthersReflect(EnumAdaptor.valueOf(reflectDivergence, NotUseAtr.class),
//                        EnumAdaptor.valueOf(reflectOptional, NotUseAtr.class),
				EnumAdaptor.valueOf(reflectPaytime, NotUseAtr.class)),
				new BreakApplication(EnumAdaptor.valueOf(reflectBreakOuting, NotUseAtr.class)),
				EnumAdaptor.valueOf(workReflect, NotUseAtr.class));
	}

	/**
	 * @author thanh_nx
	 *
	 *         事後残業申請の反映（勤務実績）
	 */

	public List<Integer> processAfter(RequireAfter require, String cid, AppOverTimeShare overTimeApp,
			DailyRecordOfApplication dailyApp) {

		List<Integer> lstId = new ArrayList<>();
		// [出退勤を反映する]をチェック
		if (this.getWorkReflect() == NotUseAtr.USE) {
			// 出退勤の反映
			lstId.addAll(ReflectAttendance.reflect(require, cid, overTimeApp.getWorkHoursOp(), ScheduleRecordClassifi.RECORD,
					dailyApp, Optional.of(true), Optional.of(true), Optional.of(TimeChangeMeans.APPLICATION)));
		}

		// 残業時間の反映
		ReflectApplicationTime.process(overTimeApp.getApplicationTime().getApplicationTime(), dailyApp,
				Optional.of(ReflectAppDestination.RECORD));
		
		//フレックス時間を反映する
		if(overTimeApp.getApplicationTime().getFlexOverTime().isPresent()) {
			ReflectFlexTime.reflectFlex(dailyApp, overTimeApp.getApplicationTime().getFlexOverTime().get());
		}


		// その他項目の反映
		this.getOthersReflect().process(overTimeApp.getApplicationTime(), dailyApp);

		// 休憩の申請反映
		this.getBreakLeaveApplication().process(overTimeApp.getBreakTimeOp(), dailyApp);

		// 残業時間の代休振替
		TranferOvertimeCompensatoryApp.process(require, cid, dailyApp.getDomain());
		return lstId;
	}

	public static interface RequireAfter extends TranferOvertimeCompensatoryApp.Require, ReflectAttendance.Require {

	}
}
