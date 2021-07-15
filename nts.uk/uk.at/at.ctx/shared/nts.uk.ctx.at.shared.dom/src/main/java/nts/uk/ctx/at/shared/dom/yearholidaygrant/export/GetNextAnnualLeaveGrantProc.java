package nts.uk.ctx.at.shared.dom.yearholidaygrant.export;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSetting;
import nts.uk.ctx.at.shared.dom.workingcondition.LaborContractTime;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.GrantHdTbl;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.GrantHdTblSet;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.LengthServiceTbl;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.UseSimultaneousGrant;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.LimitedTimeHdTime;;

/**
 * 処理：次回年休付与を取得する
 * @author shuichi_ishida
 */
public class GetNextAnnualLeaveGrantProc {

	public GetNextAnnualLeaveGrantProc() {
	}

	/**
	 * 次回年休付与を取得する
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param grantTableCode 年休付与テーブル設定コード
	 * @param entryDate 入社年月日
	 * @param criteriaDate 年休付与基準日
	 * @param period 期間
	 * @param isSingleDay 単一日フラグ
	 * @return 次回年休付与リスト
	 */
	public List<NextAnnualLeaveGrant> algorithm(
			RequireM1 require, CacheCarrier cacheCarrier,
			String companyId,
			String employeeId,
			String grantTableCode,
			GeneralDate entryDate,
			GeneralDate criteriaDate,
			DatePeriod period,
			boolean isSingleDay){

		return algorithm(require, cacheCarrier, companyId, employeeId, grantTableCode,
				entryDate, criteriaDate, period, isSingleDay,
				Optional.empty(), Optional.empty(), Optional.empty());
	}

