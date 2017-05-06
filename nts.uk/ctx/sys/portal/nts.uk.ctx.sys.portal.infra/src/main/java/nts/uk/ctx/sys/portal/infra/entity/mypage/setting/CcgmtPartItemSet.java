package nts.uk.ctx.sys.portal.infra.entity.mypage.setting;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * The Class CcgmtPartItemSetting.
 */
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CCGMT_PART_ITEM_SET")
public class CcgmtPartItemSet extends UkJpaEntity {

	@EmbeddedId
	public CcgmtPartItemSetPK ccgmtPartItemSetPK;

	/** The part item name. */
	@Column(name = "PART_ITEM_NAME")
	public String partItemName;
	
	/** The use atr. */
	@Column(name = "USE_ATR")
	public int useAtr;

	@Override
	protected Object getKey() {
		return ccgmtPartItemSetPK;
	}

}
