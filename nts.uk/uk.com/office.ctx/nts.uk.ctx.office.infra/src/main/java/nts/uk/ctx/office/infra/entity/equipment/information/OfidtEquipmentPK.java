package nts.uk.ctx.office.infra.entity.equipment.information;

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
public class OfidtEquipmentPK implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 会社ID
	 */
	@NotNull
	@Column(name = "CID")
	private String cid;
	
	/**
	 * コード
	 */
	@NotNull
	@Column(name = "CODE")
	private String code;
}
