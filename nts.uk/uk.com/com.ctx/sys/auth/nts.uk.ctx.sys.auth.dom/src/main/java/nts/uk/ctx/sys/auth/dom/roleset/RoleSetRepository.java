/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.dom.roleset;

import java.util.List;
import java.util.Optional;

/**
 * ロールセット - Interface RoleSetRepository.
 * @author HieuNV
 *
 */
public interface RoleSetRepository {

    /**
     * Find by id.
     *
     * @param roleSetCd
     * @param companyId
     * @return
     */
    Optional<RoleSet> findByRoleSetCdAndCompanyId(String roleSetCd, String companyId);

    /**
     * Find by company id.
     *
     * @param companyId
     * @return
     */
    List<RoleSet> findByCompanyId(String companyId);

    /**
     * Check duplicate RoleSetCd within a company id
     * @param roleSetCd
     * @param companyId
     * @return
     */
    boolean isDuplicateRoleSetCd(String roleSetCd, String companyId);

    /**
     * Insert a Role Set
     * @param domain
     */
    void insert(RoleSet domain);
    
    /**
     * Update the Role Set
     * @param domain
     */
    void update(RoleSet domain);

    /**
     * Delete the Role Set
     * @param roleSetCd
     * @param companyId
     */
    void delete(String roleSetCd, String companyId);

    /**
     * find by company id and person role id
     * 
     * @param companyId
     * @param personRoleId
     * @return
     */
    List<RoleSet> findByCompanyIdAndPersonRole(String companyId, String personRoleId);
    
    /**
     * find by employmentRoleId
     * 
     * @param personRoleId
     * @return
     */
    List<RoleSet> findByemploymentRoleId(String employmentRoleId);
    
    /**
     * find by employmentRoleId, companyId
     * 
     * @param companyId
     * @param personRoleId
     * @return
     */
    List<RoleSet> findByCidEmploymentRoleId(String companyId,String employmentRoleId);
    
    Optional<RoleSet> findByCidRollSetCDAuthor (String companyId , String roleSetCd , int approvalAuthority );
    
    List<RoleSet> findByCIDAndEmpRoleLst(String companyID, List<String> empRoleLst);

}
