package nts.uk.ctx.at.function.ac.child;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.function.dom.adapter.child.ChildNursingLeaveThisMonthFutureSituation;
import nts.uk.ctx.at.function.dom.adapter.child.GetRemainingNumberCareNurseAdapter;
import nts.uk.ctx.at.function.dom.adapter.child.NursingCareLeaveThisMonthFutureSituation;
import nts.uk.ctx.at.record.pub.monthly.vacation.childcarenurse.childcare.GetRemainingNumberCarePub;
import nts.uk.ctx.at.record.pub.monthly.vacation.childcarenurse.childcare.GetRemainingNumberChildCareNursePub;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.InterimRemainMngMode;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Optional;

@Stateless
public class GetRemainingNumberCareNurseAdapterImpl implements GetRemainingNumberCareNurseAdapter {
    @Inject
    private GetRemainingNumberChildCareNursePub getRemainingNumberChildCareNursePub;
    @Inject
    private GetRemainingNumberCarePub getRemainingNumberCarePub;
    @Override
    public ChildNursingLeaveThisMonthFutureSituation getChildCareRemNumWithinPeriod(
            String companyId,
            String employeeId,
            DatePeriod period,
            GeneralDate criteriaDate) {
        val result = new ChildNursingLeaveThisMonthFutureSituation();
        val rs206 = getRemainingNumberChildCareNursePub.getChildCareRemNumWithinPeriod(
                companyId,
                employeeId,
                period,
                InterimRemainMngMode.OTHER,
                criteriaDate,
                Optional.of(false),
                Collections.emptyList(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());
        if (rs206 != null) {
            //[*1]　子の看護介護休暇集計結果．集計期間の休暇情報
            result.setYm(period.end().yearMonth());
            val aggrperiodinfo = rs206.getAggrperiodinfo();
            if (aggrperiodinfo != null) {
                val thisYear = aggrperiodinfo.getThisYear().getAggrPeriodUsedNumber();
                val nextYearOpt = aggrperiodinfo.getNextYear();
                val usageTimeBeforeOpt = thisYear.getUsedTime();

                result.setNumberOfDaysUsedBeforeGrant(thisYear.getUsedDays());
                usageTimeBeforeOpt.ifPresent(result::setUsageTimeBeforeGrant);
                nextYearOpt.ifPresent(e -> {
                    result.setDaysOfUseAfterGrant(e.getAggrPeriodUsedNumber().getUsedDays());
                    e.getAggrPeriodUsedNumber().getUsedTime().ifPresent(result::setUsageTimeAfterGrant);
                });
            }
            //[*2]　子の看護介護休暇集計結果．起算日からの休暇情報
            val startdateDays = rs206.getStartdateDays();
            if (startdateDays != null) {
                val thisYear = startdateDays.getThisYear();
                val remainingNumber = thisYear.getRemainingNumber();
                val remainingTime = remainingNumber.getTime();
                result.setRemainingDaysBeforeGrant(remainingNumber.getDays());
                result.setMaxNumberOfDaysBeforeGrant(thisYear.getLimitDays());
                remainingTime.ifPresent(result::setRemainingTimesBeforeGrant);

                val nextYear = startdateDays.getNextYear();
                nextYear.ifPresent(e -> {
                    result.setRemainingDaysAfterGrant(e.getRemainingNumber().getDays());
                    e.getRemainingNumber().getTime().ifPresent(result::setRemainingTimesAfterGrant);
                    result.setMaxNumberOfDaysAfterGrant(e.getLimitDays());
                });
            }
        }
        return result;
    }

    @Override
    public NursingCareLeaveThisMonthFutureSituation getNursingCareLeaveThisMonthFutureSituation(String companyId, String employeeId, DatePeriod period, GeneralDate criteriaDate) {
        val result = new NursingCareLeaveThisMonthFutureSituation();
        val rs207 = getRemainingNumberCarePub.getCareRemNumWithinPeriod(
                companyId,
                employeeId,
                period,
                InterimRemainMngMode.OTHER,
                criteriaDate,
                Optional.of(false),
                Collections.emptyList(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());
        if (rs207!= null) {
            //[*1]　子の看護介護休暇集計結果．集計期間の休暇情報
            result.setYm(period.end().yearMonth());
            val aggrperiodinfo = rs207.getAggrperiodinfo();
            if (aggrperiodinfo != null) {
                val nextYearOpt = aggrperiodinfo.getNextYear();
                val thisYear = aggrperiodinfo.getThisYear().getAggrPeriodUsedNumber();
                val usageTimeBeforeOpt = thisYear.getUsedTime();
                result.setNumberOfDaysUsedBeforeGrant(thisYear.getUsedDays());
                usageTimeBeforeOpt.ifPresent(result::setUsageTimeBeforeGrant);
                nextYearOpt.ifPresent(e -> {
                    result.setDaysOfUseAfterGrant(e.getAggrPeriodUsedNumber().getUsedDays());
                    e.getAggrPeriodUsedNumber().getUsedTime().ifPresent(result::setUsageTimeAfterGrant);
                });
            }
            //[*2]　子の看護介護休暇集計結果．起算日からの休暇情報
            val startdateDays = rs207.getStartdateDays();
            if (startdateDays != null) {
                val nextYear = startdateDays.getNextYear();
                nextYear.ifPresent(e -> {
                    result.setRemainingDaysAfterGrant(e.getRemainingNumber().getDays());
                    e.getRemainingNumber().getTime().ifPresent(result::setRemainingTimesAfterGrant);
                    result.setMaxNumberOfDaysAfterGrant(e.getLimitDays());
                });

                val thisYear = startdateDays.getThisYear();
                val remainingNumber = thisYear.getRemainingNumber();
                val remainingTime = remainingNumber.getTime();
                result.setRemainingDaysBeforeGrant(remainingNumber.getDays());
                remainingTime.ifPresent(result::setRemainingTimesBeforeGrant);
                result.setMaxNumberOfDaysBeforeGrant(thisYear.getLimitDays());
            }
        }
        return result;
    }
}

