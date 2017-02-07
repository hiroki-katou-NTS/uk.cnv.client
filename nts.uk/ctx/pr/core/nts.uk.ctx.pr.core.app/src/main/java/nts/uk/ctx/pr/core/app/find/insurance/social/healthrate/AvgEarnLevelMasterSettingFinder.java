package nts.uk.ctx.pr.core.app.find.insurance.social.healthrate;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.pr.core.dom.insurance.social.healthrate.AvgEarnLevelMasterSettingRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class AvgEarnLevelMasterSettingFinder {

	@Inject
	private AvgEarnLevelMasterSettingRepository repository;

	public List<AvgEarnLevelMasterSettingDto> findAll() {
		String companyCode = AppContexts.user().companyCode();
		return repository.findAll(companyCode).stream().map(domain -> AvgEarnLevelMasterSettingDto.fromDomain(domain))
				.collect(Collectors.toList());
	}
}
