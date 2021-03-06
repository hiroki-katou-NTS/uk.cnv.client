package nts.uk.ctx.sys.gateway.dom.accessrestrictions;

import java.util.Optional;

import nts.uk.ctx.sys.gateway.dom.loginold.ContractCode;

public interface AccessRestrictionsRepository {

	void insert(AccessRestrictions domain);
	
	void update(AccessRestrictions domain);
	
	/** [3]εεΎγγ */
	Optional<AccessRestrictions> get(ContractCode contractCode);
}
