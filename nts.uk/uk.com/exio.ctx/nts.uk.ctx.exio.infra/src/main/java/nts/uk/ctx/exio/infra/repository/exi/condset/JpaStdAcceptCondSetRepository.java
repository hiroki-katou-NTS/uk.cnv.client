package nts.uk.ctx.exio.infra.repository.exi.condset;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.exio.dom.exi.condset.AcceptMode;
import nts.uk.ctx.exio.dom.exi.condset.AcceptanceConditionCode;
import nts.uk.ctx.exio.dom.exi.condset.AcceptanceConditionName;
import nts.uk.ctx.exio.dom.exi.condset.AcceptanceLineNumber;
import nts.uk.ctx.exio.dom.exi.condset.DeleteExistDataMethod;
import nts.uk.ctx.exio.dom.exi.condset.StdAcceptCondSet;
import nts.uk.ctx.exio.dom.exi.condset.StdAcceptCondSetRepository;
import nts.uk.ctx.exio.dom.exi.condset.SystemType;
import nts.uk.ctx.exio.dom.exi.csvimport.ExiCharset;
import nts.uk.ctx.exio.infra.entity.exi.condset.OiomtExAcCond;
import nts.uk.ctx.exio.infra.entity.exi.condset.OiomtStdAcceptCondSetPk;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

import javax.ejb.Stateless;

import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.Optional;

/**
 * The class Jpa standard acceptance condition setting repository.<br>
 * Repository 受入条件設定（定型）
 */
@Stateless
public class JpaStdAcceptCondSetRepository extends JpaRepository implements StdAcceptCondSetRepository {

	/** The query select all by company id */
	private static final String SELECT_ALL_BY_COMPANY_ID = "SELECT c FROM OiomtExAcCond c "
			+ "WHERE c.stdAcceptCondSetPk.cid = :companyId "
			+ "ORDER BY c.stdAcceptCondSetPk.conditionSetCd";

	/** The query select all */
	private static final String SELECT_ALL = "SELECT c FROM OiomtExAcCond c "
			+ "WHERE c.stdAcceptCondSetPk.cid = :companyId "
			+ "ORDER BY c.stdAcceptCondSetPk.conditionSetCd";
	
	private static final String GET_BY_CODE = "SELECT c FROM OiomtExAcCond c "
			+ " WHERE c.stdAcceptCondSetPk.cid = :companyId "
			+ " AND c.stdAcceptCondSetPk.conditionSetCd = :conditionSetCd"
			+ " ORDER BY c.stdAcceptCondSetPk.conditionSetCd";
	
	private static final String SELECT_ALL_BY_SYS = "SELECT c FROM OiomtExAcCond c "
			+ "WHERE c.stdAcceptCondSetPk.cid = :companyId "
			+ " AND c.systemType = :systemType"
			+ " ORDER BY c.stdAcceptCondSetPk.conditionSetCd";
	
	private static final String SELECT_ALL_BY_LSSYS = "SELECT c FROM OiomtExAcCond c "
			+ "WHERE c.stdAcceptCondSetPk.cid = :companyId "
			+ " AND c.systemType IN :lsSystemType"
			+ " ORDER BY c.stdAcceptCondSetPk.conditionSetCd";
	
	/**
	 * Finds all standard acceptance condition settings by company id.
	 *
	 * @param companyId the company id
	 * @return the <code>StdAcceptCondSet</code> domain list
	 */
	@Override
	public List<StdAcceptCondSet> findAllStdAcceptCondSetsByCompanyId(String companyId) {
		return this.queryProxy().query(SELECT_ALL_BY_COMPANY_ID, OiomtExAcCond.class)
								.setParameter("companyId", companyId)
								.getList(entity -> toDomain(entity));
	}

	private StdAcceptCondSet toDomain(OiomtExAcCond entity) {
		StdAcceptCondSet domain = new StdAcceptCondSet(entity.getStdAcceptCondSetPk().getCid(),
				new AcceptanceConditionCode(entity.getStdAcceptCondSetPk().getConditionSetCd()),
				new AcceptanceConditionName(entity.getConditionSetName()),
				Optional.ofNullable(entity.getSystemType() == null ? null : EnumAdaptor.valueOf(entity.getSystemType(), SystemType.class)),
				Optional.ofNullable(entity.getCategoryId()),
				EnumAdaptor.valueOf(entity.getDeleteExistData(), NotUseAtr.class),
				Optional.ofNullable(entity.getCsvDataLineNumber() == null ? null : new AcceptanceLineNumber(entity.getCsvDataLineNumber())),
				Optional.ofNullable(entity.getCsvDataStartLine() == null ? null : new AcceptanceLineNumber(entity.getCsvDataStartLine())),
				Optional.ofNullable(entity.getCharacterCode() == null ? null :  EnumAdaptor.valueOf(entity.getCharacterCode(), ExiCharset.class)),
				EnumAdaptor.valueOf(entity.getCheckCompleted(), NotUseAtr.class),
				Optional.ofNullable(entity.getDeleteExtDataMethod() == null ? null : EnumAdaptor.valueOf(entity.getDeleteExtDataMethod() ? 1 : 0, DeleteExistDataMethod.class)),
				Optional.ofNullable(entity.getAcceptMode() == null ? null : EnumAdaptor.valueOf(entity.getAcceptMode(), AcceptMode.class)));
		return domain;
	}

