package nts.uk.ctx.at.auth.dom.employmentrole;

public enum EmployeeRefRange {
	/**
	 * 全員(all)
	 */
	ALL(0,"全社員"),
	/**
	 * 社員参照範囲と同じ( allEmployeeRefRange)
	 */
	ALL_EMPLOYEE_REF_RANGE(1,"社員参照範囲と同じ");
	
	public int value;
	
	public String nameId;
	
	EmployeeRefRange(int type,String nameId){
		this.value = type;
		this.nameId = nameId;
	}
}
