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

@ApplicationScoped
public class UkServletContextListenerCollector implements ServletContextListenerCollector {
	
	private static final List<ServletContextListener> LISTENERS = Arrays.asList(
			new CustomQuartzInitializerListener(),
			new CdiJobFactoryInitializer()
			);
	
	@Override
	public List<ServletContextListener> collect() {
		
		if ("cloud".equals(ServerSystemProperties.get("nts.installtype"))) {
			// クラウドの場合、QuartzSchedulerが動くのはJobDistributorのみ
			return Collections.emptyList();
		}
		
		return LISTENERS;
	}

}
