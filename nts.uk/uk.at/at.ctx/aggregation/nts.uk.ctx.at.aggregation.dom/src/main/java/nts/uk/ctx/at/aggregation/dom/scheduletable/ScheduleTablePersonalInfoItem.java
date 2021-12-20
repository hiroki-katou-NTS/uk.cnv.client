package nts.uk.ctx.at.aggregation.dom.scheduletable;

/**
 * スケジュール表の個人情報項目
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.スケジュール表.スケジュール表の個人情報項目
 * @author dan_pv
 *
 */
public enum ScheduleTablePersonalInfoItem {

	/** 社員名 */
	EMPLOYEE_NAME(0, "Enum_ScheTable_EmployeeName"),

	/** 雇用 */
	EMPLOYMENT(1,"Enum_ScheTable_Employment"),
	
	/** 職位 */
	JOBTITLE(2, "Enum_ScheTable_JobTitle"),
	
	/** 分類 */
	CLASSIFICATION(3, "Enum_ScheTable_Classification" ),
	
	/** チーム */
	TEAM(4,"Enum_ScheTable_Team"),
	
	/** ランク */
	RANK(5,"Enum_ScheTable_Rank"),
	
	/** 看護区分 */
	NURSE_CLASSIFICATION(6,"Enum_ScheTable_Nurse_Class" );
	
	/** 資格 */
	// QUALIFICATION(7,"Enum_ScheTable_Qualification")
	
	public int value;
	public String nameId;
	
	private ScheduleTablePersonalInfoItem (int value, String nameId) {
		this.value = value;
		this.nameId = nameId;
	}
	
}