package nts.uk.ctx.at.shared.dom.remainingnumber.paymana;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.OccurrenceDigClass;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.CompensatoryDayoffDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.ManagementDataDaysAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.ManagementDataRemainUnit;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail.AccuVacationBuilder;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail.NumberConsecuVacation;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.CreateAtr;

/**
 * 振休管理データ
 * @author HopNT
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SubstitutionOfHDManagementData extends AggregateRoot {

	// 振休データID
	private String subOfHDID;
	
	private String cid;
	
	// 社員ID	
	private String sID;
	
	// 振休日
	private CompensatoryDayoffDate holidayDate;
	
	// 必要日数
	private ManagementDataDaysAtr requiredDays;	
	
	// 未相殺日数
	private ManagementDataRemainUnit remainDays;
	
	public SubstitutionOfHDManagementData(String id, String cid, String sid, boolean unknowDate, GeneralDate dayoffDate,
			Double requiredDays, Double remainDays) {
		this.subOfHDID = id;
		this.cid = cid;
		this.sID = sid;
		this.holidayDate = new CompensatoryDayoffDate(unknowDate, Optional.ofNullable(dayoffDate));
		this.requiredDays = new ManagementDataDaysAtr(requiredDays);
		this.remainDays = new ManagementDataRemainUnit(remainDays);
	}
	
	public void setRemainsDay(Double remainNumber){
		this.remainDays = new ManagementDataRemainUnit(remainNumber);
	}
	
	public void setRemainsDayToFree(Double remainNumber){
		this.remainDays = new ManagementDataRemainUnit(this.remainDays.v() + remainNumber);
	}
	
	public void update(AccumulationAbsenceDetail data) {
		
		this.holidayDate = new CompensatoryDayoffDate(data.getDateOccur().isUnknownDate(), data.getDateOccur().getDayoffDate());
		this.requiredDays = new ManagementDataDaysAtr(data.getNumberOccurren().getDay().v());
		this.remainDays = new ManagementDataRemainUnit(data.getUnbalanceNumber().getDay().v());
	}
	
	public static SubstitutionOfHDManagementData create(String cid, AccumulationAbsenceDetail data) {
		
		SubstitutionOfHDManagementData domain = new SubstitutionOfHDManagementData();
		domain.sID = data.getEmployeeId();
		domain.cid = cid;
		domain.subOfHDID = data.getManageId();
		domain.update(data);
		
		return domain;
	}
	
	// [1] 逐次発生の休暇明細に変換する
	public AccumulationAbsenceDetail convertSeqVacationState() {
		return new AccuVacationBuilder(this.sID, this.holidayDate, OccurrenceDigClass.DIGESTION,
				CreateAtr.FLEXCOMPEN.convertToMngData(true), this.subOfHDID)
						.numberOccurren(new NumberConsecuVacation(new ManagementDataRemainUnit(this.requiredDays.v()),
								Optional.empty()))
						.unbalanceNumber(new NumberConsecuVacation(this.remainDays, Optional.empty())).build();
	}

	// 2] 残数が残っている
	public boolean isRemaing() {
		return this.remainDays.v() > 0.0;
	}
}
