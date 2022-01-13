package nts.uk.ctx.at.record.dom.remainingnumber.reserveleave.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.GetAnnAndRsvRemNumWithinPeriod;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.param.AnnualLeaveInfo;
import nts.uk.ctx.at.record.dom.remainingnumber.reserveleave.export.param.AggrResultOfReserveLeave;
import nts.uk.ctx.at.record.dom.remainingnumber.reserveleave.export.param.GrantWork;
import nts.uk.ctx.at.record.dom.remainingnumber.reserveleave.export.param.NextReserveLeaveGrant;
import nts.uk.ctx.at.record.dom.remainingnumber.reserveleave.export.param.ReserveLeaveInfo;
import nts.uk.ctx.at.record.dom.remainingnumber.reserveleave.export.param.ReserveLeaveLapsedWork;
import nts.uk.ctx.at.record.dom.remainingnumber.reserveleave.export.param.RsvLeaAggrPeriodWork;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.InterimRemainMngMode;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.LeaveExpirationStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.ConfirmLeavePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveGrantDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.RemainType;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.empinfo.grantremainingdata.ReserveLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.interim.TmpResereLeaveMng;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagement;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrCompanySettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonthlyCalculatingDailys;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.GrantBeforeAfterAtr;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.EmptYearlyRetentionSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.RetentionYearlySetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.UpperLimitSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.export.GetUpperLimitSetting;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureStartForEmployee;

/**
 * 期間中の積立年休残数を取得する
 * 
 * @author shuichu_ishida
 */
public class GetRsvLeaRemNumWithinPeriod {

	/**
	 * 期間中の積立年休残数を取得する
	 * 
	 * @param param
	 *            パラメータ
	 * @return 積立年休の集計結果
	 */
	public static Optional<AggrResultOfReserveLeave> algorithm(RequireM4 require, CacheCarrier cacheCarrier,
			GetRsvLeaRemNumWithinPeriodParam param) {
		return algorithm(require, cacheCarrier, param, Optional.empty(), Optional.empty());
	}

	/**
	 * 期間中の積立年休残数を取得する （月別集計用）
	 * 
	 * @param param
	 *            パラメータ
	 * @param companySets
	 *            月別集計で必要な会社別設定
	 * @param monthlyCalcDailys
	 *            月の計算中の日別実績データ
	 * @return 積立年休の集計結果
	 */

