package nts.uk.screen.com.app.cmf.cmf001.b.get;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nts.arc.diagnose.stopwatch.embed.EmbedStopwatch;
import nts.arc.layer.app.file.storage.FileStorage;
import nts.uk.ctx.exio.app.input.setting.FromCsvBaseSettingToDomainRequireImpl;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItem;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItemsRepository;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSettingRepository;
import nts.uk.screen.com.app.cmf.cmf001.b.get.GetLayout.Require;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class GetLayoutRequire {

	@Inject
	private ImportableItemsRepository importableItemsRepo;

	@Inject
	private ExternalImportSettingRepository externalImportSettingRepo;

	@Inject
	private FileStorage fileStorage;

	public Require create() {

		return EmbedStopwatch.embed(new RequireImpl());
	}

	@RequiredArgsConstructor
	public class RequireImpl implements Require {

		@Override
		public Optional<ExternalImportSetting> getSetting(String companyId, ExternalImportCode settingCode) {
			val require = new FromCsvBaseSettingToDomainRequireImpl(fileStorage);
			return externalImportSettingRepo.get(Optional.of(require), companyId, settingCode);
		}

		@Override
		public List<ImportableItem> getImportableItems(ImportingDomainId domainId) {
			return importableItemsRepo.get(domainId);
		}

	}

}
