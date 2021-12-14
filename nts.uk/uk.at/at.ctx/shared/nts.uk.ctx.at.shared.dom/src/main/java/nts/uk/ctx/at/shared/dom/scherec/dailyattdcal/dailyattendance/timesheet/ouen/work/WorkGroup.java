package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work;

import java.util.Optional;

import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainObject;

@Getter
/** 作業グループ */
public class WorkGroup implements DomainObject {

	/** 作業CD1: int */
	private WorkCode workCD1;
	
	/** 作業CD2: int */
	private Optional<WorkCode> workCD2;
	
	/** 作業CD3: int */
	private Optional<WorkCode> workCD3;
	
	/** 作業CD4: int */
	private Optional<WorkCode> workCD4;
	
	/** 作業CD5: int */
	private Optional<WorkCode> workCD5;

	private WorkGroup(WorkCode workCD1, Optional<WorkCode> workCD2, 
			Optional<WorkCode> workCD3, Optional<WorkCode> workCD4, Optional<WorkCode> workCD5) {
		super();
		this.workCD1 = workCD1;
		this.workCD2 = workCD2;
		this.workCD3 = workCD3;
		this.workCD4 = workCD4;
		this.workCD5 = workCD5;
	}
	
	public static WorkGroup create(WorkCode workCD1, Optional<WorkCode> workCD2, 
			Optional<WorkCode> workCD3, Optional<WorkCode> workCD4, Optional<WorkCode> workCD5){
		
		return new WorkGroup(workCD1, workCD2, workCD3, workCD4, workCD5);
	}
	
	public static WorkGroup create(String workCd1, String workCd2, String workCd3, String workCd4, String workCd5){
		
		return new WorkGroup(
				new WorkCode(workCd1), 
				Optional.ofNullable(workCode(workCd2)), 
				Optional.ofNullable(workCode(workCd3)),
				Optional.ofNullable(workCode(workCd4)),
				Optional.ofNullable(workCode(workCd5)));
	}

	private static WorkCode workCode(String workCd2) {
		return workCd2 == null ? null : new WorkCode(workCd2);
	}
}
