package nts.uk.ctx.sys.auth.infra.repository.grant.roleindividualgrant;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrant;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrantRepository;
import nts.uk.ctx.sys.auth.dom.role.RoleType;
import nts.uk.ctx.sys.auth.infra.entity.grant.roleindividualgrant.SacmtRoleIndiviGrant;
import nts.uk.ctx.sys.auth.infra.entity.grant.roleindividualgrant.SacmtRoleIndiviGrantPK;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class JpaRoleIndividualGrantRepository extends JpaRepository implements RoleIndividualGrantRepository {

	private static final String SELECT_BY_DATE = "SELECT c FROM SacmtRoleIndiviGrant c WHERE c.sacmtRoleIndiviGrantPK.userID = :userID"
			+ " AND c.strD <= :date AND c.endD >= :date";
	
	private static final String SELECT_BY_DATE_AND_COMPANY_MANAGE = "SELECT c FROM SacmtRoleIndiviGrant c WHERE c.sacmtRoleIndiviGrantPK.userID = :userID"
			+ " AND c.strD <= :date AND c.endD >= :date" + " AND c.sacmtRoleIndiviGrantPK.roleType = :roleType";
	
	private static final String FIND_BY_DETAIL = "SELECT c FROM SacmtRoleIndiviGrant c"
			+ " WHERE c.sacmtRoleIndiviGrantPK.companyID = :companyId"
			+ " AND c.sacmtRoleIndiviGrantPK.roleType = :roleType"
			+ " AND c.sacmtRoleIndiviGrantPK.userID = :userId"
			+ " AND c.roleId IN :roleIDLst"
			+ " AND c.strD <= :date AND c.endD >= :date";
	//hoatt
	private static final String SELECT_BY_DATE_ROLE_CID_TYPE = "SELECT c FROM SacmtRoleIndiviGrant c"
			+ " WHERE c.sacmtRoleIndiviGrantPK.companyID = :companyId"
			+ " AND c.sacmtRoleIndiviGrantPK.userID = :userId"
			+ " AND c.strD <= :date AND c.endD >= :date" 
			+ " AND c.sacmtRoleIndiviGrantPK.roleType != :roleType";
	@Override
	public Optional<RoleIndividualGrant> findByUserAndDate(String userId, GeneralDate date) {
		return this.queryProxy().query(SELECT_BY_DATE, SacmtRoleIndiviGrant.class).setParameter("userID", userId)
				.setParameter("date", date).getSingle(c -> c.toDomain());
	}
	
	@Override
	public List<RoleIndividualGrant> findListByUserAndDate(String userId, GeneralDate date) {
		return this.queryProxy().query(SELECT_BY_DATE, SacmtRoleIndiviGrant.class).setParameter("userID", userId)
				.setParameter("date", date).getList(c -> c.toDomain());
	}
	
	@Override
	public List<RoleIndividualGrant> findListByUserAndDateForCompanyAdmin(String userId, GeneralDate date,RoleType roleType) {
		return this.queryProxy().query(SELECT_BY_DATE_AND_COMPANY_MANAGE, SacmtRoleIndiviGrant.class).setParameter("userID", userId)
				.setParameter("date", date).setParameter("roleType", roleType.value).getList(c -> c.toDomain());
	}
	
	private static final String SELECT_BY_ROLE_USER = "SELECT c FROM SacmtRoleIndiviGrant c WHERE c.sacmtRoleIndiviGrantPK.companyID = :cid"
			+ " AND c.sacmtRoleIndiviGrantPK.roleType = :roleType AND c.sacmtRoleIndiviGrantPK.userID = :userID";

	@Override
	public Optional<RoleIndividualGrant> findByUserCompanyRoleType(String userID, String companyID, int roleType) {
		return this.queryProxy().query(SELECT_BY_ROLE_USER, SacmtRoleIndiviGrant.class).setParameter("cid", companyID)
				.setParameter("roleType", roleType).setParameter("userID", userID).getSingle(c -> c.toDomain());
	}
	
	private static final String SELECT_BY_COMPANY_USER_ROLE_DATE = "SELECT c FROM SacmtRoleIndiviGrant c WHERE"
			+ " (c.sacmtRoleIndiviGrantPK.companyID = :companyId"
			+ " OR c.sacmtRoleIndiviGrantPK.companyID = '000000000000-0000')"
			+ " AND c.sacmtRoleIndiviGrantPK.roleType = :roleType AND c.sacmtRoleIndiviGrantPK.userID = :userId"
			+ " AND c.strD <= :date AND c.endD >= :date";
	
	@Override
	public Optional<RoleIndividualGrant> findByUserCompanyRoleTypeDate(String userId, String companyId, int roleType, GeneralDate date) {
		return this.queryProxy().query(SELECT_BY_COMPANY_USER_ROLE_DATE, SacmtRoleIndiviGrant.class)
				.setParameter("companyId", companyId).setParameter("userId", userId)
				.setParameter("roleType", roleType).setParameter("date", date).getSingle(c -> c.toDomain());
	}

	private static final String SELECT_BY_KEY = "SELECT c FROM SacmtRoleIndiviGrant c WHERE c.sacmtRoleIndiviGrantPK.userID = :userID"
			+ " AND c.sacmtRoleIndiviGrantPK.companyID = :companyID AND c.roleId = :roleId";

	@Override
	public Optional<RoleIndividualGrant> findByKey(String userId, String companyId, String roleId) {
		return this.queryProxy().query(SELECT_BY_KEY, SacmtRoleIndiviGrant.class).setParameter("userID", userId)
				.setParameter("companyID", companyId).setParameter("roleId", roleId).getSingle(c -> c.toDomain());
	}

	private static final String SELECT_BY_USER_AND_ROLETYPE = "SELECT c FROM SacmtRoleIndiviGrant c"
			+ " WHERE c.sacmtRoleIndiviGrantPK.userID = :userId AND c.sacmtRoleIndiviGrantPK.roleType = :roleType";

	@Override
	public List<RoleIndividualGrant> findByUserAndRole(String userId, int roleType) {
		return this.queryProxy().query(SELECT_BY_USER_AND_ROLETYPE, SacmtRoleIndiviGrant.class)

				.setParameter("userId", userId)
				.setParameter("roleType", roleType)
				.getList(c -> c.toDomain());
	}

	private static final String SELECT_BY_ROLE_ID = "SELECT c FROM SacmtRoleIndiviGrant c WHERE c.roleId = :roleId ";

	@Override
	public List<RoleIndividualGrant> findByRoleId(String roleId) {
		List<RoleIndividualGrant> result = new ArrayList<RoleIndividualGrant>();
		List<SacmtRoleIndiviGrant> entities = this.queryProxy().query(SELECT_BY_ROLE_ID, SacmtRoleIndiviGrant.class)
				.setParameter("roleId", roleId).getList();
		if (entities != null && !entities.isEmpty()) {
			result = entities.stream().map(e -> e.toDomain()).collect(Collectors.toList());
		}
		return result;
	}

	private static  final String SELECT_BY_COMPANY_ROLE_ID = "SELECT c FROM SacmtRoleIndiviGrant c WHERE c.roleId = :roleId "
			+ "AND c.sacmtRoleIndiviGrantPK.companyID = :companyID";

	@Override
	public List<RoleIndividualGrant> findByCompanyRole(String companyId, String roleId) {
		return this.queryProxy().query(SELECT_BY_COMPANY_ROLE_ID, SacmtRoleIndiviGrant.class)
				.setParameter("companyID", companyId).setParameter("roleId", roleId).getList(c -> c.toDomain());
	}

	private static final String SELECT_BY_ROLETYPE = "SELECT c FROM SacmtRoleIndiviGrant c"
			+ " WHERE c.sacmtRoleIndiviGrantPK.roleType = :roleType";

	private static final String SELECT_BY_ROLETYPE_AND_CONTRACTCD = "SELECT c FROM SacmtRoleIndiviGrant c"
			+ " WHERE c.sacmtRoleIndiviGrantPK.roleType = :roleType"
			+ " AND c.contractCd = :contractCd ";

	@Override
	public List<RoleIndividualGrant> findByRoleType(int roleType) {
		String contractCode = AppContexts.user().contractCode();
		return this.queryProxy().query(SELECT_BY_ROLETYPE_AND_CONTRACTCD,SacmtRoleIndiviGrant.class)
				.setParameter("roleType",roleType)
				.setParameter("contractCd",contractCode)
				.getList(SacmtRoleIndiviGrant::toDomain);
	}

	private static final String SELECT_BY_COMPANYID_AND_ROLETYPE = "SELECT c FROM SacmtRoleIndiviGrant c"
			+ " WHERE c.sacmtRoleIndiviGrantPK.companyID = :companyID"
			+ " AND c.sacmtRoleIndiviGrantPK.roleType = :roleType";

	@Override
	public List<RoleIndividualGrant> findByCompanyIdAndRoleType(String companyID, int roleType) {
		return this.queryProxy().query(SELECT_BY_COMPANYID_AND_ROLETYPE, SacmtRoleIndiviGrant.class)
				.setParameter("companyID", companyID).setParameter("roleType", roleType).getList(c -> c.toDomain());
	}

	public void add(RoleIndividualGrant roleIndividualGrant) {
		this.commandProxy().insert(SacmtRoleIndiviGrant.toEntity(roleIndividualGrant));
	}

	@Override
	public void update(RoleIndividualGrant roleIndividualGrant) {
		SacmtRoleIndiviGrant newEntity = SacmtRoleIndiviGrant.toEntity(roleIndividualGrant);
		SacmtRoleIndiviGrant updateEntity = this.queryProxy().find(newEntity.sacmtRoleIndiviGrantPK, SacmtRoleIndiviGrant.class).get();
		updateEntity.roleId = newEntity.roleId;
		updateEntity.strD = newEntity.strD;
		updateEntity.endD = newEntity.endD;
		this.commandProxy().update(updateEntity);
	}

	@Override
	public void remove(String userId, String companyId, int roleType) {
		this.commandProxy().remove(SacmtRoleIndiviGrant.class, new SacmtRoleIndiviGrantPK(companyId, userId, roleType));
	}

	@Override
	public Optional<RoleIndividualGrant> findByDetail(String userId, String companyId, int roleType,
			List<String> roleIDLst, GeneralDate date) {
		if(roleIDLst.isEmpty())
			return Optional.empty();
		return this.queryProxy().query(FIND_BY_DETAIL, SacmtRoleIndiviGrant.class)
				.setParameter("companyId", companyId)
				.setParameter("userId", userId)
				.setParameter("roleType", roleType)
				.setParameter("roleIDLst", roleIDLst)
				.setParameter("date", date)
				.getSingle(c -> c.toDomain());
	}

	private static final String SELECT_BY_USER_ID = "SELECT c FROM SacmtRoleIndiviGrant c WHERE c.sacmtRoleIndiviGrantPK.userID = :userId";
	
	@Override
	public void removeByUserId(String userId) {
		List<SacmtRoleIndiviGrant> entities = this.queryProxy().query(SELECT_BY_USER_ID, SacmtRoleIndiviGrant.class)
				.setParameter("userId", userId).getList();
		this.commandProxy().removeAll(entities);
	}
	private static final String FIND = "SELECT c FROM SacmtRoleIndiviGrant c"
			+ " WHERE c.sacmtRoleIndiviGrantPK.companyID = :companyId"
			+ " AND c.sacmtRoleIndiviGrantPK.roleType = :roleType"
			+ " AND c.roleId IN :roleIDLst"
			+ " AND c.strD <= :date AND c.endD >= :date";

	@Override
	public List<RoleIndividualGrant> findRoleIndividual(String companyId, int roleType, List<String> roleIDLst,
			GeneralDate date) {
		List<RoleIndividualGrant> resultList = new ArrayList<>();
		if(roleIDLst.isEmpty())
			return resultList;
		CollectionUtil.split(roleIDLst, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(FIND, SacmtRoleIndiviGrant.class)
				.setParameter("companyId", companyId)
				.setParameter("roleType", roleType)
				.setParameter("roleIDLst", subList)
				.setParameter("date", date)
				.getList(c -> c.toDomain()));
		});
		return resultList;
	}

	@Override
	public List<RoleIndividualGrant> getListDifRoleType(String userId, String companyId, int roleType,
			GeneralDate date) {
		return this.queryProxy().query(SELECT_BY_DATE_ROLE_CID_TYPE, SacmtRoleIndiviGrant.class)
				.setParameter("companyId", companyId)
				.setParameter("userId", userId)
				.setParameter("roleType", roleType)
				.setParameter("date", date)
				.getList(c -> c.toDomain());
	}

}
