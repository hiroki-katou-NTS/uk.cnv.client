/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.dom.grant.roleindividual;



import java.util.List;
import java.util.Optional;


import nts.arc.time.GeneralDate;

import nts.uk.ctx.sys.auth.dom.role.RoleType;


/**
 * The Interface RoleIndividualGrantRepository.
 */
public interface RoleIndividualGrantRepository {
	
	/**
	 * Find by user and role.
	 *
	 * @param userId the user id
	 * @param roleType the role type
	 * @return the optional
	 */
	List<RoleIndividualGrant> findByUserAndRole(String userId,RoleType roleType);
	
	/**
	 * Find by user.
	 *
	 * @param userId the user id
	 * @param date the date
	 * @return the optional
	 */
	List<RoleIndividualGrant> findByUser(String userId,GeneralDate startDate , GeneralDate endDate);
	
	List<RoleIndividualGrant> searchRoleIndividualGrant(RoleType roleType,String companyID);
	
	/**
	 * Add
	 * @param roleIndividualGrant
	 */
	void add (RoleIndividualGrant  roleIndividualGrant);
	
	/**
	 * Update 
	 * @param roleIndividualGrant
	 */
	void update (RoleIndividualGrant  roleIndividualGrant);
	
	/**
	 * Remove
	 * @param userId
	 * @param companyId
	 * @param roleType
	 */
	void remove (String userId, String companyId , RoleType roleType);

	List<RoleIndividualGrant> findByRoleId(String roleId);
	
	Optional<RoleIndividualGrant> findRoleIndividualGrant(String userID , String companyID , RoleType roleType );

	Optional<RoleIndividualGrant> findByDateAndType(GeneralDate startDate, GeneralDate endDate, RoleType roleType);

	Optional<RoleIndividualGrant> findByUser(String userId, GeneralDate date);
	
	/**
	 * find by role id
	 * 
	 * @param roleId
	 * @return
	 */


}
