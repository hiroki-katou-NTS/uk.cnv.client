package nts.uk.screen.at.app.kha003.b;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class ManHourPeriod {
    // 0: DATE  ;  1: YEAR_MONTH
    private int totalUnit;
    private String startDate;
    private String endDate;
    private String yearMonthStart;
    private String yearMonthEnd;

    public DatePeriod getDatePeriod() {
        if (totalUnit == 1) return null;
        return new DatePeriod(GeneralDate.fromString(startDate, "yyyy/MM/dd"), GeneralDate.fromString(endDate, "yyyy/MM/dd"));
    }

    public YearMonthPeriod getYearMonthPeriod() {
        if (totalUnit == 0) return null;
        return new YearMonthPeriod(
                YearMonth.of(Integer.parseInt(yearMonthStart.substring(0, 4)), Integer.parseInt(yearMonthStart.substring(4, 6))),
                YearMonth.of(Integer.parseInt(yearMonthEnd.substring(0, 4)), Integer.parseInt(yearMonthEnd.substring(4, 6)))
        );
    }

    public List<GeneralDate> getDateList() {
        val datePeriod = getDatePeriod();
        if (datePeriod == null) return new ArrayList<>();
        return datePeriod.datesBetween();
    }

    public List<YearMonth> getYearMonthList() {
        val yearMonthPeriod = getYearMonthPeriod();
        if (yearMonthPeriod == null) return new ArrayList<>();
        return yearMonthPeriod.yearMonthsBetween();
    }
}