	/** 期間中の積立年休残数を取得する （月別集計用） */
	public static Optional<AggrResultOfReserveLeave> algorithm(RequireM4 require, CacheCarrier cacheCarrier,
			GetRsvLeaRemNumWithinPeriodParam param, Optional<MonAggrCompanySettings> companySets,
			Optional<MonthlyCalculatingDailys> monthlyCalcDailys) {

		String companyId = param.getCompanyId();
		String employeeId = param.getEmployeeId();

		// 年休の使用区分を取得する
		boolean isManageAnnualLeave = false;
		AnnualPaidLeaveSetting annualLeaveSet = null;
		Optional<RetentionYearlySetting> retentionYearlySet = Optional.empty();
		Optional<Map<String, EmptYearlyRetentionSetting>> emptYearlyRetentionSetMap = Optional.empty();
		if (companySets.isPresent()) {
			annualLeaveSet = companySets.get().getAnnualLeaveSet();
			retentionYearlySet = companySets.get().getRetentionYearlySet() == null ? Optional.empty()
					: companySets.get().getRetentionYearlySet();
			if (companySets.get().getEmptYearlyRetentionSetMap() != null) {
				emptYearlyRetentionSetMap = Optional.of(companySets.get().getEmptYearlyRetentionSetMap());
			}
		} else {
			annualLeaveSet = require.annualPaidLeaveSetting(companyId);
		}
		if (annualLeaveSet != null)
			isManageAnnualLeave = annualLeaveSet.isManaged();
		if (!isManageAnnualLeave)
			return Optional.empty();

		// 「休暇の集計期間から入社前、退職後を除く」を実行する
		EmployeeImport employee = require.employee(cacheCarrier, employeeId);
		if (employee == null)
			return Optional.empty();
		Optional<DatePeriod> aggrPeriod = ConfirmLeavePeriod.sumPeriod(param.getAggrPeriod(), employee);
		if (!aggrPeriod.isPresent())
			return Optional.empty();
		param.setAggrPeriod(aggrPeriod.get());

		AggrResultOfReserveLeave aggrResult = new AggrResultOfReserveLeave();

		// 積立年休付与残数データ 取得
		List<ReserveLeaveGrantRemainingData> rsvGrantRemainingDatas = new ArrayList<>();
		rsvGrantRemainingDatas = require.reserveLeaveGrantRemainingData(employeeId);

		// 集計開始日時点の積立年休情報を作成
		ReserveLeaveInfo reserveLeaveInfo = createInfoAsOfPeriodStart(require, cacheCarrier, param, companySets,
				monthlyCalcDailys, rsvGrantRemainingDatas);

		// 積立年休付与を計算
		List<GrantWork> calcGrant = calcGrant(param.getLapsedAnnualLeaveInfos(), annualLeaveSet, param.getAggrPeriod());

		// 積立年休集計期間の作成
		List<RsvLeaAggrPeriodWork> aggrPeriodWorks = createAggregatePeriod(param.getAggrPeriod(), calcGrant,
				rsvGrantRemainingDatas);

		// 暫定積立年休管理データを取得する
		List<TmpResereLeaveMng> tmpReserveLeaveMngs = getTmpReserveLeaveMngs(require, param);

		// 上限設定の期間を計算
		UpperLimitSetting limit = calcMaxSettingPeriod(require, cacheCarrier, param.getCompanyId(), retentionYearlySet);
		
		for (val aggrPeriodWork : aggrPeriodWorks) {

			// 積立年休の消滅・付与・消化
			aggrResult = reserveLeaveInfo.lapsedGrantDigest(require, cacheCarrier, companyId, employeeId,
					aggrPeriodWork, tmpReserveLeaveMngs, aggrResult, annualLeaveSet, retentionYearlySet,
					emptYearlyRetentionSetMap, limit);
		}

		// 【渡すパラメータ】 積休情報 ← 積休の集計結果．積休情報（期間終了日時点）
		ReserveLeaveInfo reserveLeaveInfoEnd = aggrResult.getAsOfPeriodEnd();

		// マイナス分の積休付与残数を1レコードにまとめる
		Optional<ReserveLeaveGrantRemainingData> remainingShortageData = reserveLeaveInfoEnd
				.createLeaveGrantRemainingShortageData();

		// 積休不足分として作成した積休付与データを削除する
		aggrResult.deleteShortageRemainData();

		// 積休(期間終了日時点)に残数不足の付与残数データを追加
		if (remainingShortageData.isPresent()) {
			reserveLeaveInfoEnd.getGrantRemainingList().add(remainingShortageData.get());
		}

		// 「積立年休の集計結果」を返す
		return Optional.of(aggrResult);
	}

