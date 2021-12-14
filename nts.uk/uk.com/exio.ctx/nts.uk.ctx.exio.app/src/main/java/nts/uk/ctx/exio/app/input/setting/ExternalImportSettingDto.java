package nts.uk.ctx.exio.app.input.setting;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Value;
import lombok.val;
import nts.uk.ctx.exio.dom.input.canonicalize.ImportingMode;
import nts.uk.ctx.exio.dom.input.csvimport.ExternalImportCsvFileInfo;
import nts.uk.ctx.exio.dom.input.csvimport.ExternalImportRowNumber;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItem;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportName;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;
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
	
	/** レイアウト */
	private List<ExternalImportLayoutDto> layouts;
	
	public static ExternalImportSettingDto fromDomain(Require require, ExternalImportSetting domain) {
		
		return new ExternalImportSettingDto(
				domain.getCompanyId(), 
				domain.getCode().toString(), 
				domain.getName().toString(), 
				domain.getExternalImportDomainId().value, 
				domain.getImportingMode().value, 
				domain.getAssembly().getCsvFileInfo().getItemNameRowNumber().hashCode(), 
				domain.getAssembly().getCsvFileInfo().getImportStartRowNumber().hashCode(), 
				domain.getAssembly().getMapping().getMappings().stream()
					.map(m -> ExternalImportLayoutDto.fromDomain(require, domain.getExternalImportDomainId(), m))
				.collect(Collectors.toList()));
	}

	
	public ExternalImportSetting toDomain(Require require) {
		return new ExternalImportSetting(
				companyId, 
				new ExternalImportCode(code), 
				new ExternalImportName(name), 
				ImportingDomainId.valueOf(domain), 
				ImportingMode.valueOf(mode), 
				new ExternalImportAssemblyMethod(
						new ExternalImportCsvFileInfo(
								new ExternalImportRowNumber(itemNameRow), 
								new ExternalImportRowNumber(importStartRow)), 
						new ImportingMapping(createMappings(require))));
	}
	
	private List<ImportingItemMapping> createMappings(Require require){
		val optRegisteredSetting = require.getSetting(AppContexts.user().companyId(), new ExternalImportCode(code));
		if(optRegisteredSetting.isPresent()) {
			val mappings = optRegisteredSetting.get().getAssembly().getMapping().getMappings();
			if(mappings.size() > 0) {
				return mappings;
			}
		}
		return layouts.stream()
				.map(l -> new ImportingItemMapping(l.itemNo, null, null))
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
		val optFixedValue = mapping.getFixedValue();
		if(optCsvColumnNo.isPresent()){
			return "CSV";
		}
		else if(optFixedValue.isPresent()){
			return "固定値";
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
