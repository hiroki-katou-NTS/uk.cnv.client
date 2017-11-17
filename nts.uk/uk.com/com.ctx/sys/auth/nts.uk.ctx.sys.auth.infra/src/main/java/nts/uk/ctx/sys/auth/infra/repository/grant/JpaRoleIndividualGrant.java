/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.infra.repository.grant;


import java.util.Optional;
import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.entity.type.GeneralDateToDBConverter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.auth.dom.grant.RoleIndividualGrant;
import nts.uk.ctx.sys.auth.dom.grant.RoleIndividualGrantRepository;
import nts.uk.ctx.sys.auth.dom.role.RoleType;
import nts.uk.ctx.sys.auth.infra.entity.grant.SacmtRoleIndiviGrant;
import nts.uk.ctx.sys.auth.infra.entity.grant.SacmtRoleIndiviGrantPK;
import nts.uk.shr.com.time.calendar.period.DatePeriod;

@Stateless
public class JpaRoleIndividualGrant extends JpaRepository implements RoleIndividualGrantRepository {
	
	private final String SELECT_BY_ROLE = "SELECT c FROM SacmtRoleIndiviGrant c WHERE c.SacmtRoleIndiviGrantPK.cid = :cid AND c.SacmtRoleIndiviGrantPK.roleType = roleType";

	private final String SELECT_BY_DATE = "SELECT c FROM SacmtRoleIndiviGrant c WHERE c.SacmtRoleIndiviGrantPK.cid = :cid AND c.SacmtRoleIndiviGrant.      ";
	@Override
	public Optional<RoleIndividualGrant> findByUserAndRole(String userId, RoleType roleType) {
		// TODO Auto-generated method stub
		return this.queryProxy()
				.query(SELECT_BY_ROLE, RoleIndividualGrant.class)
				.setParameter("userId" ,userId )
				.setParameter("roleType", roleType).getSingle();
	}

	@Override
	public Optional<RoleIndividualGrant> findByUser(String userId, GeneralDate date) {
		// TODO Auto-generated method stub
		return this.queryProxy()
				.query(SELECT_BY_DATE, RoleIndividualGrant.class)
				.setParameter("userId", userId)
				.setParameter("date", date)
				.getSingle();
	}

	@Override
	public void add(RoleIndividualGrant roleIndividualGrant) {
			this.commandProxy().insert(toEntity(roleIndividualGrant));		
	}

	@Override
	public void update(RoleIndividualGrant roleIndividualGrant) {
		SacmtRoleIndiviGrant newEntity = toEntity(roleIndividualGrant);
		SacmtRoleIndiviGrant updateEntity = this.queryProxy().find(newEntity.sacmtRoleIndiviGrantPK, SacmtRoleIndiviGrant.class).get();
		updateEntity.roleId = newEntity.roleId;
		updateEntity.strD = newEntity.strD;
		updateEntity.endD = newEntity.endD;
		
		
	}

	@Override
	public void remove(String userId, String companyId, RoleType roleType) {
		this.commandProxy().remove(SacmtRoleIndiviGrant.class, new SacmtRoleIndiviGrantPK(companyId , userId , roleType.value ));
		this.getEntityManager().flush();
	}
	
	private SacmtRoleIndiviGrant toEntity(RoleIndividualGrant domain){
		
		return new SacmtRoleIndiviGrant(
				new SacmtRoleIndiviGrantPK(domain.getCompanyId(), domain.getUserId(), domain.getRoleType().value),
				domain.getRoleId(),
				domain.getValidPeriod().start(),
				domain.getValidPeriod().end()
				);
	}
	
	private RoleIndividualGrant toDomain(SacmtRoleIndiviGrant entity){
		return RoleIndividualGrant.createFromJavaType(	
				entity.sacmtRoleIndiviGrantPK.userId,
				entity.roleId,
				entity.sacmtRoleIndiviGrantPK.cid,
				entity.sacmtRoleIndiviGrantPK.roleType.intValue(),
				entity.strD, 
				entity.endD);
	}
    
	
    

}
