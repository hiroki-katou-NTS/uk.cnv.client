package nts.uk.ctx.office.infra.entity.equipment.achievement;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * UKDesign.データベース.ER図.オフィス支援.設備管理.実績項目設定.OFIMT_EQUIPMENT_DAY_ITEM
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "OFIMT_EQUIPMENT_DAY_ITEM")
public class OfimtEquipmentDayItem extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private OfimtEquipmentDayItemPK pk;

	/**
	 * 項目名称
	 */
	@NotNull
	@Column(name = "ITEM_NAME")
	private String itemName;
	
	/**
	 * 項目分類
	 */
	@NotNull
	@Column(name = "ITEM_CLS")
	private int itemCls;
	
	/**
	 * 必須
	 */
	@NotNull
	@Column(name = "REQUIRE")
	private int require;
	
	/**
	 * 最大値
	 */
	@Basic(optional = true)
	@Column(name = "MIN_VALUE")
	private Integer minValue;
	
	/**
	 * 最小値
	 */
	@Basic(optional = true)
	@Column(name = "MAX_VALUE")
	private Integer maxValue;
	
	/**
	 * 桁数
	 */
	@Basic(optional = true)
	@Column(name = "ITEM_LENGTH")
	private String itemLength;
	
	/**
	 * 単位
	 */
	@Basic(optional = true)
	@Column(name = "UNIT")
	private String unit;
	
	/**
	 * 説明
	 */
	@Basic(optional = true)
	@Column(name = "MEMO")
	private String memo;

	@Override
	protected Object getKey() {
		return this.pk;
	}
}
