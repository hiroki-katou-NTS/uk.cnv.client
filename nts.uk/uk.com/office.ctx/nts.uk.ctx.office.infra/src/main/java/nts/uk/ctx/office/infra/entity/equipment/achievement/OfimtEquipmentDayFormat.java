package nts.uk.ctx.office.infra.entity.equipment.achievement;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * UKDesign.データベース.ER図.オフィス支援.設備管理.実績項目設定.OFIMT_EQUIPMENT_DAY_FORMAT
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "OFIMT_EQUIPMENT_DAY_FORMAT")
public class OfimtEquipmentDayFormat extends ContractUkJpaEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private OfimtEquipmentDayFormatPK pk;
	
	/**
	 * 表示順番
	 */
	@NotNull
	@Column(name = "DISPLAY_ORDER")
	private int displayOrder;
	
	/**
	 * 表示幅
	 */
	@NotNull
	@Column(name = "DISPLAY_WIDTH")
	private int displayWidth;
	
	@Override
	protected Object getKey() {
		return pk;
	}

}
