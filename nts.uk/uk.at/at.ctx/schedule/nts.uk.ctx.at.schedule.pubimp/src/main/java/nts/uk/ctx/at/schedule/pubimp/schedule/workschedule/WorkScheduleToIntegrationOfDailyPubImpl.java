package nts.uk.ctx.at.schedule.pubimp.schedule.workschedule;

import java.util.ArrayList;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkScheduleRepository;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.WorkScheduleToIntegrationOfDailyPub;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

@Stateless
public class WorkScheduleToIntegrationOfDailyPubImpl implements WorkScheduleToIntegrationOfDailyPub{

	@Inject
	private WorkScheduleRepository repo;
	
	@Override
	public Optional<Object> getWorkSchedule(String sid, GeneralDate date) {
		Optional<WorkSchedule> workScheduleOpt = repo.get(sid, date);
		return workScheduleOpt.map(workSchedule -> {
			return new IntegrationOfDaily(workSchedule.getEmployeeID(), workSchedule.getYmd(),
					workSchedule.getWorkInfo(), CalAttrOfDailyAttd.createAllCalculate(), workSchedule.getAffInfo(), Optional.empty(), new ArrayList<>(),
					workSchedule.getOutingTime(), workSchedule.getLstBreakTime(), workSchedule.getOptAttendanceTime(),
					workSchedule.getOptTimeLeaving(), workSchedule.getOptSortTimeWork(), Optional.empty(), Optional.empty(),
					Optional.empty(), workSchedule.getLstEditState(), Optional.empty(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), Optional.empty());
		});
		
	}
	

}
