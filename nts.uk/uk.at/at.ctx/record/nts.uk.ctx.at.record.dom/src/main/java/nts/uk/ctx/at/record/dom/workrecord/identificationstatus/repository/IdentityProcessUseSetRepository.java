package nts.uk.ctx.at.record.dom.workrecord.identificationstatus.repository;

import java.util.Optional;

import nts.uk.ctx.at.record.dom.workrecord.identificationstatus.IdentityProcessUseSet;

public interface IdentityProcessUseSetRepository {
	
	Optional<IdentityProcessUseSet> findByKey(String companyId);

}
