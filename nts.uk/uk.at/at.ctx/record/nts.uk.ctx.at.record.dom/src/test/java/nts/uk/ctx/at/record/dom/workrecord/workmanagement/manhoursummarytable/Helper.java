package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.dom.adapter.function.alarmworkplace.EmployeeInfoImport;
import nts.uk.ctx.at.shared.dom.adapter.workplace.config.info.WorkplaceInfor;

import java.util.*;

public class Helper {
    public static class MasterNameInfo {
        public static List<WorkplaceInfor> workplaceInfos(boolean isCodeNull, boolean isNameNull) {
            return Collections.singletonList(
                    new WorkplaceInfor("001", "hierarchy01", isCodeNull ? null : "wkpCode01", "wkpName1",
                            isNameNull ? null : "wkpName1", "", "workplaceExternalCode1")
            );
        }

        public static List<EmployeeInfoImport> empInfos(boolean isCodeNull, boolean isNameNull) {
            return Collections.singletonList(
                    new EmployeeInfoImport("001", isCodeNull ? null : "CD_01", isNameNull ? null : "Name_1")
            );
        }

        public static List<TaskImport> tasks(boolean isCodeNull, boolean isNameNull) {
            return Collections.singletonList(
                    new TaskImport(isCodeNull ? null : "001", 1, isNameNull ? null : "Name_1")
            );
        }

        public static List<WorkplaceInfor> affWkps = Collections.singletonList(new WorkplaceInfor("001", "hierarchy01", "wkpCode01", "wkpName1",
                "wkpName1", "", "workplaceExternalCode1"));

        public static List<WorkplaceInfor> wkps = Collections.singletonList(new WorkplaceInfor("001", "hierarchy02", "wkpCode02", "wkpName2",
                "wkpName2", "", "workplaceExternalCode2"));

        public static List<EmployeeInfoImport> emps = Collections.singletonList(new EmployeeInfoImport("001", "CD_03", "Name_3"));

        public static List<TaskImport> tasks1 = Collections.singletonList(new TaskImport("001", 1, "Name_4"));

        public static List<TaskImport> tasks2 = Collections.singletonList(new TaskImport("001", 1, "Name_5"));

        public static List<TaskImport> tasks3 = Collections.singletonList(new TaskImport("001", 1, "Name_6"));

        public static List<TaskImport> tasks4 = Collections.singletonList(new TaskImport("001", 1, "Name_7"));

        public static List<TaskImport> tasks5 = Collections.singletonList(new TaskImport("001", 1, "Name_8"));
    }

    public static List<WorkDetailData> workDetailDataList(int maxSize) {
        List<WorkDetailData> lstResult = new ArrayList<>();
        for (int i = 1; i <= maxSize; i++) {
            lstResult.add(new WorkDetailData(
                    "00" + i,
                    GeneralDate.fromString("2021/05/31", "yyyy/MM/dd").addDays(i),
                    i,
                    "00" + i,
                    "00" + i,
                    "00" + i,
                    "00" + i,
                    "00" + i,
                    "00" + i,
                    "00" + i,
                    i * 2
            ));
        }
        return lstResult;
    }

    public static WorkDetailData workDetailData = new WorkDetailData(
            "SID_001",
            GeneralDate.today(),
            1,
            "AFF_WKP_001",
            "WKP_001",
            "TASK1_001",
            "TASK2_001",
            "TASK3_001",
            "TASK4_001",
            "TASK5_001",
            2);

    public static List<GeneralDate> dateList = Arrays.asList(
            GeneralDate.fromString("2021/06/01", "yyyy/MM/dd"),
            GeneralDate.fromString("2021/06/02", "yyyy/MM/dd"),
            GeneralDate.fromString("2021/06/03", "yyyy/MM/dd"));

    public static class verticalValueDaily {
        public static List<VerticalValueDaily> getList(int num) {
            return Arrays.asList(
                    new VerticalValueDaily(num, YearMonth.of(2021, 6), GeneralDate.fromString("2021/06/01", "yyyy/MM/dd")),
                    new VerticalValueDaily(num * 2, YearMonth.of(2021, 6), GeneralDate.fromString("2021/06/02", "yyyy/MM/dd")),
                    new VerticalValueDaily(num * 3, YearMonth.of(2021, 6), GeneralDate.fromString("2021/06/03", "yyyy/MM/dd")));
        }
    }

