package nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.vacationdetail;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.SeqVacationAssociationInfoList;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.VacationDetails;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutSubofHDManagement;

/**
 * @author thanh_nx
 *
 *         振休の発生消化を変更を加えて取得する
 */
public class GetModifiOutbreakDigest {

	//  取得して相殺する
	public static AfterChangeHolidayInfoResult getAndOffset(Require require, String sid, DatePeriod dateData,
			RequestChangeDigestOccr changeDigest, RequestChangeDigestOccr changeOccr) {

		val afterChangeResult = changeAccordChangeRequest(require, sid, dateData, changeDigest, changeOccr);

		// ＄変更後．紐付けされている振出の未相殺数を更新する（）
		afterChangeResult.getOffsetNotAssoci();

		// ＄変更後．紐付けされている振休の未相殺数を更新する（
		afterChangeResult.getVactionNotAssoci();

		return afterChangeResult;
	}

	// 取得する
	public static AfterChangeHolidayInfoResult get(Require require, String sid, DatePeriod dateData,
			RequestChangeDigestOccr changeDigest, RequestChangeDigestOccr changeOccr) {
		return changeAccordChangeRequest(require, sid, dateData, changeDigest, changeOccr);
	}

	// 変更要求に従って変更する
	private static AfterChangeHolidayInfoResult changeAccordChangeRequest(Require require, String sid, DatePeriod dateData,
			RequestChangeDigestOccr changeDigest, RequestChangeDigestOccr changeOccr) {
		// $消化一覧 = 上書き変更を加えた消化一覧を取得する.取得する(require, 社員ID, 期間, 消化の変更要求)
		VacationDetails digest = GetDigestListOverwriteChange.get(require, sid, dateData, changeDigest);
		// $発生一覧 = 上書き変更を加えた発生一覧を取得する.取得する(require, 社員ID, 期間, 発生の変更要求)
		VacationDetails occr = GetOccListOverwriteChange.get(require, sid, dateData, changeOccr);

		// $紐付け一覧 = require.振出振休紐付け管理を取得する(社員ID, 期間)
		List<PayoutSubofHDManagement> lstPayoutSubofHD = require.getOccDigetByListSid(sid, dateData);

		SeqVacationAssociationInfoList seqVacAssociInfo = new SeqVacationAssociationInfoList(
				lstPayoutSubofHD.stream().map(x -> x.getAssocialInfo()).collect(Collectors.toList()));

		// $補正後の紐付け一覧
		val assocAfterCorr = seqVacAssociInfo.matchAssocStateOccAndDigest(occr, digest);

		// $消化発生一覧 = 逐次発生の休暇明細一覧#作成する()
		val digestOcc = new VacationDetails(new ArrayList<>());
		digestOcc.addDetail(digest.getLstAcctAbsenDetail());
		digestOcc.addDetail(occr.getLstAcctAbsenDetail());

		val afterChangeResult = new AfterChangeHolidayInfoResult(digestOcc, assocAfterCorr);

		return afterChangeResult;
	}

	public static interface Require extends GetDigestListOverwriteChange.Require, GetOccListOverwriteChange.Require {

		// [R-1] 振出振休紐付け管理を取得する
		List<PayoutSubofHDManagement> getOccDigetByListSid(String sid, DatePeriod date);
	}
}
