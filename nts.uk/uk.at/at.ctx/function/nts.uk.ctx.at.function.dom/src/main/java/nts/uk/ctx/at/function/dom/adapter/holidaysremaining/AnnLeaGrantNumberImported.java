package nts.uk.ctx.at.function.dom.adapter.holidaysremaining;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveGrantNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingNumber;


@Getter
@Setter
@AllArgsConstructor
public class AnnLeaGrantNumberImported {
	
	/** 付与年月日 */
	private GeneralDate grantDate;
	/** 付与数 */
	private Double grantDays;
	/**
	 * 残日数
	 */
	private Double remainDay;
	/**
	 * 残時間
	 */
	private Double remainTime;
	/**
	 * 付与時間
	 */
	private Integer grantTime;

	public AnnLeaGrantNumberImported(GeneralDate grantDate,Double grantDays){
		this.grantDate = grantDate;
		this.grantDays = grantDays;
	}

}
