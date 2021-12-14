package nts.uk.screen.at.ws.schedule.scheduleteam;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Ksu001LaRequest {
	private String baseDate;
	private String workplaceGroupId;

	public GeneralDate toDate() {
		return GeneralDate.fromString(baseDate, "yyyy/MM/dd");
	}
}
