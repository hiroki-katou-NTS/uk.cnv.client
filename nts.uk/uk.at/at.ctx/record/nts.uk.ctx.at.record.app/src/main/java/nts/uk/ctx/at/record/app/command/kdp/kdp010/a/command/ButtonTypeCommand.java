package nts.uk.ctx.at.record.app.command.kdp.kdp010.a.command;

import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampType;

@Data
@NoArgsConstructor
public class ButtonTypeCommand {

	private Integer reservationArt;
	
	private StampTypeCommand stampType;

	public StampType toDomain() {
		return stampType.toDomain();
	}
}
