package nts.uk.ctx.at.shared.dom.employeeworkway;

import lombok.RequiredArgsConstructor;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.社員の働き方.就業状態
 * 就業状態
 * @author HieuLt
 *
 */
@RequiredArgsConstructor
public enum WorkingStatus {
	// 0:在籍していない
	NOT_ENROLLED(0, "在籍していない"),

	// 1:データ不正
	INVALID_DATA(1, "データ不正"),
	
	// 2:予定管理しない	 
	DO_NOT_MANAGE_SCHEDULE (2, "予定管理しない"),
	
	// 3:休職中		 
	ON_LEAVE (3, "休職中"),
	
	// 4:休業中	 
	CLOSED (4, "休業中"),
	
	//5:予定管理する
	SCHEDULE_MANAGEMENT (5,"予定管理する");
	
	
	public final int value;

	public final String name;

	/* The Constant values. */
	private final static WorkingStatus[] values = WorkingStatus.values();

	public static WorkingStatus valueOf(Integer value) {
		// Invalid object.
		if (value == null) {
			return null;
		}

		// Find value.
		for (WorkingStatus val : WorkingStatus.values) {
			if (val.value == value) {
				return val;
			}
		}
		// Not found.
		return null;
	}
	/**
	 * [1] 勤務予定が必要か
	 * 勤務予定を作成する必要があるかどうかを判定する	
	 * @return Boolean needCreateWorkSchedule
	 */
	public boolean  needCreateWorkSchedule(){
		if(this.value == WorkingStatus.NOT_ENROLLED.value){
			return false;
		}
		if(this.value == WorkingStatus.INVALID_DATA.value){
			return false;
		}
		if(this.value == WorkingStatus.DO_NOT_MANAGE_SCHEDULE.value){
			return false;
		}
		if(this.value == WorkingStatus.ON_LEAVE.value){
			return true;
		}
		if(this.value == WorkingStatus.CLOSED.value){
			return true;
		}
		return true; //SCHEDULE_MANAGEMENT
	}
}
