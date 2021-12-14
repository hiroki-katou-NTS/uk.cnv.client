package nts.uk.ctx.sys.gateway.infra.repository.stopbysystem;

import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.sys.gateway.dom.stopbysystem.StopBySystem;
import nts.uk.ctx.sys.gateway.dom.stopbysystem.StopBySystemRepository;
import nts.uk.ctx.sys.gateway.infra.entity.stopbysystem.SgwdtStopBySystem;

@Stateless
public class JpaStopBySystemRepository extends JpaRepository implements StopBySystemRepository {

	private static final String FIND_BY_KEY = "SELECT s FROM SgwdtStopBySystem s WHERE s.contractCd=:contractCd";
	private static final String FIND_BY_CD_STATUS = "SELECT c FROM SgwdtStopBySystem c"
			+ " WHERE c.contractCd = :contractCd"
			+ " AND c.systemStatus = :systemStatus";
	@Override
	public void insert(StopBySystem domain) {
		this.commandProxy().insert(toEntity(domain));

	}

	@Override
	public void update(StopBySystem domain) {
		this.commandProxy().update(toEntity(domain));

	}

	private SgwdtStopBySystem toEntity(StopBySystem domain) {
		return new SgwdtStopBySystem(domain.getContractCd(), domain.getSystemStatus().value,
				domain.getStopMessage().v(), domain.getStopMode().value, domain.getUsageStopMessage().v());
	}

	@Override
	public Optional<StopBySystem> findByKey(String contractCd) {
		return this.queryProxy().query(FIND_BY_KEY, SgwdtStopBySystem.class).setParameter("contractCd", contractCd)
				.getSingle(x -> toDomain(x));
	}

	private StopBySystem toDomain(SgwdtStopBySystem entity) {
		return StopBySystem.createFromJavaType(entity.contractCd, entity.systemStatus, entity.stopMessage,
				entity.stopMode, entity.usageStopMessage);
	}

	@Override
	public Optional<StopBySystem> findByCdStatus(String contractCd, int systemStatus) {
		
		return this.forTenantDatasource(contractCd, em ->{
			return this.queryProxy(em).query(FIND_BY_CD_STATUS, SgwdtStopBySystem.class)
					.setParameter("contractCd", contractCd)
					.setParameter("systemStatus", systemStatus)
					.getSingle(c -> toDomain(c));	
		});
	}
}
