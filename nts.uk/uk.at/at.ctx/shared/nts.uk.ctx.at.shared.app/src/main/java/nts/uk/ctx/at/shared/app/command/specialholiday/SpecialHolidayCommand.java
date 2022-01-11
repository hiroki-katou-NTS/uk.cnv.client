package nts.uk.ctx.at.shared.app.command.specialholiday;

import java.util.Optional;

import lombok.Value;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.app.command.specialholiday.grantcondition.SpecialLeaveRestrictionCommand;
import nts.uk.ctx.at.shared.app.command.specialholiday.grantinformation.GrantRegularCommand;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday;
import nts.uk.ctx.at.shared.dom.specialholiday.TargetItem;
import nts.uk.ctx.at.shared.dom.specialholiday.grantcondition.AgeRange;
import nts.uk.ctx.at.shared.dom.specialholiday.grantcondition.AgeStandard;
import nts.uk.ctx.at.shared.dom.specialholiday.grantcondition.SpecialLeaveRestriction;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.FixGrantDate;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantDate;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantDateTblReferenceGrant;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantRegular;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.PeriodGrantDate;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.RegularGrantDays;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.TypeTime;
import nts.uk.ctx.at.shared.dom.specialholiday.periodinformation.GrantDeadline;
import nts.uk.ctx.at.shared.dom.specialholiday.periodinformation.SpecialVacationDeadline;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.time.calendar.MonthDay;

@Value
public class SpecialHolidayCommand {
	/** 会社ID */
	private String companyId;

	/** 特別休暇コード */
	private int specialHolidayCode;

	/** 特別休暇名称 */
	private String specialHolidayName;

	/** 付与情報 */
	private GrantRegularCommand regularCommand;

	/** 特別休暇利用条件 */
	private SpecialLeaveRestrictionCommand leaveResCommand;

	/** 対象項目 */
	private TargetItemCommand targetItemCommand;

	/**自動付与区分 */
	private int autoGrant;
	
	/** 連続で取得する */
	public Integer continuousAcquisition;

	/** メモ */
	private String memo;

	public SpecialHoliday toDomain(String companyId) {
		return  SpecialHoliday.of(companyId,
				this.specialHolidayCode,
				this.specialHolidayName,
				this.toDomainGrantRegular(companyId),
				this.toDomainSpecLeaveRest(companyId),
				this.toDomainTargetItem(companyId),
				this.autoGrant,
				this.memo,
				NotUseAtr.valueOf(this.continuousAcquisition));
	}

	private TargetItem toDomainTargetItem(String companyId2) {
		if(this.targetItemCommand == null) {
			return null;
		}

		return TargetItem.createFromJavaType(this.targetItemCommand.getAbsenceFrameNo(), this.targetItemCommand.getFrameNo());
	}

	private SpecialLeaveRestriction toDomainSpecLeaveRest(String companyId2) {
		if(this.leaveResCommand == null) {
			return null;
		}

		return SpecialLeaveRestriction.createFromJavaType(companyId, this.specialHolidayCode,
				this.leaveResCommand.getRestrictionCls(),
				this.leaveResCommand.getAgeLimit(),
				this.leaveResCommand.getGenderRest(),
				this.leaveResCommand.getRestEmp(),
				this.leaveResCommand.getListCls(),
				this.toDomainAgeStandard(),
				this.toDomainAgeRange(),
				this.leaveResCommand.getGender(),
				this.leaveResCommand.getListEmp());
	}

	private AgeRange toDomainAgeRange() {
		if(this.leaveResCommand.getAgeRange().getAgeHigherLimit() == null || this.leaveResCommand.getAgeRange().getAgeLowerLimit() == null) {
			return null;
		}

		return AgeRange.createFromJavaType(this.leaveResCommand.getAgeRange().getAgeLowerLimit(), this.leaveResCommand.getAgeRange().getAgeHigherLimit());
	}

