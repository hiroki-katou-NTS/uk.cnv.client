package nts.uk.screen.com.app.cmf.cmf001.b.get;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class GetExternalImportSetting {

	@Inject
	private GetExternalImportSettingRequire require;

	public List<ExternalImportSettingListItemDto> getAll() {
		val require = this.require.create();
		val settings = require.getSettings(AppContexts.user().companyId());
		return ExternalImportSettingListItemDto.fromDomain(settings);
	}

	public ExternalImportSettingDto get(String settingCode) {
		val require = this.require.create();
		val settingOpt = require.getSetting(AppContexts.user().companyId(), new ExternalImportCode(settingCode));
		return ExternalImportSettingDto.fromDomain(settingOpt.get());
	}

	public static interface Require extends ExternalImportLayoutDto.Require {
		List<ExternalImportSetting> getSettings(String companyId);
		Optional<ExternalImportSetting> getSetting(String companyId, ExternalImportCode settingCode);
	}
}
