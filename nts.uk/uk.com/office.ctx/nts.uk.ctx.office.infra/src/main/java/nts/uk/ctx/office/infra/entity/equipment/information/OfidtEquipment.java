package nts.uk.ctx.office.infra.entity.equipment.information;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformation;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * UKDesign.データベース.ER図.オフィス支援.設備管理.OFIDT_EQUIPMENT
 * 設備情報
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "OFIDT_EQUIPMENT")
public class OfidtEquipment extends ContractUkJpaEntity implements Serializable,
									EquipmentInformation.MementoSetter,
									EquipmentInformation.MementoGetter {

	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private OfidtEquipmentPK pk;
	
	@Column(name = "EXCLUS_VER")
	private long version;
	
	/**
	 * 名称
	 */
	@NotNull
	@Column(name = "NAME")
	private String name;
	
	/**
	 * 有効開始日
	 */
	@NotNull
	@Column(name = "EXP_START_DATE")
	private GeneralDate effectiveStartDate;
	
	/**
	 * 有効終了日
	 */
	@NotNull
	@Column(name = "EXP_END_DATE")
	private GeneralDate effectiveEndDate;
	
	/**
	 * 備分類コード
	 */
	@NotNull
	@Column(name = "EQUIPMENT_CLS_CODE")
	private String equipmentClsCode;
	
	/**
	 * 備考
	 */
	@Basic(optional = true)
	@Column(name = "REMARK")
	private String remark;

	@Override
	protected Object getKey() {
		return this.pk;
	}

	@Override
	public String getCode() {
		if (this.pk != null) {
			return this.pk.getCode();
		}
		return null;
	}

	@Override
	public void setCode(String code) {
		if (this.pk == null) {
			this.pk = new OfidtEquipmentPK();
		}
		this.pk.setCode(code);
	}
}
