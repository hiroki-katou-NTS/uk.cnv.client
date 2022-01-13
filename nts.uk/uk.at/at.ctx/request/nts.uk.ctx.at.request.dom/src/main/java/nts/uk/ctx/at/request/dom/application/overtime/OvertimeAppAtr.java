package nts.uk.ctx.at.request.dom.application.overtime;

import lombok.AllArgsConstructor;

/**
 * refactor 4
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.申請承認.申請.残業休出申請.残業申請.残業申請区分
 * @author Doan Duy Hung
 *
 */
@AllArgsConstructor
public enum OvertimeAppAtr {
	
	/**
	 * 早出残業
	 */
	EARLY_OVERTIME(0, "Enum_APP_OVERTIME_EARLY"),
	/**
	 * 通常残業
	 */
	NORMAL_OVERTIME(1, "Enum_APP_OVERTIME_NORMAL"),
	/**
	 * 早出残業・通常残業
	 */
	EARLY_NORMAL_OVERTIME(2, "Enum_APP_OVERTIME_EARLY_NORMAL"),
	/**
	 * 複数回残業
	 */
	MULTIPLE_OVERTIME(3, "Enum_APP_OVERTIME_MULTIPLE");

	public final int value;
	
	public final String name;
}
