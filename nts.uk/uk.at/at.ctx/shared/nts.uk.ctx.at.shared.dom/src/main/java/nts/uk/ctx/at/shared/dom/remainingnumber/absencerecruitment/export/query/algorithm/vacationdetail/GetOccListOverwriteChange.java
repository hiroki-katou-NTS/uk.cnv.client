package nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.vacationdetail;

import java.util.List;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.VacationDetails;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutManagementData;

/**
 * @author thanh_nx
 *
 *         上書き変更を加えた発生一覧を取得する
 */
public class GetOccListOverwriteChange {

	// [1] 取得する
	public static VacationDetails get(Require require, String sid, DatePeriod dateData,
			RequestChangeDigestOccr changeOcc) {

		// $確定データ一覧 = require.振出管理データを取得する(社員ID, 期間)
		val payoutMagData = require.getPayoutMana(sid).stream().filter(x -> x.isRemaing())
				.collect(Collectors.toList());

		// $暫定データ一覧 = require.暫定振出管理データを取得する(社員ID, 期間)
		val lstTemporary = require.getRecBySidDatePeriod(sid, dateData);
		
		
		List<AccumulationAbsenceDetail> lstDetailFix = payoutMagData.stream()
				.map(x -> x.convertSeqVacationState()).collect(Collectors.toList());

		List<AccumulationAbsenceDetail> lstDetailTemporary = lstTemporary.stream().map(x -> x.convertUnoffset())
				.collect(Collectors.toList());

		return changeOcc.change(new VacationDetails(lstDetailFix), new VacationDetails(lstDetailTemporary));
	}

	public static interface Require {

		// [R-1] 暫定振出管理データを取得する
		// InterimRecAbasMngRepository.getRecBySidDatePeriod
		List<InterimRecMng> getRecBySidDatePeriod(String sid, DatePeriod period);

		// [R-2] 振出管理データを取得する
		// PayoutManagementDataRepository.getBySidAndDatePeriod
		List<PayoutManagementData> getPayoutMana(String sid);
	}

}
