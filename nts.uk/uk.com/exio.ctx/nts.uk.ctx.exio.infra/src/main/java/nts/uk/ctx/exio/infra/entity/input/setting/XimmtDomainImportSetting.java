package nts.uk.ctx.exio.infra.entity.input.setting;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.exio.dom.input.canonicalize.ImportingMode;
import nts.uk.ctx.exio.dom.input.csvimport.ExternalImportCsvFileInfo;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.setting.DomainImportSetting;
import nts.uk.ctx.exio.dom.input.setting.assembly.ExternalImportAssemblyMethod;
import nts.uk.ctx.exio.dom.input.setting.assembly.mapping.ImportingMapping;
import nts.uk.ctx.exio.infra.entity.input.setting.assembly.XimmtItemMapping;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 *  受入設定
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Table(name = "XIMMT_DOMAIN_IMPORT_SETTING")
public class XimmtDomainImportSetting extends ContractUkJpaEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private XimmtDomainImportSettingPK pk; 
	
	/* 受入モード */
	@Column(name = "IMPORTING_MODE")
	private int importingMode;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="domainSetting", orphanRemoval = true)
	@OrderBy(value = "pk.itemNo asc")
	public List<XimmtItemMapping> mappings;
	
	@Override
	protected Object getKey() {
		return pk;
	}
	
	public DomainImportSetting toDomain(ExternalImportCsvFileInfo csvFileInfo) {
		return new DomainImportSetting(
				ImportingDomainId.valueOf(pk.getDomainId()),
				EnumAdaptor.valueOf(importingMode, ImportingMode.class), 
				new ExternalImportAssemblyMethod(
						new ImportingMapping(mappings.stream()
								.map(m -> m.toDomain())
								.collect(Collectors.toList()))));
	}
}
