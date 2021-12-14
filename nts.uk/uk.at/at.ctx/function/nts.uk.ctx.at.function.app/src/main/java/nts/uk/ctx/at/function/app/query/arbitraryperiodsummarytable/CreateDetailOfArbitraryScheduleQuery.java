package nts.uk.ctx.at.function.app.query.arbitraryperiodsummarytable;


import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.function.dom.adapter.actualmultiplemonth.ActualMultipleMonthAdapter;
import nts.uk.ctx.at.function.dom.adapter.actualmultiplemonth.MonthlyRecordValueImport;
import nts.uk.ctx.at.function.dom.adapter.outputitemsofworkstatustable.*;
import nts.uk.ctx.at.function.dom.arbitraryperiodsummarytable.OutputSettingOfArbitrary;
import nts.uk.ctx.at.function.dom.commonform.AttendanceItemToPrint;
import nts.uk.ctx.at.function.dom.outputitemsofworkstatustable.dto.EmployeeInfor;
import nts.uk.ctx.at.function.dom.outputitemsofworkstatustable.dto.StatusOfEmployee;
import nts.uk.ctx.at.shared.dom.adapter.workplace.config.info.WorkplaceInfor;
import nts.uk.ctx.at.shared.dom.monthlyattditem.MonthlyAttendanceItem;
import nts.uk.ctx.at.shared.dom.monthlyattditem.MonthlyAttendanceItemRepository;
import nts.uk.ctx.at.shared.dom.monthlyattditem.aggregate.MonthlyAttItemCanAggregateRepository;
import nts.uk.ctx.at.shared.dom.scherec.byperiod.AttendanceTimeOfAnyPeriod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemName;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.service.CompanyMonthlyItemService;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 任意期間集計表の表示内容を作成する
 */
@Stateless
public class CreateDetailOfArbitraryScheduleQuery {

    @Inject
    private AffComHistAdapter affComHistAdapter;

    @Inject
    private AttendanceItemServiceAdapter attendanceItemServiceAdapter;
    @Inject
    private MonthlyAttItemCanAggregateRepository monthlyAttItemCanAggregateRepo;
    @Inject
    private CompanyMonthlyItemService companyMonthlyItemService;
    @Inject
    private MonthlyAttendanceItemRepository monthlyAttendanceItemRepository;
    @Inject
    private ActualMultipleMonthAdapter actualMultipleMonthAdapter;

