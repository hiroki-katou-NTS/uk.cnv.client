package nts.uk.ctx.office.infra.entity.equipment.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDateTime;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class OfidtEquipmentDayAtdPK implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 利用者ID
	 */
	@NotNull
	@Column(name = "SID")
	private String sid;
	
	/**
	 * 入力日時
	 */
	@NotNull
	@Column(name = "INPUT_DATE")
	private GeneralDateTime inputDate;
}
