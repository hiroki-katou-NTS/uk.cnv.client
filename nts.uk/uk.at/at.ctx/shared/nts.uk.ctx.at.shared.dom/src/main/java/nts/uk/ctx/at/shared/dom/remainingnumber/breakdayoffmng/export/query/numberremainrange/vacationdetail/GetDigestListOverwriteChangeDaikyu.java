package nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.vacationdetail;

import java.util.List;
import java.util.stream.Collectors;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.vacationdetail.RequestChangeDigestOccr;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.VacationDetails;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimDayOffMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.CompensatoryDayOffManaData;

/**
 * @author thanh_nx
 *
 *         上書き変更を加えた消化一覧を取得する
 */
public class GetDigestListOverwriteChangeDaikyu {

	// 取得する
	public static VacationDetails get(Require require, String sid, DatePeriod dateData,
			RequestChangeDigestOccr changeDigest) {

		// $確定データ一覧
		List<CompensatoryDayOffManaData> lstFix = require.getFixByDayOffDatePeriod(sid).stream()
				.filter(x -> x.isRemaing()).collect(Collectors.toList());

		// $暫定データ一覧
		List<InterimDayOffMng> lstTemporary = require.getTempDayOffBySidPeriod(sid, dateData);

		// $確定明細一覧
		List<AccumulationAbsenceDetail> lstDetailFix = lstFix.stream().map(x -> x.convertSeqVacationState())
				.collect(Collectors.toList());

		// $暫定明細一覧
		List<AccumulationAbsenceDetail> lstDetailTemporary = lstTemporary.stream().map(x -> x.convertSeqVacationState())
				.collect(Collectors.toList());
		return changeDigest.change(new VacationDetails(lstDetailFix), new VacationDetails(lstDetailTemporary));
	}

	public static interface Require {

		// [R-1] 暫定代休管理データを取得する
		// InterimBreakDayOffMngRepository.getDayOffBySidPeriod
		List<InterimDayOffMng> getTempDayOffBySidPeriod(String sid, DatePeriod period);

		// [R-2] 代休管理データを取得する
		// ComDayOffManaDataRepository.getByDayOffDatePeriod
		List<CompensatoryDayOffManaData> getFixByDayOffDatePeriod(String sid);
	}
}
