package nts.uk.ctx.at.record.dom.remainingnumber.specialleave.empinfo.grantremainingdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.GrantBeforeAfterAtr;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.specialholiday.SpecialLeaveGrantUseDay;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.specialholiday.SpecialLeaveUseNumber;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.export.SpecialLeaveManagementService;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.LeaveExpirationStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.RemNumShiftListWork;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.LeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveOverNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUndigestNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialholidaymng.interim.InterimSpecialHolidayMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.grantremainingdata.SpecialLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.grantremainingdata.SpecialLeaveNumberInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.grantremainingdata.grantnumber.SpecialLeaveUndigestNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.service.SpecialHolidayInterimMngData;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.service.SpecialLeaveError;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday;
import nts.uk.ctx.at.shared.dom.specialholiday.export.NextSpecialLeaveGrant;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantDateTblReferenceGrant;
import nts.uk.ctx.at.shared.dom.specialholiday.periodinformation.GrantDeadline;
import nts.uk.ctx.at.shared.dom.specialholiday.periodinformation.LimitAccumulationDays;
import nts.uk.ctx.at.shared.dom.specialholiday.periodinformation.LimitCarryoverDays;

/**
 * 特別休暇情報
 * @author shuichi_ishida
 */
@Getter
@Setter
public class SpecialLeaveInfo implements Cloneable {

	/** 年月日 */
	private GeneralDate ymd;
	/** 残数 */
	private SpecialLeaveRemaining remainingNumber;
	/** 付与残数データ */
	private List<SpecialLeaveGrantRemainingData> grantRemainingDataList;
	/** 付与情報 */
	private Optional<SpecialLeaveGrantUseDay> grantDaysInfo;

	/**
	 * コンストラクタ
	 */
	public SpecialLeaveInfo(){

		this.ymd = GeneralDate.min();
		this.remainingNumber = new SpecialLeaveRemaining();
		this.grantRemainingDataList = new ArrayList<>();
		this.grantDaysInfo = Optional.empty();
	}

	/**
	 * ファクトリー
	 * @param ymd 年月日
	 * @param remainingNumber 残数
	 * @param grantRemainingDataList 付与残数データ
	 * @return 特休情報
	 */
	public static SpecialLeaveInfo of(
			GeneralDate ymd,
			SpecialLeaveRemaining remainingNumber,
			List<SpecialLeaveGrantRemainingData> grantRemainingDataList
			){

		SpecialLeaveInfo domain = new SpecialLeaveInfo();
		domain.ymd = ymd;
		domain.remainingNumber = remainingNumber;
		domain.grantRemainingDataList = grantRemainingDataList;
		return domain;
	}

	@Override
	public SpecialLeaveInfo clone() {
		SpecialLeaveInfo cloned = new SpecialLeaveInfo();

		cloned.ymd = this.ymd;
		cloned.remainingNumber = this.remainingNumber.clone();

		cloned.grantRemainingDataList = new ArrayList<>();
		for (val grantRemainingData : this.grantRemainingDataList){
			cloned.grantRemainingDataList.add(grantRemainingData.clone());

		}
		cloned.grantDaysInfo = this.grantDaysInfo;
		return cloned;
	}

	/**
	 * 特休付与残数を更新
	 * @param grantPeriodAtr　付与前付与後
	 */
	public void updateRemainingNumber(GrantBeforeAfterAtr grantPeriodAtr){
		this.remainingNumber.updateRemainingNumber(
				this.getGrantRemainingDataList(), grantPeriodAtr);
	}

//	List<SpecialLeaveGrantRemaining> remainingDataList,
//	boolean afterGrantAtr){