	/**
	 * 集計開始日時点の積立年休情報を作成
	 * 
	 * @param param
	 *            パラメータ
	 * @param companySets
	 *            月別集計で必要な会社別設定
	 * @param monthlyCalcDailys
	 *            月の計算中の日別実績データ
	 * @param rsvGrantRemainingDatas
	 *            積立年休付与残数データリスト
	 * @return 積立年休情報
	 */
	private static ReserveLeaveInfo createInfoAsOfPeriodStart(RequireM4 require, CacheCarrier cacheCarrier,
			GetRsvLeaRemNumWithinPeriodParam param, Optional<MonAggrCompanySettings> companySets,
			Optional<MonthlyCalculatingDailys> monthlyCalcDailys,
			List<ReserveLeaveGrantRemainingData> rsvGrantRemainingDatas) {

		GeneralDate aggrStart = param.getAggrPeriod().start(); // 集計開始日

		ReserveLeaveInfo emptyInfo = new ReserveLeaveInfo();
		emptyInfo.setYmd(aggrStart);

		// 「前回の積立年休情報」を確認 （前回の積立年休の集計結果．積立年休情報（期間終了日の翌日開始時点））
		ReserveLeaveInfo prevReserveLeaveInfo = null;
		if (param.getPrevReserveLeave().isPresent()) {
			prevReserveLeaveInfo = param.getPrevReserveLeave().get().getAsOfStartNextDayOfPeriodEnd();
		}

		// 「開始日」と「年休情報．年月日」を比較
		boolean isSameInfo = false;
		if (prevReserveLeaveInfo != null) {
			if (aggrStart == prevReserveLeaveInfo.getYmd()) {
				isSameInfo = true;
			}
		}
		if (isSameInfo) {

			// 「前回の積立年休情報」を取得 → 取得内容をもとに積立年休情報を作成
			return createInfoFromRemainingData(aggrStart, prevReserveLeaveInfo.getGrantRemainingList());
		}

		// 「集計開始日を締め開始日とする」をチェック （締め開始日としない時、締め開始日を確認する）
		boolean isAfterClosureStart = false;
		Optional<GeneralDate> closureStartOpt = Optional.empty();
		boolean noCheckStartDate = false;
		// if (param.getIsNoCheckStartDate().isPresent()) noCheckStartDate =
		// param.getIsNoCheckStartDate().get();

		if (!noCheckStartDate) {

			// 休暇残数を計算する締め開始日を取得する
			GeneralDate closureStart = null; // 締め開始日
			{
				// 最新の締め終了日翌日を取得する
				Optional<ClosureStatusManagement> sttMng = require.latestClosureStatusManagement(param.getEmployeeId());
				if (sttMng.isPresent()) {
					closureStart = sttMng.get().getPeriod().end().addDays(1);
					closureStartOpt = Optional.of(closureStart);
				} else {

					// 社員に対応する締め開始日を取得する
					closureStartOpt = GetClosureStartForEmployee.algorithm(require, cacheCarrier,
							param.getEmployeeId());
					if (closureStartOpt.isPresent())
						closureStart = closureStartOpt.get();
				}
			}

			// 取得した締め開始日と「集計開始日」を比較
			if (closureStart != null) {

				// 締め開始日＜集計開始日 か確認する
				if (closureStart.before(aggrStart))
					isAfterClosureStart = true;
			}
		}

		// 「積立年休付与残数データ」
		List<ReserveLeaveGrantRemainingData> beforeClosureDatas = new ArrayList<>();

		if (isAfterClosureStart) {
			// 締め開始日<集計開始日 の時

			// 開始日までの積立年休残数を計算 （締め開始日～集計開始日前日）
			val aggrResult = GetAnnAndRsvRemNumWithinPeriod.algorithm(require, cacheCarrier, param.getCompanyId(),
					param.getEmployeeId(), new DatePeriod(closureStartOpt.get(), aggrStart.addDays(-1)), // 集計期間
					param.getMode(), // 実績のみ参照区分
					aggrStart.addDays(-1), // 基準日
					false, // isGetNextMonthData 翌月管理データ取得フラグ 現在未使用,
					true, // isCalcAttendanceRate 出勤率計算フラグ 現在未使用,
					param.getIsOverWrite(), // 上書きフラグ
					Optional.empty(), // tempAnnDataforOverWriteList// 上書き用の暫定年休管理データ なし,
					param.getForOverWriteList(), // 上書き用の暫定積休管理データ
					Optional.of(false), // 不足分付与残数データ出力区分
					Optional.of(true), // 集計開始日を締め開始日とする （締め開始日を確認しない）
					Optional.empty(), // 前回の年休の集計結果
					Optional.empty(), // 前回の積立年休の集計結果
					companySets, // 月別集計で必要な会社別設定
					Optional.empty(), // 月別集計で必要な社員別設定
					monthlyCalcDailys, // 月の計算中の日別実績データ
					param.getIsOverWritePeriod() // 上書き対象期間
			);

			// 積立年休情報（期間終了日の翌日開始時点）を取得
			if (aggrResult.getReserveLeave().isPresent()) {
				val asOfPeriodEnd = aggrResult.getReserveLeave().get().getAsOfPeriodEnd();

				// 積立年休付与残数データを取得
				beforeClosureDatas = asOfPeriodEnd.getGrantRemainingList();
			}
		} else {
			// 締め開始日>=集計開始日 or 締め開始日がnull の時

			// 「積立年休付与残数データ」を取得
			GeneralDate closureStart = aggrStart;
			if (closureStartOpt.isPresent())
				closureStart = closureStartOpt.get();
			for (val rsvGrantRemainingData : rsvGrantRemainingDatas) {
				if (rsvGrantRemainingData.getExpirationStatus() == LeaveExpirationStatus.EXPIRED)
					continue;
				if (rsvGrantRemainingData.getGrantDate().after(closureStart))
					continue;
				if (rsvGrantRemainingData.getDeadline().before(closureStart))
					continue;
				beforeClosureDatas.add(rsvGrantRemainingData);
			}
		}

		// 取得内容をもとに年休情報を作成
		return createInfoFromRemainingData(aggrStart, beforeClosureDatas);
	}

