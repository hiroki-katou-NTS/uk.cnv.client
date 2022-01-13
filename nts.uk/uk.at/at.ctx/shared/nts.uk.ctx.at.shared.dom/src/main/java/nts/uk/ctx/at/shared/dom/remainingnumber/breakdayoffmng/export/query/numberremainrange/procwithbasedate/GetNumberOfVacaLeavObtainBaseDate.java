package nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.procwithbasedate;

import java.util.ArrayList;
import java.util.Optional;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.NumberRemainVacationLeaveRangeQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.BreakDayOffRemainMngRefactParam;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.SubstituteHolidayAggrResult;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.breakinfo.FixedManagementDataMonth;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureStartForEmployee;

/**
 * @author thanh_nx
 *
 *         基準日時点で取得可能な代休日数を取得する
 */
public class GetNumberOfVacaLeavObtainBaseDate {

	// 取得する
	public static NumberConsecutiveVacation process(Require require, String cid, String sid, GeneralDate baseDate) {
		// Require．社員に対応締め開始日を取得する
		// 残日数
		double remainDayResult = 0d;

		Optional<GeneralDate> closureStartOpt = GetClosureStartForEmployee.algorithm(require, new CacheCarrier(), sid);
		if (!closureStartOpt.isPresent())
			return new NumberConsecutiveVacation(new LeaveRemainingDayNumber(remainDayResult), new LeaveRemainingTime(0));

		// 期間内の休出代休残数を取得する
		val inputParam = new BreakDayOffRemainMngRefactParam(cid, sid, new DatePeriod(closureStartOpt.get(), baseDate),
				false, baseDate, false, new ArrayList<>(), Optional.empty(), Optional.empty(), new ArrayList<>(),
				new ArrayList<>(), Optional.empty(), new FixedManagementDataMonth());
		SubstituteHolidayAggrResult subsHolResult = NumberRemainVacationLeaveRangeQuery
				.getBreakDayOffMngInPeriod(require, inputParam);

		// output残数に残時間を入れる
		// outputの残数を返す
		return new NumberConsecutiveVacation(new LeaveRemainingDayNumber(subsHolResult.getRemainDay().v()),
				new LeaveRemainingTime(subsHolResult.getRemainTime().v()));
	}

	public static interface Require extends GetClosureStartForEmployee.RequireM1,
			NumberRemainVacationLeaveRangeQuery.Require, AccumulationAbsenceDetail.Require {

	}
}
