package nts.uk.ctx.exio.app.input.setting;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nts.arc.diagnose.stopwatch.embed.EmbedStopwatch;
import nts.arc.layer.app.file.storage.FileStorage;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItem;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItemsRepository;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSettingRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class FindExternalImportSetting {
	@Inject
	public ExternalImportSettingRepository externalImportSettingRepo;
	
	@Inject
	private FileStorage fileStorage;
	
	public List<ExternalImportSettingListItemDto> findAll() {
		val settings = externalImportSettingRepo.getAll(AppContexts.user().companyId());
		return ExternalImportSettingListItemDto.fromDomain(settings);
	}
	
	public ExternalImportSettingDto find(String settingCode, int domainId) {
		val require = this.createRequire();
		val setting = require.getSetting(AppContexts.user().companyId(), new ExternalImportCode(settingCode)).get();
		val domainSetting = setting.getDomainSetting(ImportingDomainId.valueOf(domainId))
				.orElseThrow(() -> new RuntimeException("selected domain setting is not found."));
		return ExternalImportSettingDto.fromDomain(require, setting, domainSetting);
	}
	
	public Require createRequire() {
		return EmbedStopwatch.embed(new RequireImpl());
	}

	public static interface Require extends ExternalImportSettingDto.Require {
	}
	
	@Inject
	public ImportableItemsRepository importableItemsRepo;
	
	@RequiredArgsConstructor
	public class RequireImpl implements Require {
		
		@Override
		public ImportableItem getImportableItems(ImportingDomainId domainId, int itemNo) {
			return importableItemsRepo.get(domainId, itemNo).get();
		}
		
		@Override
		public Optional<ExternalImportSetting> getSetting(String companyId, ExternalImportCode settingCode) {
			val require = new FromCsvBaseSettingToDomainRequireImpl(fileStorage);
			return externalImportSettingRepo.get(Optional.of(require), companyId, settingCode);
		}
	}
}
