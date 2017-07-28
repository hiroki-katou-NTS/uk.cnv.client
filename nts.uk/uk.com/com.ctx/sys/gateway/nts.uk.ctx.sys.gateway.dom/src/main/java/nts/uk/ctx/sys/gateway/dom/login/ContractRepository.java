/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.gateway.dom.login;

import java.util.Optional;

/**
 * The Interface ContractRepository.
 */
public interface ContractRepository {

	/**
	 * Gets the contract.
	 *
	 * @return the contract
	 */
	Optional<Contract> getContract();
}
