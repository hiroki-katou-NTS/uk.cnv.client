/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.infra.repository.roleset;

import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.sys.auth.dom.roleset.DefaultRoleSet;
import nts.uk.ctx.sys.auth.dom.roleset.DefaultRoleSetRepository;
import nts.uk.ctx.sys.auth.infra.entity.roleset.SacmtRolesetDefault;
import nts.uk.ctx.sys.auth.infra.entity.roleset.SacmtDefaultRoleSetPK;

/**
 * Class JpaDefaultRoleSetRepository implement of DefaultRoleSetRepository
 * @author Hieu.NV
 *
 */
@Stateless
public class JpaDefaultRoleSetRepository extends JpaRepository implements DefaultRoleSetRepository {
    
    private static final String SELECT_DEFAULT_ROLE_SET_BY_COMPANY_ID_ROLE_SET_CD = "SELECT rs FROM SacmtRolesetDefault rs"
            + " WHERE rs.companyId = :companyId ";

    /**
     * Build Domain from Entity
     * @param entity
     * @return
     */
    private DefaultRoleSet toDomain(SacmtRolesetDefault entity) {
        return new DefaultRoleSet(entity.companyId, entity.roleSetCd);
    }

    /**
     * Build Entity from Domain
     * @param domain
     * @return
     */
    private SacmtRolesetDefault toEntity(DefaultRoleSet domain) {
        return new SacmtRolesetDefault(domain.getCompanyId(), domain.getRoleSetCd().v());
    }

    /**
     * Build Entity from Domain for updating (keep common fields)
     * @param domain
     * @param upEntity
     * @return
     */
    private SacmtRolesetDefault toEntityForUpdate(DefaultRoleSet domain, SacmtRolesetDefault upEntity) {
        upEntity.buildEntity(upEntity.companyId, domain.getRoleSetCd().v());
        return upEntity;
    }    
    
    @Override
    public Optional<DefaultRoleSet> find(String companyId) {
        return this.queryProxy().query(SELECT_DEFAULT_ROLE_SET_BY_COMPANY_ID_ROLE_SET_CD, SacmtRolesetDefault.class)
                .setParameter("companyId", companyId)
                .getSingle(c -> toDomain(c));
    }


    @Override
    public Optional<DefaultRoleSet> findByCompanyId(String companyId) {
        return this.queryProxy().find(companyId, SacmtRolesetDefault.class).map(c -> toDomain(c));
    }

    @Override
    public void insert(DefaultRoleSet domain) {
        this.commandProxy().insert(toEntity(domain));
    }

    @Override
    public void update(DefaultRoleSet domain) {
         Optional<SacmtRolesetDefault> upEntity = this.queryProxy().find(new SacmtRolesetDefault().companyId,
                 SacmtRolesetDefault.class);
        if (upEntity.isPresent()) {
            this.commandProxy().update(toEntityForUpdate(domain, upEntity.get()));
        }
    }

    @Override
    public void delete(String companyId) {
        this.commandProxy().remove(SacmtRolesetDefault.class, new SacmtDefaultRoleSetPK(companyId));
    }

    @Override
    public void addOrUpdate(DefaultRoleSet domain) {
        Optional<SacmtRolesetDefault> upEntity = this.queryProxy().find(domain.getCompanyId(),
                 SacmtRolesetDefault.class);
        if (upEntity.isPresent()) {
            this.commandProxy().update(toEntityForUpdate(domain, upEntity.get()));
        } else {
            this.commandProxy().insert(toEntity(domain));
        }
    }
}
