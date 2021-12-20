package nts.uk.shr.com.system.config.internal;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.shr.com.system.config.SystemConfigurationRepository;
import nts.uk.shr.com.system.config.SystemConfigurationValue;

@Stateless
public class JpaSystemConfigurationRepository extends JpaRepository
		implements SystemConfigurationRepository {

	@Override
	public SystemConfigurationValue get(String key) {
		return this.forDefaultDataSource(em -> {
			
			String sql = "select * from CISCT_SYSTEM_CONFIG where CONFIG_NAME = @name";
			return this.jdbcProxy(em).query(sql)
					.paramString("name", key)
					.getSingle(rec -> new SystemConfigurationValue(rec.getString("CONFIG_VALUE")))
					.orElse(SystemConfigurationValue.none());
		});
	}
}
