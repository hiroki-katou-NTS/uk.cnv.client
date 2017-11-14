/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.infra.repository.roleset;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
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
			+ " WHERE rs.roleSetPK.companyId = :companyId ";

	private RoleSet toDomain(SacmtRoleSet entity) {
	
		return RoleSet.create(entity.roleSetPK.roleSetCd
				, entity.roleSetPK.companyId
				, entity.roleSetName
				, entity.approvalAuthority
				, entity.officeHelperRole
				, entity.myNumberRole
				, entity.hRRole
				, entity.personInfRole
				, entity.employmentRole
				, entity.salaryRole
				);
	}

	private SacmtRoleSet toEntity(RoleSet domain) {
		SacmtRoleSetPK key = new SacmtRoleSetPK(domain.getRoleSetCd().v(), domain.getCompanyId());
		return new SacmtRoleSet(key
				, domain.getRoleSetName().v()
				, domain.getApprovalAuthority().value
				, RoleSet.getRoleTypeCd(domain.getOfficeHelperRole())
				, RoleSet.getRoleTypeCd(domain.getMyNumberRole())
				, RoleSet.getRoleTypeCd(domain.getHRRole())
				, RoleSet.getRoleTypeCd(domain.getPersonInfRole())
				, RoleSet.getRoleTypeCd(domain.getEmploymentRole())
				, RoleSet.getRoleTypeCd(domain.getSalaryRole())
				);

	}
	
	private SacmtRoleSet toEntiryForUpdate(RoleSet domain, SacmtRoleSet upEntity) {
		upEntity.buildEntity(upEntity.roleSetPK
				, domain.getRoleSetName().v()
				, domain.getApprovalAuthority().value
				, RoleSet.getRoleTypeCd(domain.getOfficeHelperRole())
				, RoleSet.getRoleTypeCd(domain.getMyNumberRole())
				, RoleSet.getRoleTypeCd(domain.getHRRole())
				, RoleSet.getRoleTypeCd(domain.getPersonInfRole())
				, RoleSet.getRoleTypeCd(domain.getEmploymentRole())
				, RoleSet.getRoleTypeCd(domain.getSalaryRole()));
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

}
