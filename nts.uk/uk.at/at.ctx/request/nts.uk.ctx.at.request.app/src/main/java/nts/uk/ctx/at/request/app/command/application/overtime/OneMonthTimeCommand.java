package nts.uk.ctx.at.request.app.command.application.overtime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.AgreementOneMonthTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.OneMonthTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OneMonthTimeCommand {
	
	public OneMonthErrorAlarmTimeCommand erAlTime;
	
	public Integer upperLimit;
	
	
	public OneMonthTime toDomain() {
		
		return OneMonthTime.createWithCheck(
				erAlTime.toDomain(),
				new AgreementOneMonthTime(upperLimit));
	}
}
