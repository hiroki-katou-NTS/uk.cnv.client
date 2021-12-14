package nts.uk.ctx.exio.infra.entity.input.revise;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.ReviseItem;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Embeddable
public class XimmtReviseItemPK implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Column(name = "CID")
	private String companyId;
	
	@Column(name = "SETTING_CODE")
	private String settingCode;
	
	@Column(name = "ITEM_NO")
	private int itemNo;
	
	public static XimmtReviseItemPK of(ReviseItem domain) {
		return new XimmtReviseItemPK(domain.getCompanyId(), domain.getSettingCode().v(), domain.getItemNo());
	}
}
