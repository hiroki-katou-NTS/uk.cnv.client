package nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.param;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveNumberInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.AnnualLeaveMaxData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.HalfdayAnnualLeaveMax;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.MaxMinutes;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.TimeAnnualLeaveMax;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.UsedMinutes;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.UsedTimes;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim.TempAnnualLeaveMngs;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.LeaveExpirationStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.RemNumShiftListWork;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.LeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.AnnualLeaveError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.GrantBeforeAfterAtr;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnualLeaveGrant;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnualLeaveUsedNumber;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualNumberDay;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPriority;

/**
 * 年休情報
 * 
 * @author shuichi_ishida
 */
@Getter
@Setter
public class AnnualLeaveInfo implements Cloneable {

	/** 年月日 */
	private GeneralDate ymd;
	/** 残数 */
	private AnnualLeaveRemaining remainingNumber;
	/** 付与残数データ */
	private List<AnnualLeaveGrantRemainingData> grantRemainingDataList;
	/** 上限データ */
	private AnnualLeaveMaxData maxData;
	/** 付与情報 */
	private Optional<AnnualLeaveGrant> grantInfo;
	/** 使用日数 */
	private AnnualLeaveUsedDayNumber usedDays;
	/** 使用時間 */
	private UsedMinutes usedTime;
	/** 年休設定 */
	private AnnualPaidLeaveSetting annualPaidLeaveSet;

	/**
	 * コンストラクタ
	 */
	public AnnualLeaveInfo() {

		this.ymd = GeneralDate.min();
		this.remainingNumber = new AnnualLeaveRemaining();
		this.grantRemainingDataList = new ArrayList<>();
		this.maxData = new AnnualLeaveMaxData();
		this.grantInfo = Optional.empty();
		this.usedDays = new AnnualLeaveUsedDayNumber(0.0);
		this.usedTime = new UsedMinutes(0);
		this.annualPaidLeaveSet = null;
	}

	/**
	 * ファクトリー
	 * 
	 * @param ymd
	 *            年月日
	 * @param remainingNumber
	 *            残数
	 * @param grantRemainingNumberList
	 *            付与残数データ
	 * @param maxData
	 *            上限データ
	 * @param grantInfo
	 *            付与情報
	 * @param usedDays
	 *            使用日数
	 * @param usedTime
	 *            使用時間
	 * @param afterGrantAtr
	 *            付与後フラグ
	 * @return 年休情報
	 */
	public static AnnualLeaveInfo of(GeneralDate ymd, AnnualLeaveRemaining remainingNumber,
			List<AnnualLeaveGrantRemainingData> grantRemainingNumberList, AnnualLeaveMaxData maxData,
			Optional<AnnualLeaveGrant> grantInfo, AnnualLeaveUsedDayNumber usedDays, UsedMinutes usedTime,
			boolean afterGrantAtr) {

		AnnualLeaveInfo domain = new AnnualLeaveInfo();
		domain.ymd = ymd;
		domain.remainingNumber = remainingNumber;
		domain.grantRemainingDataList = grantRemainingNumberList;
		domain.maxData = maxData;
		domain.grantInfo = grantInfo;
		domain.usedDays = usedDays;
		domain.usedTime = usedTime;
		// domain.afterGrantAtr = afterGrantAtr;
		return domain;
	}

