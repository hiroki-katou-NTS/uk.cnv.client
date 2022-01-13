package nts.uk.shr.infra.data;

import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.scoped.session.SessionContextProvider;
import nts.tenantlocator.client.TenantLocatorClient;

public class TenantLocatorService {
	
	private static final String SESSION_DATASOURCE = "nts.uk.shr.infra.data,TenantLocatorService,datasource";
	
	public static void connect(String tenantCode) {

		val datasourceOpt = TenantLocatorClient.getDataSource(tenantCode);
		if (!datasourceOpt.isPresent()) {
			disconnect();
			throw new BusinessException("Msg_314");
		}

		connectDataSource(datasourceOpt.get().getDatasourceName());
	}

	public static void connectDataSource(String dataSourceName) {

		if (dataSourceName == null) {
			throw new NullPointerException("dataSourceName");
		}
		if (dataSourceName.isEmpty()) {
			throw new RuntimeException("dataSourceName is empty");
		}

		SessionContextProvider.get().put(SESSION_DATASOURCE, dataSourceName);
	}
	
	public static void disconnect() {
		SessionContextProvider.get().put(SESSION_DATASOURCE, "");
	}
	
	public static String getConnectedDataSource() {
		return SessionContextProvider.get().get(SESSION_DATASOURCE);
	}
	
	public static String getDataSourceFor(String tenantCode) {
		return TenantLocatorClient.getDataSource(tenantCode)
				.orElseThrow(() -> new RuntimeException("テナントのデータソースが見つかりません：" + tenantCode))
				.getDatasourceName();
	}
	
	public static boolean isConnected() {
		val dataSourse = SessionContextProvider.get().get(SESSION_DATASOURCE);
		if(dataSourse == null) {
			return false;
		}
		return !dataSourse.toString().equals("");
	}
}
