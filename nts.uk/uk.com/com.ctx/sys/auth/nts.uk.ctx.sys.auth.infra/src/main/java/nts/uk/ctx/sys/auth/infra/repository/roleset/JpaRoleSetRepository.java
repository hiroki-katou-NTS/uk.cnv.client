/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.infra.repository.roleset;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.sys.auth.dom.roleset.ApprovalAuthority;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSet;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSetRepository;
import nts.uk.ctx.sys.auth.infra.entity.roleset.SacmtRoleSet;
import nts.uk.ctx.sys.auth.infra.entity.roleset.SacmtRoleSetPK;

/**
 * Class JpaRoleSetRepository implement of RoleSetRepository
 * @author Hieu.NV
 *
 */
@Stateless
public class JpaRoleSetRepository extends JpaRepository implements RoleSetRepository {

    private static final String SELECT_All_ROLE_SET_BY_COMPANY_ID = "SELECT rs FROM SacmtRoleSet rs"
            + " WHERE rs.roleSetPK.companyId = :companyId "
            + " ORDER BY rs.roleSetPK.roleSetCd ASC ";
    private static final String SELECT_All_ROLE_SET_BY_COMPANY_ID_AND_PERSON_ROLE = "SELECT rs FROM SacmtRoleSet rs"
            + " WHERE rs.roleSetPK.companyId = :companyId AND rs.personInfRole = :personRoleId ";
    
    private static final String SELECT_ROLE_SET_BY_EMPLOYMENT_ROLE_CODE = "SELECT rs FROM SacmtRoleSet rs"
            + " WHERE rs.employmentRole = :employmentRole ";
    
    private static final String SELECT_ROLE_SET_BY_CID_EMPLOYMENT_ROLE_CODE = "SELECT rs FROM SacmtRoleSet rs"
            + " WHERE rs.roleSetPK.companyId = :companyId"
            + " AND rs.employmentRole = :employmentRole ";
    
    private static final String FIND_BY_CID_ROLES = "SELECT rs FROM SacmtRoleSet rs"
            + " WHERE rs.roleSetPK.companyId = :companyID"
            + " AND rs.employmentRole IN :empRoleLst ";

    /**
     * Build Domain from Entity
     * @param entity
     * @return
     */
    private RoleSet toDomain(SacmtRoleSet entity) {
        return new RoleSet(entity.roleSetPK.roleSetCd
                , entity.roleSetPK.companyId
                , entity.roleSetName
                , EnumAdaptor.valueOf(entity.approvalAuthority, ApprovalAuthority.class)
                , entity.officeHelperRole
                , entity.myNumberRole
                , entity.hRRole
                , entity.personInfRole
                , entity.employmentRole
                , entity.salaryRole
                );
    }

    /**
     * Build Entity from Domain
     * @param domain
     * @return
     */
    private SacmtRoleSet toEntity(RoleSet domain) {
        SacmtRoleSetPK key = new SacmtRoleSetPK(domain.getRoleSetCd().v(), domain.getCompanyId());
        return new SacmtRoleSet(key
                , domain.getRoleSetName().v()
                , domain.getApprovalAuthority().value
                , domain.getOfficeHelperRoleId()
                , domain.getMyNumberRoleId()
                , domain.getHRRoleId()
                , domain.getPersonInfRoleId()
                , domain.getEmploymentRoleId()
                , domain.getSalaryRoleId()
                );

    }

    /**
     * Build Entity from Domain for updating (keep common fields)
     * @param domain
     * @param upEntity
     * @return
     */
    private SacmtRoleSet toEntiryForUpdate(RoleSet domain, SacmtRoleSet upEntity) {
        upEntity.buildEntity(upEntity.roleSetPK
                , domain.getRoleSetName().v()
                , domain.getApprovalAuthority().value
                , domain.getOfficeHelperRoleId()
                , domain.getMyNumberRoleId()
                , domain.getHRRoleId()
                , domain.getPersonInfRoleId()
                , domain.getEmploymentRoleId()
                , domain.getSalaryRoleId());
        return upEntity;
    }

    @Override
    public Optional<RoleSet> findByRoleSetCdAndCompanyId(String roleSetCd, String companyId) {
        SacmtRoleSetPK pk = new SacmtRoleSetPK(roleSetCd, companyId);
        return this.queryProxy().find(pk, SacmtRoleSet.class).map(c -> toDomain(c));
    }

