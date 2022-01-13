package nts.uk.ctx.at.record.dom.remainingnumber.reserveleave.export.param;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveNumberInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.LeaveExpirationStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.RemNumShiftListWork;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.LeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveOverNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.empinfo.grantremainingdata.ReserveLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.empinfo.grantremainingdata.daynumber.ReserveLeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.empinfo.grantremainingdata.daynumber.ReserveLeaveUsedNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.interim.TmpResereLeaveMng;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.ReserveLeaveError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.GrantBeforeAfterAtr;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.reserveleave.ReserveLeaveRemainingNumberInfo;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPriority;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.EmptYearlyRetentionSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.RetentionYearlySetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.export.GetUpperLimitSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.UpperLimitSetting;

/**
 * 積立年休情報
 * 
 * @author shuichi_ishida
 */
@Getter
@Setter
public class ReserveLeaveInfo implements Cloneable {

	/** 年月日 */
	private GeneralDate ymd;
	/** 残数 */
	private ReserveLeaveRemainingNumberInfo remainingNumber;
	/** 付与残数データ */
	private List<ReserveLeaveGrantRemainingData> grantRemainingList;
	/** 使用数 */
	private ReserveLeaveUsedDayNumber usedDays;
	/** 付与情報 */
	private Optional<ReserveLeaveGrantInfo> grantInfo;

	/**
	 * コンストラクタ
	 */
	public ReserveLeaveInfo() {

		this.ymd = GeneralDate.min();
		this.remainingNumber = new ReserveLeaveRemainingNumberInfo();
		this.grantRemainingList = new ArrayList<>();
		this.usedDays = new ReserveLeaveUsedDayNumber(0.0);
		this.grantInfo = Optional.empty();
	}

	/**
	 * ファクトリー
	 * 
	 * @param ymd
	 *            年月日
	 * @param remainingNumber
	 *            残数
	 * @param grantRemainingList
	 *            付与残数データ
	 * @param usedDays
	 *            使用数
	 * @param afterGrantAtr
	 *            付与後フラグ
	 * @param grantInfo
	 *            付与情報
	 * @return 積立年休情報
	 */
	public static ReserveLeaveInfo of(GeneralDate ymd, ReserveLeaveRemainingNumberInfo remainingNumber,
			List<ReserveLeaveGrantRemainingData> grantRemainingList, ReserveLeaveUsedDayNumber usedDays,
			Optional<ReserveLeaveGrantInfo> grantInfo) {

		ReserveLeaveInfo domain = new ReserveLeaveInfo();
		domain.ymd = ymd;
		domain.remainingNumber = remainingNumber;
		domain.grantRemainingList = grantRemainingList;
		domain.usedDays = usedDays;
		domain.grantInfo = grantInfo;
		return domain;
	}

	@Override
	public ReserveLeaveInfo clone() {
		ReserveLeaveInfo cloned = new ReserveLeaveInfo();
		try {
			cloned.ymd = this.ymd;
			cloned.remainingNumber = this.remainingNumber.clone();
			for (val grantRemainingNumber : this.grantRemainingList) {
				val detail = grantRemainingNumber.getDetails();
				Double overLimitDays = null;
				if (detail.getUsedNumber().getLeaveOverLimitNumber().isPresent()) {
					overLimitDays = detail.getUsedNumber().getLeaveOverLimitNumber().get().numberOverDays.v();
				}
				ReserveLeaveGrantRemainingData newRemainData = ReserveLeaveGrantRemainingData.createFromJavaType(
						grantRemainingNumber.getLeaveID(), grantRemainingNumber.getEmployeeId(),
						grantRemainingNumber.getGrantDate(), grantRemainingNumber.getDeadline(),
						grantRemainingNumber.getExpirationStatus().value, grantRemainingNumber.getRegisterType().value,
						detail.getGrantNumber().getDays().v(), detail.getUsedNumber().getDays().v(), overLimitDays,
						detail.getRemainingNumber().getDays().v());
				cloned.grantRemainingList.add(newRemainData);
			}
			cloned.usedDays = new ReserveLeaveUsedDayNumber(this.usedDays.v());
			if (this.grantInfo.isPresent()) {
				cloned.grantInfo = Optional.of(this.grantInfo.get().clone());
			}
		} catch (Exception e) {
			throw new RuntimeException("ReserveLeaveInfo clone error.");
		}
		return cloned;
	}