	@Override
	public AnnualLeaveInfo clone() {
		AnnualLeaveInfo cloned = new AnnualLeaveInfo();
		// try {
		cloned.ymd = this.ymd;
		cloned.remainingNumber = this.remainingNumber.clone();
		for (val grantRemainingNumber : this.grantRemainingDataList) {
			val detail = grantRemainingNumber.getDetails();
			Integer grantMinutes = null;
			if (detail.getGrantNumber().getMinutes().isPresent()) {
				grantMinutes = detail.getGrantNumber().getMinutes().get().v();
			}
			Integer usedMinutes = null;
			if (detail.getUsedNumber().getMinutes().isPresent()) {
				usedMinutes = detail.getUsedNumber().getMinutes().get().v();
			}
			Double stowageDays = null;
			if (detail.getUsedNumber().getStowageDays().isPresent()) {
				stowageDays = detail.getUsedNumber().getStowageDays().get().v();
			}
			Integer remainMinutes = null;
			if (detail.getRemainingNumber().getMinutes().isPresent()) {
				remainMinutes = detail.getRemainingNumber().getMinutes().get().v();
			}
			Double prescribedDays = null;
			Double deductedDays = null;
			Double workingDays = null;
			if (grantRemainingNumber.getAnnualLeaveConditionInfo().isPresent()) {
				val annualLeaveCond = grantRemainingNumber.getAnnualLeaveConditionInfo().get();
				prescribedDays = annualLeaveCond.getPrescribedDays().v();
				deductedDays = annualLeaveCond.getDeductedDays().v();
				workingDays = annualLeaveCond.getWorkingDays().v();
			}
			AnnualLeaveGrantRemainingData newRemainData = AnnualLeaveGrantRemainingData.createFromJavaType(
					grantRemainingNumber.getLeaveID(), grantRemainingNumber.getEmployeeId(),
					grantRemainingNumber.getGrantDate(), grantRemainingNumber.getDeadline(),
					grantRemainingNumber.getExpirationStatus().value, grantRemainingNumber.getRegisterType().value,
					detail.getGrantNumber().getDays().v(), grantMinutes, detail.getUsedNumber().getDays().v(),
					usedMinutes, stowageDays, detail.getRemainingNumber().getDays().v(), remainMinutes, 0.0,
					prescribedDays, deductedDays, workingDays);
			cloned.grantRemainingDataList.add(newRemainData);
		}
		if (this.grantInfo.isPresent()) {
			cloned.grantInfo = Optional.of(this.grantInfo.get().clone());
		}
		cloned.usedDays = new AnnualLeaveUsedDayNumber(this.usedDays.v());
		cloned.usedTime = new UsedMinutes(this.usedTime.v());
		// cloned.afterGrantAtr = this.afterGrantAtr;

		// 以下は、cloneしなくてよい。
		cloned.maxData = this.maxData;
		cloned.annualPaidLeaveSet = this.annualPaidLeaveSet;
		// }
		// catch (Exception e){
		// throw new RuntimeException("AnnualLeaveInfo clone error.");
		// }
		return cloned;
	}

	public List<AnnualLeaveGrantRemainingData> getGrantRemainingNumberList() {
		return this.grantRemainingDataList.stream().map(c -> (AnnualLeaveGrantRemainingData) c)
				.collect(Collectors.toList());
	}

	/**
	 * 年休付与残数を更新
	 */
	public void updateRemainingNumber(GrantBeforeAfterAtr grantPeriodAtr, GeneralDate endDay) {
		this.remainingNumber.updateRemainingNumber(this.grantRemainingDataList, grantPeriodAtr, this.maxData, endDay);
	}