	/**
	 * Gets standard acceptance condition settings by system type.
	 *
	 * @param cid     the company id
	 * @param sysType the system type
	 * @return the <code>StdAcceptCondSet</code> domain list
	 */
	@Override
	public List<StdAcceptCondSet> getAllStdAcceptCondSet(String cid) {
		return this.queryProxy().query(SELECT_ALL, OiomtExAcCond.class)
								.setParameter("companyId", cid)
								.getList(entity -> toDomain(entity));
	}

	/**
	 * Gets standard acceptance condition setting by id.
	 *
	 * @param cid            the company id
	 * @param sysType        the system type
	 * @param conditionSetCd the condition set code
	 * @return the optional of domain standard acceptance condition setting
	 */
	@Override
	public Optional<StdAcceptCondSet> getById(String cid, String conditionSetCd) {
		return this.queryProxy().query(GET_BY_CODE, OiomtExAcCond.class)
				.setParameter("companyId", cid)
				.setParameter("conditionSetCd", conditionSetCd)
				.getSingle(entity -> toDomain(entity));

	}

	/**
	 * Add.
	 *
	 * @param domain the domain
	 */
	@Override
	public void add(StdAcceptCondSet domain) {
		this.commandProxy().insert(toEntity(domain));
	}

	private OiomtExAcCond toEntity(StdAcceptCondSet domain) {
		OiomtStdAcceptCondSetPk pk = new OiomtStdAcceptCondSetPk(domain.getCompanyId(), domain.getConditionSetCode().v());
		String contractCd = AppContexts.user().contractCode();
		OiomtExAcCond entity = new OiomtExAcCond(pk,
				contractCd,
				domain.getSystemType().isPresent() ? domain.getSystemType().get().value : null,
				domain.getCategoryId().isPresent() ? domain.getCategoryId().get() : null,
				domain.getCsvDataItemLineNumber().isPresent() ? domain.getCsvDataItemLineNumber().get().v() : null,
				domain.getDeleteExistData().value,
				domain.getCsvDataStartLine().isPresent() ? domain.getCsvDataStartLine().get().v() : null,
				domain.getCharacterCode().isPresent() ? domain.getCharacterCode().get().value : null,
				domain.getAcceptMode().isPresent() ? domain.getAcceptMode().get().value : null,
				domain.getConditionSetName().v(), domain.getCheckCompleted().value,
				domain.getDeleteExistDataMethod().isPresent() ? domain.getDeleteExistDataMethod().get().value == 1 : null);
		return entity;
	}

	/**
	 * Update.
	 *
	 * @param domain the domain
	 */
	@Override
	public void update(StdAcceptCondSet domain) {
		OiomtExAcCond newStdAcceptCondSet = toEntity(domain);
		Optional<OiomtExAcCond> updateStdAcceptCondSet = this.queryProxy().find(newStdAcceptCondSet.getStdAcceptCondSetPk(),
																						OiomtExAcCond.class);
		if (updateStdAcceptCondSet.isPresent()) {
			this.commandProxy().update(newStdAcceptCondSet);
		}
	}

	/**
	 * Remove.
	 *
	 * @param cid            the company id
	 * @param sysType        the system type
	 * @param conditionSetCd the condition set code
	 */
	@Override
	public void remove(String cid, int sysType, String conditionSetCd) {
		this.commandProxy().remove(OiomtExAcCond.class,
								   new OiomtStdAcceptCondSetPk(cid, conditionSetCd));
	}

	/**
	 * Is setting code exist boolean.
	 *
	 * @param cid            the company id
	 * @param sysType        the system type
	 * @param conditionSetCd the condition set code
	 * @return the boolean
	 */
	@Override
	public boolean isSettingCodeExist(String cid, String conditionSetCd) {
		return this.queryProxy().find(new OiomtStdAcceptCondSetPk(cid, conditionSetCd), OiomtExAcCond.class)
								.isPresent();
	}

	/**
	 * Update from d.
	 *
	 * @param domain the domain
	 */
	@Override
	public void updateFromD(StdAcceptCondSet domain) {
		OiomtExAcCond newStdAcceptCondSet = toEntity(domain);
		Optional<OiomtExAcCond> updateStdAcceptCondSet = this.queryProxy().find(newStdAcceptCondSet.getStdAcceptCondSetPk(),
																						OiomtExAcCond.class);
		if (updateStdAcceptCondSet.isPresent()) {			
			this.commandProxy().update(newStdAcceptCondSet);
		}
	}

	@Override
	public List<StdAcceptCondSet> getStdAcceptCondSetBySysType(String cid, int sysType) {
		return this.queryProxy().query(SELECT_ALL_BY_SYS, OiomtExAcCond.class)
				.setParameter("companyId", cid)
				.setParameter("systemType", sysType)
				.getList(entity -> toDomain(entity));
	}

	@Override
	public List<StdAcceptCondSet> getStdAcceptCondSetByListSys(String cid, List<Integer> sysType) {
		return this.queryProxy().query(SELECT_ALL_BY_LSSYS, OiomtExAcCond.class)
				.setParameter("companyId", cid)
				.setParameter("lsSystemType", sysType)
				.getList(entity -> toDomain(entity));
	}

	@Override
	public void updateSystem(String cid, String conditionSetCd, int system) {
		Optional<OiomtExAcCond> updateStdAcceptCondSetSys = this.queryProxy().find(new OiomtStdAcceptCondSetPk(cid, conditionSetCd),
				OiomtExAcCond.class);
		
		if(updateStdAcceptCondSetSys.isPresent()) {
			updateStdAcceptCondSetSys.get().setSystemType(system);
			this.commandProxy().update(updateStdAcceptCondSetSys.get());
		}
	}

}