	/**
	 * 積立年休付与残数データから積立年休情報を作成
	 * 
	 * @param ymd
	 *            年月日
	 * @param grantRemainingDataList
	 *            付与残数データリスト
	 * @return 積立年休情報
	 */
	private static ReserveLeaveInfo createInfoFromRemainingData(GeneralDate ymd,
			List<ReserveLeaveGrantRemainingData> grantRemainingDataList) {

		ReserveLeaveInfo returnInfo = new ReserveLeaveInfo();
		returnInfo.setYmd(ymd);

		// 積立年休情報．積立年休付与残数 ← パラメータ「積立年休付与残数データ」
		List<ReserveLeaveGrantRemainingData> targetDatas = new ArrayList<>();
		for (val grantRemainingData : grantRemainingDataList) {
			if (grantRemainingData.getExpirationStatus() == LeaveExpirationStatus.EXPIRED)
				continue;
			targetDatas.add(grantRemainingData);
		}
		targetDatas.sort((a, b) -> a.getGrantDate().compareTo(b.getGrantDate()));
		returnInfo.setGrantRemainingList(targetDatas);

		// 積立年休情報残数を更新
		returnInfo.updateRemainingNumber(GrantBeforeAfterAtr.BEFORE_GRANT);

		// 積立年休情報を返す
		return returnInfo;
	}

	/**
	 * 上限設定の期間を計算
	 * 
	 * @param param
	 *            パラメータ
	 * @param retentionYearlySet
	 *            積立年休設定
	 * @param emptYearlyRetentionSetMap
	 *            雇用積立年休設定マップ
	 * @return 積立年休上限設定期間WORKリスト
	 */
	private static UpperLimitSetting calcMaxSettingPeriod(RequireM2 require, CacheCarrier cacheCarrier,
			String companyId, Optional<RetentionYearlySetting> retentionYearlySet) {

		// 積立年休の上限設定を取得
		return GetUpperLimitSetting.algorithm(require, cacheCarrier, companyId, retentionYearlySet);

	}

