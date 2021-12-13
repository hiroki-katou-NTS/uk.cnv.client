package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.vtotalmethod;

import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).月の勤怠計算.月別集計処理.縦計方法.出勤日数としてカウントする休暇の種類
 */
public enum LeaveCountedAsWorkDaysType {

	// 年休
	ANNUAL_LEAVE(1),

	// 積立年休
	ACCUMULATED_ANNUAL_LEAVE(2),

	// 特別休暇
	SPECIAL_VACATION(3);

	public int value;

	private LeaveCountedAsWorkDaysType(int value) {
		this.value = value;
	}
	
	/**
	 * [1] 勤務種類の分類を作成する
	 * 
	 * @return 勤務種類の分類
	 */
	public WorkTypeClassification toWorkTypeClassification() {
		switch (this) {
		case ANNUAL_LEAVE:
			return WorkTypeClassification.AnnualHoliday;
		case ACCUMULATED_ANNUAL_LEAVE:
			return WorkTypeClassification.YearlyReserved;
		case SPECIAL_VACATION:
			return WorkTypeClassification.SpecialHoliday;
		default:
			return null;
		}
	}
}
