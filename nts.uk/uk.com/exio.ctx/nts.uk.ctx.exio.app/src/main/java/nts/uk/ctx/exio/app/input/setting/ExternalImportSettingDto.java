package nts.uk.ctx.exio.app.input.setting;

import java.util.*;
import java.util.stream.Collectors;

import lombok.Value;
import lombok.val;
import nts.uk.ctx.exio.dom.input.canonicalize.ImportingMode;
import nts.uk.ctx.exio.dom.input.csvimport.ExternalImportCsvFileInfo;
import nts.uk.ctx.exio.dom.input.csvimport.ExternalImportRowNumber;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItem;
import nts.uk.ctx.exio.dom.input.setting.DomainImportSetting;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportName;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;
import nts.uk.ctx.exio.dom.input.setting.ImportSettingBaseType;
import nts.uk.ctx.exio.dom.input.setting.assembly.ExternalImportAssemblyMethod;
import nts.uk.ctx.exio.dom.input.setting.assembly.mapping.ImportingItemMapping;
import nts.uk.ctx.exio.dom.input.setting.assembly.mapping.ImportingMapping;
import nts.uk.shr.com.context.AppContexts;

@Value
public class ExternalImportSettingDto {
	
	/** 会社ID */
	private String companyId;
	
	/** 受入設定コード */
	private String code;
	
	/** 受入設定名称 */
	private String name;
	
	/** 受入グループID */
	private int domain;
	
	/** 受入モード */
	private int mode;
	
	/** CSVの項目名取得行 */
	private int itemNameRow;
	
	/** CSVの取込開始行 */
	private int importStartRow;
	
	private String baseFileId;
	
	/** レイアウト */
	private List<ExternalImportLayoutDto> layouts;
	
	public static ExternalImportSettingDto fromDomain(Require require, ExternalImportSetting domain, DomainImportSetting domainSetting) {
		return new ExternalImportSettingDto(
				domain.getCompanyId(), 
				domain.getCode().toString(), 
				domain.getName().toString(), 
				domainSetting.getDomainId().value, 
				domainSetting.getImportingMode().value, 
				domain.getCsvFileInfo().getItemNameRowNumber().hashCode(), 
				domain.getCsvFileInfo().getImportStartRowNumber().hashCode(),
				domain.getCsvFileInfo().getBaseCsvFileId().orElse(""),
				domainSetting.getAssembly().getMapping().getMappings().stream()
					.map(m -> ExternalImportLayoutDto.fromDomain(require, domainSetting.getDomainId(), m))
					.sorted(Comparator.comparing(ExternalImportLayoutDto::getItemNo))
					.collect(Collectors.toList()));
	}

	
	public ExternalImportSetting toDomain(Require require) {
		DomainImportSetting domainSetting = new DomainImportSetting (
				ImportingDomainId.valueOf(domain), 
				ImportingMode.valueOf(mode), 
				new ExternalImportAssemblyMethod(
						new ImportingMapping(createMappings(require))));
		Map<ImportingDomainId, DomainImportSetting> domainSettings = new HashMap<>();
		domainSettings.put(ImportingDomainId.valueOf(domain), domainSetting);

		return new ExternalImportSetting(
				ImportSettingBaseType.DOMAIN_BASE,
				companyId, 
				new ExternalImportCode(code), 
				new ExternalImportName(name), 
				new ExternalImportCsvFileInfo(
						new ExternalImportRowNumber(itemNameRow), 
						new ExternalImportRowNumber(importStartRow),
						Optional.empty()),
				domainSettings);
	}

	private List<ImportingItemMapping> createMappings(Require require){
		val optRegisteredSetting = require.getSetting(AppContexts.user().companyId(), new ExternalImportCode(code));
		if(optRegisteredSetting.isPresent()) {
			val mappings = optRegisteredSetting.get().getDomainSettings().get(0).getAssembly().getMapping().getMappings();
			if(mappings.size() > 0) {
				return mappings;
			}
		}
		return layouts.stream()
				.map(l -> new ImportingItemMapping(l.itemNo, true, Optional.empty(), Optional.empty()))
				.collect(Collectors.toList());
	}
	
	@Value
	private static class ExternalImportLayoutDto {
		
		/** 項目NO */
		private int itemNo;
		
		/** 項目名 */
		private String name;
		
		/** 項目型 */
		private String type;
		
		/** 受入元 */
		private String source;
		
		/** 固定値 */
		private String fixedValue;
		
		/** 詳細設定の有無 */
		private boolean alreadyDetail;

		static ExternalImportLayoutDto fromDomain(
				Require require,
				ImportingDomainId domainId,
				ImportingItemMapping domain) {
			return new ExternalImportLayoutDto(
					domain.getItemNo(), 
					require.getImportableItems(domainId, domain.getItemNo()).getItemName(),
					require.getImportableItems(domainId, domain.getItemNo()).getItemType().name(),
					checkImportSource(domain),
					domain.getFixedValue().map(f -> f.asString()).orElse(""),
					domain.isConfigured());
		}
	}
	
	private static String checkImportSource(ImportingItemMapping mapping) {
		val optCsvColumnNo = mapping.getCsvColumnNo();
		if(mapping.isFixedValue()){
			return "固定値";
		}
		else if(optCsvColumnNo.isPresent()){
			return "CSV";
		}
		else {
			return "未設定";
		}
	}
	
	public static interface Require {
		ImportableItem getImportableItems(ImportingDomainId domainId, int itemNo);
		Optional<ExternalImportSetting> getSetting(String companyId, ExternalImportCode settingCode);
	}
}
