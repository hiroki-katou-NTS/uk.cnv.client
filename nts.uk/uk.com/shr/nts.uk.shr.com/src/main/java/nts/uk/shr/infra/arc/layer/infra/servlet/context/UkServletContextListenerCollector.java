package nts.uk.shr.infra.arc.layer.infra.servlet.context;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.ServletContextListener;

import nts.arc.layer.infra.servlet.context.listener.ServletContextListenerCollector;
import nts.arc.system.ServerSystemProperties;
import nts.arc.task.schedule.internal.config.CdiJobFactoryInitializer;
import nts.arc.task.schedule.internal.config.CustomQuartzInitializerListener;
import nts.jobdistributor.common.JobDistributorServerSystemProperties;
import nts.uk.shr.com.system.property.UKServerSystemProperties;

@ApplicationScoped
public class UkServletContextListenerCollector implements ServletContextListenerCollector {
	
	private static final List<ServletContextListener> LISTENERS = Arrays.asList(
			new CustomQuartzInitializerListener(),
			new CdiJobFactoryInitializer()
			);
	
	@Override
	public List<ServletContextListener> collect() {

		if (UKServerSystemProperties.usesJobDistributor()
			|| JobDistributorServerSystemProperties.isWorker()) {
			// JobDistributorを使う場合、UK上でQuartzSchedulerが動き出してはまずい
			return Collections.emptyList();
		}
		
		return LISTENERS;
	}

}