	/**
	 * 特休の消滅・付与・消化
	 * @param require
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param specialLeaveAggregatePeriodWork 処理中の特休集計期間WORK
	 * @param interimSpecialHolidayMng 暫定特休管理データ
//	 * @param isGetNextMonthData 翌月管理データ取得フラグ
//	 * @param isCalcAttendanceRate 出勤率計算フラグ
	 * @param specialLeaveCode 特別休暇コード
	 * @param entryDate 入社日
	 * 	@param aggrResult 特休の集計結果
	 * @return 特休の集計結果
	 */
	public InPeriodOfSpecialLeaveResultInfor lapsedGrantDigest(
			SpecialLeaveManagementService.RequireM5 require,
			String companyId, String employeeId,
			SpecialLeaveAggregatePeriodWork specialLeaveAggregatePeriodWork,
			SpecialHolidayInterimMngData specialHolidayInterimMngData,
			int specialLeaveCode, GeneralDate entryDate,
			InPeriodOfSpecialLeaveResultInfor aggrResult,
			GeneralDate baseDate){

		/** 特別休暇情報．年月日を開始日に更新 */
		this.ymd = specialLeaveAggregatePeriodWork.getPeriod().start();

		/** 付与処理 */
		aggrResult = this.grantProcess(require, companyId, employeeId,
				specialLeaveAggregatePeriodWork, aggrResult, specialLeaveCode, entryDate, baseDate);

		/** 消化処理 */
		aggrResult = this.digestProcess(
				require, companyId, employeeId,
				specialLeaveAggregatePeriodWork, specialHolidayInterimMngData, aggrResult, specialLeaveCode, entryDate);

		/** 残数不足エラーをチェックする */
		aggrResult = this.checkError(aggrResult, specialLeaveAggregatePeriodWork, entryDate);

		/** 期間終了退避処理 */
		saveStateEndPeriod(specialLeaveAggregatePeriodWork, aggrResult);

		/** 終了時点更新処理 */
		updateEnd(specialLeaveAggregatePeriodWork, aggrResult);

		/** ○消滅処理 */
		aggrResult = this.lapsedProcess(specialLeaveAggregatePeriodWork, aggrResult, entryDate);

		/** 「特休の集計結果」を返す */
		return aggrResult;
	}

	/** 終了時点更新処理 */
	private void updateEnd(SpecialLeaveAggregatePeriodWork specialLeaveAggregatePeriodWork,
			InPeriodOfSpecialLeaveResultInfor aggrResult) {
		// 期間終了日翌日時点の期間かチェック
		// 処理中の「特別休暇集計期間WORK．終了日の翌日の期間かどうか」= true
		if (!specialLeaveAggregatePeriodWork.getEndDay().isNextPeriodEndAtr()){
			return;
		}

		// 「特別休暇の集計結果．特別休暇エラー情報」に受け取った特別休暇エラーを全て追加
		// ※既に「特別休暇エラー情報」に存在する特別休暇エラーは追加不要。
		for(SpecialLeaveError e : aggrResult.getSpecialLeaveErrors())
			aggrResult.addError(e);

		// 年月日を更新　←　終了日
		this.ymd = specialLeaveAggregatePeriodWork.getPeriod().end();
	}

	/** 期間終了退避処理 */
	private void saveStateEndPeriod(SpecialLeaveAggregatePeriodWork specialLeaveAggregatePeriodWork,
			InPeriodOfSpecialLeaveResultInfor aggrResult) {
		// 期間終了日時点の特休情報を消化後に退避するかチェック
		// 処理中の「特別休暇集計期間WORK．終了日の期間かどうか」=true
		if (specialLeaveAggregatePeriodWork.getEndDay().isPeriodEndAtr()){

			// 「特別休暇の集計結果．特別休暇情報(期間終了日時点)」←処理中の「特別休暇情報」
			aggrResult.setAsOfPeriodEnd(this.clone());
		}

		// 期間終了日翌日時点の期間かチェック
		if (specialLeaveAggregatePeriodWork.getEndDay().isNextPeriodEndAtr()){

			// 「特別休暇の集計結果．特別休暇情報(期間終了日の翌日開始時点)」←処理中の「特別休暇情報」
			aggrResult.setAsOfStartNextDayOfPeriodEnd(this.clone());
		}
	}

