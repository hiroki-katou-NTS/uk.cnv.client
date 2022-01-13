package nts.uk.screen.at.app.kdw013.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 
 * @author sonnlb
 *
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class TimeSpanForCalcCommand {

	private int start;
	private int end;

	public static TimeSpanForCalc toDomain(TimeSpanForCalcCommand domain) {

		return new TimeSpanForCalc(new TimeWithDayAttr(domain.getStart()), new TimeWithDayAttr(domain.getEnd()));
	}

}
