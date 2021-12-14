package nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber;

import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.TimeAnnualLeaveTimeDay;
import nts.uk.ctx.at.shared.dom.workingcondition.LaborContractTime;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.AbsenceTenProcess;

/**
 * 休暇残数
 * @author masaaki_jinno
 *
 */
@Getter
@Setter
public class LeaveRemainingNumber {

	/**
	 * 日数
	 */
	protected LeaveRemainingDayNumber days;

	/**
	 * 時間
	 */
	protected Optional<LeaveRemainingTime> minutes;

	/**
	 * コンストラクタ
	 * @param days
	 * @param minutes
	 */
	public LeaveRemainingNumber() {
		this.days = new LeaveRemainingDayNumber(0.0);
		this.minutes = Optional.of(new LeaveRemainingTime(0));
	}

	/**
	 * コンストラクタ
	 * @param days
	 * @param minutes
	 */
	public LeaveRemainingNumber(double days, Integer minutes) {
		this.days = new LeaveRemainingDayNumber(days);
		this.minutes = minutes != null ? Optional.of(new LeaveRemainingTime(minutes)) : Optional.empty();
	}

	/**
	 * コンストラクタ
	 * @param days
	 * @param minutes
	 * @return
	 */
	public static LeaveRemainingNumber createFromJavaType(double days, Integer minutes) {
		return new LeaveRemainingNumber(days, minutes);
	}

	public LeaveRemainingTime getMinutesOrZero() {
		if(!this.getMinutes().isPresent())return new LeaveRemainingTime(0);
		return this.getMinutes().get();
	}

	@Override
	public LeaveRemainingNumber clone() {
		LeaveRemainingNumber cloned = new LeaveRemainingNumber();

		cloned.days = new LeaveRemainingDayNumber(days.v());
		cloned.minutes = minutes.map(c -> new LeaveRemainingTime(c.v()));

		return cloned;
	}

	/**
	 * ファクトリー
	 * @param days 日数
	 * @param minutes 時間
	 * @return 休暇残数
	 */
	public static LeaveRemainingNumber of(
			LeaveRemainingDayNumber days,
			Optional<LeaveRemainingTime> minutes) {

		LeaveRemainingNumber domain = new LeaveRemainingNumber();
		domain.days = days;
		domain.minutes = minutes;
		return domain;
	}

