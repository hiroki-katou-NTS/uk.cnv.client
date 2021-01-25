package nts.uk.ctx.at.function.dom.commonform;

import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * 基準日で社員の雇用と締め日を取得する
 */
@RunWith(JMockit.class)
public class GetSuitableDateByClosureDateUtilityTest {

    /**
     * Test: GetSuitableDateByClosureDateUtilityTest.
     * start().day() > closureDay
     * end().day() < closureDay
     */
    @Test
    public void testGetSuitableDateByClosureDate_01() {

        DatePeriod datePeriod = new DatePeriod(GeneralDate.ymd(2020, 10, 10), GeneralDate.ymd(2020, 11, 4));
        YearMonthPeriod actual = GetSuitableDateByClosureDateUtility.convertPeriod(datePeriod, 5);

        YearMonth startYearMonth = YearMonth.of(2020, 11);
        YearMonth endYearMonth = YearMonth.of(2020, 11);
        YearMonthPeriod expected = new YearMonthPeriod(startYearMonth, endYearMonth);
        assertThat(actual).isEqualTo(expected);

    }

    /**
     * Test: GetSuitableDateByClosureDateUtilityTest.
     * start().day() < closureDay
     * end().day() > closureDay
     */
    @Test
    public void testGetSuitableDateByClosureDate_02() {

        DatePeriod datePeriod = new DatePeriod(GeneralDate.ymd(2020, 10, 1), GeneralDate.ymd(2020, 11, 30));
        YearMonthPeriod actual = GetSuitableDateByClosureDateUtility.convertPeriod(datePeriod, 10);

        YearMonth startYearMonth = YearMonth.of(2020, 10);
        YearMonth endYearMonth = YearMonth.of(2020, 12);
        YearMonthPeriod expected = new YearMonthPeriod(startYearMonth, endYearMonth);
        assertThat(actual).isEqualTo(expected);

    }

    /**
     * Test: GetSuitableDateByClosureDateUtilityTest.
     * start().day() > closureDay
     * end().day() > closureDay
     */
    @Test
    public void testGetSuitableDateByClosureDate_03() {

        DatePeriod datePeriod = new DatePeriod(GeneralDate.ymd(2020, 10, 30), GeneralDate.ymd(2020, 11, 30));
        YearMonthPeriod actual = GetSuitableDateByClosureDateUtility.convertPeriod(datePeriod, 10);

        YearMonth startYearMonth = YearMonth.of(2020, 11);
        YearMonth endYearMonth = YearMonth.of(2020, 12);
        YearMonthPeriod expected = new YearMonthPeriod(startYearMonth, endYearMonth);
        assertThat(actual).isEqualTo(expected);

    }

    /**
     * Test: GetSuitableDateByClosureDateUtilityTest.
     * start().day() < closureDay
     * end().day() < closureDay
     */
    @Test
    public void testGetSuitableDateByClosureDate_04() {

        DatePeriod datePeriod = new DatePeriod(GeneralDate.ymd(2020, 10, 10), GeneralDate.ymd(2020, 11, 10));
        YearMonthPeriod actual = GetSuitableDateByClosureDateUtility.convertPeriod(datePeriod, 20);

        YearMonth startYearMonth = YearMonth.of(2020, 10);
        YearMonth endYearMonth = YearMonth.of(2020, 11);
        YearMonthPeriod expected = new YearMonthPeriod(startYearMonth, endYearMonth);
        assertThat(actual).isEqualTo(expected);

    }

}
