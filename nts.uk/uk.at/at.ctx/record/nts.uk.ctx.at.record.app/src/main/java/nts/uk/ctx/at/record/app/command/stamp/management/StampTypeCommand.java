package nts.uk.ctx.at.record.app.command.stamp.management;

import java.util.Optional;

import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeCalArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SetPreClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampType;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
/**
 * 
 * @author phongtq
 *
 */
@Data
@NoArgsConstructor
public class StampTypeCommand {
	/** 勤務種類を半休に変更する */
	private boolean changeHalfDay;

	/** 外出区分 */
	private Integer goOutArt;
	
	/** 所定時刻セット区分 */
	private Integer setPreClockArt;
	
	/** 時刻変更区分 */
	private Integer changeClockArt;
	
	/** 計算区分変更対象 */
	private Integer changeCalArt;

	public StampTypeCommand(boolean changeHalfDay, Integer goOutArt, Integer setPreClockArt, Integer changeClockArt,
			Integer changeCalArt) {
		this.changeHalfDay = changeHalfDay;
		this.goOutArt = goOutArt;
		this.setPreClockArt = setPreClockArt;
		this.changeClockArt = changeClockArt;
		this.changeCalArt = changeCalArt;
	}
	
	public StampType toDomain() {
		return new StampType(
				this.changeHalfDay,
				this.goOutArt == null ? Optional.empty():Optional.of(GoingOutReason.valueOf(this.goOutArt)), 
				SetPreClockArt.valueOf(this.setPreClockArt), 
				ChangeClockArt.valueOf(changeClockArt), 
				ChangeCalArt.valueOf(this.changeCalArt));
	}
}
