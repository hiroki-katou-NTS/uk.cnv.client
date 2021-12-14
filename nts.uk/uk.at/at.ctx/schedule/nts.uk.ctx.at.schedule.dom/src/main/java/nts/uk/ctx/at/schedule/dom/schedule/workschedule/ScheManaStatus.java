package nts.uk.ctx.at.schedule.dom.schedule.workschedule;

import lombok.RequiredArgsConstructor;

/**
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務予定.勤務予定.勤務予定
 * 予定管理状態 Enum
 * @author HieuLt
 *
 */
@RequiredArgsConstructor
public enum ScheManaStatus {
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
	private final static ScheManaStatus[] values = ScheManaStatus.values();

	public static ScheManaStatus valueOf(Integer value) {
		// Invalid object.
		if (value == null) {
			return null;
		}

		// Find value.
		for (ScheManaStatus val : ScheManaStatus.values) {
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
		if(this.value == ScheManaStatus.NOT_ENROLLED.value){
			return false;
		}
		if(this.value == ScheManaStatus.INVALID_DATA.value){
			return false;
		}
		if(this.value == ScheManaStatus.DO_NOT_MANAGE_SCHEDULE.value){
			return false;
		}
		if(this.value == ScheManaStatus.ON_LEAVE.value){
			return true;
		}
		if(this.value == ScheManaStatus.CLOSED.value){
			return true;
		}
		return true; //SCHEDULE_MANAGEMENT
	}
}
