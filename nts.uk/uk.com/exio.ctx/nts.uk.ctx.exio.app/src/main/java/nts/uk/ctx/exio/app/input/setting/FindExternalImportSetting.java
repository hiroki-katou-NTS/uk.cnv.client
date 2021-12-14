package nts.uk.ctx.exio.app.input.setting;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nts.arc.diagnose.stopwatch.embed.EmbedStopwatch;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItem;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItemsRepository;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSettingRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class FindExternalImportSetting {
	
	public List<ExternalImportSettingListItemDto> findAll() {
		val settings = externalImportSettingRepo.getAll(AppContexts.user().companyId());
		return ExternalImportSettingListItemDto.fromDomain(settings);
	}
	
	public ExternalImportSettingDto find(String settingCode) {
		val require = this.createRequire();
		val settingOpt = require.getSetting(AppContexts.user().companyId(), new ExternalImportCode(settingCode));
		return ExternalImportSettingDto.fromDomain(require, settingOpt.get());
	}
	
	public Require createRequire() {
		return EmbedStopwatch.embed(new RequireImpl());
	}

	public static interface Require extends ExternalImportSettingDto.Require {
	}
	
	@Inject
	private ImportableItemsRepository importableItemsRepo;
	
	@Inject
	private ExternalImportSettingRepository externalImportSettingRepo;
	
	@RequiredArgsConstructor
	public class RequireImpl implements Require {
		
		@Override
		public ImportableItem getImportableItems(ImportingDomainId domainId, int itemNo) {
			return importableItemsRepo.get(domainId, itemNo).get();
		}
		
		@Override
		public Optional<ExternalImportSetting> getSetting(String companyId, ExternalImportCode settingCode) {
			return externalImportSettingRepo.get(companyId, settingCode);
		}
	}
}