	/** 初回付与か判断する */
	private boolean isFirstTimeGrant(SpecialLeaveAggregatePeriodWork aggregatePeriodWork) {

		//期間開始日に付与があるか
		if(!aggregatePeriodWork.getGrantWork().isGrantAtr()) {
			return false;
		}

		//初回付与か
		if ( aggregatePeriodWork.getGrantWork().getSpecialLeaveGrant().isPresent() ) {
			return aggregatePeriodWork.getGrantWork().getSpecialLeaveGrant().get().getTimes().v() == 1;
		}

		return false;
	}

	/**
	 * 消滅処理
	 * @param aggregatePeriodWork 処理中の特休集計期間WORK
	 * @param aggrResult 特休の集計結果
	 * @param entryDate 入社日
	 * @return 特休の集計結果
	 */
	private InPeriodOfSpecialLeaveResultInfor lapsedProcess(
			SpecialLeaveAggregatePeriodWork aggregatePeriodWork,
			InPeriodOfSpecialLeaveResultInfor aggrResult,
			GeneralDate entryDate){

		// 消滅フラグを取得
		if (!aggregatePeriodWork.getLapsedWork().isLapsedAtr()) return aggrResult;

		/** 特別休暇を消滅させる */
		this.decreaseSpecicalHoliday(aggregatePeriodWork);

		// 付与前付与後を判断する
		GrantBeforeAfterAtr grantPeriodAtr
			= aggregatePeriodWork.judgeGrantPeriodAtr(entryDate);
		
		// 特別休暇情報残数を更新
		this.updateRemainingNumber(grantPeriodAtr);

		// 特休情報を「特休の集計結果．特休情報（消滅）」に追加
		if (!aggrResult.getLapsed().isPresent()){
			aggrResult.setLapsed(Optional.of(new ArrayList<>()));
		}
		aggrResult.getLapsed().get().add(this.clone());

		// 「特休の集計結果」を返す
		return aggrResult;
	}

	/** 特別休暇を消滅させる */
	private void decreaseSpecicalHoliday(SpecialLeaveAggregatePeriodWork aggregatePeriodWork) {

		// 「特別休暇情報．付与残数データ」を取得
		// 【条件】
		// 期限日=特別休暇集計期間WORK．期間．開始日の前日
		// 年休不足ダミーフラグ=false
		// 【ソート】
		// 付与日(ASC)
		// ※期限が切れた翌日開始時点で消滅する
		this.grantRemainingDataList.sort((a,b)->a.getGrantDate().compareTo(b.getGrantDate()));

		for (val grantRemainingNumber : this.grantRemainingDataList){

			// 期限日が特休集計期間WORK.期間.開始日の前日でなければ、消滅処理しない
//			if (!grantRemainingNumber.getDeadline().equals(aggregatePeriodWork.getPeriod().start().addDays(-1))){
//				continue;
//			}
			if (!grantRemainingNumber.getDeadline().equals(aggregatePeriodWork.getPeriod().end())){
				continue;
			}

			// 特休不足ダミーフラグがtrueなら、消滅処理しない
			if (grantRemainingNumber.isDummyData() == true) continue;
			
			// 処理中の付与残数データを期限切れにする
			grantRemainingNumber.setExpirationStatus(LeaveExpirationStatus.EXPIRED);

			// 未消化数を更新
			if (!this.remainingNumber.getSpecialLeaveUndigestNumber().isPresent()) {
				this.remainingNumber.setSpecialLeaveUndigestNumber(Optional.of(new SpecialLeaveUndigestNumber()));
			}

			val remainingNumber = grantRemainingNumber.getDetails().getRemainingNumber();
			int minites = remainingNumber.getMinutes().map(c -> c.valueAsMinutes()).orElse(0);
			LeaveUndigestNumber leaveUndigestNumber = new LeaveUndigestNumber(remainingNumber.getDays().v(), minites);

			// 特別休暇情報残数．未消化←処理中の「付与残数データ．残数」を加算
			this.remainingNumber.getSpecialLeaveUndigestNumber().get().add(leaveUndigestNumber);
		}
	}