	/**
	 * 年休の消滅・付与・消化
	 * 
	 * @param require
	 * @param companyId
	 *            会社ID
	 * @param employeeId
	 *            社員ID
	 * @param aggregatePeriodWork
	 *            年休集計期間WORK
	 * @param tempAnnualLeaveMngs
	 *            暫定年休管理データリスト
	 * @param isGetNextMonthData
	 *            翌月管理データ取得フラグ
	 * @param isCalcAttendanceRate
	 *            出勤率計算フラグ
	 * @param aggrResult
	 *            年休の集計結果
	 * @param annualPaidLeaveSet
	 *            年休設定
	 * @return 年休の集計結果
	 */
	public AggrResultOfAnnualLeave lapsedGrantDigest(LeaveRemainingNumber.RequireM3 require, String companyId,
			String employeeId, AggregatePeriodWork aggregatePeriodWork, List<TempAnnualLeaveMngs> tempAnnualLeaveMngs,
			AggrResultOfAnnualLeave aggrResult, AnnualPaidLeaveSetting annualPaidLeaveSet) {

		this.annualPaidLeaveSet = annualPaidLeaveSet;

		this.ymd = aggregatePeriodWork.getPeriod().end();

		// ○付与処理
		aggrResult = this.grantProcess(companyId, employeeId, aggregatePeriodWork, aggrResult);

		// ○消化処理
		aggrResult = this.digestProcess(require, companyId, employeeId, aggregatePeriodWork, tempAnnualLeaveMngs,
				aggrResult);

		// ○エラーをチェックする
		List<AnnualLeaveError> lstError = checkError(aggregatePeriodWork, companyId, require);

		// 期間終了退避処理
		periodEndSaveProcess(companyId, employeeId, aggregatePeriodWork, aggrResult);

		// 終了時点更新処理
		this.updateProcessEnd(companyId, employeeId, aggregatePeriodWork, aggrResult, lstError);

		// ○消滅処理
		aggrResult = this.lapsedProcess(aggregatePeriodWork, aggrResult);
		// ○「年休の集計結果」を返す

		return aggrResult;

	}

