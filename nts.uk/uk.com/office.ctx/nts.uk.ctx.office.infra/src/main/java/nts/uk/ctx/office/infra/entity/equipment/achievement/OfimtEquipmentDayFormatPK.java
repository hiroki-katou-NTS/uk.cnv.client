package nts.uk.ctx.office.infra.entity.equipment.achievement;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class OfimtEquipmentDayFormatPK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 会社ID
	 */
	@NotNull
	@Column(name = "CID")
	private String cid;
	
	/**
	 * 項目NO
	 */
	@NotNull
	@Column(name = "ITEM_NO")
	private Integer itemNo;
	
}
