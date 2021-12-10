package nts.uk.ctx.exio.infra.entity.input.revise.type.codeconvert;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.exio.infra.entity.input.revise.XimmtReviseItemPK;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Embeddable
public class XimmtCodeConvertDetailPK implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name = "CID")
	private String companyId;
	
	@Column(name = "SETTING_CODE")
	private String settingCode;

	/* 受入グループID */
	@Column(name = "DOMAIN_ID")
	private int domainId;
	
	@Column(name = "ITEM_NO")
	private int itemNo;
	
	@Column(name = "TARGET_CODE")
	private String targetCode;
	
	public static XimmtCodeConvertDetailPK of(XimmtReviseItemPK parentPk, String targetCode) {
		return new XimmtCodeConvertDetailPK(
				parentPk.getCompanyId(),
				parentPk.getSettingCode(),
				parentPk.getDomainId(),
				parentPk.getItemNo(),
				targetCode);
	}
}
