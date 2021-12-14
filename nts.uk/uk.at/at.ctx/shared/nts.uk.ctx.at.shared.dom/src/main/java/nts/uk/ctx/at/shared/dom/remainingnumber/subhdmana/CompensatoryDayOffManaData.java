package nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana;

import java.util.Optional;

import lombok.AllArgsConstructor;
//import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.OccurrenceDigClass;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.CompensatoryDayoffDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.ManagementDataDaysAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.ManagementDataHours;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.ManagementDataRemainUnit;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail.AccuVacationBuilder;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail.NumberConsecuVacation;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.CreateAtr;

/**
 * 代休管理データ
 * 
 * @author HopNT
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CompensatoryDayOffManaData extends AggregateRoot {

	// ID
	private String comDayOffID;

	// 社員ID
	private String sID;

	private String cID;

	// 代休日
	
	private CompensatoryDayoffDate dayOffDate;

	// 必要日数
	private ManagementDataDaysAtr requireDays;

	// 必要時間数
	private ManagementDataHours requiredTimes;

	// 未相殺日数
	@Setter
	private ManagementDataRemainUnit remainDays;

	// 未相殺時間数
	private ManagementDataHours remainTimes;

	public CompensatoryDayOffManaData(String comDayOffID, String cid, String sID, boolean unknowDate,
			GeneralDate dayoffDate, Double days, int time, Double remainDays, int remainTimes) {
		this.comDayOffID = comDayOffID;
		this.cID = cid;
		this.sID = sID;
		this.dayOffDate = new CompensatoryDayoffDate(unknowDate, Optional.ofNullable(dayoffDate));
		this.requireDays = new ManagementDataDaysAtr(days);
		this.requiredTimes = new ManagementDataHours(time);
		this.remainDays = new ManagementDataRemainUnit(remainDays);
		this.remainTimes = new ManagementDataHours(remainTimes);
	}
	
	public void update(AccumulationAbsenceDetail in) {
		
		this.requireDays = new ManagementDataDaysAtr(in.getNumberOccurren().getDay().v());
		this.requiredTimes = new ManagementDataHours(in.getNumberOccurren().getTime().map(c -> c.valueAsMinutes()).orElse(0));
		this.remainDays = new ManagementDataRemainUnit(in.getUnbalanceNumber().getDay().v());
		this.remainTimes = new ManagementDataHours(in.getUnbalanceNumber().getTime().map(c -> c.valueAsMinutes()).orElse(0));
	}
	
	public static CompensatoryDayOffManaData of(String cid, AccumulationAbsenceDetail in) {
		
		CompensatoryDayOffManaData domain = new CompensatoryDayOffManaData();
		domain.comDayOffID = in.getManageId();
		domain.sID = in.getEmployeeId();
		domain.cID = cid;
		domain.dayOffDate = in.getDateOccur();
		domain.update(in);
		
		return domain;
	}

	//[1] 逐次発生の休暇明細に変換する
	public AccumulationAbsenceDetail convertSeqVacationState() {
		return new AccuVacationBuilder(this.sID, this.dayOffDate, OccurrenceDigClass.DIGESTION,
				CreateAtr.FLEXCOMPEN.convertToMngData(true), this.comDayOffID)
						.numberOccurren(new NumberConsecuVacation(new ManagementDataRemainUnit(this.requireDays.v()),
								Optional.of(new AttendanceTime(this.requiredTimes.v()))))
						.unbalanceNumber(new NumberConsecuVacation(this.remainDays,
								Optional.of(new AttendanceTime(this.remainTimes.v()))))
						.build();
	}
	
	// 2] 残数が残っている
	public boolean isRemaing() {
		return this.remainDays.v() > 0.0 || this.remainTimes.v() > 0;
	}
}
