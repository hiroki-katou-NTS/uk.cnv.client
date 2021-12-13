package nts.uk.screen.com.app.cmf.cmf001.f.get;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.file.storage.FileStorage;
import nts.uk.ctx.exio.app.input.setting.FromCsvBaseSettingToDomainRequireImpl;
import nts.uk.ctx.exio.dom.input.csvimport.ExternalImportCsvFileInfo;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItem;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItemsRepository;
import nts.uk.ctx.exio.dom.input.setting.DomainImportSetting;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSettingRepository;
import nts.uk.ctx.exio.dom.input.setting.assembly.mapping.ImportingItemMapping;
import nts.uk.screen.com.app.cmf.cmf001.b.get.GetLayoutParam;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class GetLayoutForCsvBase {
	@Inject
	private ExternalImportSettingRepository settingRepo;

	@Inject
	private ImportableItemsRepository importableItemsRepo;

	@Inject
	private FileStorage fileStorage;
	
	public List<CsvBaseLayoutDto> getCsvBaseDetail(GetLayoutParam query) {
		if (query.isAllItem()) {
			return getAll(query);
		} else {
			return getSpecified(query);
		}
	}
	
	// すべての項目のレイアウトを取得する
	private List<CsvBaseLayoutDto> getAll(GetLayoutParam query) {

		val require = new FromCsvBaseSettingToDomainRequireImpl(fileStorage);
		val settingOpt = settingRepo.get(Optional.of(require), AppContexts.user().companyId(), query.getSettingCode());
		if (settingOpt.isPresent()) {
 			val setting = settingOpt.get();
			Optional<DomainImportSetting> domainSetting = setting.getDomainSetting(query.getImportingDomainId());
			return getSaved(query, domainSetting, setting.getCsvFileInfo());
		}
		else {
			return getAllImportables(query);
		}
	}
	
	// 指定した項目のレイアウトを取得する
	private List<CsvBaseLayoutDto> getSpecified(GetLayoutParam query) {
		
		val results = new ArrayList<CsvBaseLayoutDto>();

		val require = new FromCsvBaseSettingToDomainRequireImpl(fileStorage);
		val settingOpt = settingRepo.get(Optional.of(require), AppContexts.user().companyId(), query.getSettingCode());
		if (settingOpt.isPresent()) {
 			val setting = settingOpt.get();
			Optional<DomainImportSetting> domainSetting = setting.getDomainSetting(query.getImportingDomainId());
			results.addAll(
					getSaved(query, domainSetting, setting.getCsvFileInfo()
				).stream()
					.filter(s -> query.getItemNoList().contains(s.getItemNo()))
					.collect(toList()));
		}
		
		val savedItemNos = results.stream().map(l -> l.getItemNo()).collect(Collectors.toSet());
		
		// 指定した項目のうち未登録の項目	
		val unsavedItemNos = query.getItemNoList().stream()
				.filter(n -> !savedItemNos.contains(n))
				.collect(Collectors.toSet());
		
		val unsavedLayouts = getAllImportables(query).stream()
				.filter(l -> unsavedItemNos.contains(l.getItemNo()))
				.collect(toList());
		
		results.addAll(unsavedLayouts);
		
		return results;
	}
	
	private List<CsvBaseLayoutDto> getAllImportables(GetLayoutParam query) {
		
		val importableItems = importableItemsRepo.get(query.getImportingDomainId());
		
		return importableItems.stream()
				.map(i -> CsvBaseLayoutDto.fromDomain(
						i,
						query.getSettingCode(),
						query.getImportingDomainId(),
						ImportingItemMapping.noSetting(i.getItemNo()),
						""))
				.collect(Collectors.toList());
	}
	
	private List<CsvBaseLayoutDto> getSaved(
			GetLayoutParam query,
			Optional<DomainImportSetting> setting,
			ExternalImportCsvFileInfo fileInfo) {
		List<ImportingItemMapping> mappings = setting.isPresent()
			? setting.get().getAssembly().getMapping().getMappings()
			: new ArrayList<>();
		return toLayouts(query, mappings, fileInfo);
	}
	
	private List<CsvBaseLayoutDto> toLayouts(GetLayoutParam query,
			List<ImportingItemMapping> mappings,
			ExternalImportCsvFileInfo fileInfo) {

		val require = new FromCsvBaseSettingToDomainRequireImpl(fileStorage);
		val importableItems = importableItemsRepo.get(query.getImportingDomainId()).stream()
				.collect(Collectors.groupingBy(ImportableItem::getItemNo));
		Map<Integer, List<String>> csvData = require.readBaseCsvWithFirstData(fileInfo);

		return mappings.stream()
				.map(i -> CsvBaseLayoutDto.fromDomain(
						importableItems.get(i.getItemNo()).get(0),
						query.getSettingCode(),
						query.getImportingDomainId(),
						new ImportingItemMapping(i.getItemNo(), i.isFixedValue(),i.getCsvColumnNo(), i.getFixedValue()),
						( i.getCsvColumnNo().isPresent()
								? csvData.get( i.getCsvColumnNo().get() - 1).get(1)
								: "")
						))
				.collect(Collectors.toList());
	}

}
