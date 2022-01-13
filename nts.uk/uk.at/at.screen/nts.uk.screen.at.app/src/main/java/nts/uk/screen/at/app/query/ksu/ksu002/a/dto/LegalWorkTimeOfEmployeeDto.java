package nts.uk.screen.at.app.query.ksu.ksu002.a.dto;

import java.util.Optional;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.LegalWorkTimeOfEmployee;

/**
 * 社員の法定労働時間Dto
 * @author thanhPV
 *
 */
@NoArgsConstructor
@Getter
public class LegalWorkTimeOfEmployeeDto {
	
	/** 社員ID */
	private String sid;
	/** 週の時間 */
	private Integer weeklyEstimateTime = 0;
	/** 月の時間 */
	private Integer monthlyEstimateTime = 0;
	
	public void setValue(Optional<LegalWorkTimeOfEmployee> domain) {
		domain.ifPresent(e -> {
			this.sid = e.getSid();
			e.getWeeklyEstimateTime().ifPresent(d-> {
				this.weeklyEstimateTime = d.valueAsMinutes();
			});
			this.monthlyEstimateTime = e.getMonthlyEstimateTime().v();
		});
	}

}
