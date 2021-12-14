package nts.uk.ctx.at.function.app.find.alarm.mailsettings;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.gul.text.StringUtil;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.MailSettingAutomaticRepository;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.MailSettingNormalRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class MailSettingFinder {

	@Inject
	private MailSettingAutomaticRepository mailAutomaticRepo;

	@Inject
	private MailSettingNormalRepository mailNormalRepo;

	public MailAutoAndNormalDto findMailSet() {
		String companyId = AppContexts.user().companyId();
		if (StringUtil.isNullOrEmpty(companyId, true))
			return null;

		MailSettingAutomaticDto mailSettingAutomaticDto = new MailSettingAutomaticDto(mailAutomaticRepo.findByCompanyId(companyId));
		MailSettingNormalDto mailSettingNormalDto = new MailSettingNormalDto(mailNormalRepo.findByCompanyId(companyId));

		return new MailAutoAndNormalDto(mailSettingAutomaticDto, mailSettingNormalDto);
	}

}