	/**
	 * 積立年休付与を計算
	 * 
	 * @param lapsedAnnualLeaveInfos
	 *            年休付与消滅時リスト
	 * @param annualLeaveSet
	 *            年休設定
	 * @param maxSetPeriods
	 *            積立年休上限設定期間WORKリスト
	 * @return 積立年休付与WORKリスト
	 */
	private static List<GrantWork> calcGrant(List<AnnualLeaveInfo> lapsedAnnualLeaveInfos,
			AnnualPaidLeaveSetting annualLeaveSet, DatePeriod period) {

		if (lapsedAnnualLeaveInfos.size() <= 0)
			return new ArrayList<>();

		AnnualLeaveInfo annualLeaveInfo = lapsedAnnualLeaveInfos.get(lapsedAnnualLeaveInfos.size() - 1);

		// 付与残数データを取得
		Map<GeneralDate, Double> grantTotal = annualLeaveInfo.getGrantRemainingDataList().stream().filter(
				x -> period.contains(x.getDeadline()) && x.getExpirationStatus() == LeaveExpirationStatus.EXPIRED)
				.sorted((x, y) -> x.getDeadline().compareTo(y.getDeadline()))
				.collect(Collectors.groupingBy(x -> x.getDeadline(),
						Collectors.collectingAndThen(Collectors.toList(), list -> list.stream()
								.mapToDouble(x -> x.getDetails().getRemainingNumber().getDays().v()).sum())));

		List<GrantWork> results = new ArrayList<>();
		AtomicInteger grantNumber = new AtomicInteger(0);
		grantTotal.entrySet().stream().forEach(x -> {
			// 積立年休付与WORKを作成 → 端数処理
			GrantWork grantWork = GrantWork.of(x.getKey().addDays(1), new LeaveGrantDayNumber(x.getValue()),
					grantNumber.incrementAndGet());

			// 積立年休付与WORKに追加
			results.add(grantWork);
		});
		return results;
	}

