package nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.vacationdetail;

import java.util.List;
import java.util.Optional;

import lombok.val;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.SubstituteHolidayAggrResult;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.InterimRemain;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.breakinfo.FixedManagementDataMonth;

/**
 * @author thanh_nx
 *
 *         逐次発生の休暇明細を取得
 */
public class GetSequentialVacationDetailDaikyu {

	public static AfterChangeHolidayDaikyuInfoResult process(Require require, String cid, String sid, DatePeriod dateData,
			FixedManagementDataMonth fixManaDataMonth, List<InterimRemain> interimMng, Optional<DatePeriod> processDate,
			Optional<SubstituteHolidayAggrResult> optBeforeResult) {

		// 代休の発生消化を変更を加えて取得する
		val createDigestOccurr = CreateChangeReqDigestOccurDaikyu.create(dateData, fixManaDataMonth, interimMng,
				processDate, optBeforeResult);

		// 変更後の代休休出情報を返す
		return GetCompenChangeOccDigest
				.get(require, sid, dateData, createDigestOccurr.getChangeDigest(), createDigestOccurr.getChangeOccr());
	}

	public static interface Require extends GetCompenChangeOccDigest.Require {

	}
}