    public static class summaryItemDetail {
        public static List<SummaryItemDetail> getItemDetails(int unit) {
            return Arrays.asList(
                    new SummaryItemDetail(
                            "01",
                            new DisplayInformation("displayCode01", "displayName01"),
                            Arrays.asList(
                                    new SummaryItemDetail(
                                            "0101",
                                            new DisplayInformation("displayCode0101", "displayName0101"),
                                            Collections.emptyList(),
                                            Arrays.asList(
                                                    new VerticalValueDaily(1, unit == 1 ? YearMonth.of(2021, 6) : null, unit == 0 ? GeneralDate.fromString("2021/06/01", "yyyy/MM/dd") : null),
                                                    new VerticalValueDaily(1, unit == 1 ? YearMonth.of(2021, 6) : null, unit == 0 ? GeneralDate.fromString("2021/06/02", "yyyy/MM/dd") : null),
                                                    new VerticalValueDaily(1, unit == 1 ? YearMonth.of(2021, 6) : null, unit == 0 ? GeneralDate.fromString("2021/06/03", "yyyy/MM/dd") : null)),
                                            Optional.of(3)
                                    ),
                                    new SummaryItemDetail(
                                            "0102",
                                            new DisplayInformation("displayCode0102", "displayName0102"),
                                            Collections.emptyList(),
                                            Arrays.asList(
                                                    new VerticalValueDaily(1, unit == 1 ? YearMonth.of(2021, 6) : null, unit == 0 ? GeneralDate.fromString("2021/06/01", "yyyy/MM/dd") : null),
                                                    new VerticalValueDaily(1, unit == 1 ? YearMonth.of(2021, 6) : null, unit == 0 ? GeneralDate.fromString("2021/06/02", "yyyy/MM/dd") : null),
                                                    new VerticalValueDaily(1, unit == 1 ? YearMonth.of(2021, 6) : null, unit == 0 ? GeneralDate.fromString("2021/06/03", "yyyy/MM/dd") : null)),
                                            Optional.of(3)
                                    )
                            ),
                            Arrays.asList(
                                    new VerticalValueDaily(2, unit == 1 ? YearMonth.of(2021, 6) : null, unit == 0 ? GeneralDate.fromString("2021/06/01", "yyyy/MM/dd") : null),
                                    new VerticalValueDaily(2, unit == 1 ? YearMonth.of(2021, 6) : null, unit == 0 ? GeneralDate.fromString("2021/06/02", "yyyy/MM/dd") : null),
                                    new VerticalValueDaily(2, unit == 1 ? YearMonth.of(2021, 6) : null, unit == 0 ? GeneralDate.fromString("2021/06/03", "yyyy/MM/dd") : null)),
                            Optional.of(6)
                    ),
                    new SummaryItemDetail(
                            "02",
                            new DisplayInformation("displayCode02", "displayName02"),
                            Arrays.asList(
                                    new SummaryItemDetail(
                                            "0201",
                                            new DisplayInformation("displayCode0201", "displayName0201"),
                                            Collections.emptyList(),
                                            Arrays.asList(
                                                    new VerticalValueDaily(1, unit == 1 ? YearMonth.of(2021, 6) : null, unit == 0 ? GeneralDate.fromString("2021/06/01", "yyyy/MM/dd") : null),
                                                    new VerticalValueDaily(1, unit == 1 ? YearMonth.of(2021, 6) : null, unit == 0 ? GeneralDate.fromString("2021/06/02", "yyyy/MM/dd") : null),
                                                    new VerticalValueDaily(1, unit == 1 ? YearMonth.of(2021, 6) : null, unit == 0 ? GeneralDate.fromString("2021/06/03", "yyyy/MM/dd") : null)),
                                            Optional.of(3)
                                    ),
                                    new SummaryItemDetail(
                                            "0202",
                                            new DisplayInformation("displayCode0202", "displayName0202"),
                                            Collections.emptyList(),
                                            Arrays.asList(
                                                    new VerticalValueDaily(1, unit == 1 ? YearMonth.of(2021, 6) : null, unit == 0 ? GeneralDate.fromString("2021/06/01", "yyyy/MM/dd") : null),
                                                    new VerticalValueDaily(1, unit == 1 ? YearMonth.of(2021, 6) : null, unit == 0 ? GeneralDate.fromString("2021/06/02", "yyyy/MM/dd") : null),
                                                    new VerticalValueDaily(1, unit == 1 ? YearMonth.of(2021, 6) : null, unit == 0 ? GeneralDate.fromString("2021/06/03", "yyyy/MM/dd") : null)),
                                            Optional.of(3)
                                    )
                            ),
                            Arrays.asList(
                                    new VerticalValueDaily(2, unit == 1 ? YearMonth.of(2021, 6) : null, unit == 0 ? GeneralDate.fromString("2021/06/01", "yyyy/MM/dd") : null),
                                    new VerticalValueDaily(2, unit == 1 ? YearMonth.of(2021, 6) : null, unit == 0 ? GeneralDate.fromString("2021/06/02", "yyyy/MM/dd") : null),
                                    new VerticalValueDaily(2, unit == 1 ? YearMonth.of(2021, 6) : null, unit == 0 ? GeneralDate.fromString("2021/06/03", "yyyy/MM/dd") : null)),
                            Optional.of(6)
                    )
            );
        }

