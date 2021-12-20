package nts.uk.screen.at.app.ksu003.start;

import java.util.Optional;

import lombok.Value;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.shared.dom.employeeworkway.EmployeeWorkingStatus;

@Value
public class ScheWorkDto {
	public String empId;
	
	public EmployeeWorkingStatus manaStatuTempo;
	
	public WorkSchedule schedule;

	public ScheWorkDto(String empId, EmployeeWorkingStatus manaStatuTempo, Optional<WorkSchedule> schedule) {
		super();
		this.empId = empId;
		this.manaStatuTempo = manaStatuTempo;
		this.schedule = schedule.isPresent() ? schedule.get() : null;
	} 
}
