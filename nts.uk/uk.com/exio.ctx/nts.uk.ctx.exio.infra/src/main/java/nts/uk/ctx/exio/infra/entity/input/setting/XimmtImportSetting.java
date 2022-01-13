package nts.uk.ctx.exio.infra.entity.input.setting;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.exio.dom.input.csvimport.ExternalImportCsvFileInfo;
import nts.uk.ctx.exio.dom.input.csvimport.ExternalImportRowNumber;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportName;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;
import nts.uk.ctx.exio.dom.input.setting.FromCsvBaseSettingToDomainRequire;
import nts.uk.ctx.exio.dom.input.setting.ImportSettingBaseType;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 *  受入設定
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Table(name = "XIMMT_IMPORT_SETTING")
public class XimmtImportSetting extends ContractUkJpaEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private XimmtImportSettingPK pk;

	/* 受入設定のベースタイプ */
	@Column(name = "BASE_TYPE")
	private int baseType;
	
	/* 受入設定名称 */
	@Column(name = "NAME")
	private String name;
		
	/* CSV項目名取得行 */
	@Column(name = "ITEM_NAME_ROW_NUMBER")
	private int itemNameRowNumber;
	
	/* CSV受入開始行 */
	@Column(name = "IMPORT_START_ROW_NUMBER")
	private int importStartRowNumber;
	
	/* CSVファイルID */
	@Column(name = "BASE_CSV_FILE_ID")
	private String baseCsvFileId;
	
	@Override
	protected Object getKey() {
		return pk;
	}
	
	/**
	 * @param require CSVベースの場合かつベースCSVを読む必要がある場合に指定する。通常実装クラスはFromCsvBaseSettingToDomainRequireImplを使用すること
	 * @return
	 */
	public ExternalImportSetting toDomain(Optional<FromCsvBaseSettingToDomainRequire> require) {
		ImportSettingBaseType type = ImportSettingBaseType.valueOf(this.baseType);

		return new ExternalImportSetting(
				type,
				this.pk.getCompanyId(), 
				new ExternalImportCode(this.pk.getCode()), 
				new ExternalImportName(this.name),
				new ExternalImportCsvFileInfo(
						new ExternalImportRowNumber(this.itemNameRowNumber),
						new ExternalImportRowNumber(this.importStartRowNumber),
						Optional.ofNullable(this.baseCsvFileId)),
				new HashMap<>());
	}
	
}
