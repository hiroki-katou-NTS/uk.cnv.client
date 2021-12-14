package nts.uk.ctx.at.function.dom.adapter.holidaysremaining;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;


@Getter
@Setter
@AllArgsConstructor
public class AnnLeaGrantNumberImported {
	
	/** 付与年月日 */
	private GeneralDate grantDate;
	/** 付与数 */
	private Double grantDays;
}