    public DetailOfArbitrarySchedule getContentOfArbitrarySchedule(DatePeriod period,
                                                                   String aggrFrameCode,
                                                                   List<EmployeeInfor> employeeBasicInfoImportList,
                                                                   List<WorkplaceInfor> workplaceInforList,
                                                                   OutputSettingOfArbitrary ofArbitrary,
                                                                   boolean isDetail,
                                                                   boolean isWorkplaceTotal,
                                                                   boolean isTotal,
                                                                   boolean isCumulativeWorkplace,
                                                                   List<Integer> workplacePrintTargetList) {
        val cid = AppContexts.user().companyId();

        val listWplDistinct = workplaceInforList.stream().filter(distinctByKey(WorkplaceInfor::getWorkplaceId))
                .collect(Collectors.toList());
        val listSidDistinct = employeeBasicInfoImportList.stream().filter(distinctByKey(EmployeeInfor::getEmployeeId))
                .collect(Collectors.toList());

        Map<String, EmployeeInfor> mapEmployeeInfor =
                listSidDistinct.stream().collect(Collectors.toMap(EmployeeInfor::getEmployeeId, i -> i));

        List<String> lisSids = new ArrayList<>(mapEmployeeInfor.keySet());

        //1.  ⓪: <call> 社員の指定期間中の所属期間を取得する
        List<StatusOfEmployee> employees = affComHistAdapter.getListAffComHist(lisSids, period);
        List<AttendanceItemToPrint> outputItemList = ofArbitrary != null ?
                ofArbitrary.getOutputItemList() : Collections.emptyList();
        val listAttId = outputItemList.stream().map(AttendanceItemToPrint::getAttendanceId).distinct()
                .collect(Collectors.toList());
        //2.  ①: <call> 任意期間別実績を取得する
        val values = attendanceItemServiceAdapter.getRecordValues(lisSids, aggrFrameCode, listAttId);
        //val listAttId = listAttIds.stream().sorted(Integer::compareTo).collect(Collectors.toList());
        //3. [①.isEmpty()]
        if (values.values().isEmpty()) {
            throw new BusinessException("Msg_1894");
        }

        //4.  ② 集計可能勤怠項目ID
        List<Integer> getAggregableMonthlyAttId = monthlyAttItemCanAggregateRepo.getMonthlyAtdItemCanAggregate(cid)
                .stream()
                .map(t -> t.v().intValue())
                .collect(Collectors.toList());
        //5.  ③: <call> 月次の勤怠項目の名称を取得する
        //会社ID = ログイン会社ID
        //ロールID = ログイン社員の就業ロールID
        //印刷する勤怠項目．勤怠項目ID>
        val roleId = Optional.ofNullable(AppContexts.user().roles().forAttendance());

        val nameAttendanceItems = companyMonthlyItemService.getMonthlyItems(cid, roleId, listAttId, null);
        //6. ④: <call> 月次の勤怠項目を取得する
        List<MonthlyAttendanceItem> monthlyAttendanceItemList = this.monthlyAttendanceItemRepository.findByAttendanceItemId(cid, listAttId);
        // SUM THEO SID, ATTID
        Map<String, List<DisplayContent>> mapEplIdAndDisplayContent = new HashMap<>();

        for (StatusOfEmployee employee : employees) {
            val listValue = values.getOrDefault(employee.getEmployeeId(), new AnyPeriodRecordValuesExportDto(aggrFrameCode,Collections.emptyList()));
            if (listValue == null || listValue.getItemValues() == null) continue;
            val listItemSids = listValue.getItemValues();
            val listAtt = listItemSids.stream()
                    .filter(x -> checkAttId(getAggregableMonthlyAttId, x.getItemId()))
                    .collect(Collectors.toCollection(ArrayList::new));

            List<DisplayContent> rs = new ArrayList<>();
            for (Integer h : listAttId) {
                Double vl;
                val its = listAtt.stream().filter(q -> q.getItemId() == h && q.getValue() != null).collect(Collectors.toList());
                if (its != null && its.size() != 0) {
                    vl = its.stream().filter(q -> checkNumber(q.getValue())).mapToDouble(x -> Double.parseDouble(x.getValue())).sum();
                } else {
                    vl = null;
                }
                val itemPrint = outputItemList.stream().filter(x -> x.getAttendanceId() == h).findFirst();
                itemPrint.ifPresent(j -> {
                    val item = new DisplayContent(
                            vl,
                            j.getAttendanceId(),
                            j.getRanking()
                    );
                    rs.add(item);
                });
            }
            mapEplIdAndDisplayContent.put(employee.getEmployeeId(), rs);
        }
        // SUM THEO WPLID, ATTID
        Map<String, List<DisplayContent>> mapWplIdAndDisplayContent = new HashMap<>();
        listWplDistinct.forEach(e -> {
            val listEm = listSidDistinct.stream().filter(x -> x.getWorkPlaceId().equals(e.getWorkplaceId()))
                    .collect(Collectors.toList());
            List<DisplayContent> listValues = new ArrayList<>();
            List<DisplayContent> rs = new ArrayList<>();
            listEm.forEach(i -> {
                listValues.addAll(mapEplIdAndDisplayContent.getOrDefault(i.getEmployeeId(), Collections.emptyList()));
            });
            for (Integer h : listAttId) {
                Double vl;
                val its = listValues.stream().filter(q -> q.getAttendanceItemId() == h && q.getValue() != null)
                        .collect(Collectors.toList());
                if (its != null && its.size() != 0) {
                    vl = its.stream().filter(q -> checkNumber(q.getValue().toString()))
                            .mapToDouble(x -> (x.getValue())).sum();
                } else {
                    vl = null;
                }
                val itemPrint = outputItemList.stream().filter(x -> x.getAttendanceId() == h).findFirst();
                itemPrint.ifPresent(j -> {

                    val item = new DisplayContent(
                            vl,
                            j.getAttendanceId(),
                            j.getRanking()

                    );
                    rs.add(item);
                });
            }
            mapWplIdAndDisplayContent.put(e.getWorkplaceId(), rs);
        });


        Map<Integer, Integer> mapAttendanceItemToPrint =
                outputItemList.stream().filter(distinctByKey(AttendanceItemToPrint::getAttendanceId))
                        .collect(Collectors.toMap(AttendanceItemToPrint::getAttendanceId, AttendanceItemToPrint::getRanking));
        //7.
        List<AttendanceItemDisplayContents> contentsList = new ArrayList<>();

        List<AttendanceDetailDisplayContents> detailDisplayContents = new ArrayList<>();

        List<WorkplaceTotalDisplayContent> totalDisplayContents = new ArrayList<>();

        List<DisplayContent> totalAll = new ArrayList<>();

        List<CumulativeWorkplaceDisplayContent> cumulativeWorkplaceDisplayContents = new ArrayList<>();

        listAttId.forEach(
                e -> {
                    Optional<AttItemName> attendanceInfo = nameAttendanceItems.stream()
                            .filter(attd -> attd.getAttendanceItemId() == e)
                            .findFirst();
                    Optional<MonthlyAttendanceItem> monthlyAttendanceInfo = monthlyAttendanceItemList.stream()
                            .filter(attd -> attd.getAttendanceItemId() == e)
                            .findFirst();
                    if (attendanceInfo.isPresent() && monthlyAttendanceInfo.isPresent()) {
                        contentsList.add(new AttendanceItemDisplayContents(
                                monthlyAttendanceInfo.get().getPrimitiveValue(),
                                e,
                                //attendanceInfo.get().getAttendanceItemName(),
                                checkAttId(getAggregableMonthlyAttId, e) ? attendanceInfo.get().getAttendanceItemName() : "",
                                monthlyAttendanceInfo.get().getMonthlyAttendanceAtr(),
                                mapAttendanceItemToPrint.getOrDefault(e, null)
                        ));
                    }

                }
        );

        listWplDistinct.forEach(e -> {
            val epls = employeeBasicInfoImportList.stream()
                    .filter(j -> j.getWorkPlaceId().equals(e.getWorkplaceId())).collect(Collectors.toList());
            detailDisplayContents.add(new AttendanceDetailDisplayContents(
                    e.getWorkplaceId(),
                    e.getWorkplaceCode(),
                    e.getWorkplaceName(),
                    e.getHierarchyCode(),
                    epls.stream().map(i -> new DisplayedEmployee(
                            getListContent(mapEplIdAndDisplayContent.getOrDefault(i.getEmployeeId(), Collections.emptyList()),
                                    outputItemList, getAggregableMonthlyAttId),
                            i.getEmployeeId(),
                            i.getEmployeeCode(),
                            i.getEmployeeName()
                    )).collect(Collectors.toList())
            ));
            totalDisplayContents.add(new WorkplaceTotalDisplayContent(
                            e.getWorkplaceId(),
                            e.getWorkplaceCode(),
                            e.getWorkplaceName(),
                            e.getHierarchyCode(),
                            mapWplIdAndDisplayContent.getOrDefault(e.getWorkplaceId(), Collections.emptyList())
                    )
            );
        });

        if (!values.values().isEmpty()) {
            // SUM THEO ATTID
            val listValues = values.values().stream().flatMap(x -> x.getItemValues().stream())
                    .filter(x -> checkAttId(getAggregableMonthlyAttId, x.getItemId()))
                    .collect(Collectors.toList());
            for (Integer h : listAttId) {
                val its = listValues.stream().filter(q -> q.getItemId() == h && q.getValue() != null).collect(Collectors.toList());
                val itemPrint = outputItemList.stream().filter(x -> x.getAttendanceId() == h).findFirst();
                Double vl;
                if (its != null && its.size() != 0) {
                    vl = its.stream().filter(q -> checkNumber(q.getValue())).mapToDouble(x ->
                            Double.parseDouble(x.getValue())).sum();
                } else {
                    vl = null;
                }
                itemPrint.ifPresent(j -> {
                    val item = new DisplayContent(
                            vl,
                            j.getAttendanceId(),
                            j.getRanking()
                    );
                    totalAll.add(item);
                });
            }

            val listWPL = workplacePrintTargetList.stream().sorted().collect(Collectors.toList());
            Collections.reverse(listWPL);
            listWPL.forEach(i -> {
                        val item = totalDisplayContents.stream().filter(e ->
                                e.getLevel() >= i).collect(Collectors.toList());

                        List<DisplayContent> listOfWorkplaces = new ArrayList<>();
                        item.forEach(
                                k -> listOfWorkplaces.addAll(k.getListOfWorkplaces())
                        );
                        if (!item.isEmpty()) {
                            val m = item.get(0);
                            cumulativeWorkplaceDisplayContents.add(new CumulativeWorkplaceDisplayContent(
                                    m.getWorkplaceId(),
                                    m.getWorkplaceCode(),
                                    m.getWorkplaceName(),
                                    m.getHierarchyCode(),
                                    outputItemList.stream().map(e -> {
                                                val its = listOfWorkplaces.stream().filter(q -> q.getAttendanceItemId() == e.getAttendanceId()
                                                        && q.getValue() != null).collect(Collectors.toList());
                                                Double vl;
                                                if (its != null && its.size() != 0) {
                                                    vl = its.stream().filter(q -> checkNumber(q.getValue().toString())).mapToDouble(x ->
                                                            Double.parseDouble(x.getValue().toString())).sum();
                                                } else {
                                                    vl = null;
                                                }
                                                return new DisplayContent(
                                                        vl,
                                                        e.getAttendanceId(),
                                                        e.getRanking()
                                                );
                                            }

                                    ).collect(Collectors.toList()),
                                    i
                            ));
                        } else {
                            cumulativeWorkplaceDisplayContents.add(new CumulativeWorkplaceDisplayContent(
                                    "",
                                    "",
                                    "",
                                    "",
                                    Collections.emptyList(),
                                    i
                            ));
                        }
                    }
            );
        }

        val compareWplc = Comparator.comparing(AttendanceDetailDisplayContents::getWorkplaceCd);
        val totalDisplayContentComparator = Comparator.comparing(WorkplaceTotalDisplayContent::getHierarchyCode);
        detailDisplayContents.sort(Comparator.comparing(AttendanceDetailDisplayContents::getWorkplaceCd)
                .thenComparing(AttendanceDetailDisplayContents::getHierarchyCode));
        return new DetailOfArbitrarySchedule(
                contentsList,
                detailDisplayContents.stream().sorted(compareWplc).collect(Collectors.toList()),
                totalDisplayContents.stream().sorted(totalDisplayContentComparator).collect(Collectors.toList()),
                totalAll,
                cumulativeWorkplaceDisplayContents
        );

    }