	public List<ReserveLeaveGrantRemainingData> getGrantRemainingNumberList() {
		return this.grantRemainingList.stream().map(c -> (ReserveLeaveGrantRemainingData) c)
				.collect(Collectors.toList());
	}

	/**
	 * 積立年休付与残数を更新
	 */
	public void updateRemainingNumber(GrantBeforeAfterAtr grantPeriodAtr) {
		this.remainingNumber.updateRemainingNumber(this.grantRemainingList, grantPeriodAtr);
	}

	/**
	 * 積立年休の消滅・付与・消化
	 * 
	 * @param companyId
	 *            会社ID
	 * @param employeeId
	 *            社員ID
	 * @param aggrPeriodWork
	 *            積立年休集計期間WORK
	 * @param tmpReserveLeaveMngs
	 *            暫定積立年休管理データリスト
	 * @param isGetNextMonthData
	 *            翌月管理データ取得フラグ
	 * @param aggrResult
	 *            積立年休の集計結果
	 * @param getUpperLimitSetting
	 *            社員の保持年数を取得
	 * @param calcDeadlineForGrantDate
	 *            付与日から期限日を計算
	 * @param annualPaidLeaveSet
	 *            年休設定
	 * @param retentionYearlySet
	 *            積立年休設定
	 * @param emptYearlyRetentionSetMap
	 *            雇用積立年休設定マップ
	 * @return 積立年休の集計結果
	 */
	public AggrResultOfReserveLeave lapsedGrantDigest(RequireM1 require, CacheCarrier cacheCarrier, String companyId,
			String employeeId, RsvLeaAggrPeriodWork aggrPeriodWork, List<TmpResereLeaveMng> tmpReserveLeaveMngs,
			AggrResultOfReserveLeave aggrResult, AnnualPaidLeaveSetting annualPaidLeaveSet,
			Optional<RetentionYearlySetting> retentionYearlySet,
			Optional<Map<String, EmptYearlyRetentionSetting>> emptYearlyRetentionSetMap, UpperLimitSetting limit) {

		// 年月日を更新 ← 開始日
		this.ymd = aggrPeriodWork.getPeriod().start();

		// 付与処理
		aggrResult = this.grantProcess(require, cacheCarrier, companyId, employeeId, aggrPeriodWork, aggrResult,
				retentionYearlySet, emptYearlyRetentionSetMap, limit);

		if (!aggrPeriodWork.getEndWork().isNextPeriodEndAtr()) {

			// 消化処理
			aggrResult = this.digestProcess(require, companyId, employeeId, aggrPeriodWork, tmpReserveLeaveMngs,
					aggrResult, annualPaidLeaveSet);
		}

		// 期間終了日時点の積立年休情報を消化後に退避するかチェック
		if (aggrPeriodWork.getEndWork().isPeriodEndAtr()) {

			// 「積立年休の集計結果．積立年休情報（期間終了日時点）」 ← 処理中の「積立年休情報」
			aggrResult.setAsOfPeriodEnd(this.clone());
		}

		if (aggrPeriodWork.getEndWork().isNextPeriodEndAtr()) {
			// 「積立年休の集計結果．積立年休情報（期間終了日の翌日開始時点）」 ← 処理中の「積立年休情報」
			aggrResult.setAsOfStartNextDayOfPeriodEnd(this.clone());
		}

		if (!aggrPeriodWork.getEndWork().isNextPeriodEndAtr()) {
			// 年月日を更新 ← 終了日
			this.ymd = aggrPeriodWork.getPeriod().end();
		}

		// 消滅処理
		aggrResult = this.lapsedProcess(aggrPeriodWork, aggrResult);

		// 「積立年休の集計結果」を返す
		return aggrResult;
	}

