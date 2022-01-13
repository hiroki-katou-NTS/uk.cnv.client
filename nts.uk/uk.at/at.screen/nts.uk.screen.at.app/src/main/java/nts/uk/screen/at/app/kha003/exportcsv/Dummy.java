package nts.uk.screen.at.app.kha003.exportcsv;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.*;
import nts.uk.shr.com.enumcommon.NotUseAtr;

import java.util.*;

public class Dummy {
    private static final String DATE_FORMAT = "yyyy/MM/dd";

    public static class SummaryTableFormat {
        public static ManHourSummaryTableFormat create() {
            return new ManHourSummaryTableFormat(
                    new ManHourSummaryTableCode("01"),
                    new ManHourSummaryTableName("test"),
                    new DetailFormatSetting(
                            DisplayFormat.HEXA_DECIMAL,
                            TotalUnit.DATE,
                            NotUseAtr.USE,
                            Arrays.asList(
//                                    new SummaryItem(1, SummaryItemType.EMPLOYEE),
                                    new SummaryItem(1, SummaryItemType.TASK1),
                                    new SummaryItem(1, SummaryItemType.TASK3),
                                    new SummaryItem(1, SummaryItemType.TASK5)
                            )
                    )
            );
        }
    }

    public static class SummaryTableOutputContent {
        public static ManHourSummaryTableOutputContent create() {
            List<SummaryItemDetail> itemDetails = Arrays.asList(
                    new SummaryItemDetail(
                            "01",
                            new DisplayInformation("01", "A作業"),
                            Arrays.asList(
                                    new SummaryItemDetail(
                                            "0101",
                                            new DisplayInformation("0101", "AA作業"),
                                            Arrays.asList(
                                                    new SummaryItemDetail(
                                                            "010101",
                                                            new DisplayInformation("A01010101", "AAA作業"),
//                                                            Arrays.asList(
//                                                                    new SummaryItemDetail(
//                                                                            "010101",
//                                                                            new DisplayInformation("A01010101", "LEVEL4_01"),
//                                                                            Collections.emptyList(),
//                                                                            Arrays.asList(
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
//                                                                            Optional.of(3)
//                                                                    ),
//                                                                    new SummaryItemDetail(
//                                                                            "010101",
//                                                                            new DisplayInformation("A01010101", "LEVEL4_02"),
//                                                                            Collections.emptyList(),
//                                                                            Arrays.asList(
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
//                                                                            Optional.of(6)
//                                                                    )
//                                                            ),
                                                            Collections.emptyList(),
                                                            Arrays.asList(
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
                                                            Optional.of(9)
                                                    ),
                                                    new SummaryItemDetail(
                                                            "010102",
                                                            new DisplayInformation("A01010101", "AAB作業"),
//                                                            Arrays.asList(
//                                                                    new SummaryItemDetail(
//                                                                            "010101",
//                                                                            new DisplayInformation("A01010101", "LEVEL4_03"),
//                                                                            Collections.emptyList(),
//                                                                            Arrays.asList(
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
//                                                                            Optional.of(3)
//                                                                    ),
//                                                                    new SummaryItemDetail(
//                                                                            "010102",
//                                                                            new DisplayInformation("A01010102", "LEVEL4_04"),
//                                                                            Collections.emptyList(),
//                                                                            Arrays.asList(
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
//                                                                            Optional.of(6)
//                                                                    )
//                                                            ),
                                                            Collections.emptyList(),
                                                            Arrays.asList(
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
                                                            Optional.of(9)
                                                    )
                                            ),
                                            Arrays.asList(
                                                    new VerticalValueDaily(6, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                                    new VerticalValueDaily(6, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                                    new VerticalValueDaily(6, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
                                            Optional.of(18)
                                    ),
                                    // LEVEL1_02
                                    new SummaryItemDetail(
                                            "0102",
                                            new DisplayInformation("0102", "AB作業"),
                                            Arrays.asList(
                                                    new SummaryItemDetail(
                                                            "010201",
                                                            new DisplayInformation("010201", "ABA作業"),
//                                                            Arrays.asList(
//                                                                    new SummaryItemDetail(
//                                                                            "010201",
//                                                                            new DisplayInformation("010201", "LEVEL4_05"),
//                                                                            Collections.emptyList(),
//                                                                            Arrays.asList(
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
//                                                                            Optional.of(3)
//                                                                    ),
//                                                                    new SummaryItemDetail(
//                                                                            "010202",
//                                                                            new DisplayInformation("010202", "LEVEL4_06"),
//                                                                            Collections.emptyList(),
//                                                                            Arrays.asList(
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
//                                                                            Optional.of(6)
//                                                                    )
//                                                            ),
                                                            Collections.emptyList(),
                                                            Arrays.asList(
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
                                                            Optional.of(9)
                                                    ),
                                                    new SummaryItemDetail(
                                                            "010202",
                                                            new DisplayInformation("010202", "ABB作業"),
//                                                            Arrays.asList(
//                                                                    new SummaryItemDetail(
//                                                                            "01020201",
//                                                                            new DisplayInformation("01020201", "LEVEL4_07"),
//                                                                            Collections.emptyList(),
//                                                                            Arrays.asList(
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
//                                                                            Optional.of(3)
//                                                                    ),
//                                                                    new SummaryItemDetail(
//                                                                            "01020202",
//                                                                            new DisplayInformation("01020202", "LEVEL4_08"),
//                                                                            Collections.emptyList(),
//                                                                            Arrays.asList(
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
//                                                                            Optional.of(6)
//                                                                    )
//                                                            ),
                                                            Collections.emptyList(),
                                                            Arrays.asList(
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
                                                            Optional.of(9)
                                                    )
                                            ),
                                            Arrays.asList(
                                                    new VerticalValueDaily(6, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                                    new VerticalValueDaily(6, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                                    new VerticalValueDaily(6, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
                                            Optional.of(18)
                                    )
                            ),
                            Arrays.asList(
                                    new VerticalValueDaily(12, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                    new VerticalValueDaily(12, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                    new VerticalValueDaily(12, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
                            Optional.of(36)
                    ),
                    new SummaryItemDetail(
                            "02",
                            new DisplayInformation("02", "B作業"),
                            Arrays.asList(
                                    new SummaryItemDetail(
                                            "0202",
                                            new DisplayInformation("0202", "BA作業"),
                                            Arrays.asList(
                                                    new SummaryItemDetail(
                                                            "010101",
                                                            new DisplayInformation("A01010101", "BAA作業"),
//                                                            Arrays.asList(
//                                                                    new SummaryItemDetail(
//                                                                            "010101",
//                                                                            new DisplayInformation("A01010101", "LEVEL4_09"),
//                                                                            Collections.emptyList(),
//                                                                            Arrays.asList(
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
//                                                                            Optional.of(3)
//                                                                    ),
//                                                                    new SummaryItemDetail(
//                                                                            "010101",
//                                                                            new DisplayInformation("A01010101", "LEVEL4_10"),
//                                                                            Collections.emptyList(),
//                                                                            Arrays.asList(
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
//                                                                            Optional.of(6)
//                                                                    )
//                                                            ),
                                                            Collections.emptyList(),
                                                            Arrays.asList(
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
                                                            Optional.of(9)
                                                    ),
                                                    new SummaryItemDetail(
                                                            "010102",
                                                            new DisplayInformation("A01010101", "BAB作業"),
//                                                            Arrays.asList(
//                                                                    new SummaryItemDetail(
//                                                                            "010101",
//                                                                            new DisplayInformation("A01010101", "LEVEL4_11"),
//                                                                            Collections.emptyList(),
//                                                                            Arrays.asList(
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
//                                                                            Optional.of(3)
//                                                                    ),
//                                                                    new SummaryItemDetail(
//                                                                            "010102",
//                                                                            new DisplayInformation("A01010102", "LEVEL4_12"),
//                                                                            Collections.emptyList(),
//                                                                            Arrays.asList(
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
//                                                                            Optional.of(6)
//                                                                    )
//                                                            ),
                                                            Collections.emptyList(),
                                                            Arrays.asList(
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
                                                            Optional.of(9)
                                                    )
                                            ),
                                            Arrays.asList(
                                                    new VerticalValueDaily(6, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                                    new VerticalValueDaily(6, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                                    new VerticalValueDaily(6, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
                                            Optional.of(18)
                                    ),
                                    // LEVEL1_02
                                    new SummaryItemDetail(
                                            "0202",
                                            new DisplayInformation("0102", "BB作業"),
                                            Arrays.asList(
                                                    new SummaryItemDetail(
                                                            "010201",
                                                            new DisplayInformation("010201", "BBA作業"),
//                                                            Arrays.asList(
//                                                                    new SummaryItemDetail(
//                                                                            "010201",
//                                                                            new DisplayInformation("010201", "LEVEL4_13"),
//                                                                            Collections.emptyList(),
//                                                                            Arrays.asList(
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
//                                                                            Optional.of(3)
//                                                                    ),
//                                                                    new SummaryItemDetail(
//                                                                            "010202",
//                                                                            new DisplayInformation("010202", "LEVEL4_14"),
//                                                                            Collections.emptyList(),
//                                                                            Arrays.asList(
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
//                                                                            Optional.of(6)
//                                                                    )
//                                                            ),
                                                            Collections.emptyList(),
                                                            Arrays.asList(
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
                                                            Optional.of(9)
                                                    ),
                                                    new SummaryItemDetail(
                                                            "010202",
                                                            new DisplayInformation("010202", "BBB作業"),
//                                                            Arrays.asList(
//                                                                    new SummaryItemDetail(
//                                                                            "01020201",
//                                                                            new DisplayInformation("01020201", "LEVEL4_15"),
//                                                                            Collections.emptyList(),
//                                                                            Arrays.asList(
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(1, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
//                                                                            Optional.of(3)
//                                                                    ),
//                                                                    new SummaryItemDetail(
//                                                                            "01020202",
//                                                                            new DisplayInformation("01020202", "LEVEL4_16"),
//                                                                            Collections.emptyList(),
//                                                                            Arrays.asList(
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
//                                                                                    new VerticalValueDaily(2, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
//                                                                            Optional.of(6)
//                                                                    )
//                                                            ),
                                                            Collections.emptyList(),
                                                            Arrays.asList(
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                                                    new VerticalValueDaily(3, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
                                                            Optional.of(9)
                                                    )
                                            ),
                                            Arrays.asList(
                                                    new VerticalValueDaily(6, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                                    new VerticalValueDaily(6, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                                    new VerticalValueDaily(6, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
                                            Optional.of(18)
                                    )
                            ),
                            Arrays.asList(
                                    new VerticalValueDaily(12, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                                    new VerticalValueDaily(12, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                                    new VerticalValueDaily(12, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))),
//                            Optional.of(36)
                            Optional.of(2000)
                    )
            );

            List<VerticalValueDaily> verticalTotalValues = Arrays.asList(
                    new VerticalValueDaily(24500, null, GeneralDate.fromString("2021/06/01", DATE_FORMAT)),
                    new VerticalValueDaily(24600, null, GeneralDate.fromString("2021/06/02", DATE_FORMAT)),
                    new VerticalValueDaily(24700, null, GeneralDate.fromString("2021/06/03", DATE_FORMAT))
            );
            return new ManHourSummaryTableOutputContent(
                    itemDetails,
                    verticalTotalValues,
                    Optional.of(7200)
            );
        }
    }
}
