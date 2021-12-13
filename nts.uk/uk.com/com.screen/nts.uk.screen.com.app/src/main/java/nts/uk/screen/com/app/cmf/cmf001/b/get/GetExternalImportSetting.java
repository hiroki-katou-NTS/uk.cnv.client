package nts.uk.screen.com.app.cmf.cmf001.b.get;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.file.storage.FileStorage;
import nts.uk.ctx.exio.app.input.setting.FromCsvBaseSettingToDomainRequireImpl;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class GetExternalImportSetting {

	@Inject
	private GetExternalImportSettingRequire require;

	@Inject
	private FileStorage fileStorage;

	public List<ExternalImportSettingListItemDto> getAll() {
		val require = this.require.create();
		val settings = require.getSettings(AppContexts.user().companyId());
		return ExternalImportSettingListItemDto.fromDomain(settings);
	}

	public List<ExternalImportSettingListItemDto> getDomainBase() {
		val require = this.require.create();
		val settings = require.getDomainBaseSettings(AppContexts.user().companyId());
		return ExternalImportSettingListItemDto.fromDomain(settings);
	}

	public List<ExternalImportSettingListItemDto> getCsvBase() {
		val require = this.require.create();
		val settings = require.getCsvBaseSettings(AppContexts.user().companyId());
		return ExternalImportSettingListItemDto.fromDomain(settings);
	}

	public ExternalImportSettingDto get(String settingCode) {
		val require = this.require.create();
		val setting = require.getSetting(AppContexts.user().companyId(), new ExternalImportCode(settingCode)).get();
		
		val toDomainRequire = new FromCsvBaseSettingToDomainRequireImpl(fileStorage);
		return ExternalImportSettingDto.fromDomain(toDomainRequire, setting);
	}

	public static interface Require extends ExternalImportLayoutDto.Require {
		List<ExternalImportSetting> getSettings(String companyId);
		List<ExternalImportSetting> getDomainBaseSettings(String companyId);
		List<ExternalImportSetting> getCsvBaseSettings(String companyId);
		Optional<ExternalImportSetting> getSetting(String companyId, ExternalImportCode settingCode);
	}
}
