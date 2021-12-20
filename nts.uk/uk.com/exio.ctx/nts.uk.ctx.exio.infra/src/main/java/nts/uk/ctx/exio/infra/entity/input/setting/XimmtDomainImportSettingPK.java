package nts.uk.ctx.exio.infra.entity.input.setting;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Embeddable
public class XimmtDomainImportSettingPK implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/* 会社ID */
	@Column(name = "CID")
	private String companyId;
	
	/* 受入設定コード */
	@Column(name = "SETTING_CODE")
	private String code;
	
	/* 受入グループID */
	@Column(name = "DOMAIN_ID")
	private int domainId;
}
