package nts.uk.ctx.sys.portal.app.find.jobtitletying;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;


import nts.uk.ctx.sys.portal.dom.jobtitletying.JobTitleTyingRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class JobTitleTyingFinder {
	@Inject
	private JobTitleTyingRepository jobTitleTyingRepository;
	
	public List<JobTitleTyingDto> findWebMenuCode(String jobId){
		String companyID = AppContexts.user().companyId();
		return this.jobTitleTyingRepository.findWebMenuCode(companyID, jobId).stream()
				.map(item -> JobTitleTyingDto.fromDomain(item)).collect(Collectors.toList());
	}
}
