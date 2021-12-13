package nts.uk.ctx.at.schedule.app.find.task.taskschedule.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkScheduleRepository;

/**
 * Get data cho man test ksu003c
 * @author quytb
 *
 */

@Stateless
public class Ksu003cTestFinder {
	
	@Inject
	WorkScheduleRepository repository;
	
	public List<String> getEmpWithWorkSchedule(List<String> empIds, GeneralDate ymd ) {
		List<String> listEmpId = new ArrayList<String>();
		empIds.stream().forEach(employeeId -> {
			Optional<WorkSchedule> opt = repository.get(employeeId, ymd);
			if(opt.isPresent()) {
				listEmpId.add(opt.get().getEmployeeID());
			}
		});
		return listEmpId;		
	}
}
