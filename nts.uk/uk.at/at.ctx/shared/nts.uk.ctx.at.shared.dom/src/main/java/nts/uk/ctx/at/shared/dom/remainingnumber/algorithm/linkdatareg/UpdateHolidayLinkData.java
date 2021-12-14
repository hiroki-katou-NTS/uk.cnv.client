package nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.linkdatareg;

import java.util.List;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.OccurrenceDigClass;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.vacationdetail.AfterChangeHolidayInfoResult;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.vacationdetail.GetModifiOutbreakDigest;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.vacationdetail.RequestChangeDigestOccr;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimAbsMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.VacationDetails;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutSubofHDManagement;

/**
 * @author thanh_nx
 *
 *振出振休紐付けデータを更新する
 */
public class UpdateHolidayLinkData {

	// [1] 更新する
	public static AtomTask updateProcess(Require require, String sid, List<GeneralDate> lstDate,
			List<InterimAbsMng> lstAbsMng, List<InterimRecMng> lstRecMng) {

		//	$期間 
		DatePeriod period = new DatePeriod(GeneralDate.min(), GeneralDate.max());
		// $変更後の振休振出情報=
		AfterChangeHolidayInfoResult afterResult = updateAfterChange(require, sid, period, lstDate, lstRecMng, lstAbsMng);
		//＄暫定振出
		List<InterimRecMng> furisyutsu = updateNumberUnoffOccur(afterResult, lstRecMng);

		//暫定振休
		List<InterimAbsMng> furikyu = updateNumberUnoffDigest(afterResult, lstAbsMng);
		
		// ＄紐付け情報
		val linkCouple = afterResult.getSeqVacInfoList().getSeqVacInfoList().stream()
				.map(x -> PayoutSubofHDManagement.of(sid, x)).collect(Collectors.toList());
		
		//$暫定振出管理を削除する年月日一覧
		List<GeneralDate> lstFurisyutsu = furisyutsu.stream().map(x -> x.getYmd()).filter(x -> !lstDate.contains(x)).collect(Collectors.toList());
		lstFurisyutsu.addAll(lstDate);
		
		//	$暫定振休管理を削除する年月日一覧
		List<GeneralDate> lstFurikyu= furikyu.stream().map(x -> x.getYmd()).filter(x -> !lstDate.contains(x)).collect(Collectors.toList());
		lstFurikyu.addAll(lstDate);
		
		return AtomTask.of(() -> {
			// [R-1] 振出振休紐付け管理を削除する
			require.deletePayoutWithPeriod(sid, period);

			// [R-2] 振出振休紐付け管理を登録する
			require.insertPayoutList(linkCouple);

			// [R-3] 暫定振出管理を削除する
			require.deleteRecMngWithDateList(sid, lstFurisyutsu);

			// [R-4] 暫定振出管理を登録する
			require.insertRecMngList(furisyutsu);

			// [R-5] 暫定振休管理を削除する
			require.deleteAbsMngWithDateList(sid, lstFurikyu);

			// [R-6] 暫定振休管理を登録する
			require.insertAbsMngList(furikyu);
		});
	}

	// [1] 変更要求と紐付いている暫定データを取得する(発生)
	private static List<InterimRecMng> getOccurTempDataFromAssoci(Require require, String sid,
			List<GeneralDate> lstDate) {
		// $紐付け一覧
		List<PayoutSubofHDManagement> linkData = require.getByListDate(sid, lstDate);

		return require.getRecBySidDateList(sid,
				linkData.stream().map(x -> x.getAssocialInfo().getOutbreakDay()).collect(Collectors.toList()));
	}

	// [2] 変更要求と紐付いている暫定データを取得する(消化)
	private static List<InterimAbsMng> getDigestTempDataFromAssoci(Require require, String sid,
			List<GeneralDate> lstDate) {
		// $紐付け一覧
		List<PayoutSubofHDManagement> linkData = require.getByListOccDate(sid, lstDate);

		return require.getAbsBySidDateList(sid,
				linkData.stream().map(x -> x.getAssocialInfo().getDateOfUse()).collect(Collectors.toList()));
	}

	// [3] 発生変更要求を作成する
	private static RequestChangeDigestOccr createOccrChangeRequest(Require require, String sid,
			List<GeneralDate> lstDate, List<InterimRecMng> lstRecMng) {
		// $紐付いている発生一覧
		val lstInterimRecMng = getOccurTempDataFromAssoci(require, sid, lstDate).stream()
				.filter(x -> lstRecMng.stream().noneMatch(y -> y.getYmd().equals(x.getYmd())))
				.collect(Collectors.toList());
		lstRecMng.addAll(lstInterimRecMng);
		// ＄逐次発生一覧
		val lstOccr = lstRecMng.stream().map(x -> x.convertUnoffset()).collect(Collectors.toList());
		// $発生の変更要求
		return RequestChangeDigestOccr.createChangeRequestbyDate(lstDate, new VacationDetails(lstOccr));
	}
		