	/**
	 * 積立年休集計期間を作成
	 * 
	 * @param period
	 *            期間
	 * @param nextReserveLeaveGrantList
	 *            次回積立年休付与リスト
	 * @param maxSetPeriods
	 *            積立年休上限設定期間WORKリスト
	 * @param grantRemainingDataList
	 *            付与残数データリスト
	 * @return 積立年休集計期間WORKリスト
	 */
	private static List<RsvLeaAggrPeriodWork> createAggregatePeriod(DatePeriod period,
			List<GrantWork> nextReserveLeaveGrantList, List<ReserveLeaveGrantRemainingData> grantRemainingDataList) {

		List<RsvLeaAggrPeriodWork> results = new ArrayList<>();

		// 処理単位分割日リスト
		Map<GeneralDate, RsvLeaDividedDay> dividedDayMap = new HashMap<>();

		// 期間終了日翌日
		GeneralDate dayOfPeriodEnd = period.end();
		GeneralDate nextDayOfPeriodEnd = period.end();
		if (nextDayOfPeriodEnd.before(GeneralDate.max()))
			nextDayOfPeriodEnd = dayOfPeriodEnd.addDays(1);

		// 「積立年休付与残数データ」を取得 （期限日 昇順、付与日 昇順）
		List<ReserveLeaveGrantRemainingData> remainingDatas = new ArrayList<>();
		for (val grantRemainingData : grantRemainingDataList) {
			if (!period.contains(grantRemainingData.getDeadline()))
				continue;
			if (grantRemainingData.getExpirationStatus() != LeaveExpirationStatus.AVAILABLE)
				continue;
			remainingDatas.add(grantRemainingData);
		}
		Collections.sort(remainingDatas, new Comparator<ReserveLeaveGrantRemainingData>() {
			@Override
			public int compare(ReserveLeaveGrantRemainingData o1, ReserveLeaveGrantRemainingData o2) {
				int compDeadline = o1.getDeadline().compareTo(o2.getDeadline());
				if (compDeadline != 0)
					return compDeadline;
				return o1.getGrantDate().compareTo(o2.getGrantDate());
			}
		});

		// 取得した「積立年休付与残数データ」をすべて「処理単位分割日リスト」に追加
		for (val remainingData : remainingDatas) {
			val nextDayOfDeadline = remainingData.getDeadline().addDays(1);
			dividedDayMap.putIfAbsent(nextDayOfDeadline, new RsvLeaDividedDay(nextDayOfDeadline));
			dividedDayMap.get(nextDayOfDeadline).getLapsedWork().setLapsedAtr(true);
		}

		// 「次回積立年休付与リスト」を全て「処理単位分割日リスト」に追加
		for (val nextReserveLeaveGrant : nextReserveLeaveGrantList) {
			val grantDate = nextReserveLeaveGrant.getGrantYmd();
			if (grantDate.beforeOrEquals(period.start()))
				continue;
			if (grantDate.after(nextDayOfPeriodEnd))
				continue;

			dividedDayMap.putIfAbsent(grantDate, new RsvLeaDividedDay(grantDate));
			dividedDayMap.get(grantDate).getGrantWork().setGrantAtr(true);
			dividedDayMap.get(grantDate).getGrantWork().setReserveLeaveGrant(Optional.of(NextReserveLeaveGrant
					.of(nextReserveLeaveGrant.getGrantYmd(), nextReserveLeaveGrant.getGrantDays(), GeneralDate.max())));
		}

		// 期間終了日の「処理単位分割日」を取得・追加 → フラグ設定
		dividedDayMap.putIfAbsent(dayOfPeriodEnd, new RsvLeaDividedDay(dayOfPeriodEnd));
		dividedDayMap.get(dayOfPeriodEnd).getEndWork().setPeriodEndAtr(true);

		// 期間終了日翌日の「処理単位分割日」を取得・追加 → フラグ設定
		dividedDayMap.putIfAbsent(nextDayOfPeriodEnd, new RsvLeaDividedDay(nextDayOfPeriodEnd));
		dividedDayMap.get(nextDayOfPeriodEnd).getEndWork().setNextPeriodEndAtr(true);

		// 「処理単位分割日」をソート
		List<RsvLeaDividedDay> dividedDayList = new ArrayList<>();
		dividedDayList.addAll(dividedDayMap.values());
		dividedDayList.sort((a, b) -> a.getYmd().compareTo(b.getYmd()));

		// 「積立年休集計期間WORK」を作成
		if (dividedDayList.get(0).getYmd().after(period.start())) {
			RsvLeaAggrPeriodWork startWork = new RsvLeaAggrPeriodWork();
			val startWorkEnd = dividedDayList.get(0).getYmd().addDays(-1);
			startWork.setPeriod(new DatePeriod(period.start(), startWorkEnd));
			// 消滅 ←最初の「処理単位分割日．消滅情報WORK」
			startWork.setLapsedAtr(dividedDayList.get(0).getLapsedWork());

			results.add(startWork);
		}

		// 付与後フラグ
		GrantBeforeAfterAtr grantPeriodAtr = GrantBeforeAfterAtr.BEFORE_GRANT;

		for (int index = 0; index < dividedDayList.size(); index++) {
			val nowDividedDay = dividedDayList.get(index);
			RsvLeaDividedDay nextDividedDay = null;
			if (index + 1 < dividedDayList.size())
				nextDividedDay = dividedDayList.get(index + 1);

			// 付与フラグをチェック
			if (nowDividedDay.getGrantWork().isGrantAtr()) {
				grantPeriodAtr = GrantBeforeAfterAtr.AFTER_GRANT;
			}
			nowDividedDay.getGrantWork().setGrantPeriodAtr(grantPeriodAtr);

			// 集計期間をチェック
			GeneralDate workPeriodEnd = nextDayOfPeriodEnd;
			if (nextDividedDay != null)
				workPeriodEnd = nextDividedDay.getYmd().addDays(-1);
			DatePeriod workPeriod = new DatePeriod(nowDividedDay.getYmd(), workPeriodEnd);

			// 積立年休集計期間WORKを作成し、Listに追加
			RsvLeaAggrPeriodWork nowWork = new RsvLeaAggrPeriodWork(workPeriod, nowDividedDay.getGrantWork(),
					nextDividedDay != null ? nextDividedDay.getLapsedWork() : new ReserveLeaveLapsedWork(false),
					nowDividedDay.getEndWork());
			results.add(nowWork);
		}

		// 積立年休集計期間WORKリストを返す
		return results;
	}