	/**
	 * 付与処理
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param aggregatePeriodWork 特休集計期間WORK
	 * @param aggrResult 特休の集計結果
	 * @param specialLeaveCode 特別休暇コード
	 * @param entryDate 入社日
	 * @return 特休の集計結果
	 */
	private InPeriodOfSpecialLeaveResultInfor grantProcess(
			SpecialLeaveManagementService.RequireM5 require,
			String companyId,
			String employeeId,
			SpecialLeaveAggregatePeriodWork aggregatePeriodWork,
			InPeriodOfSpecialLeaveResultInfor aggrResult,
			int specialLeaveCode,
			GeneralDate entryDate,
			GeneralDate baseDate){

		/** 付与をチェック */
		if (!aggregatePeriodWork.getGrantWork().isGrantAtr()) return aggrResult;

		/** ダミーデータを削除する */
		this.deleteDummy();

		/** 特別休暇を付与する */
		grantSpecialHoliday(require, companyId, employeeId, aggregatePeriodWork, specialLeaveCode, baseDate);
		
		/** 付与情報を更新 */
		this.grantDaysInfo = updateGrantDaysInfo(aggregatePeriodWork.getGrantWork().getSpecialLeaveGrant());

		/** 付与前付与後を判断する */
		GrantBeforeAfterAtr grantPeriodAtr = aggregatePeriodWork.judgeGrantPeriodAtr(entryDate);

		/** 特別休暇情報残数を更新 */
		this.updateRemainingNumber(grantPeriodAtr);

		/** 「特別休暇情報(付与時点)」に「特別休暇情報」を追加 */
		if ( !aggrResult.getAsOfGrant().isPresent() ){
			aggrResult.setAsOfGrant(Optional.of(new ArrayList<SpecialLeaveInfo>()));
		}
		aggrResult.getAsOfGrant().get().add(this.clone());

		/** 「特別休暇の集計結果」を返す */
		return aggrResult;
	}

	/** 特別休暇を付与する */
	private SpecialLeaveInfo grantSpecialHoliday(SpecialLeaveManagementService.RequireM5 require, String companyId,
			String employeeId, SpecialLeaveAggregatePeriodWork aggregatePeriodWork, int specialLeaveCode, GeneralDate baseDate) {

		// 特別休暇を付与する

		// 特別休暇付与残数データを作成する --------------------------

		// パラメータ「次回特別休暇付与.期限日」を取得
		if ( aggregatePeriodWork.getGrantWork().getSpecialLeaveGrant().isPresent() ){

			// 「特別休暇付与残数データ」を作成
			val newRemainData = aggregatePeriodWork.getGrantWork().getSpecialLeaveGrant().get()
					.toSpecialLeaveGrantRemainingData(employeeId, specialLeaveCode);		


			// 作成した「特休付与残数データ」を付与残数データリストに追加
			this.grantRemainingDataList.add(newRemainData);
		}

		// 付与上限まで調整 --------------------------

		// 上限設定されているか確認

		// 特別休暇コードから設定を取得（会社設定）
		Optional<SpecialHoliday> specialHolidayOpt
			= require.specialHoliday(companyId, specialLeaveCode);

		// 繰越上限日数 > 0
		if ( specialHolidayOpt.isPresent() ){

			// 繰越上限日数
			int limitCarryoverDays = 0;
			Optional<LimitCarryoverDays> LimitCarryoverDaysOp = specialHolidayOpt.get().getGrantRegular().getLimitCarryoverDays();
			if ( LimitCarryoverDaysOp.isPresent() ) {
				limitCarryoverDays = LimitCarryoverDaysOp.get().v();
			}

			if ( limitCarryoverDays > 0 ){

				// 合計
				LeaveRemainingNumber leaveRemainingNumber = new LeaveRemainingNumber();

				// 特別休暇付与残数データ.明細.残数の合計を取得
				this.getGrantRemainingDataList().forEach(c->{
					leaveRemainingNumber.add(c.getDetails().getRemainingNumber());
				});

				// 残数合計と上限日数の差分を求める
				double diff = leaveRemainingNumber.getDays().v() - limitCarryoverDays;

				// 差分を残数から引き、上限超過消滅日数に加算
				if ( diff > 0 ){

					// 付与日でソート
					List<SpecialLeaveGrantRemainingData> list = this.getGrantRemainingDataList().stream()
						.sorted((a,b)->a.getGrantDate().compareTo(b.getGrantDate()))
						.collect(Collectors.toList());

					// 付与残数データの一番新しいデータ
					if ( list.size() > 0 ){
						SpecialLeaveGrantRemainingData specialLeaveGrantRemainingData
							= list.get(list.size()-1);

						// 付与残数データの一番新しいデータから上限を超えた差分を引く
						double daysDiff
							= specialLeaveGrantRemainingData.getDetails().getRemainingNumber().getDays().v() - diff;
						int time = 0;
						if ( specialLeaveGrantRemainingData.getDetails().getRemainingNumber().getMinutes().isPresent() ){
							time = specialLeaveGrantRemainingData.getDetails().getRemainingNumber().getMinutes().get().v();
						}

						// 付与残数データ.使用数.上限超過消滅日数←　付与残数データ.使用数.上限超過消滅日数　＋　差分
						if(specialLeaveGrantRemainingData.getDetails().getUsedNumber().getLeaveOverLimitNumber().isPresent()){
							specialLeaveGrantRemainingData.getDetails().getUsedNumber().getLeaveOverLimitNumber().get()
									.add(new LeaveOverNumber(diff, time));
						}else{
							specialLeaveGrantRemainingData.getDetails().getUsedNumber()
									.setLeaveOverLimitNumber(Optional.of(new LeaveOverNumber(diff, time)));
						}
						
						// 付与残数データ.残数←　付与残数データ.残数　ー　差分
						specialLeaveGrantRemainingData.getDetails().setRemainingNumber(require, companyId, employeeId, baseDate,
								new LeaveRemainingNumber(daysDiff, time));



					}
				}
			}
		}
		return this;
	}
	
