package nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.OccurrenceDigClass;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.CompensatoryDayoffDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.DigestionAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.ManagementDataDaysAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.ManagementDataHours;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.ManagementDataRemainUnit;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail.AccuVacationBuilder;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail.NumberConsecuVacation;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.UnbalanceVacation;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.CreateAtr;

/**
 * 休出管理データ
 * @author HopNT
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LeaveManagementData extends AggregateRoot {
	
	// ID
	private String ID;
	
	// 会社ID
	private String cID;
	
	// 社員ID
	private String sID;
	
	// 休出日
	private CompensatoryDayoffDate comDayOffDate;
	
	// 使用期限日
	private GeneralDate expiredDate;
	
	// 発生日数
	private ManagementDataDaysAtr occurredDays;
	
	// 発生時間数
	private ManagementDataHours occurredTimes;
	
	// 未使用日数
	private ManagementDataRemainUnit unUsedDays;
	
	// 未使用時間数
	private ManagementDataHours unUsedTimes;
	
	// 代休消化区分r
	private DigestionAtr subHDAtr;
	
	// １日相当時間
	private AttendanceTime fullDayTime;
	
	// 半日相当時間
	private AttendanceTime halfDayTime;
	
	// 消滅日
	public Optional<GeneralDate> disapearDate;
	
	public LeaveManagementData(String id, String cid, String sid, boolean unknowDate, GeneralDate dayoffDate, GeneralDate expiredDate,
			Double occurredDays, int occurredTimes, Double unUsedDays, int unUsedTimes, int subHDAtr, int equivalentADay, int equivalentHalfDay){
		this.ID = id;
		this.cID = cid;
		this.sID = sid;
		this.comDayOffDate = new CompensatoryDayoffDate(unknowDate, Optional.ofNullable(dayoffDate));
		this.expiredDate = expiredDate;
		this.occurredDays = new ManagementDataDaysAtr(occurredDays);
		this.occurredTimes = new ManagementDataHours(occurredTimes);
		this.unUsedDays = new ManagementDataRemainUnit(unUsedDays);
		this.unUsedTimes = new ManagementDataHours(unUsedTimes);
		this.subHDAtr = EnumAdaptor.valueOf(subHDAtr, DigestionAtr.class);
		this.fullDayTime = new AttendanceTime(equivalentADay);
		this.halfDayTime = new AttendanceTime(equivalentHalfDay);
		this.disapearDate = Optional.empty();
	}
	
	
	public LeaveManagementData(String id, String cid, String sid, boolean unknowDate, GeneralDate dayoffDate, GeneralDate expiredDate,
			Double occurredDays, int occurredTimes, Double unUsedDays, int unUsedTimes, int subHDAtr, int equivalentADay, int equivalentHalfDay, GeneralDate disapearDate){
		this.ID = id;
		this.cID = cid;
		this.sID = sid;
		this.comDayOffDate = new CompensatoryDayoffDate(unknowDate, Optional.ofNullable(dayoffDate));
		this.expiredDate = expiredDate;
		this.occurredDays = new ManagementDataDaysAtr(occurredDays);
		this.occurredTimes = new ManagementDataHours(occurredTimes);
		this.unUsedDays = new ManagementDataRemainUnit(unUsedDays);
		this.unUsedTimes = new ManagementDataHours(unUsedTimes);
		this.subHDAtr = EnumAdaptor.valueOf(subHDAtr, DigestionAtr.class);
		this.fullDayTime = new AttendanceTime(equivalentADay);
		this.halfDayTime = new AttendanceTime(equivalentHalfDay);
		this.disapearDate = Optional.ofNullable(disapearDate);
		
	}


	public LeaveManagementData(String id2, String cid2, String sid2, boolean unknowDate, GeneralDate dayoffDate,
			GeneralDate expiredDate2, ManagementDataDaysAtr occurredDays2, ManagementDataHours occurredTimes2,
			Double unUsedDay, int unUsedTimes2, int subHDAtr2, AttendanceTime fullDayTime2,
			AttendanceTime halfDayTime2, Optional<GeneralDate> disapearDate2) {
		this.ID = id2;
		this.cID = cid2;
		this.sID = sid2;
		this.comDayOffDate = new CompensatoryDayoffDate(unknowDate, Optional.ofNullable(dayoffDate));
		this.expiredDate = expiredDate2;
		this.occurredDays = occurredDays2;
		this.occurredTimes = occurredTimes2;
		this.unUsedDays = new ManagementDataRemainUnit(unUsedDay);
		this.unUsedTimes = new ManagementDataHours(unUsedTimes2);
		this.subHDAtr = EnumAdaptor.valueOf(subHDAtr2, DigestionAtr.class);
		this.fullDayTime = fullDayTime2;
		this.halfDayTime = halfDayTime2;
		this.disapearDate = disapearDate2;
	}
	
	public void update(UnbalanceVacation in) {
		
		this.expiredDate = in.getDeadline();
		this.occurredDays = new ManagementDataDaysAtr(in.getNumberOccurren().getDay().v());
		this.occurredTimes = new ManagementDataHours(in.getNumberOccurren().getTime().map(c -> c.valueAsMinutes()).orElse(0));
		this.unUsedDays = new ManagementDataRemainUnit(in.getUnbalanceNumber().getDay().v());
		this.unUsedTimes = new ManagementDataHours(in.getUnbalanceNumber().getTime().map(c -> c.valueAsMinutes()).orElse(0));
		this.subHDAtr = in.getDigestionCate();
		this.fullDayTime = in.getTimeOneDay();
		this.halfDayTime = in.getTimeHalfDay();
		this.disapearDate = in.getExtinctionDate();
	}
	
	public static LeaveManagementData of(String cid, UnbalanceVacation in) {
		
		LeaveManagementData domain = new LeaveManagementData();
		domain.cID = cid;
		domain.ID = in.getManageId();
		domain.sID = in.getEmployeeId();
		domain.comDayOffDate = in.getDateOccur();
		domain.update(in);
		
		return domain;
	}
	
	//[1] 休出の未相殺に変換する
	public AccumulationAbsenceDetail convertSeqVacationState() {
		val common = new AccuVacationBuilder(this.sID, this.comDayOffDate, OccurrenceDigClass.OCCURRENCE,
				CreateAtr.FLEXCOMPEN.convertToMngData(true), this.getID())
						.numberOccurren(new NumberConsecuVacation(new ManagementDataRemainUnit(this.occurredDays.v()),
								Optional.of(new AttendanceTime(this.occurredTimes.v()))))
						.unbalanceNumber(new NumberConsecuVacation(this.unUsedDays,
								Optional.of(new AttendanceTime(this.unUsedTimes.v()))))
						.build();
		return new UnbalanceVacation(this.expiredDate, this.subHDAtr, this.disapearDate, common, this.fullDayTime,
				this.halfDayTime);
	}
	
	// 2] 残数が残っている
	public boolean isRemaing() {
		return this.subHDAtr == DigestionAtr.UNUSED;
	}
}
