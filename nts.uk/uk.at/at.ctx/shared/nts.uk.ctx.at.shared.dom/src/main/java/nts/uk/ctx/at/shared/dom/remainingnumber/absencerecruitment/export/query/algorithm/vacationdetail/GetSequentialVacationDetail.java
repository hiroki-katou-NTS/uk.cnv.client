package nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.vacationdetail;

import java.util.List;
import java.util.Optional;

import lombok.val;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.CompenLeaveAggrResult;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.InterimRemain;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.breakinfo.FixedManagementDataMonth;

/**
 * @author thanh_nx
 *
 *         逐次発生の休暇明細を取得
 */
public class GetSequentialVacationDetail {

	/**
	 * @param cid              会社ID
	 * @param sid              社員ID
	 * @param dateData         期間
	 * @param fixManaDataMonth 追加用確定データ
	 * @param interimMng       上書き暫定データ
	 * @param processDate      上書き期間
	 * @param optBeforeResult  前回集計結果
	 * @return
	 */
	public static AfterChangeHolidayInfoResult process(Require require, String cid, String sid, DatePeriod dateData,
			FixedManagementDataMonth fixManaDataMonth, List<InterimRemain> interimMng, Optional<DatePeriod> processDate,
			Optional<CompenLeaveAggrResult> optBeforeResult) {

		// 消化発生の変更要求を作成
		val createDigestOccurr = CreateChangeReqDigestOccur.create(dateData, fixManaDataMonth, interimMng, processDate,
				optBeforeResult);

		// 振休の発生消化を変更を加えて取得する
		AfterChangeHolidayInfoResult afterChangeHolidayResult = GetModifiOutbreakDigest.get(require, sid, dateData,
				createDigestOccurr.getChangeDigest(), createDigestOccurr.getChangeOccr());

		// 変更後の振休振出情報を返す
		return afterChangeHolidayResult;
	}

	public static interface Require extends GetModifiOutbreakDigest.Require {

	}
}
