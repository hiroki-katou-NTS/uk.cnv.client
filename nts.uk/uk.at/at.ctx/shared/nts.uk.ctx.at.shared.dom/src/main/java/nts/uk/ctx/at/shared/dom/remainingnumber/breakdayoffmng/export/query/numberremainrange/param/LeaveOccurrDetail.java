package nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param;

import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.DigestionAtr;

/**
 * 休暇発生明細
 */
@Getter
public class LeaveOccurrDetail{

	/**
	 * 期限日
	 */
	private GeneralDate deadline;

	/**
	 * 消化区分
	 */
	@Setter
	private DigestionAtr digestionCate;

	/**
	 * 消滅日
	 */
	@Setter
	private Optional<GeneralDate> extinctionDate;

	public LeaveOccurrDetail(GeneralDate deadline, DigestionAtr digestionCate,
			Optional<GeneralDate> extinctionDate) {
		this.deadline = deadline;
		this.digestionCate = digestionCate;
		this.extinctionDate = extinctionDate;
	}

}