	/**
	 * 暫定積立年休管理データを取得する
	 * 
	 * @param param
	 *            パラメータ
	 * @return 暫定積立年休管理データWORKリスト
	 */
	private static List<TmpResereLeaveMng> getTmpReserveLeaveMngs(RequireM1 require,
			GetRsvLeaRemNumWithinPeriodParam param) {

		List<TmpResereLeaveMng> results = new ArrayList<>();

		// 「モード」をチェック
		if (param.getMode() == InterimRemainMngMode.MONTHLY) {
			// 月次モード

			// 「月次処理用の暫定残数管理データを作成する」を実行する
			// val dailyInterimRemainMngDataMap =
			// this.interimRemOffMonth.monthInterimRemainData(
			// param.getCompanyId(), param.getEmployeeId(),
			// param.getAggrPeriod());

			// 受け取った「日別暫定管理データ」を積立年休のみに絞り込む
			// for (val dailyInterimRemainMngData :
			// dailyInterimRemainMngDataMap.values()){
			// if (!dailyInterimRemainMngData.getResereData().isPresent())
			// continue;
			// if (dailyInterimRemainMngData.getRecAbsData().size() <= 0)
			// continue;
			// val master = dailyInterimRemainMngData.getRecAbsData().get(0);
			// val data = dailyInterimRemainMngData.getResereData().get();
			// results.add(TmpReserveLeaveMngWork.of(master, data));
			// }
		}
		if (param.getMode() == InterimRemainMngMode.OTHER) {
			// その他モード

			// 「暫定積立年休管理データ」を取得する
			require.tmpResereLeaveMng(param.getEmployeeId(), param.getAggrPeriod()).forEach(x -> {
				results.add(new TmpResereLeaveMng(x.getRemainManaID(), x.getSID(), x.getYmd(), x.getCreatorAtr(),
						RemainType.FUNDINGANNUAL, x.getUseDays()));
			});
		}

		// 「上書きフラグ」をチェック
		if (param.getIsOverWrite().isPresent()) {
			if (param.getIsOverWrite().get()) {
				if (param.getIsOverWritePeriod().isPresent()) {

					// 上書き対象期間内の暫定積休管理データを削除
					results.removeIf(x -> param.getIsOverWritePeriod().get().contains(x.getYmd()));

					// 上書き用データがある時、追加する
					if (param.getForOverWriteList().isPresent()) {
						val overWrites = param.getForOverWriteList().get();
						for (val overWrite : overWrites) {
							// 上書き用データを追加
							results.add(overWrite);
						}
					}
				}
			}
		}

		results.sort((a, b) -> a.getYmd().compareTo(b.getYmd()));
		return results;
	}

	public static interface RequireM1 {

		List<TmpResereLeaveMng> tmpResereLeaveMng(String sid, DatePeriod datePeriod);
	}

	public static interface RequireM2 extends GetUpperLimitSetting.RequireM1 {
	}

	public static interface RequireM3 extends GetClosureStartForEmployee.RequireM1 {

		Optional<ClosureStatusManagement> latestClosureStatusManagement(String employeeId);
	}

	public static interface RequireM4 extends RequireM3, RequireM2, RequireM1, ReserveLeaveInfo.RequireM1,
			nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.GetAnnLeaRemNumWithinPeriodProc.RequireM3 {

		// AnnualPaidLeaveSetting annualPaidLeaveSetting (String companyId);

		List<ReserveLeaveGrantRemainingData> reserveLeaveGrantRemainingData(String employeeId);
	}

}