	/**
	 * 付与情報を更新
	 * @param nextGrant
	 * @return
	 */
	private Optional<SpecialLeaveGrantUseDay> updateGrantDaysInfo(Optional<NextSpecialLeaveGrant> nextGrant){
		if(!nextGrant.isPresent()){
			return this.grantDaysInfo;
		}
		
		double grantDay = nextGrant.get().getGrantDays().v();
		if(this.grantDaysInfo.isPresent()){
			grantDay += this.grantDaysInfo.get().v();
		}
		return Optional.of(new SpecialLeaveGrantUseDay(grantDay));
		
	}
	
	

	/**
	 * 消化処理
	 * @param require
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param aggregatePeriodWork 特休集計期間WORK
	 * @param specialHolidayInterimMngData 暫定特休管理データ
	 * @param aggrResult 特休の集計結果
	 * 	@param specialLeaveCode 特休コード
	 * @param entryDate 入社日
	 * @return 特休の集計結果
	 */
	private InPeriodOfSpecialLeaveResultInfor digestProcess(
			LeaveRemainingNumber.RequireM3 require,
			String companyId,
			String employeeId,
			SpecialLeaveAggregatePeriodWork aggregatePeriodWork,
			SpecialHolidayInterimMngData specialHolidayInterimMngData,
			InPeriodOfSpecialLeaveResultInfor aggrResult,
			int specialLeaveCode,
			GeneralDate entryDate
			){

		// 集計期間の翌日を集計する時は、消化処理は行わない
		if ( aggregatePeriodWork.getEndDay().isNextPeriodEndAtr() ){
			return aggrResult;
		}

		/** 付与前付与後を判断する */
		GrantBeforeAfterAtr grantPeriodAtr = aggregatePeriodWork.judgeGrantPeriodAtr(entryDate);

		// 時間休暇消化日一覧（List）
		List<GeneralDate> digestDateList = new ArrayList<GeneralDate>();

		val listInterimSpecialHolidayMng = getRemainData(specialHolidayInterimMngData);

		for (val interimSpecialHolidayMng : listInterimSpecialHolidayMng) {
			if (!aggregatePeriodWork.getPeriod().contains(interimSpecialHolidayMng.getYmd())) continue;

			// 特休を消化する
			{
				// 特休付与残数を取得
				val targetRemainingDatas = new ArrayList<LeaveGrantRemainingData>();
				for (val remainingData : this.grantRemainingDataList){
					if (interimSpecialHolidayMng.getYmd().before(remainingData.getGrantDate())) continue;
					if (interimSpecialHolidayMng.getYmd().after(remainingData.getDeadline())) continue;
					targetRemainingDatas.add(remainingData);
				}

				// 休暇残数を指定使用数消化する ----------------------------------

				// 「休暇残数シフトリストWORK」一時変数を作成
				RemNumShiftListWork remNumShiftListWork = new RemNumShiftListWork();

				// 使用数変数作成
				double days = 0.0;
				if ( interimSpecialHolidayMng.getUseDays().isPresent() ){
					days = interimSpecialHolidayMng.getUseDays().get().v();
				}
				int minutes = 0;
				if ( interimSpecialHolidayMng.getUseTimes().isPresent() ){
					minutes = interimSpecialHolidayMng.getUseTimes().get().v();
				}
				LeaveUsedNumber leaveUsedNumber = new LeaveUsedNumber();
				leaveUsedNumber.setDays(new LeaveUsedDayNumber(days));
				leaveUsedNumber.setMinutes(Optional.of(new LeaveUsedTime(minutes)));

				// 休暇残数を指定使用数消化する
				Optional<LeaveGrantRemainingData> dummyData
					= LeaveGrantRemainingData.digest(
						require,
						targetRemainingDatas,
						remNumShiftListWork,
						leaveUsedNumber,
						companyId,
						employeeId,
						interimSpecialHolidayMng.getYmd()
						);

				// 付与残数データにダミーデータリストを追加
				if(dummyData.isPresent()) {
					SpecialLeaveGrantRemainingData addData = SpecialLeaveGrantRemainingData.of(dummyData.get(), specialLeaveCode);
					this.grantRemainingDataList.add(addData);
				}

				// 時間休暇消化数を求める
				int unDigestTime = 0;
				if ( remNumShiftListWork.getUnusedNumber().getMinutes().isPresent() ){
					unDigestTime = remNumShiftListWork.getUnusedNumber().getMinutes().get().v();
				}

				SpecialLeaveUsedWork work = SpecialLeaveUsedWork.of(interimSpecialHolidayMng);
				int usedTime = work.calcDigestTime(unDigestTime);

				// 時間休暇消化日一覧に追加をするか

				// 消化できた時間があるか （消化できた時間　＞０）
				if ( 0 < usedTime ){
					// 消化日を追加
					digestDateList.add(interimSpecialHolidayMng.getYmd());
				}

				// 残数（現在）を消化後の状態にする
				this.updateRemainingNumber(grantPeriodAtr);

				// 実特休（特休（マイナスあり））に使用数を加算する
				this.remainingNumber.getSpecialLeaveWithMinus().addUsedNumber(
						SpecialLeaveUseNumber.of(interimSpecialHolidayMng.getUseDays().map(x -> x.v()).orElse(0.0),
								interimSpecialHolidayMng.getUseTimes().map(x -> x.v()).orElse(0)),
						grantPeriodAtr);

				// 特休情報残数を更新
				this.updateRemainingNumber(grantPeriodAtr);

			}
		}

		// 「特休の集計結果」を返す
		return aggrResult;
	}