	/** 残数不足のときにはtrueを返す */
	public boolean isShortageRemain() {

		// 日数 < 0のとき
		if ( getDays().v() < 0.0d ) {
			return true;
		}
		// 日数 == 0.0 && 時間 < 0 のとき
		if ( getMinutes().isPresent() ) {
			if (getMinutes().get().v() < 0 ) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 残数を加算
	 * @param aLeaveRemainingNumber
	 */
	public void add(LeaveRemainingNumber aLeaveRemainingNumber){

		// 日付加算
		days = days.add(aLeaveRemainingNumber.getDays());

		// 時間加算
		if ( aLeaveRemainingNumber.getMinutes().isPresent() ){
			if ( this.getMinutes().isPresent() ){
				this.setMinutes(Optional.of(
					this.getMinutes().get().add(
							aLeaveRemainingNumber.getMinutes().get())));
			}
			else
			{
				this.setMinutes(Optional.of(new LeaveRemainingTime(
						aLeaveRemainingNumber.getMinutes().get().v())));
			}
		}
	}
	public LeaveUsedNumber digestLeaveUsedNumber(
			RequireM3 require,
			LeaveRemainingNumber leaveRemainNumber,
			String companyId,
			String employeeId,
			GeneralDate baseDate){
		
		LeaveUsedNumber usedNumber = new LeaveUsedNumber(leaveRemainNumber.getDays().v(),leaveRemainNumber.getMinutesOrZero().v());
		
		return this.digestLeaveUsedNumber(require, usedNumber, companyId, employeeId, baseDate);
	}
	

	/**
	 * 休暇使用数を消化する
	 * @param require 残数処理 Requireクラス
	 * @param leaveUsedNumber 休暇使用数
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param baseDate 基準日
	 * 
	 * ※戻り値は、「消化しきれない日数」を返す。
	 * 消化後の残数は、パラメータのインスタンスをそのまま更新します。
	 */
	public LeaveUsedNumber digestLeaveUsedNumber(
			RequireM3 require,
			LeaveUsedNumber leaveUsedNumber,
			String companyId,
			String employeeId,
			GeneralDate baseDate){

		// 消化できなかった休暇使用数クラスのオブジェクトを作成
		LeaveUsedNumber unusedNumber = new LeaveUsedNumber();

		while(true){
			if ( 1 <= this.days.v() // 1<=メンバ変数.休暇残数.日数
				&& 1 <= leaveUsedNumber.getMinutesOrZero().v() // input.休暇使用数.時間
			){
				boolean needStacking = false; // 積み崩しが必要かどうか

				if (!minutes.isPresent()){
					// メンバ変数.時間が存在しないときは、積み崩しが必要
					needStacking = true;

				} else if ( this.getMinutes().get().v() < leaveUsedNumber.getMinutesOrZero().v() ){
					// メンバ変数.時間 < input.休暇使用数.時間のときは、積み崩しが必要
					needStacking = true;
				}

				if (needStacking){ // 積み崩しが必要なとき

					// 年休１日に相当する時間年休時間を取得する
					Optional<LaborContractTime> contractTimeOpt
						= getContractTime(require, companyId, employeeId, baseDate);

					// 積み崩し処理を行う
					if (contractTimeOpt.isPresent()){

						// メンバ変数.日数から１をマイナス。
						days = new LeaveRemainingDayNumber(days.v() - 1);

						// メンバ変数.時間に、「年休１日に相当する時間年休時間」を加算。
						if (!minutes.isPresent()){
							minutes = Optional.of(new LeaveRemainingTime(
								contractTimeOpt.get().v()));
						} else {
							minutes = Optional.of(new LeaveRemainingTime(
								minutes.get().v() +	contractTimeOpt.get().v()));
						}
					} else {
						break;
					}
				} else {
					break;
				}
			} else {
				break;
			}
		}

		// 残数が足りているか？
		boolean surfaceRemNum = false;

		// Input.休暇使用数.日数<=メンバ変数.日数
		if ( leaveUsedNumber.getDays().v() <= days.v() ){
			if ( leaveUsedNumber.getMinutesOrZero().v() == 0 ){
				// Input.休暇使用数.時間が０のときは足りている
				surfaceRemNum = true;

			} else if ( this.getMinutes().isPresent() ){
				// Input.休暇使用数.時間<=メンバ変数.時間
				if ( leaveUsedNumber.getMinutesOrZero().v()
						<= this.getMinutes().get().v() ){
					surfaceRemNum = true;
				}
			}
		}

		// 一時変数（日付）
		double this_day = this.getDays().v();
		double input_day = leaveUsedNumber.getDays().v();

		// 一時変数（時間）
		int this_time = 0;
		int input_time = 0;

		if ( this.getMinutes().isPresent() ){
			this_time = this.getMinutes().get().v();
		}
		input_time = leaveUsedNumber.getMinutesOrZero().v();

		// 残数が足りている場合
		if ( surfaceRemNum ){
			this.days = new LeaveRemainingDayNumber(this_day-input_day);
			this.minutes = Optional.of(new LeaveRemainingTime(this_time-input_time));

		} else { // 残数が足りていない場合

			// Input.休暇使用数.時間 <= メンバ変数.時間 のとき
			// ・メンバ変数.時間 ← メンバ変数.時間 ーInput.休暇使用数.時間
			if (input_time <= this_time){
				this.minutes = Optional.of(new LeaveRemainingTime(this_time-input_time));
			}

			// メンバ変数.時間 ＜ Input.休暇使用数.時間のとき
			// ・消化できなかった休暇使用数.時間 ← Input.休暇使用数.時間ーメンバ変数.時間
			// ・メンバ変数.時間←０
			if (this_time < input_time){
				unusedNumber.minutes = Optional.of(new LeaveUsedTime(input_time-this_time));
				this.minutes = Optional.of(new LeaveRemainingTime(0));
			}

			// Input.休暇使用数.日数 <= 休暇残数.日数 のとき
			// ・メンバ変数.日数 ← メンバ変数.日数 ーInput.休暇使用数.日数
			if (input_day <= this_day){
				this.days = new LeaveRemainingDayNumber(this_day-input_day);
			}

			// メンバ変数.日数 ＜ Input.休暇使用数.日数のとき
			// ・消化できなかった休暇使用数.日数 ← Input.休暇使用数.日数ーメンバ変数.日数
			// ・メンバ変数.日数 ←０
			if (this_day < input_day){
				unusedNumber.days = new LeaveUsedDayNumber(input_day - this_day);
				this.days = new LeaveRemainingDayNumber(0.0);
			}
		}

		return unusedNumber;
	}

	/**
	 * １日に相当する契約時間を取得する
	 * @param repositoriesRequiredByRemNum ロードデータ（キャッシュ）
	 * @param employeeId 社員ID
	 * @param baseDate 基準日
	 * @return　労働契約時間
	 */
	static public Optional<LaborContractTime> getContractTime(
		RequireM3 require,
		String companyID,
		String employeeId,
		GeneralDate baseDate){

		// ドメインモデル「年休設定」を取得する
		AnnualPaidLeaveSetting annualPaidLeave = require.annualPaidLeaveSetting(companyID);
		if(annualPaidLeave == null){
			return Optional.empty();
		}

		// 1日の時間を取得
		TimeAnnualLeaveTimeDay timeAnnualLeaveTimeDay
			= annualPaidLeave.getTimeSetting().getTimeAnnualLeaveTimeDay();

		return timeAnnualLeaveTimeDay.getContractTime(require, employeeId, baseDate);
	}

	public static interface RequireM3 extends AbsenceTenProcess.RequireM1,TimeAnnualLeaveTimeDay.Require  {

		// 労働条件取得
		Optional<WorkingConditionItem> workingConditionItem(String employeeId, GeneralDate baseDate);
	}
	
	public LeaveUsedNumber toLeaveUsedNumber(Optional<LeaveUsedDayNumber> stowageDays,
			Optional<LeaveOverNumber> leaveOverLimitNumber) {
		if (this.isShortageRemain()) {
			return LeaveUsedNumber.of(new LeaveUsedDayNumber(0.0), Optional.empty(), stowageDays, leaveOverLimitNumber);
		}

		return LeaveUsedNumber.of(
				new LeaveUsedDayNumber(this.getDays().v()), this.getMinutes().isPresent()
						? Optional.of(new LeaveUsedTime(this.getMinutesOrZero().v())) : Optional.empty(),
				stowageDays, leaveOverLimitNumber);
	}

}