	private AgeStandard toDomainAgeStandard() {
		if(this.leaveResCommand.getAgeStandard() == null) {
			return null;
		}

		MonthDay ageBaseDate = new MonthDay(this.leaveResCommand.getAgeStandard().getAgeBaseDate() / 100, this.leaveResCommand.getAgeStandard().getAgeBaseDate() % 100);

		return AgeStandard.createFromJavaType(this.leaveResCommand.getAgeStandard().getAgeCriteriaCls(), ageBaseDate);
	}


	private GrantRegular toDomainGrantRegular(String companyId) {
		if(this.regularCommand == null) {
			return null;
		}

		//KMF004A 特別休暇情報の登録　実装時にこの処理を実装すること
		return GrantRegular.of(TypeTime.toEnum(this.regularCommand.getTypeTime()),
				Optional.ofNullable(GrantDate.toEnum(this.regularCommand.getGrantDate())),
				toDomainFixGrantDate(),
				toDomainGrantDateTblReferenceGrant(),
				toDomainPeriodGrantDate());
	}
	
	private Optional<FixGrantDate> toDomainFixGrantDate() {
		
		if (this.regularCommand.getFixGrantDate() == null) {
			return Optional.empty();
		}
		
		Integer months = null;
		Integer years = null;
		
		if (this.regularCommand.getFixGrantDate().getGrantPeriodic().getExpirationDate() != null) {
			months = this.regularCommand.getFixGrantDate().getGrantPeriodic().getExpirationDate().getMonths();
			years  = this.regularCommand.getFixGrantDate().getGrantPeriodic().getExpirationDate().getYears();
		}
		
		GrantDeadline grantPeriodic = GrantDeadline.createFromJavaType(
				this.regularCommand.getFixGrantDate().getGrantPeriodic().getTimeSpecifyMethod(),
				Optional.ofNullable(SpecialVacationDeadline.createFromJavaType(months, years)),
				this.regularCommand.getFixGrantDate().getGrantPeriodic().getLimitAccumulationDays() == null ? null : this.regularCommand.getFixGrantDate().getGrantPeriodic().getLimitAccumulationDays().getLimitCarryoverDays());
		
		FixGrantDate fixGrantDate = FixGrantDate.createFromJavaType(companyId,
				this.specialHolidayCode,
				this.regularCommand.getFixGrantDate().getGrantDays(),
				grantPeriodic,
				this.regularCommand.getFixGrantDate().getGrantMonthDay());
		
		return Optional.ofNullable(fixGrantDate);
	}
	
	
	
	private Optional<PeriodGrantDate> toDomainPeriodGrantDate() {
		
		if (this.regularCommand.getPeriodGrantDate() == null) {
			return Optional.empty();
		}
		
		String formatDate = "yyyy/MM/dd";
		String start = this.regularCommand.getPeriodGrantDate().getPeriod().getStart();
		String end   = this.regularCommand.getPeriodGrantDate().getPeriod().getEnd();
		if(start == null || end == null)
			return Optional.empty();
		DatePeriod period = new DatePeriod(GeneralDate.fromString(start, formatDate), GeneralDate.fromString(end, formatDate));
		
		return Optional.ofNullable(PeriodGrantDate.of(period, RegularGrantDays.createFromJavaType(this.regularCommand.getPeriodGrantDate().getGrantDays()))); 
	}
	
	private Optional<GrantDateTblReferenceGrant> toDomainGrantDateTblReferenceGrant() {
		
		if (this.regularCommand.getGrantPeriodic() == null) {
			return Optional.empty();
		}
		
		int months = this.regularCommand.getGrantPeriodic().getExpirationDate().getMonths();
		int years  = this.regularCommand.getGrantPeriodic().getExpirationDate().getYears();
		
		GrantDeadline grantDeadline = GrantDeadline.createFromJavaType(
				this.regularCommand.getGrantPeriodic().getTimeSpecifyMethod(),
				Optional.ofNullable(SpecialVacationDeadline.createFromJavaType(months, years)),
				this.regularCommand.getGrantPeriodic().getLimitAccumulationDays().getLimitCarryoverDays());
		
		return Optional.ofNullable(new GrantDateTblReferenceGrant(grantDeadline));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	



	
}
