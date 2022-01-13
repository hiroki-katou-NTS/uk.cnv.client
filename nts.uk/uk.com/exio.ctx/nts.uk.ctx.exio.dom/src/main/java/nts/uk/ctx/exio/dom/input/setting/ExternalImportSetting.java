package nts.uk.ctx.exio.dom.input.setting;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.objecttype.DomainAggregate;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.csvimport.ExternalImportCsvFileInfo;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.setting.assembly.ExternalImportAssemblyMethod;

/**
 * 受入設定
 */
@Getter
public class ExternalImportSetting implements DomainAggregate {
	/**設定ベース種類*/
	private ImportSettingBaseType baseType;

	/** 会社ID */
	private String companyId;

	/** 受入設定コード */
	private ExternalImportCode code;

	/** 受入設定名称 */
	@Setter
	private ExternalImportName name;

	/** CSVファイル情報 */
	@Setter
	private ExternalImportCsvFileInfo csvFileInfo;

	/** ドメイン受入設定 **/
	private Map<ImportingDomainId, DomainImportSetting> domainSettings;

	public ExternalImportSetting(
			ImportSettingBaseType baseType,
			String companyId,
			ExternalImportCode code,
			ExternalImportName name,
			ExternalImportCsvFileInfo csvFileInfo,
			Map<ImportingDomainId, DomainImportSetting> domainSettings) {

		this.baseType = baseType;
		this.companyId = companyId;
		this.code = code;
		this.name = name;
		this.csvFileInfo = csvFileInfo;
		this.domainSettings = domainSettings;
	}
	
	public List<DomainImportSetting> getDomainSettings() {
		return this.domainSettings.values().stream()
				.sorted((ds1, ds2) -> ds1.getDomainId().compareTo(ds2.getDomainId()))
				.collect(Collectors.toList());
	}

	public void assemble(DomainImportSetting.RequireAssemble require, ExecutionContext context, InputStream csvFileStream) {
		domainSettings.forEach((domainId, setting) -> {
			setting.assemble(require, context, csvFileInfo, csvFileStream);
		});
	}
	
	public static interface RequireMerge extends DomainImportSetting.RequireMerge {
	}
	public static interface RequireAssemble extends DomainImportSetting.RequireAssemble {
	}

	public ExternalImportAssemblyMethod getAssembly(ImportingDomainId domainId) {
		return getDomainSetting(domainId).get().getAssembly();
	}
	
	public Optional<DomainImportSetting> getDomainSetting(ImportingDomainId domain) {
		if (baseType == ImportSettingBaseType.DOMAIN_BASE)  return this.domainSettings.entrySet().stream().findFirst().map(es-> es.getValue());

		return this.domainSettings.containsKey(domain)
				? Optional.of(this.domainSettings.get(domain))
				: Optional.empty();
	}

	public void putDomainSettings(ImportingDomainId domainId, DomainImportSetting domainImportSetting) {
		this.domainSettings.put(domainId, domainImportSetting);
	}
}
