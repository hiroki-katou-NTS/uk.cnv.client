package nts.uk.shr.infra.data;

import lombok.val;
import nts.arc.scoped.session.SessionContextProvider;
import nts.tenantlocator.client.TenantLocatorClient;

public class TenantLocatorService {
	
	private static final String SESSION_DATASOURCE = "nts.uk.shr.infra.data,TenantLocatorService,datasource";
	
	public static void connect(String tenantCode) {
		val datasource = TenantLocatorClient.getDataSource(tenantCode);
		if(datasource.isPresent()) {
			SessionContextProvider.get().put(SESSION_DATASOURCE, datasource.get().getDatasourceName());
		}
		else {
			SessionContextProvider.get().put(SESSION_DATASOURCE, "");
		}
	}
	
	public static void disconnect() {
		SessionContextProvider.get().put(SESSION_DATASOURCE, "");
	}
	
	public static String getConnectedDataSource() {
		return SessionContextProvider.get().get(SESSION_DATASOURCE);
	}
	
	public static String getDataSourceFor(String tenantCode) {
		return TenantLocatorClient.getDataSource(tenantCode).get().getDatasourceName();
	}
	
	public static boolean isConnected() {
		val dataSourse = SessionContextProvider.get().get(SESSION_DATASOURCE);
		if(dataSourse == null) {
			return false;
		}
		return !dataSourse.toString().equals("");
	}
}
