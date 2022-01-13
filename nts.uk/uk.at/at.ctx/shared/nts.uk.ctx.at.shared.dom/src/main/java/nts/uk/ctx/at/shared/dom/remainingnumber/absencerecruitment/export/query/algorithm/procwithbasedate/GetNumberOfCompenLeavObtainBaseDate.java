package nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.procwithbasedate;

import java.util.ArrayList;
import java.util.Optional;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.NumberCompensatoryLeavePeriodQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.AbsRecMngInPeriodRefactParamInput;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.CompenLeaveAggrResult;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.breakinfo.FixedManagementDataMonth;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureStartForEmployee;

/**
 * @author thanh_nx
 *
 *         基準日時点で取得可能な振休日数を取得する
 */
public class GetNumberOfCompenLeavObtainBaseDate {

	// 取得する
	public static LeaveRemainingDayNumber process(Require require, String cid, String sid, GeneralDate baseDate) {

		double remainResult = 0d;
		// Require．社員に対応締め開始日を取得する
		Optional<GeneralDate> closureStartOpt = GetClosureStartForEmployee.algorithm(require, new CacheCarrier(), sid);
		if (!closureStartOpt.isPresent())
			return new LeaveRemainingDayNumber(remainResult);

		// 期間内の振出振休を取得する
		val inputParam = new AbsRecMngInPeriodRefactParamInput(cid, sid,
				new DatePeriod(closureStartOpt.get(), baseDate), baseDate, false, false, new ArrayList<>(),
				new ArrayList<>(), new ArrayList<>(), Optional.empty(), Optional.empty(), Optional.empty(),
				new FixedManagementDataMonth());
		CompenLeaveAggrResult compenLeavResult = NumberCompensatoryLeavePeriodQuery.process(require, inputParam);

		// outputの残数を返す
		return new LeaveRemainingDayNumber(compenLeavResult.getRemainDay().v());
	}

	public static interface Require extends GetClosureStartForEmployee.RequireM1,
			NumberCompensatoryLeavePeriodQuery.Require, AccumulationAbsenceDetail.Require {

	}

}
