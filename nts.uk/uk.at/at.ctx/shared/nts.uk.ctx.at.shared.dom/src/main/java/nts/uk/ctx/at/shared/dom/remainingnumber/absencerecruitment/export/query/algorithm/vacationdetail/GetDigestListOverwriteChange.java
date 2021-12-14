package nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.vacationdetail;

import java.util.List;
import java.util.stream.Collectors;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimAbsMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.VacationDetails;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SubstitutionOfHDManagementData;

/**
 * @author thanh_nx
 *
 *         上書き変更を加えた消化一覧を取得する
 */
public class GetDigestListOverwriteChange {

	// 取得する
	public static VacationDetails get(Require require, String sid, DatePeriod dateData,
			RequestChangeDigestOccr changeDigest) {

		// $確定データ一覧 = require.振休管理データを取得する(社員ID, 期間)
		List<SubstitutionOfHDManagementData> lstAbsMngFix = require.getByYmdUnOffset(sid).stream()
				.filter(x -> x.isRemaing()).collect(Collectors.toList());

		// $暫定データ一覧 = require.暫定振休管理データを取得する(社員ID, 期間)
		List<InterimAbsMng> lstTemporary  = require.getAbsBySidDatePeriod(sid, dateData);
		

		List<AccumulationAbsenceDetail> lstDetailFix = lstAbsMngFix.stream()
				.map(x -> x.convertSeqVacationState()).collect(Collectors.toList());

		List<AccumulationAbsenceDetail> lstDetailTemporary = lstTemporary.stream().map(x -> x.convertSeqVacationState())
				.collect(Collectors.toList());
		return changeDigest.change(new VacationDetails(lstDetailFix), new VacationDetails(lstDetailTemporary));
	}

	public static interface Require {
		// [R-1] 暫定振休管理データを取得する
		// InterimRecAbasMngRepository
		List<InterimAbsMng> getAbsBySidDatePeriod(String sid, DatePeriod period);

		// [R-2] 振休管理データを取得する
		// SubstitutionOfHDManaDataRepository.getBySidAndDatePeriod
		List<SubstitutionOfHDManagementData> getByYmdUnOffset(String sid);
	}
}
