package nts.uk.ctx.at.aggregation.dom.scheduletable;

/**
 * スケジュール表の個人情報項目
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.就業機能.スケジュール表.スケジュール表の個人情報項目
 * @author dan_pv
 *
 */
public enum ScheduleTablePersonalInfoItem {

	/** 社員名 */
	EMPLOYEE_NAME(0, "社員名"),

	/** 雇用 */
	EMPLOYMENT(1,"雇用"),
	
	/** 職位 */
	JOBTITLE(2, "職位"),
	
	/** 分類 */
	CLASSIFICATION(3, "分類" ),
	
	/** チーム */
	TEAM(4,"チーム"),
	
	/** ランク */
	RANK(5,"ランク"),
	
	/** 看護区分 */
	NURSE_CLASSIFICATION(6,"看護区分" ),
	
	/** 資格 */
	QUALIFICATION(7,"資格");
	
	public int value;
	public String nameId;
	
	private ScheduleTablePersonalInfoItem (int value, String nameId) {
		this.value = value;
		this.nameId = nameId;
	}
	
}