	/**
	 * 次回年休付与を取得する （※付与年月日、期限日をセットするだけに変更）
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param grantTableCode 年休付与テーブル設定コード
	 * @param entryDate 入社年月日
	 * @param criteriaDate 年休付与基準日
	 * @param period 期間
	 * @param isSingleDay 単一日フラグ
	 * @param grantHdTblSetParam 年休付与テーブル設定
	 * @param lengthServiceTblsParam 勤続年数テーブルリスト
	 * @param closureStartDate 締め開始日
	 * @return 次回年休付与リスト
	 */
	public static List<NextAnnualLeaveGrant> algorithm(RequireM1 require, CacheCarrier cacheCarrier,
			String companyId, String employeeId, String grantTableCode, GeneralDate entryDate, GeneralDate criteriaDate,
			DatePeriod period, boolean isSingleDay, Optional<GrantHdTblSet> grantHdTblSetParam,
			Optional<List<LengthServiceTbl>> lengthServiceTblsParam, Optional<GeneralDate> closureStartDate
			){

		List<NextAnnualLeaveGrant> nextAnnualLeaveGrantList = new ArrayList<>();

		boolean isPeriodNull = false;

//		パラメータ「期間」がNULLの場合
//		期間．開始日←取得した「締め開始日」の翌日
//		期間．終了日←取得した「締め開始日」の翌日の2年後
//
//		※パラメータ「期間.終了日」がNULLの場合
//		期間．終了日←パラメータ「期間.開始日」の2年後

		// ooooo 期間がNullかつ締め日がNullのときはどうするか？

		if ( period.start() == null && period.end() == null ){
			isPeriodNull = true;

			if ( closureStartDate.isPresent() ){
				period = new DatePeriod(
					closureStartDate.get().addDays(1)
					, closureStartDate.get().addDays(1).addYears(2));
			}
		}
		// パラメータ「期間.終了日」がNULLの場合
		else if ( period.end() == null ){
			isPeriodNull = true;

			period = new DatePeriod(
				period.start()
				, period.start().addYears(2));
		}

//		// 「年休付与テーブル設定」を取得する
//		Optional<GrantHdTblSet> grantHdTblSetOpt = Optional.empty();
//		if (grantHdTblSetParam.isPresent()){
//			grantHdTblSetOpt = grantHdTblSetParam;
//		}
//		else {
//			grantHdTblSetOpt = this.yearHolidayRepo.findByCode(companyId, grantTableCode);
//		}
//		if (!grantHdTblSetOpt.isPresent()) return nextAnnualLeaveGrantList;
//		val grantHdTblSet = grantHdTblSetOpt.get();
//
//		// 一斉付与日　確認
//		this.simultaneousGrantMDOpt = Optional.empty();
//		if (grantHdTblSet.getUseSimultaneousGrant() == UseSimultaneousGrant.USE){
//			this.simultaneousGrantMDOpt = Optional.of(grantHdTblSet.getSimultaneousGrandMonthDays());
//		}
//

		// 「年休付与テーブル設定」を取得する
		/** TODO: ??? */
		Optional<GrantHdTblSet> grantHdTblSetOpt = Optional.empty();
		if (grantHdTblSetParam.isPresent()){
			grantHdTblSetOpt = grantHdTblSetParam;
		}
		else {
			grantHdTblSetOpt = require.grantHdTblSet(companyId, grantTableCode);
		}
		if (!grantHdTblSetOpt.isPresent()) return nextAnnualLeaveGrantList;
		val grantHdTblSet = grantHdTblSetOpt.get();

		// 一斉付与日　確認
		Optional<Integer> simultaneousGrantMDOpt = Optional.empty();
		if (grantHdTblSet.getUseSimultaneousGrant() == UseSimultaneousGrant.USE){
			simultaneousGrantMDOpt = Optional.of(grantHdTblSet.getSimultaneousGrandMonthDays());
		}

		List<LengthServiceTbl> lengthServiceTbls;
		// 「勤続年数テーブル」を取得する
		if (lengthServiceTblsParam.isPresent()){
			lengthServiceTbls = lengthServiceTblsParam.get();
		}
		else {
			lengthServiceTbls = require.lengthServiceTbl(companyId, grantTableCode);
		}
		if (lengthServiceTbls.size() <= 0) return nextAnnualLeaveGrantList;

		// 年休付与年月日を計算
//		if ( getNextAnnualLeaveGrantProcMulti != null){
		GetNextAnnualLeaveGrantProcKdm002.calcAnnualLeaveGrantDate(
					entryDate, criteriaDate, simultaneousGrantMDOpt, lengthServiceTbls,
					period, isSingleDay, nextAnnualLeaveGrantList);
//		}

		// アルゴリズム「社員の労働条件を取得する」を実行し、契約時間を取得する
		Optional<WorkingConditionItem> workCond
			= require.workingConditionItem(employeeId, criteriaDate);

		// 契約時間を取得する
		int contractTimeTmp = 0;
		if (workCond.isPresent()) {
			contractTimeTmp = workCond.get().getContractTime().v();
		}

		final int contractTime = contractTimeTmp;
		for (val nextAnnualLeaveGrant : nextAnnualLeaveGrantList){

			// 付与回数をもとに年休付与テーブルを取得
			val grantTimes = nextAnnualLeaveGrant.getTimes().v();
			val grantHdTblOpt = require.grantHdTbl(companyId, 1, grantTableCode, grantTimes);

			// 次回年休付与に付与日数・半日年休上限回数・時間年休上限日数をセット
			if (!grantHdTblOpt.isPresent()) continue;
			val grantHdTbl = grantHdTblOpt.get();
			nextAnnualLeaveGrant.setGrantDays(nts.gul.util.value.Finally.of(grantHdTbl.getGrantDays()));
			nextAnnualLeaveGrant.setHalfDayAnnualLeaveMaxTimes(grantHdTbl.getLimitDayYear());
			nextAnnualLeaveGrant.setTimeAnnualLeaveMaxDays(grantHdTbl.getLimitTimeHd());

			// 要修正　契約時間を取得する
			nextAnnualLeaveGrant.setTimeAnnualLeaveMaxTime(
					grantHdTbl.getLimitTimeHd().map(c->new LimitedTimeHdTime(c.v() * contractTime)));
		}

		// 期間がNULLであった場合は取得した付与年月日の最初の1件にする
		if ( isPeriodNull ){

			if ( 0 < nextAnnualLeaveGrantList.size() ){
				// ソート　ASC 付与年月日
				nextAnnualLeaveGrantList.sort((a, b) -> a.getGrantDate().compareTo(b.getGrantDate()));
				NextAnnualLeaveGrant aNextAnnualLeaveGrant = nextAnnualLeaveGrantList.get(0);

				// 最初の1件にする
				nextAnnualLeaveGrantList.clear();
				nextAnnualLeaveGrantList.add(aNextAnnualLeaveGrant);
			}
		}

		// 年休設定
		AnnualPaidLeaveSetting annualPaidLeaveSet = require.annualPaidLeaveSetting(companyId);

		for (val nextAnnualLeaveGrant : nextAnnualLeaveGrantList){

			// 付与日から期限日を計算
			val deadLine = annualPaidLeaveSet.calcDeadline(
					nextAnnualLeaveGrant.getGrantDate());

			// 期限日をセットする
			nextAnnualLeaveGrant.setDeadLine(deadLine);
		}

//		val annualLeaveGrant = aggregatePeriodWork.getAnnualLeaveGrant().get();
//		val grantDate = annualLeaveGrant.getGrantDate();
//		val deadline = this.annualPaidLeaveSet.calcDeadline(grantDate);

		// 次回年休付与を返す
		return nextAnnualLeaveGrantList;
	}

	public static interface RequireM1 {

		Optional<GrantHdTblSet> grantHdTblSet(String companyId, String yearHolidayCode);

		List<LengthServiceTbl> lengthServiceTbl(String companyId, String yearHolidayCode);

		Optional<GrantHdTbl> grantHdTbl(String companyId, int conditionNo, String yearHolidayCode, int grantNum);

		AnnualPaidLeaveSetting annualPaidLeaveSetting(String companyId);

		// 社員の労働条件を取得する
		Optional<WorkingConditionItem> workingConditionItem(String employeeId, GeneralDate criteriaDate);

	}
}