	// [4] 消化変更要求を作成する]
	private static RequestChangeDigestOccr createDigestChangeRequest(Require require, String sid,
			List<GeneralDate> lstDate, List<InterimAbsMng> lstAbsMng) {
		// $紐付いている消化一覧
		val lstInterimAbsMng = getDigestTempDataFromAssoci(require, sid, lstDate).stream()
				.filter(x -> lstAbsMng.stream().noneMatch(y -> y.getYmd().equals(x.getYmd())))
				.collect(Collectors.toList());
		lstAbsMng.addAll(lstInterimAbsMng);
		// ＄逐次消化一覧
		val lstDigest = lstAbsMng.stream().map(x -> x.convertSeqVacationState()).collect(Collectors.toList());
		// $消化の変更要求
		return RequestChangeDigestOccr.createChangeRequestbyDate(lstDate, new VacationDetails(lstDigest));

	}
		
		//	[5] 変更する
		private static AfterChangeHolidayInfoResult updateAfterChange(Require require, String sid,
				DatePeriod period,
				List<GeneralDate> lstDate, List<InterimRecMng> lstRecMng, List<InterimAbsMng> lstAbsMng) {
			
			// $発生の変更要求
			val changeOccr = createOccrChangeRequest(require, sid, lstDate, lstRecMng);

			// $消化の変更要求
			val changeDigest = createDigestChangeRequest(require, sid, lstDate, lstAbsMng);
			
			return  GetModifiOutbreakDigest.getAndOffset(require, sid, period,
					changeDigest, changeOccr);
			
		}

		// [6] 発生一覧の未相殺数を更新する
	private static List<InterimRecMng> updateNumberUnoffOccur(AfterChangeHolidayInfoResult afterResult,
			List<InterimRecMng> lstRecMng) {
		// $変更後の発生一覧
		List<AccumulationAbsenceDetail> furisyutsuChange = afterResult.getVacationDetail().getLstAcctAbsenDetail()
				.stream().filter(x -> x.getOccurrentClass() == OccurrenceDigClass.OCCURRENCE)
				.collect(Collectors.toList());
		return lstRecMng.stream().map(x -> {
			val dataTemp = furisyutsuChange.stream().filter(y -> y.getManageId().equals(x.getRemainManaID()))
					.findFirst();
			return dataTemp.map(z -> x.updateUnoffsetNum(z)).orElse(null);
		}).collect(Collectors.toList());

	}
		
	// [7] 消化一覧の未相殺数を更新する]
	private static List<InterimAbsMng> updateNumberUnoffDigest(AfterChangeHolidayInfoResult afterResult,
			List<InterimAbsMng> lstAbsMng) {
		// $変更後の消化一覧
		List<AccumulationAbsenceDetail> furikyuChange = afterResult.getVacationDetail().getLstAcctAbsenDetail().stream()
				.filter(x -> x.getOccurrentClass() == OccurrenceDigClass.DIGESTION).collect(Collectors.toList());
		return lstAbsMng.stream().map(x -> {
			val dataTemp = furikyuChange.stream().filter(y -> y.getManageId().equals(x.getRemainManaID())).findFirst();
			return dataTemp.map(z -> x.updateUnoffsetNum(z)).orElse(null);
		}).collect(Collectors.toList());
	}
		
		
	public static interface Require extends GetModifiOutbreakDigest.Require {

		// [R-1] 振出振休紐付け管理を削除する
		// PayoutSubofHDManaRepository.deletePayoutWithPeriod
		void deletePayoutWithPeriod(String sid, DatePeriod period);

		// [R-2] 振出振休紐付け管理を登録する
		// PayoutSubofHDManaRepository.
		void insertPayoutList(List<PayoutSubofHDManagement> lstDomain);

		// [R-3] 暫定振出管理を削除する
		// InterimRecAbasMngRepository.deleteRecMngWithPeriod
		void deleteRecMngWithDateList(String sid, List<GeneralDate> lstDate);

		// [R-4] 暫定振出管理を登録する
		// InterimRecAbasMngRepository.insertRecMngList
		void insertRecMngList(List<InterimRecMng> lstDomain);

		// [R-5] 暫定振休管理を削除する
		// InterimRecAbasMngRepository.deleteAbsMngWithPeriod
		void deleteAbsMngWithDateList(String sid, List<GeneralDate> lstDate);

		// [R-6] 暫定振休管理を登録する
		// InterimRecAbasMngRepository.insertAbsMngList
		void insertAbsMngList(List<InterimAbsMng> lstDomain);
		
		//PayoutSubofHDManaRepository
		List<PayoutSubofHDManagement> getByListDate(String sid, List<GeneralDate> lstDate);

		//PayoutSubofHDManaRepository
		List<PayoutSubofHDManagement> getByListOccDate(String sid, List<GeneralDate> lstDate);
		
		// InterimRecAbasMngRepository
		List<InterimAbsMng> getAbsBySidDateList(String sid, List<GeneralDate> lstDate);
		
		//InterimRecAbasMngRepository
		List<InterimRecMng> getRecBySidDateList(String sid, List<GeneralDate> lstDate);

	}

}
