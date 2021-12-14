package nts.uk.shr.com.task.schedule.internal;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.task.schedule.JobScheduleOptions;
import nts.arc.task.schedule.JobScheduler;
import nts.arc.task.schedule.job.NtsJobKey;
import nts.arc.task.schedule.produce.ProducedJobScheduler;
import nts.arc.time.GeneralDateTime;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.task.schedule.ScheduleInfo;
import nts.uk.shr.com.task.schedule.UkJobScheduleOptions;
import nts.uk.shr.com.task.schedule.UkJobScheduler;

@Stateless
public class DefaultUkJobScheduler implements UkJobScheduler {

	@Inject
	@ProducedJobScheduler
	private JobScheduler scheduler;

	@Override
	public ScheduleInfo scheduleOnCurrentCompany(UkJobScheduleOptions options) {
		val scheduleInfo = ScheduleInfo.createNew();
		val jobKey = createJobKey(scheduleInfo.getScheduleId());
		
		val ntsOptions = new JobScheduleOptions(
				jobKey,
				options.getJobClass(),
				options.getUserData(),
				options.getSchedulingMethod(),
				options.getStartDateTime(),
				options.getEndDateTime());
		
		this.scheduler.schedule(ntsOptions);
		
		return scheduleInfo;
	}

	@Override
	public void unscheduleOnCurrentCompany(String scheduleId) {
		val jobKey = createJobKey(scheduleId);
		this.scheduler.unschedule(jobKey);
	}

	@Override
	public Optional<GeneralDateTime> getNextFireTime(String scheduleId) {
		val jobKey = createJobKey(scheduleId);
		return this.scheduler.getNextFireTime(jobKey);
	}
	
	private static NtsJobKey createJobKey(String scheduleId) {
		String tenantCode = AppContexts.user().contractCode();
		String companyId = AppContexts.user().companyId();
		return new NtsJobKey(tenantCode, companyId, scheduleId);
	}
	
}
