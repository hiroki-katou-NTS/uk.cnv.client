package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import lombok.val;
import mockit.Expectations;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.dom.adapter.function.alarmworkplace.EmployeeInfoImport;
import nts.uk.ctx.at.shared.dom.adapter.workplace.config.info.WorkplaceInfor;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.i18n.TextResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@RunWith(JMockit.class)
public class ManHourSummaryTableFormatTest {
    private final List<SummaryItem> summaryItemFullList = Helper.DetailFormatSetting.summaryItemList;
    List<SummaryItem> summaryItemList2Level = Arrays.asList(
            new SummaryItem(2, SummaryItemType.TASK1),
            new SummaryItem(1, SummaryItemType.EMPLOYEE));
    private final List<YearMonth> yearMonthList = Collections.singletonList(YearMonth.of(2021, 6));
    private final List<GeneralDate> dateList = Arrays.asList(
            GeneralDate.fromString("2021/06/01", "yyyy/MM/dd"),
            GeneralDate.fromString("2021/06/02", "yyyy/MM/dd"));
    private final List<WorkDetailData> workDetail = Helper.workDetailDataList(2);

    private final List<WorkplaceInfor> workplaceInfos = Helper.createWorkplaceInfos;
    private final List<EmployeeInfoImport> empInfos = Helper.createEmpInfos;
    private final List<TaskImport> tasks = Helper.createTasks;
    private final MasterNameInformation masterNameInfo = new MasterNameInformation(workplaceInfos, workplaceInfos, empInfos, tasks, tasks, tasks, tasks, tasks);

    /**
     * Getter
     */
    @Test
    public void getters() {
        val detailFormatSetting = new DetailFormatSetting(DisplayFormat.DECIMAL, TotalUnit.DATE, NotUseAtr.USE, summaryItemFullList);
        val instance = new ManHourSummaryTableFormat(new ManHourSummaryTableCode("CD01"), new ManHourSummaryTableName("NAME01"), detailFormatSetting);
        NtsAssert.invokeGetters(instance);
    }

    /**
     * Test create detail format setting
     */
    @Test
    public void create_detail_format_setting_test() {
        val instance = DetailFormatSetting.create(DisplayFormat.DECIMAL, TotalUnit.DATE, NotUseAtr.USE, summaryItemList2Level);

        // Assertion
        assertThat(instance.getDisplayFormat().value).isEqualTo(0);
        assertThat(instance.getTotalUnit().value).isEqualTo(0);
        assertThat(instance.getDisplayVerticalHorizontalTotal().value).isEqualTo(1);
        assertThat(instance.getSummaryItemList())
                .extracting(SummaryItem::getHierarchicalOrder, x -> x.getSummaryItemType().value, x -> x.getSummaryItemType().nameId)
                .containsExactly(
                        tuple(1, 2, "社員"),
                        tuple(2, 3, "作業1")
                );
    }

