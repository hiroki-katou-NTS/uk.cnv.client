package nts.uk.ctx.at.schedule.dom.schedule.createworkschedule.createschedulecommon.correctworkschedule;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;

/**
 * 勤務予定を補正する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務予定.勤務予定処理.作成処理.アルゴリズム.勤務予定処理.勤務予定作成する.勤務予定作成共通処理.勤務予定を補正する
 * @author tutk
 *
 */
public class CorrectWorkSchedule {
	
	public WorkSchedule createWorkSchedule(WorkSchedule workSchedule,String employeeId,GeneralDate targetDate) {
		//勤務予定から日別勤怠（Work）に変換する
		//TODO : Chưa remove từ record sang shared nên chưa sử dụng được (tạo 1 biến class IntegrationOfDaily , biến nào k có thì để empty)
		//勤怠ルールの補正処理 
		//TODO Thuật toán này hiện chưa ai làm + cũng chưa phải làm. nên tạm thời bỏ qua
		//勤務予定情報を計算する -CalculateDailyRecordServiceCenter
		//TODO: đang để record -> có thể sẽ chueyern về shared, 
		return workSchedule; //Return tạm thời
		
	}

}
