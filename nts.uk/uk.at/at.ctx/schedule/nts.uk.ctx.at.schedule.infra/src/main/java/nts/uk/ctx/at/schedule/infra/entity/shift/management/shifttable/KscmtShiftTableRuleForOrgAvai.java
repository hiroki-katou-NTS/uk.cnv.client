package nts.uk.ctx.at.schedule.infra.entity.shift.management.shifttable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;

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

/**
 * @author tutk
 */
@NoArgsConstructor
@Entity
@Table(name = "KSCMT_SHIFTTBL_RULE_ORG_AVAILABILITY")
public class KscmtShiftTableRuleForOrgAvai extends ContractUkJpaEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 会社ID
     */
    @EmbeddedId
    public KscmtShiftTableRuleForOrgAvaiPK kscmtShiftTableRuleForOrgAvaiPK;

    /**
     * 勤務希望に休日を使用するか
     */
    @Column(name = "AVAILABILITY_HD_ATR")
    public boolean holidayAtr;

    /**
     * 勤務希望にシフトを使用するか
     */
    @Column(name = "AVAILABILITY_SHIFT_ATR")
    public boolean shiftAtr;

    /**
     * 勤務希望に時間帯を使用するか
     */
    @Column(name = "AVAILABILITY_TIMESHEET_ATR")
    public boolean timeSheetAtr;

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
    public boolean dateCloseIsLastDay;


    /**
     * 勤務希望の締切日 - シフト表のルール.シフト表の日付設定.勤務希望の締切日.日
     */
    @Column(name = "DATE_SET_DEADLINE_DAY")
    public Integer dateDeadlineDay;

    /**
     * 勤務希望の締切日が末日か - シフト表のルール.シフト表の日付設定.勤務希望の締切日.末日とする
     */
    @Column(name = "DATE_SET_DEADLINE_IS_LAST_DAY")
    public boolean dateDeadlineIsLastDay;

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
    @JoinColumns({@JoinColumn(name = "CID", referencedColumnName = "CID", insertable = false, updatable = false),
            @JoinColumn(name = "TARGET_UNIT", referencedColumnName = "TARGET_UNIT", insertable = false, updatable = false),
            @JoinColumn(name = "TARGET_ID", referencedColumnName = "TARGET_ID", insertable = false, updatable = false)
    })
    public KscmtShiftTableRuleForOrg kscmtShiftTableRuleForOrg;


    @Override
    protected Object getKey() {
        return this.kscmtShiftTableRuleForOrgAvaiPK;
    }

    public KscmtShiftTableRuleForOrgAvai(KscmtShiftTableRuleForOrgAvaiPK kscmtShiftTableRuleForOrgAvaiPK,
                                         int holidayAtr, int shiftAtr, int timeSheetAtr, Integer fromNoticeDays, Integer periodUnit,
                                         Integer dateCloseDay, Integer dateCloseIsLastDay, Integer dateDeadlineDay, Integer dateDeadlineIsLastDay,
                                         Integer dateHDUpperlimit, Integer weekSetStart, Integer weekSetDeadlineAtr, Integer weekSetDeadlineWeek) {
        super();
        this.kscmtShiftTableRuleForOrgAvaiPK = kscmtShiftTableRuleForOrgAvaiPK;
        this.holidayAtr = BooleanUtils.toBoolean(holidayAtr);
        this.shiftAtr = BooleanUtils.toBoolean(shiftAtr);
        this.timeSheetAtr = BooleanUtils.toBoolean(timeSheetAtr);
        this.fromNoticeDays = fromNoticeDays;
        this.periodUnit = periodUnit;
        this.dateCloseDay = dateCloseDay;
        this.dateCloseIsLastDay = BooleanUtils.toBoolean(dateCloseIsLastDay);
        this.dateDeadlineDay = dateDeadlineDay;
        this.dateDeadlineIsLastDay = BooleanUtils.toBoolean(dateDeadlineIsLastDay);
        this.dateHDUpperlimit = dateHDUpperlimit;
        this.weekSetStart = weekSetStart;
        this.weekSetDeadlineAtr = weekSetDeadlineAtr;
        this.weekSetDeadlineWeek = weekSetDeadlineWeek;
    }

    public static KscmtShiftTableRuleForOrgAvai toEntity(String companyId, int targetUnit, String targetID, ShiftTableRule shiftTableRule) {
        if (!shiftTableRule.getShiftTableSetting().isPresent()) {
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
            dateDeadlineDay = data.getAvailabilityDeadLine().getDay();
            dateDeadlineIsLastDay = data.getAvailabilityDeadLine().isLastDay() ? 1 : 0;
            dateHDUpperlimit = data.getHolidayMaxDays().v();
            dateCloseDay = data.getClosureDate().getClosingDate().getDay();
            dateCloseIsLastDay = data.getClosureDate().getClosingDate().isLastDay() ? 1 : 0;
        } else {
            WorkAvailabilityRuleWeekSetting data = (WorkAvailabilityRuleWeekSetting) shiftTableRule.getShiftTableSetting().get();
            weekSetStart = data.getFirstDayOfWeek().value;
            weekSetDeadlineAtr = data.getExpectDeadLine().getWeekAtr().value;
            weekSetDeadlineWeek = data.getExpectDeadLine().getDayOfWeek().value;
        }

        return new KscmtShiftTableRuleForOrgAvai(
                new KscmtShiftTableRuleForOrgAvaiPK(companyId, targetUnit, targetID),
                shiftTableRule.getAvailabilityAssignMethodList().stream().anyMatch(c -> c == AssignmentMethod.HOLIDAY) ? 1 : 0,
                shiftTableRule.getAvailabilityAssignMethodList().stream().anyMatch(c -> c == AssignmentMethod.SHIFT) ? 1 : 0,
                shiftTableRule.getAvailabilityAssignMethodList().stream().anyMatch(c -> c == AssignmentMethod.TIME_ZONE) ? 1 : 0,
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
                shiftTableSetting = Optional.of(new WorkAvailabilityRuleDateSetting(
                        new OneMonth(new DateInMonth(this.dateCloseDay, this.dateCloseIsLastDay)),
                        new DateInMonth(this.dateDeadlineDay, this.dateDeadlineIsLastDay),
                        new HolidayAvailabilityMaxdays(this.dateHDUpperlimit)
                ));
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
        if (this.holidayAtr) {
            availabilityAssignMethodList.add(AssignmentMethod.HOLIDAY);
        }
        if (this.shiftAtr) {
            availabilityAssignMethodList.add(AssignmentMethod.SHIFT);
        }

        if (this.timeSheetAtr) {
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