	// 初回付与か判断する
	public boolean isFirstTimeGrant(RsvLeaAggrPeriodWork aggregatePeriodWork) {

		// 期間開始日に付与があるか
		if (!aggregatePeriodWork.getGrantWork().isGrantAtr()) {
			return false;
		}

		// 初回付与か
		return aggregatePeriodWork.getGrantWork().getGrantNumber() == 1;
	}

	/**
	 * 消滅処理
	 * 
	 * @param aggrPeriodWork
	 *            処理中の積立年休集計期間WORK
	 * @param aggrResult
	 *            積立年休の集計結果
	 * @return 積立年休の集計結果
	 */
	private AggrResultOfReserveLeave lapsedProcess(RsvLeaAggrPeriodWork aggrPeriodWork,
			AggrResultOfReserveLeave aggrResult) {

		// 消滅フラグを取得
		if (!aggrPeriodWork.getLapsedAtr().isLapsedAtr())
			return aggrResult;

		// 「付与残数データ」を取得
		val itrGrantRemainingNumber = this.grantRemainingList.listIterator();
		while (itrGrantRemainingNumber.hasNext()) {
			val grantRemainingNumber = itrGrantRemainingNumber.next();

			// 期限日=積立年休集計期間WORK．期間．終了日でなければ、消滅処理しない
			if (!grantRemainingNumber.getDeadline().equals(aggrPeriodWork.getPeriod().end())) {
				continue;
			}

			// 積立年休不足ダミーフラグがtrueなら、消滅処理しない
			if (grantRemainingNumber.isDummyData() == true)
				continue;

			// 処理中の付与残数データを期限切れにする
			grantRemainingNumber.setExpirationStatus(LeaveExpirationStatus.EXPIRED);

			// 未消化数を更新
			val targetUndigestNumber = this.remainingNumber.getReserveLeaveUndigestedNumber();
			val remainingNumber = grantRemainingNumber.getDetails().getRemainingNumber();
			targetUndigestNumber.addDays(remainingNumber.getDays().v());
		}

		// 付与前付与後を判断する
		GrantBeforeAfterAtr grantPeriodAtr = aggrPeriodWork.getGrantWork().judgeGrantPeriodAtr();

		// 積立年休情報残数を更新
		this.updateRemainingNumber(grantPeriodAtr);

		// 積立年休情報を「積立年休の集計結果．積立年休情報（消滅）」に追加
		if (!aggrResult.getLapsed().isPresent())
			aggrResult.setLapsed(Optional.of(new ArrayList<>()));
		aggrResult.getLapsed().get().add(this.clone());

		// 「積立年休の集計結果」を返す
		return aggrResult;
	}

	/**
	 * 付与処理
	 * 
	 * @param companyId
	 *            会社ID
	 * @param employeeId
	 *            社員ID
	 * @param aggrPeriodWork
	 *            積立年休集計期間WORK
	 * @param aggrResult
	 *            積立年休の集計結果
	 * @param getUpperLimitSetting
	 *            社員の保持年数を取得
	 * @param calcDeadlineForGrantDate
	 *            付与日から期限日を計算
	 * @param retentionYearlySet
	 *            積立年休設定
	 * @param emptYearlyRetentionSetMap
	 *            雇用積立年休設定マップ
	 * @return 積立年休の集計結果
	 */
	private AggrResultOfReserveLeave grantProcess(RequireM1 require, CacheCarrier cacheCarrier, String companyId,
			String employeeId, RsvLeaAggrPeriodWork aggrPeriodWork, AggrResultOfReserveLeave aggrResult,
			Optional<RetentionYearlySetting> retentionYearlySet,
			Optional<Map<String, EmptYearlyRetentionSetting>> emptYearlyRetentionSetMap, UpperLimitSetting limit) {

		// 「付与フラグ」をチェック
		if (!aggrPeriodWork.getGrantWork().isGrantAtr())
			return aggrResult;

		// 付与日から期限日を計算
		if (!aggrPeriodWork.getGrantWork().getReserveLeaveGrant().isPresent()) return aggrResult;

		// 「積立年休付与残数データ」を作成する
		val newRemainData = aggrPeriodWork.getGrantWork().getReserveLeaveGrant().get().toReserveLeaveGrantRemainingData(employeeId);


		// 作成した「積立年休付与残数データ」を付与残数データリストに追加
		this.grantRemainingList.add(newRemainData);

		// 付与情報に付与時の情報をセット
		this.grantInfo = aggrPeriodWork.getGrantWork().getReserveLeaveGrant().get().toReserveLeaveGrantInfo(grantInfo);

		// 付与前付与後を判断する
		GrantBeforeAfterAtr grantPeriodAtr = aggrPeriodWork.getGrantWork().judgeGrantPeriodAtr();

		// 積立年休情報残数を更新
		this.updateRemainingNumber(grantPeriodAtr);

		// 上限を超過した積立年休を消滅させる
		this.lapsedExcessReserveLeave(aggrPeriodWork, limit);

		// 「積立年休情報（付与時点）」に「積立年休情報」を追加
		if (!aggrResult.getAsOfGrant().isPresent())
			aggrResult.setAsOfGrant(Optional.of(new ArrayList<>()));
		aggrResult.getAsOfGrant().get().add(this.clone());

		// 「積立年休の集計結果」を返す
		return aggrResult;
	}

