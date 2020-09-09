package nts.uk.ctx.at.request.dom.setting.request.gobackdirectlycommon;
//従業員が申請時に選択する条件
public enum ApplicationStatus {
	
	DO_NOT_REFLECT(0,"反映しない"),
	
	DO_REFLECT(1,"反映する"),
	
	DO_NOT_REFLECT_1(2,"申請時に決める(初期値：反映しない)"),
	
	DO_REFLECT_1(3,"申請時に決める(初期値：反映する)");
	
	public final int value;
	
	public final String nameId;
	
	private ApplicationStatus(int value, String nameId) {
		this.value = value;
		this.nameId = nameId;
	}
}
