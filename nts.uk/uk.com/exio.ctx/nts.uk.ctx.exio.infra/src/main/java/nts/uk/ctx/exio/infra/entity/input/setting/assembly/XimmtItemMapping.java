package nts.uk.ctx.exio.infra.entity.input.setting.assembly;

import java.io.Serializable;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.layer.infra.data.jdbc.map.JpaEntityMapper;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.StringifiedValue;
import nts.uk.ctx.exio.dom.input.setting.assembly.mapping.ImportingItemMapping;
import nts.uk.ctx.exio.infra.entity.input.setting.XimmtDomainImportSetting;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 *  項目マッピング
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Table(name = "XIMMT_ITEM_MAPPING")
public class XimmtItemMapping extends ContractUkJpaEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private XimmtItemMappingPK pk;

	/* CSV列番号 */
	@Column(name = "IS_FIXED_VALUE")
	private boolean isFixedValue;

	/* CSV列番号 */
	@Column(name = "CSV_COLUMN_NO")
	private Integer csvColumnNo;
	
	/* 固定値 */
	@Column(name = "FIXED_VALUE")
	private String fixedValue;
	
	@ManyToOne
	@JoinColumns( {
		@JoinColumn(name = "CID", referencedColumnName = "CID", insertable = false, updatable = false),
		@JoinColumn(name = "SETTING_CODE", referencedColumnName = "SETTING_CODE", insertable = false, updatable = false),
		@JoinColumn(name = "DOMAIN_ID", referencedColumnName = "DOMAIN_ID", insertable = false, updatable = false)
	})
	public XimmtDomainImportSetting domainSetting;
	
	public static final JpaEntityMapper<XimmtItemMapping> MAPPER = new JpaEntityMapper<>(XimmtItemMapping.class);
	
	public XimmtItemMapping(XimmtItemMappingPK pk, boolean isFixedValue, Integer csvColumnNo, String fixedValue) {
		super();
		this.pk = pk;
		this.isFixedValue = isFixedValue;
		this.csvColumnNo = csvColumnNo;
		this.fixedValue = fixedValue;
	}
	
	@Override
	protected Object getKey() {
		return pk;
	} 
	
	public ImportingItemMapping toDomain(){
		return new ImportingItemMapping(
				pk.getItemNo(),
				isFixedValue,
				Optional.ofNullable(csvColumnNo),
				Optional.ofNullable(fixedValue).map(StringifiedValue::of));
	}
}
