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
public class LeaveOccurrDetail extends AccumulationAbsenceDetail {

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

	public LeaveOccurrDetail(AccumulationAbsenceDetail detail, GeneralDate deadline, DigestionAtr digestionCate,
			Optional<GeneralDate> extinctionDate) {
		super(new AccuVacationBuilder(detail.getEmployeeId(), detail.getDateOccur(), detail.getOccurrentClass(),
				detail.getDataAtr(), detail.getManageId()).numberOccurren(detail.getNumberOccurren())
						.unbalanceNumber(detail.getUnbalanceNumber()));
		this.deadline = deadline;
		this.digestionCate = digestionCate;
		this.extinctionDate = extinctionDate;
	}
	
	/**
	 * 	[1] 消化状態を判断する
	 */
	public DigestionAtr judgeDigestiveStatus(GeneralDate ymd) {
		
		if(this.digestionCate == DigestionAtr.EXPIRED) {
			if(this.deadline.before(ymd)) {
				return DigestionAtr.EXPIRED;
			}
			return DigestionAtr.UNUSED;
		}
		
		return this.digestionCate;
		
	}

}