	/** 「List<特別休暇暫定管理データ」を取得する */
	private List<InterimSpecialHolidayMng> getRemainData(SpecialHolidayInterimMngData specialHolidayInterimMngData) {
		// 暫定残数管理データ
		val listInterimSpecialHolidayMng = specialHolidayInterimMngData.getLstSpecialInterimMng();

		// パラメータ「List<特別休暇暫定管理データ」を取得する
		// 【条件】 対象日 >= 特別休暇集計期間WORK．期間．開始日
		// 		AND 対象日 <= 特別休暇集計期間WORK．期間．終了日
		// 【ソート】 対象日
		listInterimSpecialHolidayMng.sort((a, b) -> a.getYmd().compareTo(b.getYmd()));

		return listInterimSpecialHolidayMng;
	}

	private InPeriodOfSpecialLeaveResultInfor checkError(
			InPeriodOfSpecialLeaveResultInfor resultInfo,
			SpecialLeaveAggregatePeriodWork specialLeaveAggregatePeriodWork,
			GeneralDate entryDate) {
		// 残数不足エラーをチェックする -----------------------------
		List<SpecialLeaveError> errors = new ArrayList<>();
		{
			// 特休残数がマイナスかチェック
			val withMinus = this.remainingNumber.getSpecialLeaveWithMinus();
			if (withMinus.getRemainingNumberInfo().getRemainingNumber().isMinus()){

				// 付与前付与後を判断する
				GrantBeforeAfterAtr grantPeriodAtr
					= specialLeaveAggregatePeriodWork.judgeGrantPeriodAtr(entryDate);

				if (grantPeriodAtr.equals(GrantBeforeAfterAtr.AFTER_GRANT)){
					// 「特休不足エラー（付与後）」を追加
					errors.add(SpecialLeaveError.AFTERGRANT);
				}
				else {
					// 「特休不足エラー（付与前）」を追加
					errors.add(SpecialLeaveError.BEFOREGRANT);
				}
			}
		}
		resultInfo.getSpecialLeaveErrors().addAll(errors);
		return resultInfo;
	}

