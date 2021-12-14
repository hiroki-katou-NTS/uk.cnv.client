/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.infra.repository.role;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.auth.dom.role.RoleRepository;
import nts.uk.ctx.sys.auth.infra.entity.role.SacmtRole;
import nts.uk.ctx.sys.auth.infra.entity.role.SacmtRole_;


/**
 * The Class JpaRoleRepository.
 */
@Stateless
public class JpaRoleRepository extends JpaRepository implements RoleRepository {
	
	private final static String GET_BY_ROLE_TYPE = "SELECT e FROM SacmtRole e"
			+ " WHERE e.cid = :companyId AND e.roleType = :roleType"
			+ " ORDER BY e.assignAtr ASC, e.code ASC ";
	
	private final static String GET_BY_ROLE_TYPE_ROLE_ATR = "SELECT e FROM SacmtRole e"
			+ " WHERE e.cid = :companyId AND e.roleType = :roleType"
			+ " AND e.assignAtr = :roleAtr ORDER BY e.assignAtr ASC, e.code ASC ";
	
	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.auth.dom.role.RoleRepository#findById(java.lang.String)
	 */
	@Override
	public List<Role> findByListId(List<String> lstRoleId) {
		//if is empty lstRoleId
		if (lstRoleId.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<SacmtRole> sacmtRoles = new ArrayList<>();
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<SacmtRole> cq = criteriaBuilder.createQuery(SacmtRole.class);
		Root<SacmtRole> root = cq.from(SacmtRole.class);

		// select root
		cq.select(root);

		CollectionUtil.split(lstRoleId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {
			// add where
			List<Predicate> predicateList = new ArrayList<>();

			predicateList.add(root.get(SacmtRole_.roleId).in(subList));
			cq.where(predicateList.toArray(new Predicate[] {}));

			sacmtRoles.addAll(em.createQuery(cq).getResultList());
		});
		
		return sacmtRoles.stream().map(sacmtRole -> new Role(new JpaRoleGetMemento(sacmtRole)))
				.collect(Collectors.toList());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.auth.dom.role.RoleRepository#findByListRoleId(java.lang.String, java.util.List)
	 */
	@Override
	public List<Role> findByListRoleId(String companyId, List<String> lstRoleId) {
		// if is empty lstRoleId
		if (lstRoleId.isEmpty()) {
			return Collections.emptyList();
		}
				
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<SacmtRole> cq = criteriaBuilder.createQuery(SacmtRole.class);
		Root<SacmtRole> root = cq.from(SacmtRole.class);

		// select root
		cq.select(root);
		
		List<SacmtRole> resultList = new ArrayList<>();
		
		CollectionUtil.split(lstRoleId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			// add where
			List<Predicate> predicateList = new ArrayList<>();

			predicateList.add(root.get(SacmtRole_.roleId).in(subList));
			predicateList.add(criteriaBuilder.equal(root.get(SacmtRole_.cid), companyId));
			cq.where(predicateList.toArray(new Predicate[] {}));
			
			resultList.addAll(em.createQuery(cq).getResultList());
		});
		
		return resultList.stream().map(sacmtRole -> new Role(new JpaRoleGetMemento(sacmtRole)))
				.collect(Collectors.toList());
	}
	
	
	
	@Override
	public void insert(Role role) {
		this.commandProxy().insert(toEntity(role));		
	}

	@Override
	public void update(Role role) {
		SacmtRole updateEntity = this.queryProxy().find(role.getRoleId(), SacmtRole.class).get();
		updateEntity.setCid(role.getCompanyId());
		updateEntity.setRoleType(role.getRoleType().value);
		updateEntity.setReferenceRange(role.getEmployeeReferenceRange().value);
		updateEntity.setName(role.getName().toString());
		updateEntity.contractCd = (role.getContractCode().toString());
		updateEntity.setAssignAtr(role.getAssignAtr().value);
		this.commandProxy().update(updateEntity);		
	}
	
	@Override
	public void remove(String roleId) {		
		this.commandProxy().remove(SacmtRole.class, roleId);
	}
	
	private SacmtRole  toEntity(Role role){
		SacmtRole entity = new SacmtRole();
		entity.setRoleId(role.getRoleId());
		entity.setCid(role.getCompanyId());
		entity.setCode(role.getRoleCode().toString());
		entity.setRoleType(role.getRoleType().value);
		entity.setReferenceRange(role.getEmployeeReferenceRange().value);
		entity.setName(role.getName().toString());
		entity.contractCd = (role.getContractCode().toString());
		entity.setAssignAtr(role.getAssignAtr().value);
		return entity;
	}

	@Override
	public List<Role> findByType(String companyId, int roleType) {
		List<Role> result = new ArrayList<>();
		
		List<SacmtRole> entities = this.queryProxy().query(GET_BY_ROLE_TYPE, SacmtRole.class)
				.setParameter("companyId", companyId).setParameter("roleType", roleType).getList();
		if (entities != null && entities.size() !=0) {
			return entities.stream().map(x->new Role(new JpaRoleGetMemento(x))).collect(Collectors.toList());
		}
		return result;
	}
	
	@Override
	public List<Role> findByTypeAndRoleAtr(String companyId, int roleType, int roleAtr) {
		List<SacmtRole> entities = this.queryProxy().query(GET_BY_ROLE_TYPE_ROLE_ATR, SacmtRole.class)
				.setParameter("companyId", companyId).setParameter("roleType", roleType)
				.setParameter("roleAtr", roleAtr).getList();
		return entities.stream().map(x -> new Role(new JpaRoleGetMemento(x))).collect(Collectors.toList());
	}

	@Override
	public List<Role> findByType(int roleType) {
		List<Role> result = new ArrayList<>();
		String query ="SELECT e FROM SacmtRole e WHERE e.roleType = :roleType ORDER BY e.assignAtr ASC, e.code ASC ";
		List<SacmtRole> entities = this.queryProxy().query(query, SacmtRole.class).setParameter("roleType", roleType).getList();
		if (entities != null  && !entities.isEmpty()) {
			return entities.stream().map(x ->new Role(new JpaRoleGetMemento(x))).collect(Collectors.toList());
		}
		return result;
	}
	
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public Optional<Role> findByRoleId(String roleId) {
		String query ="SELECT e FROM SacmtRole e WHERE e.roleId = :roleId ";
		return this.queryProxy().query(query, SacmtRole.class)
				.setParameter("roleId", roleId).getList().stream().map(x ->new Role(new JpaRoleGetMemento(x))).findFirst();
	}

	@Override
	public Optional<Role> findRoleByRoleCode(String companyId,String roleCode, int roleType) {
		String query ="SELECT e FROM SacmtRole e WHERE e.code = :code AND e.roleType = :roleType "
				+ " AND e.cid = :companyId ";
		return this.queryProxy().query(query, SacmtRole.class)
				.setParameter("code", roleCode)
				.setParameter("roleType", roleType)
				.setParameter("companyId", companyId)
				.getList().stream().map(x ->new Role(new JpaRoleGetMemento(x))).findFirst();
	}

	@Override
	public Optional<Role> findByContractCDRoleTypeAndCompanyID(String contractCD, int roleType, String companyID) {
		String query = "SELECT e FROM SacmtRole e WHERE e.contractCode = :contractCode AND e.roleType = :roleType AND e.cid = :cid";
		return this.queryProxy().query(query, SacmtRole.class)
				.setParameter("contractCode", contractCD)
				.setParameter("roleType", roleType)
				.setParameter("cid", companyID)
				.getList().stream().map(x ->new Role (new JpaRoleGetMemento(x))).findFirst();
	}

	@Override
	public List<Role> findByTypeAtr(String companyId, int roleType, int RoleAtr) {
		List<Role> result = new ArrayList<>();
		
		String query ="SELECT e FROM SacmtRole e WHERE e.cid = :companyId AND e.roleType = :roleType AND e.assignAtr = :assignAtr ORDER BY e.assignAtr ASC, e.code ASC ";
		List<SacmtRole> entities = this.queryProxy().query(query, SacmtRole.class)
				.setParameter("companyId", companyId).setParameter("roleType", roleType).setParameter("assignAtr", RoleAtr).getList();
		if (entities != null && entities.size() !=0) {
			return entities.stream().map(x->new Role(new JpaRoleGetMemento(x))).collect(Collectors.toList());
		}
		return result;
	}

	@Override
	public Map<String, String> findRoleIdAndNameByListRoleId(String cid, List<String> roleIds) {
		Map<String, String> result = new HashMap<>();
		CollectionUtil.split(roleIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String sql ="SELECT ROLE_ID, ROLE_NAME FROM SACMT_ROLE WHERE CID = ? AND ROLE_ID IN ("+ NtsStatement.In.createParamsString(subList) + ")";
			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				stmt.setString(1, cid);
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString(i + 2, subList.get(i));
				}
				new NtsResultSet(stmt.executeQuery()).getList(r -> {
					result.put(r.getString("ROLE_ID"), r.getString("ROLE_NAME"));
					return null;
				});
			}catch(SQLException e) {
				throw new RuntimeException(e);
			}
		});
		return result;
	}

}
