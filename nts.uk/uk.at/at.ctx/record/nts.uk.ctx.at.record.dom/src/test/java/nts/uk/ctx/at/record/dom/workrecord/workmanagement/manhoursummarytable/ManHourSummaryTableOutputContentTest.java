package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import lombok.val;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@RunWith(JMockit.class)
public class ManHourSummaryTableOutputContentTest {
    private static final String DATE_FORMAT = "yyyy/MM/dd";
    private final List<YearMonth> yearMonthList = Collections.singletonList(YearMonth.of(2021, 6));
    private final List<GeneralDate> dateList = Helper.dateList;

    /**
     * Getter
     */
    @Test
    public void getters() {
        val itemDetails = Helper.summaryItemDetail.getItemDetails(0);
        val instance = ManHourSummaryTableOutputContent.create(itemDetails);
        NtsAssert.invokeGetters(instance);
    }

    @Test
    public void create_test() {
        val itemDetails = Helper.summaryItemDetail.getItemDetails(0);
        val instance = ManHourSummaryTableOutputContent.create(itemDetails);

        // Assertion
        assertThat(instance.getVerticalTotalValues()).isEmpty();
        assertThat(instance.getTotalPeriod()).isEqualTo(Optional.empty());
        assertThat(instance.getItemDetails()).hasSize(2);
        //1
        {
            assertThat(instance.getItemDetails().get(0)).extracting(
                    x -> x.getCode(),
                    x -> x.getDisplayInfo().getCode(),
                    x -> x.getDisplayInfo().getName(),
                    x -> x.getTotalPeriod().get()
            ).containsExactly("01", "displayCode01", "displayName01", 6);
            assertThat(instance.getItemDetails().get(0).getVerticalTotalList())
                    .extracting(
                            VerticalValueDaily::getWorkingHours,
                            VerticalValueDaily::getYearMonth,
                            VerticalValueDaily::getDate)
                    .containsExactly(
                            tuple(2, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                            tuple(2, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                            tuple(2, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))
                    );
            assertThat(instance.getItemDetails().get(0).getChildHierarchyList()).hasSize(2);
            //1.1
            {
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(0)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("0101", "displayCode0101", "displayName0101", 3);
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(0).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(1, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))
                        );
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(0).getChildHierarchyList()).isEmpty();
            }
            //1.2
            {
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(1)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("0102", "displayCode0102", "displayName0102", 3);
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(1).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(1, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))
                        );
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(1).getChildHierarchyList()).isEmpty();
            }
        }

        //2
        {
            assertThat(instance.getItemDetails().get(1)).extracting(
                    x -> x.getCode(),
                    x -> x.getDisplayInfo().getCode(),
                    x -> x.getDisplayInfo().getName(),
                    x -> x.getTotalPeriod().get()
            ).containsExactly("02", "displayCode02", "displayName02", 6);
            assertThat(instance.getItemDetails().get(1).getVerticalTotalList())
                    .extracting(
                            VerticalValueDaily::getWorkingHours,
                            VerticalValueDaily::getYearMonth,
                            VerticalValueDaily::getDate)
                    .containsExactly(
                            tuple(2, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                            tuple(2, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                            tuple(2, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))
                    );
            assertThat(instance.getItemDetails().get(1).getChildHierarchyList()).hasSize(2);
            //2.1
            {
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(0)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("0201", "displayCode0201", "displayName0201", 3);
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(0).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(1, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))
                        );
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(0).getChildHierarchyList()).isEmpty();
            }
            //2.2
            {
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(1)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("0202", "displayCode0202", "displayName0202", 3);
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(1).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(1, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))
                        );
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(1).getChildHierarchyList()).isEmpty();
            }
        }
    }

    /**
     * TotalUnit = Date
     */
    @Test
    public void calculateTotal_totalUnit_Date() {
        val itemDetails = Helper.summaryItemDetail.getItemDetails(0);
        val instance = ManHourSummaryTableOutputContent.create(itemDetails);

        // Execute
        instance.calculateTotal(TotalUnit.DATE, dateList, yearMonthList);

        // Assertion
        assertThat(instance.getTotalPeriod().get()).isEqualTo(12);
        assertThat(instance.getVerticalTotalValues())
                .extracting(
                        VerticalValueDaily::getWorkingHours,
                        VerticalValueDaily::getYearMonth,
                        VerticalValueDaily::getDate)
                .containsExactly(
                        tuple(4, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                        tuple(4, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                        tuple(4, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))
                );

        // List SummaryItemDetail
        assertThat(instance.getItemDetails()).hasSize(2);
        //1
        {
            assertThat(instance.getItemDetails().get(0)).extracting(
                    x -> x.getCode(),
                    x -> x.getDisplayInfo().getCode(),
                    x -> x.getDisplayInfo().getName(),
                    x -> x.getTotalPeriod().get()
            ).containsExactly("01", "displayCode01", "displayName01", 6);
            assertThat(instance.getItemDetails().get(0).getVerticalTotalList())
                    .extracting(
                            VerticalValueDaily::getWorkingHours,
                            VerticalValueDaily::getYearMonth,
                            VerticalValueDaily::getDate)
                    .containsExactly(
                            tuple(2, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                            tuple(2, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                            tuple(2, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))
                    );
            assertThat(instance.getItemDetails().get(0).getChildHierarchyList()).hasSize(2);
            //1.1
            {
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(0)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("0101", "displayCode0101", "displayName0101", 3);
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(0).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(1, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))
                        );
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(0).getChildHierarchyList()).isEmpty();
            }
            //1.2
            {
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(1)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("0102", "displayCode0102", "displayName0102", 3);
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(1).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(1, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))
                        );
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(1).getChildHierarchyList()).isEmpty();
            }
        }

        //2
        {
            assertThat(instance.getItemDetails().get(1)).extracting(
                    x -> x.getCode(),
                    x -> x.getDisplayInfo().getCode(),
                    x -> x.getDisplayInfo().getName(),
                    x -> x.getTotalPeriod().get()
            ).containsExactly("02", "displayCode02", "displayName02", 6);
            assertThat(instance.getItemDetails().get(1).getVerticalTotalList())
                    .extracting(
                            VerticalValueDaily::getWorkingHours,
                            VerticalValueDaily::getYearMonth,
                            VerticalValueDaily::getDate)
                    .containsExactly(
                            tuple(2, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                            tuple(2, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                            tuple(2, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))
                    );
            assertThat(instance.getItemDetails().get(1).getChildHierarchyList()).hasSize(2);
            //2.1
            {
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(0)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("0201", "displayCode0201", "displayName0201", 3);
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(0).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(1, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))
                        );
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(0).getChildHierarchyList()).isEmpty();
            }
            //2.2
            {
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(1)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("0202", "displayCode0202", "displayName0202", 3);
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(1).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(1, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))
                        );
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(1).getChildHierarchyList()).isEmpty();
            }
        }
    }

    /**
     * TotalUnit = Date
     * dateList empty
     */
    @Test
    public void calculateTotal_totalUnit_Date_dateList_Empty() {
        val itemDetails = Helper.summaryItemDetail.getItemDetails(0);
        val instance = ManHourSummaryTableOutputContent.create(itemDetails);

        // Execute
        instance.calculateTotal(TotalUnit.DATE, Collections.emptyList(), yearMonthList);

        // Assertion
        assertThat(instance.getTotalPeriod().get()).isEqualTo(0);
        assertThat(instance.getVerticalTotalValues()).isEmpty();
        // List SummaryItemDetail
        assertThat(instance.getItemDetails()).hasSize(2);
        //1
        {
            assertThat(instance.getItemDetails().get(0)).extracting(
                    x -> x.getCode(),
                    x -> x.getDisplayInfo().getCode(),
                    x -> x.getDisplayInfo().getName(),
                    x -> x.getTotalPeriod().get()
            ).containsExactly("01", "displayCode01", "displayName01", 6);
            assertThat(instance.getItemDetails().get(0).getVerticalTotalList())
                    .extracting(
                            VerticalValueDaily::getWorkingHours,
                            VerticalValueDaily::getYearMonth,
                            VerticalValueDaily::getDate)
                    .containsExactly(
                            tuple(2, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                            tuple(2, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                            tuple(2, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))
                    );
            assertThat(instance.getItemDetails().get(0).getChildHierarchyList()).hasSize(2);
            //1.1
            {
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(0)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("0101", "displayCode0101", "displayName0101", 3);
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(0).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(1, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))
                        );
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(0).getChildHierarchyList()).isEmpty();
            }
            //1.2
            {
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(1)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("0102", "displayCode0102", "displayName0102", 3);
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(1).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(1, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))
                        );
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(1).getChildHierarchyList()).isEmpty();
            }
        }

        //2
        {
            assertThat(instance.getItemDetails().get(1)).extracting(
                    x -> x.getCode(),
                    x -> x.getDisplayInfo().getCode(),
                    x -> x.getDisplayInfo().getName(),
                    x -> x.getTotalPeriod().get()
            ).containsExactly("02", "displayCode02", "displayName02", 6);
            assertThat(instance.getItemDetails().get(1).getVerticalTotalList())
                    .extracting(
                            VerticalValueDaily::getWorkingHours,
                            VerticalValueDaily::getYearMonth,
                            VerticalValueDaily::getDate)
                    .containsExactly(
                            tuple(2, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                            tuple(2, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                            tuple(2, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))
                    );
            assertThat(instance.getItemDetails().get(1).getChildHierarchyList()).hasSize(2);
            //2.1
            {
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(0)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("0201", "displayCode0201", "displayName0201", 3);
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(0).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(1, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))
                        );
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(0).getChildHierarchyList()).isEmpty();
            }
            //2.2
            {
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(1)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("0202", "displayCode0202", "displayName0202", 3);
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(1).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(1, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                tuple(1, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))
                        );
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(1).getChildHierarchyList()).isEmpty();
            }
        }
    }

    /**
     * TotalUnit = Date
     * itemDetails empty
     */
    @Test
    public void calculateTotal_totalUnit_Date_itemDetails_empty() {
        List<SummaryItemDetail> itemDetails = Collections.emptyList();
        val instance = ManHourSummaryTableOutputContent.create(itemDetails);

        // Execute
        instance.calculateTotal(TotalUnit.DATE, dateList, yearMonthList);

        // Assertion
        assertThat(instance.getTotalPeriod().get()).isEqualTo(0);
        assertThat(instance.getItemDetails()).isEmpty();
        assertThat(instance.getVerticalTotalValues())
                .extracting(
                        VerticalValueDaily::getWorkingHours,
                        VerticalValueDaily::getYearMonth,
                        VerticalValueDaily::getDate)
                .containsExactly(
                        tuple(0, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                        tuple(0, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                        tuple(0, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))
                );
    }

    /**
     * TotalUnit = Date
     * dateList empty
     * itemDetails empty
     */
    @Test
    public void calculateTotal_totalUnit_Date_dateList_Empty_itemDetails_empty() {
        List<SummaryItemDetail> itemDetails = Collections.emptyList();
        val instance = ManHourSummaryTableOutputContent.create(itemDetails);

        // Execute
        instance.calculateTotal(TotalUnit.DATE, Collections.emptyList(), yearMonthList);

        // Assertion
        assertThat(instance.getTotalPeriod().get()).isEqualTo(0);
        assertThat(instance.getItemDetails()).isEmpty();
        assertThat(instance.getVerticalTotalValues()).isEmpty();
    }


    /**
     * TotalUnit = YearMonth
     */
    @Test
    public void calculateTotal_totalUnit_YearMonth() {
        val itemDetails = Helper.summaryItemDetail.getItemDetails(1);
        val instance = ManHourSummaryTableOutputContent.create(itemDetails);

        // Execute
        instance.calculateTotal(TotalUnit.YEAR_MONTH, dateList, yearMonthList);

        // Assertion
        assertThat(instance.getTotalPeriod().get()).isEqualTo(12);
        assertThat(instance.getVerticalTotalValues())
                .extracting(
                        VerticalValueDaily::getWorkingHours,
                        VerticalValueDaily::getYearMonth,
                        VerticalValueDaily::getDate)
                .containsExactly(
                        tuple(12, YearMonth.of(2021, 6), null)
                );
        // List SummaryItemDetail
        assertThat(instance.getItemDetails()).hasSize(2);
        //1
        {
            assertThat(instance.getItemDetails().get(0)).extracting(
                    x -> x.getCode(),
                    x -> x.getDisplayInfo().getCode(),
                    x -> x.getDisplayInfo().getName(),
                    x -> x.getTotalPeriod().get()
            ).containsExactly("01", "displayCode01", "displayName01", 6);
            assertThat(instance.getItemDetails().get(0).getVerticalTotalList())
                    .extracting(
                            VerticalValueDaily::getWorkingHours,
                            VerticalValueDaily::getYearMonth,
                            VerticalValueDaily::getDate)
                    .containsExactly(
                            tuple(2, YearMonth.of(2021, 6), null),
                            tuple(2, YearMonth.of(2021, 6), null),
                            tuple(2, YearMonth.of(2021, 6), null));
            assertThat(instance.getItemDetails().get(0).getChildHierarchyList()).hasSize(2);
            //1.1
            {
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(0)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("0101", "displayCode0101", "displayName0101", 3);
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(0).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(1, YearMonth.of(2021, 6), null),
                                tuple(1, YearMonth.of(2021, 6), null),
                                tuple(1, YearMonth.of(2021, 6), null));
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(0).getChildHierarchyList()).isEmpty();
            }
            //1.2
            {
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(1)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("0102", "displayCode0102", "displayName0102", 3);
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(1).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(1, YearMonth.of(2021, 6), null),
                                tuple(1, YearMonth.of(2021, 6), null),
                                tuple(1, YearMonth.of(2021, 6), null));
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(1).getChildHierarchyList()).isEmpty();
            }
        }

        //2
        {
            assertThat(instance.getItemDetails().get(1)).extracting(
                    x -> x.getCode(),
                    x -> x.getDisplayInfo().getCode(),
                    x -> x.getDisplayInfo().getName(),
                    x -> x.getTotalPeriod().get()
            ).containsExactly("02", "displayCode02", "displayName02", 6);
            assertThat(instance.getItemDetails().get(1).getVerticalTotalList())
                    .extracting(
                            VerticalValueDaily::getWorkingHours,
                            VerticalValueDaily::getYearMonth,
                            VerticalValueDaily::getDate)
                    .containsExactly(
                            tuple(2, YearMonth.of(2021, 6), null),
                            tuple(2, YearMonth.of(2021, 6), null),
                            tuple(2, YearMonth.of(2021, 6), null));
            assertThat(instance.getItemDetails().get(1).getChildHierarchyList()).hasSize(2);
            //2.1
            {
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(0)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("0201", "displayCode0201", "displayName0201", 3);
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(0).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(1, YearMonth.of(2021, 6), null),
                                tuple(1, YearMonth.of(2021, 6), null),
                                tuple(1, YearMonth.of(2021, 6), null));
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(0).getChildHierarchyList()).isEmpty();
            }
            //2.2
            {
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(1)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("0202", "displayCode0202", "displayName0202", 3);
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(1).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(1, YearMonth.of(2021, 6), null),
                                tuple(1, YearMonth.of(2021, 6), null),
                                tuple(1, YearMonth.of(2021, 6), null));
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(1).getChildHierarchyList()).isEmpty();
            }
        }
    }

    /**
     * TotalUnit = YearMonth
     * yearMonthList EMPTY
     */
    @Test
    public void calculateTotal_totalUnit_YearMonth_yearMonthList_empty() {
        val itemDetails = Helper.summaryItemDetail.getItemDetails(1);
        val instance = ManHourSummaryTableOutputContent.create(itemDetails);

        // Execute
        instance.calculateTotal(TotalUnit.YEAR_MONTH, dateList, Collections.emptyList());

        // Assertion
        assertThat(instance.getTotalPeriod().get()).isEqualTo(0);
        assertThat(instance.getVerticalTotalValues()).isEmpty();
        // List SummaryItemDetail
        assertThat(instance.getItemDetails()).hasSize(2);
        //1
        {
            assertThat(instance.getItemDetails().get(0)).extracting(
                    x -> x.getCode(),
                    x -> x.getDisplayInfo().getCode(),
                    x -> x.getDisplayInfo().getName(),
                    x -> x.getTotalPeriod().get()
            ).containsExactly("01", "displayCode01", "displayName01", 6);
            assertThat(instance.getItemDetails().get(0).getVerticalTotalList())
                    .extracting(
                            VerticalValueDaily::getWorkingHours,
                            VerticalValueDaily::getYearMonth,
                            VerticalValueDaily::getDate)
                    .containsExactly(
                            tuple(2, YearMonth.of(2021, 6), null),
                            tuple(2, YearMonth.of(2021, 6), null),
                            tuple(2, YearMonth.of(2021, 6), null));
            assertThat(instance.getItemDetails().get(0).getChildHierarchyList()).hasSize(2);
            //1.1
            {
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(0)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("0101", "displayCode0101", "displayName0101", 3);
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(0).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(1, YearMonth.of(2021, 6), null),
                                tuple(1, YearMonth.of(2021, 6), null),
                                tuple(1, YearMonth.of(2021, 6), null));
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(0).getChildHierarchyList()).isEmpty();
            }
            //1.2
            {
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(1)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("0102", "displayCode0102", "displayName0102", 3);
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(1).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(1, YearMonth.of(2021, 6), null),
                                tuple(1, YearMonth.of(2021, 6), null),
                                tuple(1, YearMonth.of(2021, 6), null));
                assertThat(instance.getItemDetails().get(0).getChildHierarchyList().get(1).getChildHierarchyList()).isEmpty();
            }
        }

        //2
        {
            assertThat(instance.getItemDetails().get(1)).extracting(
                    x -> x.getCode(),
                    x -> x.getDisplayInfo().getCode(),
                    x -> x.getDisplayInfo().getName(),
                    x -> x.getTotalPeriod().get()
            ).containsExactly("02", "displayCode02", "displayName02", 6);
            assertThat(instance.getItemDetails().get(1).getVerticalTotalList())
                    .extracting(
                            VerticalValueDaily::getWorkingHours,
                            VerticalValueDaily::getYearMonth,
                            VerticalValueDaily::getDate)
                    .containsExactly(
                            tuple(2, YearMonth.of(2021, 6), null),
                            tuple(2, YearMonth.of(2021, 6), null),
                            tuple(2, YearMonth.of(2021, 6), null));
            assertThat(instance.getItemDetails().get(1).getChildHierarchyList()).hasSize(2);
            //2.1
            {
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(0)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("0201", "displayCode0201", "displayName0201", 3);
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(0).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(1, YearMonth.of(2021, 6), null),
                                tuple(1, YearMonth.of(2021, 6), null),
                                tuple(1, YearMonth.of(2021, 6), null));
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(0).getChildHierarchyList()).isEmpty();
            }
            //2.2
            {
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(1)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("0202", "displayCode0202", "displayName0202", 3);
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(1).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(1, YearMonth.of(2021, 6), null),
                                tuple(1, YearMonth.of(2021, 6), null),
                                tuple(1, YearMonth.of(2021, 6), null));
                assertThat(instance.getItemDetails().get(1).getChildHierarchyList().get(1).getChildHierarchyList()).isEmpty();
            }
        }
    }

    /**
     * TotalUnit = YearMonth
     * itemDetails EMPTY
     */
    @Test
    public void calculateTotal_totalUnit_YearMonth_itemDetails_empty() {
        List<SummaryItemDetail> itemDetails = Collections.emptyList();
        val instance = ManHourSummaryTableOutputContent.create(itemDetails);

        // Execute
        instance.calculateTotal(TotalUnit.YEAR_MONTH, dateList, yearMonthList);

        // Assertion
        assertThat(instance.getTotalPeriod().get()).isEqualTo(0);
        assertThat(instance.getVerticalTotalValues())
                .extracting(
                        VerticalValueDaily::getWorkingHours,
                        VerticalValueDaily::getYearMonth,
                        VerticalValueDaily::getDate)
                .containsExactly(tuple(0, YearMonth.of(2021, 6), null));
        assertThat(instance.getItemDetails()).isEmpty();
    }

    /**
     * TotalUnit = YearMonth
     * yearMonthList EMPTY
     * itemDetails EMPTY
     */
    @Test
    public void calculateTotal_totalUnit_YearMonth_yearMonthList_empty_itemDetails_empty() {
        List<SummaryItemDetail> itemDetails = Collections.emptyList();
        val instance = ManHourSummaryTableOutputContent.create(itemDetails);

        // Execute
        instance.calculateTotal(TotalUnit.YEAR_MONTH, dateList, Collections.emptyList());

        // Assertion
        assertThat(instance.getTotalPeriod().get()).isEqualTo(0);
        assertThat(instance.getVerticalTotalValues()).isEmpty();
        assertThat(instance.getItemDetails()).isEmpty();
    }
}