	/**
	 * マイナス分の特休付与残数を1レコードにまとめる
	 * @return 特別休暇付与残数データ
	 */
	public Optional<SpecialLeaveGrantRemainingData>
			createLeaveGrantRemainingShortageData(){

		// 残数不足（ダミー）として作成した「特別休暇付与残数(List)」を取得
		List<SpecialLeaveGrantRemainingData> remainingList
			= this.getGrantRemainingDataList();
		List<SpecialLeaveGrantRemainingData> dummyRemainingList
			= remainingList.stream()
				.filter(c -> c.isDummyData())
				.collect(Collectors.toList());

		if ( dummyRemainingList.size()==0 ) {
			return Optional.empty();
		}

		// 取得した特別休暇付与残数の「特別休暇使用数」、「特別休暇残数」をそれぞれ合計
		LeaveRemainingNumber leaveRemainingNumberTotal = new LeaveRemainingNumber();
		LeaveUsedNumber leaveUsedNumberTotal = new LeaveUsedNumber();
		dummyRemainingList.forEach(c->{
			leaveRemainingNumberTotal.add(c.getDetails().getRemainingNumber());
			leaveUsedNumberTotal.add(c.getDetails().getUsedNumber());
		});

		// 合計した「特別休暇使用数」「特別休暇残数」から特別休暇付与残数を作成
		SpecialLeaveGrantRemainingData specialLeaveGrantRemainingData
			= new SpecialLeaveGrantRemainingData(dummyRemainingList.stream().findFirst().get());
		SpecialLeaveNumberInfo leaveNumberInfo = new SpecialLeaveNumberInfo();
		// 明細．残数　←　合計した「特別休暇残数」
		leaveNumberInfo.setRemainingNumber(leaveRemainingNumberTotal);
		// 明細．使用数　←　合計した「特別休暇使用数」
		leaveNumberInfo.setUsedNumber(leaveUsedNumberTotal);
		specialLeaveGrantRemainingData.setDetails(leaveNumberInfo);

		return Optional.of(specialLeaveGrantRemainingData);
	}

	/**
	 * 付与残数データから特別休暇不足分の特別休暇付与残数を削除
	 */
	public void deleteDummy(){
		// 特別休暇付与残数が残数不足の特別休暇付与残数をListから削除
		List<SpecialLeaveGrantRemainingData> noDummyList
			= this.getGrantRemainingDataList().stream()
				.filter(c->!c.isDummyData())
				.collect(Collectors.toList());
		this.setGrantRemainingDataList(noDummyList);
	}
}


