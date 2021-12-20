package nts.uk.screen.com.app.cmf.cmf001.b.get;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Value;
import nts.uk.ctx.exio.dom.input.canonicalize.ImportingMode;
import nts.uk.ctx.exio.dom.input.csvimport.ExternalImportCsvFileInfo;
import nts.uk.ctx.exio.dom.input.csvimport.ExternalImportRowNumber;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.setting.DomainImportSetting;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportName;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;
import nts.uk.ctx.exio.dom.input.setting.FromCsvBaseSettingToDomainRequire;
import nts.uk.ctx.exio.dom.input.setting.ImportSettingBaseType;
import nts.uk.ctx.exio.dom.input.setting.assembly.ExternalImportAssemblyMethod;
import nts.uk.shr.com.context.AppContexts;

@Value
public class ExternalImportSettingDto {

	/** 会社ID */
	private String companyId;

	/** 受入設定コード */
	private String code;

	/** 受入設定名称 */
	private String name;

	/** CSVの項目名取得行 */
	private int itemNameRow;

	/** CSVの取込開始行 */
	private int importStartRow;
	
	private String csvFileId;
	
	private List<ImportDomainDto> domains;
	
	private List<String> csvItems;

	public static interface RequireMerge extends
		ExternalImportSetting.RequireMerge{
	}

	public static ExternalImportSettingDto fromDomain(FromCsvBaseSettingToDomainRequire toDomainRequire, ExternalImportSetting setting) {

		List<ImportDomainDto> domains = setting.getDomainSettings().stream()
			.map(ds -> new ImportDomainDto(ds.getDomainId().value, ds.getAssembly().getAllItemNo()))
			.collect(Collectors.toList());

		List<String> items = (setting.getBaseType() != ImportSettingBaseType.CSV_BASE || !setting.getCsvFileInfo().getBaseCsvFileId().isPresent())
				? new ArrayList<String>()
				:  toDomainRequire.createBaseCsvInfo(setting.getCsvFileInfo()).orElse(new ArrayList<String>());
		
		return new ExternalImportSettingDto(
				setting.getCompanyId(),
				setting.getCode().toString(),
				setting.getName().toString(),
				setting.getCsvFileInfo().getItemNameRowNumber().hashCode(),
				setting.getCsvFileInfo().getImportStartRowNumber().hashCode(),
				setting.getCsvFileInfo().getBaseCsvFileId().orElse(""),
				domains,
				items);
	}

	public ExternalImportSetting toDomain(ImportSettingBaseType baseType) {
		Map<ImportingDomainId, DomainImportSetting> domainSettings = this.domains.stream()
			.map(d -> new DomainImportSetting(
				ImportingDomainId.valueOf(d.getDomainId()),
				ImportingMode.DELETE_RECORD_BEFOREHAND,
				ExternalImportAssemblyMethod.create(d.getItemNoList())))
			.collect(Collectors.toMap(DomainImportSetting::getDomainId, d -> d));
		
		return new ExternalImportSetting(
				baseType,
				AppContexts.user().companyId(),
				new ExternalImportCode(code),
				new ExternalImportName(name),
				toCsvFileInfo(),
				domainSettings);
	}

	private ExternalImportCsvFileInfo toCsvFileInfo() {
		return new ExternalImportCsvFileInfo(
				new ExternalImportRowNumber(itemNameRow),
				new ExternalImportRowNumber(importStartRow),
				 Optional.ofNullable(this.csvFileId));
	}

}