    private List<DisplayContent> getListContent(List<DisplayContent> contentList, List<AttendanceItemToPrint> outputItemList,
                                                List<Integer> getAggregableMonthlyAttId) {
        val rs = new ArrayList<DisplayContent>();
        outputItemList.forEach(
                e -> {
                    val item0pt = contentList.stream().filter(j -> j.getAttendanceItemId() == e.getAttendanceId()).findFirst();
                    if (item0pt.isPresent()) {
                        val item = item0pt.get();
                        if (!checkAttId(getAggregableMonthlyAttId, item.getAttendanceItemId())) {
                            item.setValue(null);
                        }
                        rs.add(item);
                    }
                }
        );
        return rs;

    }

    private static boolean checkAttId(List<Integer> attIds, int attId) {
        return attIds.stream().anyMatch(e -> e == attId);
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private boolean checkNumber(String x) {
        if (x == null) {
            return false;
        }
        try {
            val db = Double.parseDouble(x);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private boolean checkInPeriod(List<DatePeriod> listPeriod, GeneralDate date) {
        boolean exit = false;
        for (DatePeriod e : listPeriod) {
            if (e.datesBetween().stream().anyMatch(i -> i.equals(date))) {
                exit = true;
                break;
            }
            exit = false;
        }
        return exit;
    }

    private boolean checkInYearMonthPeriod(List<DatePeriod> listPeriod, YearMonth yearMonth) {
        boolean exit = false;
        for (DatePeriod e : listPeriod) {
            if (e.datesBetween().stream().anyMatch(i -> i.yearMonth().equals(yearMonth))) {
                exit = true;
                break;
            }
            exit = false;
        }
        return exit;
    }
}
