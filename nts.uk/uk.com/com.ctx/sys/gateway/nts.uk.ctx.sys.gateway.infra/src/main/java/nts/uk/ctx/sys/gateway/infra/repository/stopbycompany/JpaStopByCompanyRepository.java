package nts.uk.ctx.sys.gateway.infra.repository.stopbycompany;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.sys.gateway.dom.stopbycompany.StopByCompany;
import nts.uk.ctx.sys.gateway.dom.stopbycompany.StopByCompanyRepository;
import nts.uk.ctx.sys.gateway.infra.entity.stopbycompany.SgwdtStopByCompany;
import nts.uk.ctx.sys.gateway.infra.entity.stopbycompany.SgwdtStopByCompanyPK;

@Stateless
public class JpaStopByCompanyRepository extends JpaRepository implements StopByCompanyRepository {

	private static final String FIND_BY_KEY = "SELECT s FROM SgwdtStopByCompany s WHERE s.pk.contractCd=:contractCd AND s.pk.companyCd = :companyCd";

	private static final String FIND_BY_CONTRACTCD_AND_STATE = "SELECT s FROM SgwdtStopByCompany s WHERE s.pk.contractCd=:contractCd AND s.systemStatus=:systemStatus";

	private static final String GET_LST_BY_CONTRACTCD = "SELECT c FROM SgwdtStopByCompany c"
			+ " WHERE c.pk.contractCd = :contractCd";
	private static final String FIND_BY_CD_STATUS = "SELECT c FROM SgwdtStopByCompany c"
			+ " WHERE c.pk.contractCd = :contractCd"
			+ " AND c.systemStatus = :systemStatus";
	private static final String FIND_BY_CD_STT = "SELECT c FROM SgwdtStopByCompany c"
			+ " WHERE c.pk.contractCd = :contractCd"
			+ " AND c.pk.companyCd = :companyCd"
			+ " AND c.systemStatus = :systemStatus";
	@Override
	public void insert(StopByCompany domain) {
		this.commandProxy().insert(toEntity(domain));
	}

	private SgwdtStopByCompany toEntity(StopByCompany domain) {
		SgwdtStopByCompanyPK pk = new SgwdtStopByCompanyPK(domain.getContractCd(), domain.getCompanyCd());
		return new SgwdtStopByCompany(pk, domain.getSystemStatus().value, domain.getStopMessage().v(),
				domain.getStopMode().value, domain.getUsageStopMessage().v());
	}

	@Override
	public void update(StopByCompany domain) {
		this.commandProxy().update(toEntity(domain));
	}

	@Override
	public Optional<StopByCompany> findByKey(String contractCd, String companyCd) {
		return this.queryProxy().query(FIND_BY_KEY, SgwdtStopByCompany.class).setParameter("contractCd", contractCd)
				.setParameter("companyCd", companyCd).getSingle(x -> toDomain(x));
	}

	private StopByCompany toDomain(SgwdtStopByCompany entity) {
		return StopByCompany.createFromJavaType(entity.pk.contractCd, entity.pk.companyCd, entity.systemStatus,
				entity.stopMessage, entity.stopMode, entity.usageStopMessage);
	}

	@Override
	public List<StopByCompany> findByContractCodeAndState(String contractCd, int systemStatus) {
		return this.queryProxy().query(FIND_BY_CONTRACTCD_AND_STATE, SgwdtStopByCompany.class)
				.setParameter("contractCd", contractCd).setParameter("systemStatus", systemStatus)
				.getList(x -> toDomain(x));
	}
	/**
	 * @author hoatt
	 * get 会社単位の利用停止
	 * @param 契約コード - contractCd
	 * @return
	 */
	@Override
	public List<StopByCompany> getListComByContractCD(String contractCd) {
		return this.queryProxy().query(GET_LST_BY_CONTRACTCD, SgwdtStopByCompany.class)
				.setParameter("contractCd", contractCd)
				.getList(x -> toDomain(x));
	}
	@Override
	public List<StopByCompany> findByCdStatus(String contractCd, int systemStatus) {
		return this.queryProxy().query(FIND_BY_CD_STATUS, SgwdtStopByCompany.class)
				.setParameter("contractCd", contractCd)
				.setParameter("systemStatus", systemStatus)
				.getList(c -> toDomain(c));
	}

	/**
	  * ドメインモデル「会社単位の利用停止」を取得する
	  * @param 契約コード contractCd
	  * @param 会社コード companyCd
	  * @param システム利用状態 systemStatus
	  * @return
	  */
	@Override
	public Optional<StopByCompany> findByCdStt(String contractCd, String companyCd, int systemStatus) {
		return this.forTenantDatasource(contractCd, em ->{
			return this.queryProxy(em).query(FIND_BY_CD_STT, SgwdtStopByCompany.class)
					.setParameter("contractCd", contractCd)
					.setParameter("companyCd", companyCd)
					.setParameter("systemStatus", systemStatus)
					.getSingle(c -> toDomain(c));
		});
	}
}
