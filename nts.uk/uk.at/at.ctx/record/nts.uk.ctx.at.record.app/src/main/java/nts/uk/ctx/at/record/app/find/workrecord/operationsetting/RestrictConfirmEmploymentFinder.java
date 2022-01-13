package nts.uk.ctx.at.record.app.find.workrecord.operationsetting;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.confirmemployment.RestrictConfirmEmployment;
import nts.uk.ctx.at.record.dom.confirmemployment.RestrictConfirmEmploymentRepository;

@Stateless
public class RestrictConfirmEmploymentFinder {

	@Inject
	private RestrictConfirmEmploymentRepository restrictConfirmEmploymentRepository;
	
	public RestrictConfirmEmploymentDto findByCompanyId(String cid) {
		RestrictConfirmEmployment restrictConfirmEmployment = restrictConfirmEmploymentRepository.findByCompanyID(cid).orElse(null);
		RestrictConfirmEmploymentDto dto = RestrictConfirmEmploymentDto.fromDomain(restrictConfirmEmployment);
		
		return dto;
	}
}