        public static List<SummaryItemDetail> getList(int maxSize) {
            List<SummaryItemDetail> lstSummaryItemDetail = new ArrayList<>();
            List<SummaryItemDetail> lstChildSummaryItemDetail = new ArrayList<>();

            for (int c = 1; c <= maxSize; c++) {
                lstChildSummaryItemDetail.add(new SummaryItemDetail(
                        "0" + c + 1,
                        new DisplayInformation("0" + c + 1, "NAME" + c + 1),
                        Collections.emptyList(),
                        Helper.verticalValueDaily.getList(c),
                        Optional.of(Helper.verticalValueDaily.getList(c).stream().mapToInt(VerticalValueDaily::getWorkingHours).sum())
                ));
            }
            for (int i = 1; i <= maxSize; i++) {
                lstSummaryItemDetail.add(new SummaryItemDetail(
                        "0" + i,
                        new DisplayInformation("0" + i, "NAME" + i),
                        lstChildSummaryItemDetail,
                        Helper.verticalValueDaily.getList(i),
                        Optional.of(Helper.verticalValueDaily.getList(i).stream().mapToInt(VerticalValueDaily::getWorkingHours).sum())
                ));
            }
            return lstSummaryItemDetail;
        }
    }

    public static class DetailFormatSetting {
        public static List<SummaryItem> summaryItemList = Arrays.asList(
                new SummaryItem(3, SummaryItemType.EMPLOYEE),
                new SummaryItem(1, SummaryItemType.AFFILIATION_WORKPLACE),
                new SummaryItem(2, SummaryItemType.WORKPLACE),
                new SummaryItem(8, SummaryItemType.TASK5),
                new SummaryItem(4, SummaryItemType.TASK1),
                new SummaryItem(6, SummaryItemType.TASK3),
                new SummaryItem(5, SummaryItemType.TASK2),
                new SummaryItem(7, SummaryItemType.TASK4)
        );
    }

    public static List<WorkplaceInfor> createWorkplaceInfos = Arrays.asList(
            new WorkplaceInfor("001", "hierarchy01", "wkpCode01", "wkpName1", "wkpName1", "", "workplaceExternalCode1"),
            new WorkplaceInfor("002", "hierarchy02", "wkpCode02", "wkpName2", "wkpName2", "", "workplaceExternalCode2")
    );

    public static List<EmployeeInfoImport> createEmpInfos = Arrays.asList(
            new EmployeeInfoImport("001", "CD_01", "Name_1"),
            new EmployeeInfoImport("002", "CD_02", "Name_2")
    );

    public static List<TaskImport> createTasks = Arrays.asList(
            new TaskImport("001", 1, "Name_1"),
            new TaskImport("002", 2, "Name_2")
    );
}
