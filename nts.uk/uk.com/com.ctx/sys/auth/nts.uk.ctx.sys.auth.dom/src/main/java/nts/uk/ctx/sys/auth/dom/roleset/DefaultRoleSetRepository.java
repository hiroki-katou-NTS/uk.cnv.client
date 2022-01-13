/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.dom.roleset;

import java.util.Optional;

/**
 * 既定のロールセット - Interface DefaultRoleSetRepository
 * @author HieuNV
 *
 */
public interface DefaultRoleSetRepository {

    /**
     * Find by Company Id.
     *
     * @param companyId
     * @return
     */
    Optional<DefaultRoleSet> findByCompanyId(String companyId);

    /**
     * Check exist of default role set
     * @param companyId
     * @param roleSetCd
     * @return
     */
    Optional<DefaultRoleSet> find(String companyId);

    /**
     * Insert a Default Role Set
     * @param domain
     */
    void insert(DefaultRoleSet domain);

    /**
     * Update the Default Role Set
     * @param domain
     */
    void update(DefaultRoleSet domain);

    /**
     * Delete the Default Role Set
     * @param companyId
     */
    void delete(String companyId);

    /**
     * Perform check if the RoleSet is existed then update, else insert
     * @param domain
     */
    void addOrUpdate(DefaultRoleSet domain);
}
