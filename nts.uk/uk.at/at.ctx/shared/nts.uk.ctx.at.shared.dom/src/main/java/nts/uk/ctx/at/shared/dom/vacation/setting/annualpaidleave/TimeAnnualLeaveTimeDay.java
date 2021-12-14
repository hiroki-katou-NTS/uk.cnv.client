package nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.AbsenceTenProcess;
import nts.uk.ctx.at.shared.dom.workingcondition.LaborContractTime;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
/**
 * @author Hieult
 */
/** 時間年休一日の時間 **/
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TimeAnnualLeaveTimeDay {
	
	/* 一日の時間年休時間参照場所 */
	private DayTimeAnnualLeave timeOfDayReference;
	
	/* 全社一律の時間 : 労働契約時間 */
	private Optional<LaborContractTime>  uniformTime;
	
	/* 契約時間丸め */
	private Optional<ContractTimeRound> contractTimeRound;
	
	public Optional<LaborContractTime> getContractTime(Require require, String employeeId, GeneralDate baseDate){
		// 会社一律で設定されているとき
		if ( this.getTimeOfDayReference().equals(DayTimeAnnualLeave.Company_wide_Uniform)){

			// １日の時間をセット
			return this.getUniformTime();

		} else { // 社員の契約時間により算定

			// アルゴリズム「社員の労働条件を取得する」を実行し、契約時間を取得する
			Optional<WorkingConditionItem> workCond
				= require.workingConditionItem(employeeId, baseDate);

			if (!workCond.isPresent())
				return Optional.empty();
		
			// 丸め処理
			// 取得した契約時間を1時間単位で切り上げる
			if ( this.getContractTimeRound().get().equals(ContractTimeRound.Round_up_to_1_hour) ){
				return Optional.of(workCond.get().getContractTime().roundUp1Hour());
			}
			else{
				return Optional.of(workCond.get().getContractTime());
			}
		}
	}
	
	public static interface Require extends AbsenceTenProcess.RequireM1 {

		// 労働条件取得
		Optional<WorkingConditionItem> workingConditionItem(String employeeId, GeneralDate baseDate);
	}
}
