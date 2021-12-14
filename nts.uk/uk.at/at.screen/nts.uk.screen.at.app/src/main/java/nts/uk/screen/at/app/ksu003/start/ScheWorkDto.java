package nts.uk.screen.at.app.ksu003.start;

import java.util.Optional;

import lombok.Value;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.ScheManaStatuTempo;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;

@Value
public class ScheWorkDto {
	public String empId;
	
	public ScheManaStatuTempo manaStatuTempo;
	
	public WorkSchedule schedule;

	public ScheWorkDto(String empId, ScheManaStatuTempo manaStatuTempo, Optional<WorkSchedule> schedule) {
		super();
		this.empId = empId;
		this.manaStatuTempo = manaStatuTempo;
		this.schedule = schedule.isPresent() ? schedule.get() : null;
	} 
}