	// 初回付与かチェックする
	public boolean isFirstTimeGrant(AggregatePeriodWork aggregatePeriodWork) {

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
	 * @param aggregatePeriodWork
	 *            処理中の年休集計期間WORK
	 * @param aggrResult
	 *            年休の集計結果
	 * @return 年休の集計結果
	 */
	private AggrResultOfAnnualLeave lapsedProcess(AggregatePeriodWork aggregatePeriodWork,
			AggrResultOfAnnualLeave aggrResult) {

		// 消滅フラグを取得
		if (!aggregatePeriodWork.getLapsedAtr().isLapsedAtr())
			return aggrResult;

		// 年休を消滅させる
		extinguishAnnualLeave(aggregatePeriodWork);

		// 年休情報残数を更新
		this.updateRemainingNumber(aggregatePeriodWork.getGrantWork().getGrantPeriodAtr(),
				aggregatePeriodWork.getPeriod().end());

		// 年休情報を「年休の集計結果．年休情報（消滅）」に追加
		if (!aggrResult.getLapsed().isPresent())
			aggrResult.setLapsed(Optional.of(new ArrayList<>()));
		aggrResult.getLapsed().get().add(this.clone());

		// 「年休の集計結果」を返す
		return aggrResult;
	}

	// 年休を消滅させる
	public void extinguishAnnualLeave(AggregatePeriodWork aggregatePeriodWork) {
		// 「付与残数データ」を取得
		val itrGrantRemainingNumber = this.grantRemainingDataList.listIterator();
		while (itrGrantRemainingNumber.hasNext()) {
			val grantRemainingNumber = itrGrantRemainingNumber.next();

			if (!grantRemainingNumber.getDeadline().equals(aggregatePeriodWork.getPeriod().end())) {
				continue;
			}

			// 年休不足ダミーフラグがtrueなら、消滅処理しない
			if (grantRemainingNumber.isDummyData() == true)
				continue;

			// 処理中の付与残数データを期限切れにする
			grantRemainingNumber.setExpirationStatus(LeaveExpirationStatus.EXPIRED);

		}
	}

	/**
	 * 付与処理
	 * 
	 * @param companyId
	 *            会社ID
	 * @param employeeId
	 *            社員ID
	 * @param aggregatePeriodWork
	 *            処理中の年休集計期間WORK
	 * @param aggrResult
	 *            年休の集計結果
	 * @return 年休の集計結果
	 */
	private AggrResultOfAnnualLeave grantProcess(String companyId, String employeeId,
			AggregatePeriodWork aggregatePeriodWork, AggrResultOfAnnualLeave aggrResult) {

		// 「付与フラグ」をチェック
		if (!aggregatePeriodWork.getGrantWork().isGrantAtr())
			return aggrResult;

		/** ダミーデータを削除する */
		this.deleteDummy();

		// 付与日から期限日を計算
		if (!aggregatePeriodWork.getGrantWork().getAnnualLeaveGrant().isPresent())
			return aggrResult;

		// 「年休付与残数データ」を作成する
		val newRemainData = aggregatePeriodWork.getGrantWork().getAnnualLeaveGrant().get()
				.toAnnualLeaveGrantRemainingData(employeeId);

		// 作成した「年休付与残数データ」を付与残数データリストに追加
		this.grantRemainingDataList.add(newRemainData);

		// 年休情報．付与情報に付与時の情報をセット
		this.grantInfo = aggregatePeriodWork.getGrantWork().getAnnualLeaveGrant().get()
				.toAnnualLeaveGrant(this.grantInfo);

		// 年休付与時に作成する
		this.maxData = new AnnualLeaveMaxData(employeeId,
				aggregatePeriodWork.getGrantWork().getAnnualLeaveGrant()
						.flatMap(alg -> alg.getHalfDayAnnualLeaveMaxTimes()
								.map(talmt -> new HalfdayAnnualLeaveMax(new AnnualNumberDay(talmt.v())))),
				aggregatePeriodWork.getGrantWork().getAnnualLeaveGrant().flatMap(alg -> alg.getTimeAnnualLeaveMaxTime()
						.map(talmt -> new TimeAnnualLeaveMax(new MaxMinutes(talmt.v())))));

		// 年休情報残数を更新
		this.updateRemainingNumber(aggregatePeriodWork.getGrantWork().getGrantPeriodAtr(),
				aggregatePeriodWork.getPeriod().end());

		// 「年休情報（付与時点）」に「年休情報」を追加
		if (!aggrResult.getAsOfGrant().isPresent())
			aggrResult.setAsOfGrant(Optional.of(new ArrayList<>()));
		aggrResult.getAsOfGrant().get().add(this.clone());

		// 「年休の集計結果」を返す
		return aggrResult;
	}

	/**
	 * 消化処理
	 * 
	 * @param repositoriesRequiredByRemNum
	 *            残数処理 キャッシュクラス
	 * @param companyId
	 *            会社ID
	 * @param employeeId
	 *            社員ID
	 * @param aggregatePeriodWork
	 *            処理中の年休集計期間WORK
	 * @param tempAnnualLeaveMngs
	 *            暫定年休管理データリスト
	 * @param aggrResult
	 *            年休の集計結果
	 * @return 年休の集計結果
	 */
	private AggrResultOfAnnualLeave digestProcess(LeaveRemainingNumber.RequireM3 require, String companyId,
			String employeeId, AggregatePeriodWork aggregatePeriodWork, List<TempAnnualLeaveMngs> tempAnnualLeaveMngs,
			AggrResultOfAnnualLeave aggrResult) {

		// 集計期間の翌日を集計する時は、消化処理は行わない
		if (aggregatePeriodWork.getEndWork().isNextPeriodEndAtr()) {
			return aggrResult;
		}

		// 「暫定年休管理データリスト」を取得する
		tempAnnualLeaveMngs.sort((a, b) -> a.getYmd().compareTo(b.getYmd()));

		// 空の時間年休消化日一覧を作成
		List<GeneralDate> digestDateList = new ArrayList<GeneralDate>();

		for (val tempAnnualLeaveMng : tempAnnualLeaveMngs) {

			// 年休使用数WORK
			AnnualLeaveUsedNumber usedNumber = new AnnualLeaveUsedNumber();

			if (!aggregatePeriodWork.getPeriod().contains(tempAnnualLeaveMng.getYmd()))
				continue;
			// 年休を消化する

			// 年休付与残数を取得
			List<LeaveGrantRemainingData> targetRemainingDatas = new ArrayList<LeaveGrantRemainingData>();

			for (val remainingData : this.grantRemainingDataList) {
				if (tempAnnualLeaveMng.getYmd().before(remainingData.getGrantDate()))
					continue;
				if (tempAnnualLeaveMng.getYmd().after(remainingData.getDeadline()))
					continue;
				targetRemainingDatas.add(remainingData);
			}

			// 取得設定をチェック
			if (this.annualPaidLeaveSet.getAcquisitionSetting().annualPriority == AnnualPriority.FIFO) {

				// 当年付与分から消化する （付与日 降順(DESC)）
				targetRemainingDatas.sort((a, b) -> -a.getGrantDate().compareTo(b.getGrantDate()));
			} else {

				// 繰越分から消化する （付与日 昇順(ASC)）
				targetRemainingDatas.sort((a, b) -> a.getGrantDate().compareTo(b.getGrantDate()));
			}

			// 使用数変数作成
			LeaveUsedNumber leaveUsedNumber = new LeaveUsedNumber();
			leaveUsedNumber.setDays(new LeaveUsedDayNumber(
					tempAnnualLeaveMng.getUsedNumber().getUsedDayNumber().map(mapper -> mapper.v()).orElse(0.0)));
			leaveUsedNumber.setMinutes(Optional.of(new LeaveUsedTime(
					tempAnnualLeaveMng.getUsedNumber().getUsedTime().map(mapper -> mapper.v()).orElse(0))));

			// 使用数に加算する
			Optional<AnnualLeaveUsedDayNumber> days = Optional
					.of(new AnnualLeaveUsedDayNumber(leaveUsedNumber.getDays().v()));
			Optional<UsedMinutes> times = Optional
					.of(new UsedMinutes(leaveUsedNumber.getMinutesOrZero().valueAsMinutes()));

			AnnualLeaveUsedNumber addNumber = AnnualLeaveUsedNumber.of(days, times);
			usedNumber.addUsedNumber(addNumber);

			// 「休暇残数シフトリストWORK」一時変数を作成
			RemNumShiftListWork remNumShiftListWork = new RemNumShiftListWork();

			// 休暇残数を指定使用数消化する
			Optional<LeaveGrantRemainingData> dummyData = LeaveGrantRemainingData.digest(require, targetRemainingDatas,
					remNumShiftListWork, leaveUsedNumber, companyId, employeeId,
					aggregatePeriodWork.getPeriod().start());

			// 付与残数データにダミーデータリストを追加
			if (dummyData.isPresent()) {
				this.grantRemainingDataList.add(AnnualLeaveGrantRemainingData.of(dummyData.get()));
			}

			// 時間年休消化数を求める

			// 消化できた時間を求める

			// 年休使用数．時間
			LeaveUsedTime leaveUsedTime = new LeaveUsedTime(
					tempAnnualLeaveMng.getUsedNumber().getUsedTime().map(c -> c.v()).orElse(0));

			// 消化できた時間 ←年休使用数．時間 ー INPUT.消化できなかった休暇使用数．時間
			// 消化できなかった休暇使用数．時間
			Optional<LeaveUsedTime> unUsedTime = remNumShiftListWork.getUnusedNumber().getMinutes();
			if (unUsedTime.isPresent()) {
				leaveUsedTime.addMinutes(unUsedTime.get().v() * -1);
			}
			// 時間年休消化日一覧に追加をする
			{
				// 消化できた時間 ＞０
				if (leaveUsedTime.v() > 0) {
					// 消化日←年月日
					// ※注意
					// 同じ年月日が既にリストにあったとしても追加する
					digestDateList.add(tempAnnualLeaveMng.getYmd());
				}
			}

			// 消化する
			this.maxData = this.maxData.digest(tempAnnualLeaveMng);

			// 残数（現在）を消化後の状態にする
			{
				// 実年休（年休（マイナスあり））に使用数を加算する
				this.getRemainingNumber().getAnnualLeaveWithMinus().addUsedNumber(usedNumber,
						aggregatePeriodWork.getGrantWork().getGrantPeriodAtr());
				// 年休情報残数を更新
				this.updateRemainingNumber(aggregatePeriodWork.getGrantWork().getGrantPeriodAtr(),
						aggregatePeriodWork.getPeriod().end());
			}
		}

		// 時間年休使用数を求める
		calcAnnualUsedTimes(tempAnnualLeaveMngs, digestDateList);

		// 「年休の集計結果」を返す
		return aggrResult;

	}

	/**
	 * 時間年休使用数を求める
	 * 
	 * @param tempAnnualLeaveMngs
	 *            暫定年休管理データ
	 * @param digestDateList
	 *            時間年休消化日数一覧
	 */
	private void calcAnnualUsedTimes(List<TempAnnualLeaveMngs> tempAnnualLeaveMngs, List<GeneralDate> digestDateList) {

		// 暫定年休管理データの内時間年休使用している件数を求める
		int useTimes = tempAnnualLeaveMngs.stream()
				.filter(c -> c.getUsedNumber().getUsedTime().map(d -> d.v()).orElse(0) > 0) // 暫定年休管理データ．年休使用数．時間＞０
				.collect(Collectors.toList()).size();
		// 年休（マイナスあり）．使用数．時間年休使用回数←暫定年休管理データ．年休使用数．時間＞０の件数
		this.getRemainingNumber().getAnnualLeaveWithMinus().getUsedNumberInfo()
				.setAnnualLeaveUsedTimes(new UsedTimes(useTimes));

		// 暫定年休管理データの内時間年休使用している日の件数を求める
		int useDayTimes = tempAnnualLeaveMngs.stream()
				.filter(c -> c.getUsedNumber().getUsedTime().map(d -> d.v()).orElse(0) > 0) // 暫定年休管理データ．年休使用数．時間＞０
				.map(c -> c.getYmd()).distinct() // 日付をユニークに取得
				.collect(Collectors.toList()).size();
		// 年休（マイナスあり）．使用数．時間年休使用日数←暫定年休管理データ．年休使用数．時間＞０の内同じ対象日のデータを除いた件数
		this.getRemainingNumber().getAnnualLeaveWithMinus().getUsedNumberInfo()
				.setAnnualLeaveUsedDayTimes(new UsedTimes(useDayTimes));

		// 時間年休を消化できた件数を求める
		int digestTimes = digestDateList.size();
		// 年休（マイナスなし）．使用数．時間年休使用回数←時間年休消化日数一覧の件数
		this.getRemainingNumber().getAnnualLeaveNoMinus().getUsedNumberInfo()
				.setAnnualLeaveUsedTimes(new UsedTimes(digestTimes));

		// 時間年休を消化できた日の件数を求める
		int digestDayTimes = digestDateList.stream().distinct().collect(Collectors.toList()).size(); // 日付をユニークに取得
		// 年休（マイナスなし）．使用数．時間年休使用日数←時間年休消化日数一覧の内同じ消化日を除いた件数
		this.getRemainingNumber().getAnnualLeaveNoMinus().getUsedNumberInfo()
				.setAnnualLeaveUsedDayTimes(new UsedTimes(digestDayTimes));
	}

	/**
	 * エラーをチェックする
	 * 
	 * @param aggregatePeriodWork
	 *            年休集計期間WORK
	 * @return List<年休エラー>
	 */
	public List<AnnualLeaveError> checkError(AggregatePeriodWork aggregatePeriodWork, String companyId, LeaveRemainingNumber.RequireM3 require ) {

		List<AnnualLeaveError> annualLeaveErrors = new ArrayList<AnnualLeaveError>();

		// 年休残数不足エラーチェック
		Optional<AnnualLeaveError> annualLeaveErrorOpt = this.remainingNumber.remainShortageCheck(aggregatePeriodWork);

		if (annualLeaveErrorOpt.isPresent()) {
			annualLeaveErrors.add(annualLeaveErrorOpt.get());
		}

		// 付与前付与後を判断する
		GrantBeforeAfterAtr grantAtr = aggregatePeriodWork.getGrantWork().judgeGrantPeriodAtr();
		// エラーチェック
		annualLeaveErrors.addAll(this.maxData.ErroeCheck(grantAtr, companyId, require));

		return annualLeaveErrors;
	}

	// 期間終了退避処理
	public void periodEndSaveProcess(String cid, String sid, AggregatePeriodWork aggregatePeriodWork,
			AggrResultOfAnnualLeave aggResult) {

		// ○期間終了日時点の年休情報を付与消滅前に退避するかチェック
		if (aggregatePeriodWork.getEndWork().isPeriodEndAtr()) {
			// ○「年休の集計結果．年休情報(期間終了日時点)」←処理中の「年休情報」
			aggResult.setAsOfPeriodEnd(this.clone());
		}

		// ○期間終了日翌日時点の期間かチェック
		if (aggregatePeriodWork.getEndWork().isNextPeriodEndAtr()) {
			// ○「年休の集計結果．年休情報(期間終了日の翌日開始時点)」←処理中の「年休情報」
			aggResult.setAsOfStartNextDayOfPeriodEnd(this.clone());
		}

		return;
	}

	// 終了時点更新処理
	public void updateProcessEnd(String cid, String sid, AggregatePeriodWork aggregatePeriodWork,
			AggrResultOfAnnualLeave aggResult, List<AnnualLeaveError> lstError) {

		// ○期間終了日翌日時点の期間かチェック
		if (aggregatePeriodWork.getEndWork().isNextPeriodEndAtr())
			return;

		// ○「年休の集計結果．年休エラー情報」に受け取った年休エラーを全て追加
		aggResult.getAnnualLeaveErrors().addAll(lstError);
		aggResult.setAnnualLeaveErrors(
				aggResult.getAnnualLeaveErrors().stream().distinct().collect(Collectors.toList()));

		// ○年休情報．年月日を終了日に更新
		aggResult.getAsOfPeriodEnd().setYmd(aggregatePeriodWork.getPeriod().end());

	}

	/**
	 * マイナス分の年休付与残数を1レコードにまとめる
	 * 
	 * @return 年休付与残数データ
	 */
	public Optional<AnnualLeaveGrantRemainingData> createLeaveGrantRemainingShortageData() {

		// 残数不足（ダミー）として作成した「年休付与残数(List)」を取得
		List<AnnualLeaveGrantRemainingData> remainingList = this.getGrantRemainingDataList();
		List<AnnualLeaveGrantRemainingData> dummyRemainingList = remainingList.stream().filter(c -> c.isDummyData())
				.collect(Collectors.toList());

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
		AnnualLeaveGrantRemainingData annualLeaveGrantRemainingData = new AnnualLeaveGrantRemainingData(
				dummyRemainingList.stream().findFirst().get());

		AnnualLeaveNumberInfo leaveNumberInfo = new AnnualLeaveNumberInfo();

		// 明細．残数 ← 合計した「年休残数」
		leaveNumberInfo.setRemainingNumber(leaveRemainingNumberTotal);
		// 明細．使用数 ← 合計した「年休使用数」
		leaveNumberInfo.setUsedNumber(leaveUsedNumberTotal);
		annualLeaveGrantRemainingData.setDetails(leaveNumberInfo);

		return Optional.of(annualLeaveGrantRemainingData);
	}

	/**
	 * 付与残数データから年休不足分の年休付与残数を削除
	 */
	public void deleteDummy() {
		// 年休付与残数が残数不足の年休付与残数をListから削除
		List<AnnualLeaveGrantRemainingData> noDummyList = this.getGrantRemainingDataList().stream()
				.filter(c -> !c.isDummyData()).collect(Collectors.toList());
		this.setGrantRemainingDataList(noDummyList);
	}

}
