package nts.uk.ctx.office.infra.entity.equipment.achievement;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * UKDesign.データベース.ER図.オフィス支援.設備管理.実績項目設定.OFIMT_EQUIPMENT_DAY_RPT
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "OFIMT_EQUIPMENT_DAY_RPT")
public class OfimtEquipmentDayRpt extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 会社ID
	 */
	@Id
	@NotNull
	@Column(name = "CID")
	private String cid;
	
	/**
	 * 帳票タイトル
	 */
	@NotNull
	@Column(name = "RPT_TITLE")
	private String reportTitle;
	
	@Override
	protected Object getKey() {
		return this.cid;
	}

}
