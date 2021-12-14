package nts.uk.ctx.at.function.dom.alarm.alarmlist.persistenceextractresult;

import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.function.dom.adapter.alarm.AffAtWorkplaceExport;
import nts.uk.ctx.at.function.dom.adapter.toppagealarmpub.DeleteInfoAlarmImport;
import nts.uk.ctx.at.function.dom.adapter.toppagealarmpub.TopPageAlarmImport;
import nts.uk.ctx.at.shared.dom.alarmList.AlarmPatternCode;
import nts.uk.ctx.at.shared.dom.alarmList.AlarmPatternName;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.*;
import nts.uk.ctx.at.shared.dom.alarmList.AlarmCategory;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.AlarmListCheckType;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.ExtractionAlarmPeriodDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DumData {
    public static final List<AlarmEmployeeList> alarmListExtractResults =
            Arrays.asList(
                    new AlarmEmployeeList(
                            Arrays.asList(
                                    new AlarmExtractInfoResult(
                                            "ConditionNo1",
                                            new AlarmCheckConditionCode("C01"),
                                            AlarmCategory.DAILY,
                                            AlarmListCheckType.FixCheck,
                                            Arrays.asList(
                                                    new ExtractResultDetail(
                                                            new ExtractionAlarmPeriodDate(Optional.of(GeneralDate.today()), Optional.of(GeneralDate.today().addDays(2))),
                                                            "alarmName1",
                                                            "alarmContent1",
                                                            GeneralDateTime.now(),
                                                            Optional.of("S001"),
                                                            Optional.of("comment1"),
                                                            Optional.of("checkValue1")
                                                    ),
                                                    new ExtractResultDetail(
                                                            new ExtractionAlarmPeriodDate(Optional.of(GeneralDate.today()), Optional.of(GeneralDate.today().addDays(1))),
                                                            "alarmName2",
                                                            "alarmContent2",
                                                            GeneralDateTime.now(),
                                                            Optional.of("S002"),
                                                            Optional.of("comment2"),
                                                            Optional.of("checkValue2")
                                                    )
                                            )
                                    ),
                                    new AlarmExtractInfoResult(
                                            "ConditionNo2",
                                            new AlarmCheckConditionCode("C02"),
                                            AlarmCategory.SCHEDULE_DAILY,
                                            AlarmListCheckType.FreeCheck,
                                            Arrays.asList(
                                                    new ExtractResultDetail(
                                                            new ExtractionAlarmPeriodDate(Optional.of(GeneralDate.today()), Optional.of(GeneralDate.today().addDays(2))),
                                                            "alarmName3",
                                                            "alarmContent3",
                                                            GeneralDateTime.now(),
                                                            Optional.of("S003"),
                                                            Optional.of("comment"),
                                                            Optional.of("checkValue3")
                                                    ),
                                                    new ExtractResultDetail(
                                                            new ExtractionAlarmPeriodDate(Optional.of(GeneralDate.today()), Optional.of(GeneralDate.today().addDays(1))),
                                                            "alarmName4",
                                                            "alarmContent4",
                                                            GeneralDateTime.now(),
                                                            Optional.of("S004"),
                                                            Optional.of("comment4"),
                                                            Optional.of("checkValue4")
                                                    )
                                            )
                                    )
                            ),
                            "00001"
                    ),
                    new AlarmEmployeeList(
                            Arrays.asList(
                                    new AlarmExtractInfoResult(
                                            "ConditionNo1",
                                            new AlarmCheckConditionCode("C03"),
                                            AlarmCategory.DAILY,
                                            AlarmListCheckType.FixCheck,
                                            Arrays.asList(
                                                    new ExtractResultDetail(
                                                            new ExtractionAlarmPeriodDate(Optional.of(GeneralDate.today()), Optional.of(GeneralDate.today().addDays(2))),
                                                            "alarmName1",
                                                            "alarmContent1",
                                                            GeneralDateTime.now(),
                                                            Optional.of("workplaceId1"),
                                                            Optional.of("comment1"),
                                                            Optional.of("checkValue1")
                                                    ),
                                                    new ExtractResultDetail(
                                                            new ExtractionAlarmPeriodDate(Optional.of(GeneralDate.today()), Optional.of(GeneralDate.today().addDays(1))),
                                                            "alarmName2",
                                                            "alarmContent2",
                                                            GeneralDateTime.now(),
                                                            Optional.of("workplaceId2"),
                                                            Optional.of("comment2"),
                                                            Optional.of("checkValue2")
                                                    )
                                            )
                                    ),
                                    new AlarmExtractInfoResult(
                                            "ConditionNo2",
                                            new AlarmCheckConditionCode("C02"),
                                            AlarmCategory.SCHEDULE_DAILY,
                                            AlarmListCheckType.FreeCheck,
                                            Arrays.asList(
                                                    new ExtractResultDetail(
                                                            new ExtractionAlarmPeriodDate(Optional.of(GeneralDate.today()), Optional.of(GeneralDate.today().addDays(1))),
                                                            "alarmName3",
                                                            "alarmContent3",
                                                            GeneralDateTime.now(),
                                                            Optional.of("workplaceId3"),
                                                            Optional.of("comment"),
                                                            Optional.of("checkValue3")
                                                    ),
                                                    new ExtractResultDetail(
                                                            new ExtractionAlarmPeriodDate(Optional.of(GeneralDate.today()), Optional.of(GeneralDate.today().addDays(2))),
                                                            "alarmName4",
                                                            "alarmContent4",
                                                            GeneralDateTime.now(),
                                                            Optional.of("workplaceId4"),
                                                            Optional.of("comment4"),
                                                            Optional.of("checkValue4")
                                                    )
                                            )
                                    )
                            ),
                            "00002"
                    )
            );

    public static final PersistenceAlarmListExtractResult dumDomain =
            new PersistenceAlarmListExtractResult(
                    new AlarmPatternCode("01"),
                    new AlarmPatternName("日々の勤怠チェック"),
                    alarmListExtractResults,
                    "000000000001",
                    "Z"
            );

    public static final List<TopPageAlarmImport> alarmListInfos = Arrays.asList(
            new TopPageAlarmImport(
                    1,
                    GeneralDateTime.now().addDays(3),
                    "sya001",
                    0,
                    Optional.of("patternCode1"),
                    Optional.of("patternName1"),
                    Optional.of("linkUrl"),
                    Optional.of("displayMessage"),
                    new ArrayList<>(),
                    new ArrayList<>()
            ),
            new TopPageAlarmImport(
                    1,
                    GeneralDateTime.now().addDays(-1),
                    "sya002",
                    1,
                    Optional.of("patternCode2"),
                    Optional.of("patternName2"),
                    Optional.of("linkUrl"),
                    Optional.of("displayMessage"),
                    new ArrayList<>(),
                    new ArrayList<>()
            ),
            new TopPageAlarmImport(
                    1,
                    GeneralDateTime.now().addDays(1),
                    "sya003",
                    1,
                    Optional.of("patternCode3"),
                    Optional.of("patternName3"),
                    Optional.of("linkUrl"),
                    Optional.of("displayMessage"),
                    new ArrayList<>(),
                    new ArrayList<>()
            ),
            new TopPageAlarmImport(
                    1,
                    GeneralDateTime.now(),
                    "sya004",
                    1,
                    Optional.of("patternCode4"),
                    Optional.of("patternName4"),
                    Optional.of("linkUrl"),
                    Optional.of("displayMessage"),
                    new ArrayList<>(),
                    new ArrayList<>()
            ),
            new TopPageAlarmImport(
                    1,
                    GeneralDateTime.now(),
                    "sya005",
                    1,
                    Optional.of("patternCode4"),
                    Optional.of("patternName4"),
                    Optional.of("linkUrl"),
                    Optional.of("displayMessage"),
                    new ArrayList<>(),
                    new ArrayList<>()
            )
    );

    public static DeleteInfoAlarmImport deleteInfoAlarmImport = new DeleteInfoAlarmImport(
            1,
            Arrays.asList(
                    "sya001",
                    "sya002",
                    "sya003",
                    "sya004",
                    "sya005"
            ),
            0,
            Optional.of("patternCode1")
    );

    public static List<AffAtWorkplaceExport> affAtWorkplaceExports01 = Arrays.asList(
            new AffAtWorkplaceExport(
                    "sya001",
                    "S001",
                    "H01",
                    "n01"
            ),
            new AffAtWorkplaceExport(
                    "sya002",
                    "S001",
                    "H02",
                    "n02"
            ),
            new AffAtWorkplaceExport(
                    "sya003",
                    "S001",
                    "H03",
                    "n03"
            ),
            new AffAtWorkplaceExport(
                    "sya004",
                    "S002",
                    "H04",
                    "n04"
            ),
            new AffAtWorkplaceExport(
                    "sya005",
                    "S002",
                    "H05",
                    "n05"
            ),
            new AffAtWorkplaceExport(
                    "del001",
                    "S002",
                    "H01",
                    "n05"
            ),
            new AffAtWorkplaceExport(
                    "del002",
                    "S002",
                    "H02",
                    "n02"
            )
    );

    public static List<AffAtWorkplaceExport> affAtWorkplaceExports = Arrays.asList(
            new AffAtWorkplaceExport(
                    "sya001",
                    "S001",
                    "H01",
                    "n01"
            ),
            new AffAtWorkplaceExport(
                    "sya002",
                    "S001",
                    "H02",
                    "n02"
            ),
            new AffAtWorkplaceExport(
                    "sya003",
                    "S001",
                    "H03",
                    "n03"
            ),
            new AffAtWorkplaceExport(
                    "sya004",
                    "S002",
                    "H04",
                    "n04"
            ),
            new AffAtWorkplaceExport(
                    "sya005",
                    "S002",
                    "H05",
                    "n05"
            )
    );

    public static DeleteInfoAlarmImport deleteInfoAlarmImport2 = new DeleteInfoAlarmImport(
            1,
            Arrays.asList(
                    "sya004",
                    "sya005"
            ),
            0,
            Optional.of("patternCode1")
    );

    public static final List<TopPageAlarmImport> alarmListInfos2 = Arrays.asList(
            new TopPageAlarmImport(
                    1,
                    GeneralDateTime.now().addDays(2),
                    "sya001",
                    0,
                    Optional.of("patternCode1"),
                    Optional.of("patternName1"),
                    Optional.of("linkUrl"),
                    Optional.of("displayMessage"),
                    new ArrayList<>(),
                    new ArrayList<>()
            ),
            new TopPageAlarmImport(
                    1,
                    GeneralDateTime.now().addDays(1),
                    "sya002",
                    1,
                    Optional.of("patternCode2"),
                    Optional.of("patternName2"),
                    Optional.of("linkUrl"),
                    Optional.of("displayMessage"),
                    new ArrayList<>(),
                    new ArrayList<>()
            ),
            new TopPageAlarmImport(
                    1,
                    GeneralDateTime.now(),
                    "sya003",
                    1,
                    Optional.of("patternCode2"),
                    Optional.of("patternName2"),
                    Optional.of("linkUrl"),
                    Optional.of("displayMessage"),
                    new ArrayList<>(),
                    new ArrayList<>()
            )
    );

    public static List<String> employeeIds = Arrays.asList(
            "sya001",
            "sya002",
            "sya003",
            "sya004",
            "sya005"
    );

    public static final List<TopPageAlarmImport> alarmListInfos3 = Arrays.asList(
            new TopPageAlarmImport(
                    1,
                    GeneralDateTime.now().addDays(3),
                    "sya006",
                    0,
                    Optional.of("patternCode1"),
                    Optional.of("patternName1"),
                    Optional.of("linkUrl"),
                    Optional.of("displayMessage"),
                    new ArrayList<>(),
                    new ArrayList<>()
            )
    );

    public static List<AffAtWorkplaceExport> affAtWorkplaceExports2 = Arrays.asList(
            new AffAtWorkplaceExport(
                    "sya004",
                    "S002",
                    "H04",
                    "n04"
            ),
            new AffAtWorkplaceExport(
                    "sya005",
                    "S002",
                    "H05",
                    "n05"
            )
    );
}