    /**
     * displayVerticalHorizontalTotal = USE
     * TotalUnit = DATE
     * summaryItemList NOT_EMPTY
     */
    @Test
    public void createOutputContent_unitDate() {
        val detailFormatSetting = DetailFormatSetting.create(DisplayFormat.MINUTE, TotalUnit.DATE, NotUseAtr.USE, summaryItemList2Level);
        val instance = new ManHourSummaryTableFormat(new ManHourSummaryTableCode("CD01"), new ManHourSummaryTableName("NAME01"), detailFormatSetting);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Action
        val result = instance.createOutputContent(dateList, yearMonthList, workDetail, masterNameInfo);

        // Assertion
        assertThat(result.getTotalPeriod().get()).isEqualTo(6);
        assertThat(result.getVerticalTotalValues())
                .extracting(
                        VerticalValueDaily::getWorkingHours,
                        VerticalValueDaily::getYearMonth,
                        VerticalValueDaily::getDate)
                .containsExactly(
                        tuple(2, null, GeneralDate.fromString("2021/06/01", "yyyy/MM/dd")),
                        tuple(4, null, GeneralDate.fromString("2021/06/02", "yyyy/MM/dd"))
                );
        // List SummaryItemDetail
        assertThat(result.getItemDetails()).hasSize(2);
        //1
        {
            assertThat(result.getItemDetails().get(0)).extracting(
                    x -> x.getCode(),
                    x -> x.getDisplayInfo().getCode(),
                    x -> x.getDisplayInfo().getName(),
                    x -> x.getTotalPeriod().get()
            ).containsExactly("001", "CD_01", "Name_1", 2);
            assertThat(result.getItemDetails().get(0).getVerticalTotalList())
                    .extracting(
                            VerticalValueDaily::getWorkingHours,
                            VerticalValueDaily::getYearMonth,
                            VerticalValueDaily::getDate)
                    .containsExactly(
                            tuple(2, null, GeneralDate.fromString("2021/06/01", "yyyy/MM/dd")),
                            tuple(0, null, GeneralDate.fromString("2021/06/02", "yyyy/MM/dd")));
            assertThat(result.getItemDetails().get(0).getChildHierarchyList()).hasSize(1);
            //1.1
            {
                assertThat(result.getItemDetails().get(0).getChildHierarchyList().get(0)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("001", "001", "Name_1", 2);
                assertThat(result.getItemDetails().get(0).getChildHierarchyList().get(0).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(2, null, GeneralDate.fromString("2021/06/01", "yyyy/MM/dd")),
                                tuple(0, null, GeneralDate.fromString("2021/06/02", "yyyy/MM/dd"))
                        );
                assertThat(result.getItemDetails().get(0).getChildHierarchyList().get(0).getChildHierarchyList()).isEmpty();
            }
        }

        //2
        {
            assertThat(result.getItemDetails().get(1)).extracting(
                    x -> x.getCode(),
                    x -> x.getDisplayInfo().getCode(),
                    x -> x.getDisplayInfo().getName(),
                    x -> x.getTotalPeriod().get()
            ).containsExactly("002", "CD_02", "Name_2", 4);
            assertThat(result.getItemDetails().get(1).getVerticalTotalList())
                    .extracting(
                            VerticalValueDaily::getWorkingHours,
                            VerticalValueDaily::getYearMonth,
                            VerticalValueDaily::getDate)
                    .containsExactly(
                            tuple(0, null, GeneralDate.fromString("2021/06/01", "yyyy/MM/dd")),
                            tuple(4, null, GeneralDate.fromString("2021/06/02", "yyyy/MM/dd"))
                    );
            assertThat(result.getItemDetails().get(1).getChildHierarchyList()).hasSize(1);
            //2.1
            {
                assertThat(result.getItemDetails().get(1).getChildHierarchyList().get(0)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("002", "002", "Name_2", 4);
                assertThat(result.getItemDetails().get(1).getChildHierarchyList().get(0).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(0, null, GeneralDate.fromString("2021/06/01", "yyyy/MM/dd")),
                                tuple(4, null, GeneralDate.fromString("2021/06/02", "yyyy/MM/dd"))
                        );
                assertThat(result.getItemDetails().get(1).getChildHierarchyList().get(0).getChildHierarchyList()).isEmpty();
            }
        }
    }

    /**
     * displayVerticalHorizontalTotal = NOT_USE
     * TotalUnit = DATE
     * summaryItemList NOT_EMPTY
     */
    @Test
    public void createOutputContent_unitDate_disp_notUse() {
        val detailFormatSetting = DetailFormatSetting.create(DisplayFormat.MINUTE, TotalUnit.DATE, NotUseAtr.NOT_USE, summaryItemList2Level);
        val instance = new ManHourSummaryTableFormat(new ManHourSummaryTableCode("CD01"), new ManHourSummaryTableName("NAME01"), detailFormatSetting);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Action
        val result = instance.createOutputContent(dateList, yearMonthList, workDetail, masterNameInfo);

        // Assertion
        assertThat(result.getTotalPeriod()).isEqualTo(Optional.empty());
        assertThat(result.getVerticalTotalValues()).isEmpty();

        // List SummaryItemDetail
        assertThat(result.getItemDetails()).hasSize(2);
        //1
        {
            assertThat(result.getItemDetails().get(0).getTotalPeriod()).isEqualTo(Optional.empty());
            assertThat(result.getItemDetails().get(0)).extracting(
                    x -> x.getCode(),
                    x -> x.getDisplayInfo().getCode(),
                    x -> x.getDisplayInfo().getName()
            ).containsExactly("001", "CD_01", "Name_1");
            assertThat(result.getItemDetails().get(0).getVerticalTotalList()).isEmpty();
            assertThat(result.getItemDetails().get(0).getChildHierarchyList()).hasSize(1);
            //1.1
            {
                assertThat(result.getItemDetails().get(0).getChildHierarchyList().get(0).getTotalPeriod()).isEqualTo(Optional.empty());
                assertThat(result.getItemDetails().get(0).getChildHierarchyList().get(0)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName()
                ).containsExactly("001", "001", "Name_1");
                assertThat(result.getItemDetails().get(0).getChildHierarchyList().get(0).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(2, null, GeneralDate.fromString("2021/06/01", "yyyy/MM/dd")),
                                tuple(0, null, GeneralDate.fromString("2021/06/02", "yyyy/MM/dd"))
                        );
                assertThat(result.getItemDetails().get(0).getChildHierarchyList().get(0).getChildHierarchyList()).isEmpty();
            }
        }

        //2
        {
            assertThat(result.getItemDetails().get(1).getTotalPeriod()).isEqualTo(Optional.empty());
            assertThat(result.getItemDetails().get(1)).extracting(
                    x -> x.getCode(),
                    x -> x.getDisplayInfo().getCode(),
                    x -> x.getDisplayInfo().getName()
            ).containsExactly("002", "CD_02", "Name_2");
            assertThat(result.getItemDetails().get(1).getVerticalTotalList()).isEmpty();
            assertThat(result.getItemDetails().get(1).getChildHierarchyList()).hasSize(1);
            //2.1
            {
                assertThat(result.getItemDetails().get(1).getTotalPeriod()).isEqualTo(Optional.empty());
                assertThat(result.getItemDetails().get(1).getChildHierarchyList().get(0)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName()
                ).containsExactly("002", "002", "Name_2");
                assertThat(result.getItemDetails().get(1).getChildHierarchyList().get(0).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(
                                tuple(0, null, GeneralDate.fromString("2021/06/01", "yyyy/MM/dd")),
                                tuple(4, null, GeneralDate.fromString("2021/06/02", "yyyy/MM/dd"))
                        );
                assertThat(result.getItemDetails().get(1).getChildHierarchyList().get(0).getChildHierarchyList()).isEmpty();
            }
        }
    }

    /**
     * displayVerticalHorizontalTotal = NOT_USE
     * TotalUnit = DATE
     * summaryItemList EMPTY
     */
    @Test
    public void createOutputContent_unitDate_disp_notUse_summaryItemList_empty() {
        val detailFormatSetting = DetailFormatSetting.create(DisplayFormat.MINUTE, TotalUnit.DATE, NotUseAtr.NOT_USE, Collections.emptyList());
        val instance = new ManHourSummaryTableFormat(new ManHourSummaryTableCode("CD01"), new ManHourSummaryTableName("NAME01"), detailFormatSetting);

        // Action
        val result = instance.createOutputContent(dateList, yearMonthList, workDetail, masterNameInfo);

        // Assertion
        assertThat(result.getTotalPeriod()).isEqualTo(Optional.empty());
        assertThat(result.getVerticalTotalValues()).isEmpty();
        assertThat(result.getItemDetails()).isEmpty();
    }

    /**
     * displayVerticalHorizontalTotal = USE
     * TotalUnit = DATE
     * summaryItemList EMPTY
     */
    @Test
    public void createOutputContent_unitDate_disp_Use_summaryItemList_empty() {
        val detailFormatSetting = DetailFormatSetting.create(DisplayFormat.MINUTE, TotalUnit.DATE, NotUseAtr.USE, Collections.emptyList());
        val instance = new ManHourSummaryTableFormat(new ManHourSummaryTableCode("CD01"), new ManHourSummaryTableName("NAME01"), detailFormatSetting);

        // Action
        val result = instance.createOutputContent(dateList, yearMonthList, workDetail, masterNameInfo);

        // Assertion
        assertThat(result.getTotalPeriod().get()).isEqualTo(0);
        assertThat(result.getItemDetails()).isEmpty();
        assertThat(result.getVerticalTotalValues())
                .extracting(
                        VerticalValueDaily::getWorkingHours,
                        VerticalValueDaily::getYearMonth,
                        VerticalValueDaily::getDate)
                .containsExactly(
                        tuple(0, null, GeneralDate.fromString("2021/06/01", "yyyy/MM/dd")),
                        tuple(0, null, GeneralDate.fromString("2021/06/02", "yyyy/MM/dd"))
                );
    }

    /**
     * displayVerticalHorizontalTotal = USE
     * TotalUnit = YEAR_MONTH
     * summaryItemList NOT_EMPTY
     */
    @Test
    public void createOutputContent_unitMonth() {
        val detailFormatSetting = DetailFormatSetting.create(DisplayFormat.MINUTE, TotalUnit.YEAR_MONTH, NotUseAtr.USE, summaryItemList2Level);
        val instance = new ManHourSummaryTableFormat(new ManHourSummaryTableCode("CD01"), new ManHourSummaryTableName("NAME01"), detailFormatSetting);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Action
        val result = instance.createOutputContent(dateList, yearMonthList, workDetail, masterNameInfo);

        // Assertion
        assertThat(result.getTotalPeriod().get()).isEqualTo(6);
        assertThat(result.getVerticalTotalValues())
                .extracting(
                        VerticalValueDaily::getWorkingHours,
                        VerticalValueDaily::getYearMonth,
                        VerticalValueDaily::getDate)
                .containsExactly(tuple(6, YearMonth.of(2021, 6), null));

        // List SummaryItemDetail
        assertThat(result.getItemDetails()).hasSize(2);
        //1
        {
            assertThat(result.getItemDetails().get(0)).extracting(
                    x -> x.getCode(),
                    x -> x.getDisplayInfo().getCode(),
                    x -> x.getDisplayInfo().getName(),
                    x -> x.getTotalPeriod().get()
            ).containsExactly("001", "CD_01", "Name_1", 2);
            assertThat(result.getItemDetails().get(0).getVerticalTotalList())
                    .extracting(
                            VerticalValueDaily::getWorkingHours,
                            VerticalValueDaily::getYearMonth,
                            VerticalValueDaily::getDate)
                    .containsExactly(tuple(2, YearMonth.of(2021, 6), null));
            assertThat(result.getItemDetails().get(0).getChildHierarchyList()).hasSize(1);
            //1.1
            {
                assertThat(result.getItemDetails().get(0).getChildHierarchyList().get(0)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("001", "001", "Name_1", 2);
                assertThat(result.getItemDetails().get(0).getChildHierarchyList().get(0).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(tuple(2, YearMonth.of(2021, 6), null));
                assertThat(result.getItemDetails().get(0).getChildHierarchyList().get(0).getChildHierarchyList()).isEmpty();
            }
        }

        //2
        {
            assertThat(result.getItemDetails().get(1)).extracting(
                    x -> x.getCode(),
                    x -> x.getDisplayInfo().getCode(),
                    x -> x.getDisplayInfo().getName(),
                    x -> x.getTotalPeriod().get()
            ).containsExactly("002", "CD_02", "Name_2", 4);
            assertThat(result.getItemDetails().get(1).getVerticalTotalList())
                    .extracting(
                            VerticalValueDaily::getWorkingHours,
                            VerticalValueDaily::getYearMonth,
                            VerticalValueDaily::getDate)
                    .containsExactly(tuple(4, YearMonth.of(2021, 6), null));
            assertThat(result.getItemDetails().get(1).getChildHierarchyList()).hasSize(1);
            //2.1
            {
                assertThat(result.getItemDetails().get(1).getChildHierarchyList().get(0)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName(),
                        x -> x.getTotalPeriod().get()
                ).containsExactly("002", "002", "Name_2", 4);
                assertThat(result.getItemDetails().get(1).getChildHierarchyList().get(0).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(tuple(4, YearMonth.of(2021, 6), null));
                assertThat(result.getItemDetails().get(1).getChildHierarchyList().get(0).getChildHierarchyList()).isEmpty();
            }
        }
    }

    /**
     * displayVerticalHorizontalTotal = NOT_USE
     * TotalUnit = YEAR_MONTH
     * summaryItemList NOT_EMPTY
     */
    @Test
    public void createOutputContent_unitMonth_disp_notUse() {
        val detailFormatSetting = DetailFormatSetting.create(DisplayFormat.MINUTE, TotalUnit.YEAR_MONTH, NotUseAtr.NOT_USE, summaryItemList2Level);
        val instance = new ManHourSummaryTableFormat(new ManHourSummaryTableCode("CD01"), new ManHourSummaryTableName("NAME01"), detailFormatSetting);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Action
        val result = instance.createOutputContent(dateList, yearMonthList, workDetail, masterNameInfo);

        // Assertion
        assertThat(result.getTotalPeriod()).isEqualTo(Optional.empty());
        assertThat(result.getVerticalTotalValues()).isEmpty();
        assertThat(result.getItemDetails()).hasSize(2);
        //1
        {
            assertThat(result.getItemDetails().get(0).getTotalPeriod()).isEqualTo(Optional.empty());
            assertThat(result.getItemDetails().get(0)).extracting(
                    x -> x.getCode(),
                    x -> x.getDisplayInfo().getCode(),
                    x -> x.getDisplayInfo().getName()
            ).containsExactly("001", "CD_01", "Name_1");
            assertThat(result.getItemDetails().get(0).getVerticalTotalList()).isEmpty();
            assertThat(result.getItemDetails().get(0).getChildHierarchyList()).hasSize(1);
            //1.1
            {
                assertThat(result.getItemDetails().get(0).getChildHierarchyList().get(0).getTotalPeriod()).isEqualTo(Optional.empty());
                assertThat(result.getItemDetails().get(0).getChildHierarchyList().get(0)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName()
                ).containsExactly("001", "001", "Name_1");
                assertThat(result.getItemDetails().get(0).getChildHierarchyList().get(0).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(tuple(2, YearMonth.of(2021, 6), null));
                assertThat(result.getItemDetails().get(0).getChildHierarchyList().get(0).getChildHierarchyList()).isEmpty();
            }
        }

        //2
        {
            assertThat(result.getItemDetails().get(1).getTotalPeriod()).isEqualTo(Optional.empty());
            assertThat(result.getItemDetails().get(1)).extracting(
                    x -> x.getCode(),
                    x -> x.getDisplayInfo().getCode(),
                    x -> x.getDisplayInfo().getName()
            ).containsExactly("002", "CD_02", "Name_2");
            assertThat(result.getItemDetails().get(1).getVerticalTotalList()).isEmpty();
            assertThat(result.getItemDetails().get(1).getChildHierarchyList()).hasSize(1);
            //2.1
            {
                assertThat(result.getItemDetails().get(1).getChildHierarchyList().get(0).getTotalPeriod()).isEqualTo(Optional.empty());
                assertThat(result.getItemDetails().get(1).getChildHierarchyList().get(0)).extracting(
                        x -> x.getCode(),
                        x -> x.getDisplayInfo().getCode(),
                        x -> x.getDisplayInfo().getName()
                ).containsExactly("002", "002", "Name_2");
                assertThat(result.getItemDetails().get(1).getChildHierarchyList().get(0).getVerticalTotalList())
                        .extracting(
                                VerticalValueDaily::getWorkingHours,
                                VerticalValueDaily::getYearMonth,
                                VerticalValueDaily::getDate)
                        .containsExactly(tuple(4, YearMonth.of(2021, 6), null));
                assertThat(result.getItemDetails().get(1).getChildHierarchyList().get(0).getChildHierarchyList()).isEmpty();
            }
        }
    }

    /**
     * displayVerticalHorizontalTotal = NOT_USE
     * TotalUnit = YEAR_MONTH
     * summaryItemList EMPTY
     */
    @Test
    public void createOutputContent_unitMonth_disp_notUse_summaryItemList_empty() {
        val detailFormatSetting = DetailFormatSetting.create(DisplayFormat.MINUTE, TotalUnit.YEAR_MONTH, NotUseAtr.NOT_USE, Collections.emptyList());
        val instance = new ManHourSummaryTableFormat(new ManHourSummaryTableCode("CD01"), new ManHourSummaryTableName("NAME01"), detailFormatSetting);

        // Action
        val result = instance.createOutputContent(dateList, yearMonthList, workDetail, masterNameInfo);

        // Assertion
        assertThat(result.getTotalPeriod()).isEqualTo(Optional.empty());
        assertThat(result.getVerticalTotalValues()).isEmpty();
        assertThat(result.getItemDetails()).isEmpty();
    }

    /**
     * displayVerticalHorizontalTotal = USE
     * TotalUnit = YEAR_MONTH
     * summaryItemList EMPTY
     */
    @Test
    public void createOutputContent_unitMonth_disp_Use_summaryItemList_empty() {
        val detailFormatSetting = DetailFormatSetting.create(DisplayFormat.MINUTE, TotalUnit.YEAR_MONTH, NotUseAtr.USE, Collections.emptyList());
        val instance = new ManHourSummaryTableFormat(new ManHourSummaryTableCode("CD01"), new ManHourSummaryTableName("NAME01"), detailFormatSetting);

        // Action
        val result = instance.createOutputContent(dateList, yearMonthList, workDetail, masterNameInfo);

        // Assertion
        assertThat(result.getTotalPeriod().get()).isEqualTo(0);
        assertThat(result.getItemDetails()).isEmpty();
        assertThat(result.getVerticalTotalValues())
                .extracting(
                        VerticalValueDaily::getWorkingHours,
                        VerticalValueDaily::getYearMonth,
                        VerticalValueDaily::getDate)
                .containsExactly(tuple(0, YearMonth.of(2021, 6), null));
    }
}