	/**
	 * 上限を超過した積立年休を消滅させる
	 * 
	 * @param aggrPeriodWork
	 *            処理中の積立年休集計期間WORK
	 */
	private void lapsedExcessReserveLeave(RsvLeaAggrPeriodWork aggrPeriodWork, UpperLimitSetting limit) {

		// 上限日数と積立年休残日数を比較
		Integer maxDays = limit.getMaxDaysCumulation().v();
		val noMinus = this.remainingNumber.getReserveLeaveNoMinus();
		double totalRemain = noMinus.getRemainingNumberInfo().getRemainingNumber().getTotalRemainingDays().v();
		if (maxDays.doubleValue() < totalRemain) {

			// 付与残数データを取得 （付与日 昇順、期限日 昇順）
			List<ReserveLeaveGrantRemainingData> remainingDatas = this.grantRemainingList.stream()
					.filter(c -> c.getExpirationStatus() == LeaveExpirationStatus.AVAILABLE
							&& aggrPeriodWork.getPeriod().contains(c.getGrantDate()))
					.collect(Collectors.toList());
			// ソート
			Collections.sort(remainingDatas, new Comparator<ReserveLeaveGrantRemainingData>() {
				@Override
				public int compare(ReserveLeaveGrantRemainingData o1, ReserveLeaveGrantRemainingData o2) {
					int compGrant = o1.getGrantDate().compareTo(o2.getGrantDate());
					if (compGrant != 0)
						return compGrant;
					return o1.getDeadline().compareTo(o2.getDeadline());
				}
			});

			// 上限を超過した日数を計算 → 上限超過日数
			double excessDays = totalRemain - maxDays.doubleValue();

			for (val remainingData : remainingDatas) {

				// 積立年休残日数と上限超過日数を比較
				val details = remainingData.getDetails();
				if (excessDays <= details.getRemainingNumber().getDays().v()) {
					details.getUsedNumber().leaveOverLimitNumber = Optional.of(new LeaveOverNumber(excessDays, 0));
					details.getRemainingNumber().add(new LeaveRemainingNumber(-excessDays, 0));
					excessDays = 0.0;
				} else {
					details.getUsedNumber().leaveOverLimitNumber = Optional
							.of(new LeaveOverNumber(details.getRemainingNumber().getDays().v(), 0));
					details.getRemainingNumber()
							.add(new LeaveRemainingNumber(-details.getRemainingNumber().getDays().v(), 0));
					excessDays -= details.getRemainingNumber().getDays().v();
				}

				// 上限超過日数が残っているかチェック
				if (excessDays <= 0.0)
					break;
			}

			// 付与前付与後を判断する
			GrantBeforeAfterAtr grantPeriodAtr = aggrPeriodWork.getGrantWork().judgeGrantPeriodAtr();

			// 積立年休情報残数を更新
			this.updateRemainingNumber(grantPeriodAtr);
		}
	}

