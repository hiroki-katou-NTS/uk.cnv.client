package nts.uk.ctx.sys.portal.app.find.toppagesetting;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.sys.portal.dom.toppagesetting.TopPageSelfSetRepository;
import nts.uk.shr.com.context.AppContexts;
@Stateless
public class TopPageSelfSetSelectedFinder {
	@Inject
	private TopPageSelfSetRepository repository;
	public Optional<TopPageSelfSettingDto> getTopPageSelfSet(){
		String employeeId = AppContexts.user().employeeCode();
		Optional<TopPageSelfSettingDto> lst = this.repository.getTopPageSelfSet(employeeId)
				.map(c->TopPageSelfSettingDto.fromDomain(c));
		return lst;
	}
}
