package nts.uk.ctx.at.record.app.command.stamp.management;

import java.util.Optional;

import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonType;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ReservationArt;

/**
 * 
 * @author phongtq
 *
 */
@Data
@NoArgsConstructor
public class ButtonTypeCommand {
	/** 予約区分 */
	private int reservationArt;
	
	/** 打刻種類 */
	private StampTypeCommand stampType;

	public ButtonTypeCommand(int reservationArt, StampTypeCommand stampType) {
		super();
		this.reservationArt = reservationArt;
		this.stampType = stampType;
	}
	
	public ButtonType toDomain() {
		return new ButtonType(ReservationArt.valueOf(this.reservationArt), this.stampType == null? Optional.empty(): Optional.of(stampType.toDomain()));
	}
}