	/**
	 * 消化処理
	 * 
	 * @param companyId
	 *            会社ID
	 * @param employeeId
	 *            社員ID
	 * @param aggrPeriodWork
	 *            積立年休集計期間WORK
	 * @param tmpReserveLeaveMngs
	 *            暫定積立年休管理データリスト
	 * @param aggrResult
	 *            積立年休の集計結果
	 * @param annualPaidLeaveSet
	 *            年休設定
	 * @return 積立年休の集計結果
	 */
	private AggrResultOfReserveLeave digestProcess(
			// RequireM1 require,
			LeaveRemainingNumber.RequireM3 require, String companyId, String employeeId,
			RsvLeaAggrPeriodWork aggrPeriodWork, List<TmpResereLeaveMng> tmpReserveLeaveMngs,
			AggrResultOfReserveLeave aggrResult, AnnualPaidLeaveSetting annualPaidLeaveSet) {

		// 集計期間の翌日を集計する時は、消化処理は行わない
		if (aggrPeriodWork.getEndWork().isNextPeriodEndAtr()) {
			return aggrResult;
		}

		// 「暫定積立年休管理データリスト」を取得する
		List<TmpResereLeaveMng> targetList = new ArrayList<>();
		for (val tmpReserveLeaveMng : tmpReserveLeaveMngs) {
			if (!aggrPeriodWork.getPeriod().contains(tmpReserveLeaveMng.getYmd()))
				continue;
			targetList.add(tmpReserveLeaveMng);
		}

		targetList.sort((a, b) -> a.getYmd().compareTo(b.getYmd()));

		// 付与前付与後を判断する
		GrantBeforeAfterAtr grantPeriodAtr = aggrPeriodWork.getGrantWork().judgeGrantPeriodAtr();

		for (val tmpReserveLeaveMng : targetList) {

			// 積立年休を消化する
			{
				// 積立年休使用数WORK
				ReserveLeaveUsedNumber usedNumber = new ReserveLeaveUsedNumber();

				// 積立年休付与残数を取得
				List<LeaveGrantRemainingData> targetRemainingDatas = new ArrayList<>();
				for (val remainingData : this.grantRemainingList) {
					if (tmpReserveLeaveMng.getYmd().before(remainingData.getGrantDate()))
						continue;
					if (tmpReserveLeaveMng.getYmd().after(remainingData.getDeadline()))
						continue;
					targetRemainingDatas.add(remainingData);
				}

				// 取得設定をチェック
				if (annualPaidLeaveSet.getAcquisitionSetting().annualPriority == AnnualPriority.FIFO) {

					// 当年付与分から消化する （付与日 降順(DESC)）
					targetRemainingDatas.sort((a, b) -> -a.getGrantDate().compareTo(b.getGrantDate()));
				} else {

					// 繰越分から消化する （付与日 昇順(ASC)）
					targetRemainingDatas.sort((a, b) -> a.getGrantDate().compareTo(b.getGrantDate()));
				}

				// 使用数変数作成
				LeaveUsedNumber leaveUsedNumber = new LeaveUsedNumber();
				leaveUsedNumber.setDays(new LeaveUsedDayNumber(tmpReserveLeaveMng.getUseDays().v()));

				// 使用数に加算する
				LeaveUsedDayNumber days = new LeaveUsedDayNumber(leaveUsedNumber.getDays().v());

				ReserveLeaveUsedNumber addNumber = ReserveLeaveUsedNumber.of(days, Optional.empty(), Optional.empty(),
						Optional.empty());
				usedNumber.add(addNumber);

				// 「休暇残数シフトリストWORK」一時変数を作成
				RemNumShiftListWork remNumShiftListWork = new RemNumShiftListWork();

				// 休暇残数を指定使用数消化する
				Optional<LeaveGrantRemainingData> dummyData = LeaveGrantRemainingData.digest(
						require,
						targetRemainingDatas,
						remNumShiftListWork,
						leaveUsedNumber,
						companyId,
						employeeId,
						aggrPeriodWork.getPeriod().start());
				
				if(dummyData.isPresent()){
					ReserveLeaveGrantRemainingData addData = ReserveLeaveGrantRemainingData.of(dummyData.get());
					this.grantRemainingList.add(addData);
				}

				// 残数（現在）を消化後の状態にする
				{
					// 実積立年休（年休（マイナスあり））に使用数を加算する
					this.getRemainingNumber().getReserveLeaveWithMinus().getUsedNumber()
							.addUsedDays(usedNumber.getDays().v(), grantPeriodAtr);

					// 積立年休情報残数を更新
					this.updateRemainingNumber(grantPeriodAtr);
				}
			}
		}

		// 残数不足エラーをチェックする
		{
			// 残数がマイナスかチェック
			val withMinus = this.remainingNumber.getReserveLeaveWithMinus();
			if (withMinus.getRemainingNumberInfo().getRemainingNumber().getTotalRemainingDays().v() < 0.0) {

				if (grantPeriodAtr.equals(GrantBeforeAfterAtr.AFTER_GRANT)) {

					// 「積立年休不足エラー（付与後）」を追加
					aggrResult.addError(ReserveLeaveError.SHORTAGE_RSVLEA_AFTER_GRANT);
				} else {

					// 「積立年休不足エラー（付与前）」を追加
					aggrResult.addError(ReserveLeaveError.SHORTAGE_RSVLEA_BEFORE_GRANT);
				}
			}
		}

		// 「積立年休の集計結果」を返す
		return aggrResult;
	}

