package nts.uk.ctx.at.record.app.command.kdp.kdp010.a.command;

import java.util.Optional;

import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeCalArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockAtr;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SetPreClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampType;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;

/**
 * 
 * @author ThanhPV
 *
 */
@Data
@NoArgsConstructor
public class StampTypeCommand {

	private Boolean changeHalfDay;

	private Integer goOutArt;

	private Integer setPreClockArt;

	private Integer changeClockArt;

	private Integer changeCalArt;

	public StampType toDomain() {
		return new StampType(
				this.changeHalfDay, 
				Optional.ofNullable(this.goOutArt == null? null: GoingOutReason.valueOf(this.goOutArt)), 
				SetPreClockArt.valueOf(this.setPreClockArt), 
				ChangeClockAtr.valueOf(this.changeClockArt), 
				ChangeCalArt.valueOf(this.changeCalArt));
	}
}
