package nts.uk.ctx.at.record.app.command.kdp.kdp010.a.command;

import java.util.Optional;

import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonType;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ReservationArt;

@Data
@NoArgsConstructor
public class ButtonTypeCommand {

	private Integer reservationArt;
	
	private StampTypeCommand stampType;

	public ButtonType toDomain() {
		return new ButtonType(ReservationArt.valueOf(this.reservationArt),
				this.reservationArt == 0 ? Optional.of(this.stampType.toDomain()) : Optional.empty());
	}
}