	/**
	 * マイナス分の年休付与残数を1レコードにまとめる
	 * 
	 * @return 年休付与残数データ
	 */
	public Optional<ReserveLeaveGrantRemainingData> createLeaveGrantRemainingShortageData() {

		// 残数不足（ダミー）として作成した「年休付与残数(List)」を取得
		List<ReserveLeaveGrantRemainingData> dummyRemainingList = this.getGrantRemainingList().stream()
				.filter(c -> c.isDummyData()).collect(Collectors.toList());

		if (dummyRemainingList.size() == 0) {
			return Optional.empty();
		}

		// 取得した年休付与残数の「年休使用数」、「年休残数」をそれぞれ合計
		LeaveRemainingNumber leaveRemainingNumberTotal = new LeaveRemainingNumber();
		LeaveUsedNumber leaveUsedNumberTotal = new LeaveUsedNumber();
		dummyRemainingList.forEach(c -> {
			leaveRemainingNumberTotal.add(c.getDetails().getRemainingNumber());
			leaveUsedNumberTotal.add(c.getDetails().getUsedNumber());
		});

		// 合計した「年休使用数」「年休残数」から年休付与残数を作成

		// 最初の1件目を取得
		ReserveLeaveGrantRemainingData reserveLeaveGrantRemainingData
		= ReserveLeaveGrantRemainingData.of(dummyRemainingList.stream().findFirst().get());

		AnnualLeaveNumberInfo leaveNumberInfo = new AnnualLeaveNumberInfo();
		// 明細．残数 ← 合計した「年休残数」
		leaveNumberInfo.setRemainingNumber(leaveRemainingNumberTotal);
		// 明細．使用数 ← 合計した「年休使用数」
		leaveNumberInfo.setUsedNumber(leaveUsedNumberTotal);

		reserveLeaveGrantRemainingData.setDetails(leaveNumberInfo);
		

		return Optional.of(reserveLeaveGrantRemainingData);
	}

	/**
	 * 付与残数データから年休不足分の年休付与残数を削除
	 */
	public void deleteDummy() {
		// 年休付与残数が残数不足の年休付与残数をListから削除
		List<ReserveLeaveGrantRemainingData> noDummyList = this.getGrantRemainingList().stream()
				.filter(c -> !c.isDummyData()).collect(Collectors.toList());
		this.setGrantRemainingList(noDummyList);
	}

	public static interface RequireM1 extends GetUpperLimitSetting.RequireM1, LeaveRemainingNumber.RequireM3 {

	}
}