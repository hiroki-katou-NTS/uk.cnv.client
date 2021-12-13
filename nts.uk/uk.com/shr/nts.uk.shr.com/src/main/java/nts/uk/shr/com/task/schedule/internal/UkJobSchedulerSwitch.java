package nts.uk.shr.com.task.schedule.internal;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import nts.arc.task.schedule.JobScheduler;
import nts.arc.task.schedule.internal.DefaultJobScheduler;
import nts.arc.task.schedule.produce.JobSchedulerSwitch;
import nts.jobdistributor.client.schedule.DistributorJobScheduler;
import nts.uk.shr.com.system.property.UKServerSystemProperties;

@ApplicationScoped
@Slf4j
public class UkJobSchedulerSwitch implements JobSchedulerSwitch {

	@Inject
	private DefaultJobScheduler defaultJobScheduler;
	
	@Inject
	private DistributorJobScheduler distributorJobScheduler;
	
	@Override
	public JobScheduler get() {
		if (UKServerSystemProperties.usesJobDistributor()) {
			log.info("SchedulerSwitch: JobDistributor");
			return distributorJobScheduler;
		}

		log.info("SchedulerSwitch: Default");
		return defaultJobScheduler;
	}

}
