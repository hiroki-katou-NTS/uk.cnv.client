package nts.uk.ctx.office.infra.entity.equipment;

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
public class OfidtEquipmentClsPK implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 契約コード
	 */
	@NotNull
	@Column(name = "CONTRACT_CD")
	private String contractCd;
	
	/**
	 * コード
	 */
	@NotNull
	@Column(name = "CODE")
	private String code;
}
