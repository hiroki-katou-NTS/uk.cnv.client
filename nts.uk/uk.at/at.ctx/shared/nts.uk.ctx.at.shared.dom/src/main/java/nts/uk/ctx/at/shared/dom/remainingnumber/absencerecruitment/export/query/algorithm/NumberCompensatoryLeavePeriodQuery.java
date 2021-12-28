package nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.AbsDaysRemain;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.PauseError;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.AbsRecMngInPeriodRefactParamInput;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.CompenLeaveAggrResult;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.CompenSuspensionAggrResult;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.vacationdetail.CalcNumCarryAtBeginMonthFromHol;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.vacationdetail.GetSequentialVacationDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.DayOffError;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.AccumulationAbsenceDetailComparator;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.SeqVacationAssociationInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.VacationDetails;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.vacationdetail.CorrectDaikyuFurikyuFixed;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.empinfo.grantremainingdata.daynumber.ReserveLeaveRemainingDayNumber;

/**
 * @author ThanhNX
 *
 *         期間内の振出振休残数を取得する
 */
public class NumberCompensatoryLeavePeriodQuery {

	private NumberCompensatoryLeavePeriodQuery() {
	};

	/**
	 * Requestlist204 期間内の振出振休残数を取得する
	 * 
	 * @return
	 */
	public static CompenLeaveAggrResult process(Require require, AbsRecMngInPeriodRefactParamInput inputParam) {

		CompenLeaveAggrResult result = new CompenLeaveAggrResult();
		//逐次発生の休暇明細一覧を取得
		val sequentialVacaDetail = GetSequentialVacationDetail.process(require, inputParam.getCid(),
				inputParam.getSid(), inputParam.getDateData(), inputParam.getFixManaDataMonth(),
				inputParam.getInterimMng(), inputParam.getProcessDate(), inputParam.getOptBeforeResult());
		List<AccumulationAbsenceDetail> lstAbsRec = sequentialVacaDetail.getVacationDetail().getLstAcctAbsenDetail();

		//休出振出管理データを補正する。
		CorrectDaikyuFurikyuFixed.correct(sequentialVacaDetail.getVacationDetail(),
				sequentialVacaDetail.getSeqVacInfoList());
				
		// 振休振出から月初の繰越数を計算
		val calcNumCarry = CalcNumCarryAtBeginMonthFromHol.calculate(require, inputParam.getCid(), inputParam.getSid(),
				inputParam.getDateData(), sequentialVacaDetail.getVacationDetail(), inputParam.isMode());
		result.setCarryoverDay(calcNumCarry);
	
		// 「振出振休明細」をソートする
		lstAbsRec.sort(new AccumulationAbsenceDetailComparator());

		// 振出と振休の相殺処理
		Pair<Optional<DayOffError>, List<SeqVacationAssociationInfo>> lstSeqVacation = CompenSuspensionOffsetProcess.process(require,
				inputParam.getCid(), inputParam.getSid(), inputParam.getDateData().end(), lstAbsRec);
		 List<SeqVacationAssociationInfo> linkData = lstSeqVacation.getRight();
		 linkData.addAll(sequentialVacaDetail.getSeqVacInfoList().getSeqVacInfoList());
		result.setLstSeqVacation(linkData);
		lstSeqVacation.getLeft().ifPresent(x -> result.getPError().add(PauseError.PREFETCH_ERROR));
		// 残数と未消化を集計する
		AbsDaysRemain absRemain = TotalRemainUndigest.process(lstAbsRec, inputParam.getScreenDisplayDate(),
				inputParam.isMode());
		result.setRemainDay(new ReserveLeaveRemainingDayNumber(absRemain.getRemainDays()));
		result.setUnusedDay(new ReserveLeaveRemainingDayNumber(absRemain.getUnDigestedDays()));

		// 発生数・使用数を計算する
		CompenSuspensionAggrResult compenSusAggr = CalcCompenNumberOccurUses.calc(lstAbsRec, inputParam.getDateData());
		result.setOccurrenceDay(new ReserveLeaveRemainingDayNumber(compenSusAggr.getSuOccurDay()));
		result.setDayUse(new ReserveLeaveRemainingDayNumber(compenSusAggr.getSuDayUse()));
		// 振休のエラーチェックをする
		if (result.getRemainDay().v() < 0) {
			result.getPError().add(PauseError.PAUSEREMAINNUMBER);
		}
		// 次の集計期間の開始日を計算する
		result.setNextDay(Finally.of(inputParam.getDateData().end().addDays(1)));
		result.setVacationDetails(new VacationDetails(lstAbsRec));
		return result;

	}

	public static interface Require extends GetSequentialVacationDetail.Require, CalcNumCarryAtBeginMonthFromHol.Require,
			CompenSuspensionOffsetProcess.Require {

	}

}