    @Override
    public List<RoleSet> findByCompanyId(String companyId) {
        return this.queryProxy().query(SELECT_All_ROLE_SET_BY_COMPANY_ID, SacmtRoleSet.class)
                .setParameter("companyId", companyId)
                .getList(c -> toDomain(c));
    }

    @Override
    public void insert(RoleSet domain) {
        this.commandProxy().insert(toEntity(domain));
    }

    @Override
    public void update(RoleSet domain) {
        Optional<SacmtRoleSet> upEntity = this.queryProxy().find(
                 new SacmtRoleSetPK(domain.getRoleSetCd().v(), domain.getCompanyId()),
                 SacmtRoleSet.class);
        if (upEntity.isPresent()) {
            this.commandProxy().update(toEntiryForUpdate(domain, upEntity.get()));
        }
    }

    @Override
    public void delete(String roleSetCd, String companyId) {
        SacmtRoleSetPK pk = new SacmtRoleSetPK(roleSetCd, companyId);
        this.commandProxy().remove(SacmtRoleSet.class, pk);
    }

    @Override
    public boolean isDuplicateRoleSetCd(String roleSetCd, String companyId) {
        SacmtRoleSetPK pk = new SacmtRoleSetPK(roleSetCd, companyId);
        return this.queryProxy().find(pk, SacmtRoleSet.class).isPresent();
        
    }

    public List<RoleSet> findByCompanyIdAndPersonRole(String companyId, String personRoleId) {
        List<RoleSet> result = new ArrayList<RoleSet>();
        List<SacmtRoleSet> entities = this.queryProxy()
                .query(SELECT_All_ROLE_SET_BY_COMPANY_ID_AND_PERSON_ROLE, SacmtRoleSet.class)
                .setParameter("companyId", companyId).setParameter("personRoleId", personRoleId).getList();
        if(entities !=null && !entities.isEmpty()){
            result = entities.stream().map(e ->toDomain(e)).collect(Collectors.toList());
        }
        return result;
    }

	@Override
	public List<RoleSet> findByemploymentRoleId(String employmentRoleId) {
		List<RoleSet> result = new ArrayList<RoleSet>();
        List<SacmtRoleSet> entities = this.queryProxy()
                .query(SELECT_ROLE_SET_BY_EMPLOYMENT_ROLE_CODE, SacmtRoleSet.class)
                .setParameter("employmentRole", employmentRoleId).getList();
        if(entities !=null && !entities.isEmpty()){
            result = entities.stream().map(e ->toDomain(e)).collect(Collectors.toList());
        }
        return result;
	}

	@Override
	public List<RoleSet> findByCidEmploymentRoleId(String companyId, String employmentRoleId) {
		List<RoleSet> result = new ArrayList<RoleSet>();
        List<SacmtRoleSet> entities = this.queryProxy()
                .query(SELECT_ROLE_SET_BY_CID_EMPLOYMENT_ROLE_CODE, SacmtRoleSet.class)
                .setParameter("companyId", companyId)
                .setParameter("employmentRole", employmentRoleId).getList();
        if(entities !=null && !entities.isEmpty()){
            result = entities.stream().map(e ->toDomain(e)).collect(Collectors.toList());
        }
        return result;
	}
     
	private static final String SELECT_BY_CID_ROLLSETCD_AUTHOR = "SELECT rs FROM SacmtRoleSet rs"
            + " WHERE rs.roleSetPK.companyId = :companyId"
            + " AND rs.roleSetPK.roleSetCd = :roleSetCd "
            + " AND rs.approvalAuthority = :approvalAuthority";
	@Override
	public Optional<RoleSet> findByCidRollSetCDAuthor(String companyId, String roleSetCd, int approvalAuthority) {
		return this.queryProxy().query(SELECT_BY_CID_ROLLSETCD_AUTHOR ,SacmtRoleSet.class)
				.setParameter("companyId", companyId)
				.setParameter("roleSetCd", roleSetCd)
				.setParameter("approvalAuthority", approvalAuthority)
				.getSingle( c -> toDomain(c));
	}

	@Override
	public List<RoleSet> findByCIDAndEmpRoleLst(String companyID, List<String> empRoleLst) {
		List<RoleSet> resultList = new ArrayList<>();
		if(empRoleLst.isEmpty())
			return resultList;
		
		CollectionUtil.split(empRoleLst, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(FIND_BY_CID_ROLES ,SacmtRoleSet.class)
				.setParameter("companyID", companyID)
				.setParameter("empRoleLst", subList)
				.getList(c -> toDomain(c)));
		});
		return resultList;
	}
}
