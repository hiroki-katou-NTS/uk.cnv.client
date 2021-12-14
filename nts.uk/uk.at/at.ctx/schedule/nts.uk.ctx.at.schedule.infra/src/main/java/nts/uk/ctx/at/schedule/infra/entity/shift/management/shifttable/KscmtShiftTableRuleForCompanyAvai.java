package nts.uk.ctx.at.schedule.infra.entity.shift.management.shifttable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.arc.time.calendar.DateInMonth;
import nts.arc.time.calendar.DayOfWeek;
import nts.arc.time.calendar.OneMonth;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.DeadlineDayOfWeek;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.DeadlineWeekAtr;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.FromNoticeDays;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.HolidayAvailabilityMaxdays;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.ShiftTableRule;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.WorkAvailabilityPeriodUnit;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.WorkAvailabilityRule;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.WorkAvailabilityRuleDateSetting;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.WorkAvailabilityRuleWeekSetting;
import nts.uk.ctx.at.schedule.dom.shift.management.workavailability.AssignmentMethod;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@NoArgsConstructor
@Entity
@Table(name = "KSCMT_SHIFTTBL_RULE_CMP_AVAILABILITY")
public class KscmtShiftTableRuleForCompanyAvai extends ContractUkJpaEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 会社ID
	 */
	@Id
	@Column(name = "CID")
	public String companyId;
	
	/**
	 * 勤務希望に休日を使用するか
	 */
	@Column(name = "AVAILABILITY_HD_ATR")
	public int holidayAtr;
	
	/**
	 * 勤務希望にシフトを使用するか
	 */
	@Column(name = "AVAILABILITY_SHIFT_ATR")
	public int shiftAtr;
	
	/**
	 * 勤務希望に時間帯を使用するか
	 */
	@Column(name = "AVAILABILITY_TIMESHEET_ATR")
	public int timeSheetAtr;
	
	/**
	 * 何日前に通知するかの日数
	 */
	@Column(name = "FROM_NOTICE_DAYS")
	public Integer fromNoticeDays;
	
	/**
	 * シフト勤務単位は - (勤務希望の収集が月単位か週単位か) - シフト表のルール.シフト表の設定
	 */
	@Column(name = "PERIOD_UNIT")
	public Integer periodUnit;
	
	
	/**
	 * 月単位管理の場合の月の締め日 - シフト表のルール.シフト表の日付設定.締め日.日
	 */
	@Column(name = "DATE_SET_CLOSE_DAY")
	public Integer dateCloseDay;
	
	
	/**
	 * 月単位管理の場合の月の締めが末日か - シフト表のルール.シフト表の日付設定.締め日.末日とする
	 */
	@Column(name = "DATE_SET_CLOSE_IS_LAST_DAY")
	public Integer dateCloseIsLastDay;
	
	
	
	/**
	 * 勤務希望の締切日 - シフト表のルール.シフト表の日付設定.勤務希望の締切日.日
	 */
	@Column(name = "DATE_SET_DEADLINE_DAY")
	public Integer dateDeadlineDay;
	
	/**
	 * 勤務希望の締切日が末日か - シフト表のルール.シフト表の日付設定.勤務希望の締切日.末日とする
	 */
	@Column(name = "DATE_SET_DEADLINE_IS_LAST_DAY")
	public Integer dateDeadlineIsLastDay;
	
	/**
	 * 希望休日の上限日数 - シフト表のルール.シフト表の日付設定.希望休日の上限
	 */
	@Column(name = "DATE_SET_HD_UPPERLIMIT")
	public Integer dateHDUpperlimit;
	
	/**
	 * 勤務希望運用区分 - シフト表のルール.シフト表の曜日設定.開始曜日
	 */
	@Column(name = "WEEK_SET_START")
	public Integer weekSetStart;
	
	/**
	 * 勤務希望を収集する週 - シフト表のルール.シフト表の曜日設定.勤務希望の締切曜日.週
	 */
	@Column(name = "WEEK_SET_DEADLINE_ATR")
	public Integer weekSetDeadlineAtr;
	
	/**
	 * 曜日 - シフト表のルール.シフト表の曜日設定.勤務希望の締切曜日.曜日
	 */
	@Column(name = "WEEK_SET_DEADLINE_WEEK")
	public Integer weekSetDeadlineWeek;
	
	
	@OneToOne
	@JoinColumns({ @JoinColumn(name = "CID", referencedColumnName = "CID", insertable = false, updatable = false)})
	public KscmtShiftTableRuleForCompany kscmtShiftTableRuleForCompany;
	

	@Override
	protected Object getKey() {
		return this.companyId;
	}

	public KscmtShiftTableRuleForCompanyAvai(String companyId, int holidayAtr, int shiftAtr, int timeSheetAtr,
			Integer fromNoticeDays, Integer periodUnit, Integer dateCloseDay, Integer dateCloseIsLastDay,
			Integer dateDeadlineDay, Integer dateDeadlineIsLastDay, Integer dateHDUpperlimit, Integer weekSetStart,
			Integer weekSetDeadlineAtr, Integer weekSetDeadlineWeek) {
		super();
		this.companyId = companyId;
		this.holidayAtr = holidayAtr;
		this.shiftAtr = shiftAtr;
		this.timeSheetAtr = timeSheetAtr;
		this.fromNoticeDays = fromNoticeDays;
		this.periodUnit = periodUnit;
		this.dateCloseDay = dateCloseDay;
		this.dateCloseIsLastDay = dateCloseIsLastDay;
		this.dateDeadlineDay = dateDeadlineDay;
		this.dateDeadlineIsLastDay = dateDeadlineIsLastDay;
		this.dateHDUpperlimit = dateHDUpperlimit;
		this.weekSetStart = weekSetStart;
		this.weekSetDeadlineAtr = weekSetDeadlineAtr;
		this.weekSetDeadlineWeek = weekSetDeadlineWeek;
	}
	
	public static KscmtShiftTableRuleForCompanyAvai toEntity(String companyId,ShiftTableRule shiftTableRule) {
		if(!shiftTableRule.getShiftTableSetting().isPresent()) {
			return null;
		}

		Integer dateCloseDay = null;
		Integer dateCloseIsLastDay = null;
		Integer dateDeadlineDay = null;
		Integer dateDeadlineIsLastDay = null;
		Integer dateHDUpperlimit = null;

		Integer weekSetStart = null;
		Integer weekSetDeadlineAtr = null;
		Integer weekSetDeadlineWeek = null;

		if (shiftTableRule.getShiftTableSetting().get().getShiftPeriodUnit() == WorkAvailabilityPeriodUnit.MONTHLY) {
			WorkAvailabilityRuleDateSetting data = (WorkAvailabilityRuleDateSetting) shiftTableRule.getShiftTableSetting().get();
			dateCloseDay = data.getClosureDate().getClosingDate().getDay();
			dateCloseIsLastDay = data.getClosureDate().getClosingDate().isLastDay() ? 1 : 0;
			dateDeadlineDay = data.getAvailabilityDeadLine().getDay();
			dateDeadlineIsLastDay = data.getAvailabilityDeadLine().isLastDay() ? 1 : 0;
			dateHDUpperlimit = data.getHolidayMaxDays().v();
		} else {
			WorkAvailabilityRuleWeekSetting data = (WorkAvailabilityRuleWeekSetting) shiftTableRule.getShiftTableSetting().get();
			weekSetStart = data.getFirstDayOfWeek().value;
			weekSetDeadlineAtr = data.getExpectDeadLine().getWeekAtr().value;
			weekSetDeadlineWeek = data.getExpectDeadLine().getDayOfWeek().value;
		}

		return new KscmtShiftTableRuleForCompanyAvai(
				companyId,
				shiftTableRule.getAvailabilityAssignMethodList().stream().anyMatch(c -> c == AssignmentMethod.HOLIDAY) ? 1 : 0,
				shiftTableRule.getAvailabilityAssignMethodList().stream().anyMatch(c -> c == AssignmentMethod.SHIFT) ? 1 : 0,
				shiftTableRule.getAvailabilityAssignMethodList().stream().anyMatch(c-> c ==AssignmentMethod.TIME_ZONE) ? 1 : 0,
				shiftTableRule.getFromNoticeDays().isPresent() ? shiftTableRule.getFromNoticeDays().get().v() : null,
				shiftTableRule.getShiftTableSetting().get().getShiftPeriodUnit().value,
				dateCloseDay,
				dateCloseIsLastDay,
				dateDeadlineDay,
				dateDeadlineIsLastDay, 
				dateHDUpperlimit, 
				weekSetStart, 
				weekSetDeadlineAtr,
				weekSetDeadlineWeek
		);
	}

	public ShiftTableRule toDomain(int usePublicAtr, int useWorkAvailabilityAtr) {
		Optional<WorkAvailabilityRule> shiftTableSetting = Optional.empty();
		if (this.periodUnit != null) {
			if (this.periodUnit == WorkAvailabilityPeriodUnit.MONTHLY.value) {
				shiftTableSetting = Optional.of(
						new WorkAvailabilityRuleDateSetting(
								new OneMonth(new DateInMonth(this.dateCloseDay, this.dateCloseIsLastDay == 1)),
                                new DateInMonth(this.dateDeadlineDay, this.dateDeadlineIsLastDay == 1),
                                new HolidayAvailabilityMaxdays(this.dateHDUpperlimit)
						)
				);
			} else {
				shiftTableSetting = Optional.of(
						new WorkAvailabilityRuleWeekSetting(
								DayOfWeek.valueOf(this.weekSetStart),
								new DeadlineDayOfWeek(
										DeadlineWeekAtr.of(this.weekSetDeadlineAtr),
										DayOfWeek.valueOf(this.weekSetDeadlineWeek)
								)
						)
				);
			}
		}

		List<AssignmentMethod> availabilityAssignMethodList = new ArrayList<>();
		if (this.holidayAtr == 1) {
			availabilityAssignMethodList.add(AssignmentMethod.HOLIDAY);
		}
		if (this.shiftAtr == 1) {
			availabilityAssignMethodList.add(AssignmentMethod.SHIFT);
		}
		if (this.timeSheetAtr == 1) {
			availabilityAssignMethodList.add(AssignmentMethod.TIME_ZONE);
		}

		return new ShiftTableRule(
				NotUseAtr.valueOf(usePublicAtr),
				NotUseAtr.valueOf(useWorkAvailabilityAtr),
				shiftTableSetting,
				availabilityAssignMethodList,
				this.fromNoticeDays != null ? Optional.of(new FromNoticeDays(this.fromNoticeDays)) : Optional.empty()
		);
	}
}
