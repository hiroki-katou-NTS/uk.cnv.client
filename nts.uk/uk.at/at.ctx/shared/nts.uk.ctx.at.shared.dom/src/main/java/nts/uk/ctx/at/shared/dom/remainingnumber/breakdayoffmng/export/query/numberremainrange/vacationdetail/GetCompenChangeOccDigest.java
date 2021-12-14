package nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.vacationdetail;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.vacationdetail.RequestChangeDigestOccr;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.SeqVacationAssociationInfoList;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.VacationDetails;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveComDayOffManagement;

/**
 * @author thanh_nx
 *
 *         代休の発生消化を変更を加えて取得する
 */
public class GetCompenChangeOccDigest {

	// 取得して相殺する
	public static AfterChangeHolidayDaikyuInfoResult getAndOffset(Require require, String sid, DatePeriod dateData,
			RequestChangeDigestOccr changeDigest, RequestChangeDigestOccr changeOccr) {
		// ＄変更後
		val afterChangeResult = changeAccordChangeRequest(require, sid, dateData, changeDigest, changeOccr);

		// ＄変更後．紐付けされている休出の未相殺数を更新する（）
		afterChangeResult.updateUnoffsetAssociVacation();

		// ＄変更後．紐付けされている代休の未相殺数を更新する（）
		afterChangeResult.updateUnoffsetsAssociSubstVacation();

		return afterChangeResult;
	}
	
	// 取得する
	public static AfterChangeHolidayDaikyuInfoResult get(Require require, String sid, DatePeriod dateData,
			RequestChangeDigestOccr changeDigest, RequestChangeDigestOccr changeOccr) {
		return changeAccordChangeRequest(require, sid, dateData, changeDigest, changeOccr);
	}
	
	// 変更要求に従って変更する
	private static AfterChangeHolidayDaikyuInfoResult changeAccordChangeRequest(Require require, String sid,
			DatePeriod dateData, RequestChangeDigestOccr changeDigest, RequestChangeDigestOccr changeOccr) {

		// $消化一覧 =
		val digest = GetDigestListOverwriteChangeDaikyu.get(require, sid, dateData, changeDigest);

		// 発生一覧
		val occr = GetOccListOverwriteChangeDaikyu.get(require, sid, dateData, changeOccr);

		// $紐付け一覧
		val lstCouple = require.getDigestOccByListComId(sid, dateData);
		// $逐次発生の紐付け情報一覧
		SeqVacationAssociationInfoList seqVacAssociInfo = new SeqVacationAssociationInfoList(
				lstCouple.stream().map(x -> x.getAssocialInfo()).collect(Collectors.toList()));

		// $補正後の紐付け一覧
		val assocAfterCorr = seqVacAssociInfo.matchAssocStateOccAndDigest(occr, digest);

		// $消化発生一覧
		val digestOcc = new VacationDetails(new ArrayList<>());
		digestOcc.addDetail(digest.getLstAcctAbsenDetail());
		digestOcc.addDetail(occr.getLstAcctAbsenDetail());

		// ＄変更後
		val afterChangeResult = new AfterChangeHolidayDaikyuInfoResult(digestOcc, assocAfterCorr);
		return afterChangeResult;
	}

	public static interface Require
			extends GetDigestListOverwriteChangeDaikyu.Require, GetOccListOverwriteChangeDaikyu.Require {

		// [R-1] 休出代休紐付け管理を取得する
		// LeaveComDayOffManaRepository.getDigestOccByListComId
		List<LeaveComDayOffManagement> getDigestOccByListComId(String sid, DatePeriod period);
	}
}
