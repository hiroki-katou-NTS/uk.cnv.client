package nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.vacationdetail;

import java.util.List;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.vacationdetail.RequestChangeDigestOccr;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.VacationDetails;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManagementData;

/**
 * @author thanh_nx
 *
 *         上書き変更を加えた発生一覧を取得する
 */
public class GetOccListOverwriteChangeDaikyu {

	// [1] 取得する
	public static VacationDetails get(Require require, String sid, DatePeriod dateData,
			RequestChangeDigestOccr changeOcc) {

		// $確定データ一覧 = require.振出管理データを取得する(社員ID, 期間)
		val lstFix = require.getFixLeavByDayOffDatePeriod(sid).stream().filter(x -> x.isRemaing())
				.collect(Collectors.toList());

		// $暫定データ一覧 = require.暫定振出管理データを取得する(社員ID, 期間)
		val lstTemporary = require.getTempBreakBySidPeriod(sid, dateData);

		List<AccumulationAbsenceDetail> lstDetailFix = lstFix.stream().map(x -> x.convertSeqVacationState())
				.collect(Collectors.toList());

		List<AccumulationAbsenceDetail> lstDetailTemporary = lstTemporary.stream().map(x -> x.convertUnoffset())
				.collect(Collectors.toList());

		return changeOcc.change(new VacationDetails(lstDetailFix), new VacationDetails(lstDetailTemporary));
	}

	public static interface Require {

		// [R-1] 暫定休出管理データを取得する
		// InterimBreakDayOffMngRepository.getBySidPeriod
		List<InterimBreakMng> getTempBreakBySidPeriod(String sid, DatePeriod period);

		// [R-2] 休出管理データを取得する
		// LeaveManaDataRepository.getByDayOffDatePeriod
		List<LeaveManagementData> getFixLeavByDayOffDatePeriod(String sid);
	}
}
