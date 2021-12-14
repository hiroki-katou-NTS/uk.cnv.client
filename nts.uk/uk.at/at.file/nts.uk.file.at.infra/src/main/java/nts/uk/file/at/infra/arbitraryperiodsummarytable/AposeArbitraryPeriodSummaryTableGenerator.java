package nts.uk.file.at.infra.arbitraryperiodsummarytable;

import com.aspose.cells.*;
import lombok.val;
import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.function.app.query.arbitraryperiodsummarytable.*;
import nts.uk.ctx.at.function.dom.arbitraryperiodsummarytable.OutputSettingOfArbitrary;
import nts.uk.ctx.at.function.dom.outputitemsofworkstatustable.enums.CommonAttributesOfForms;
import nts.uk.ctx.sys.gateway.dom.adapter.company.CompanyBsImport;
import nts.uk.file.at.app.export.arbitraryperiodsummarytable.ArbitraryPeriodSummaryDto;
import nts.uk.file.at.app.export.arbitraryperiodsummarytable.ArbitraryPeriodSummaryTableFileQuery;
import nts.uk.file.at.app.export.arbitraryperiodsummarytable.ArbitraryPeriodSummaryTableGenerator;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportContext;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportGenerator;
import nts.uk.shr.infra.file.report.masterlist.data.ColumnTextAlign;
import org.apache.logging.log4j.util.Strings;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class AposeArbitraryPeriodSummaryTableGenerator extends AsposeCellsReportGenerator implements ArbitraryPeriodSummaryTableGenerator {
    private static final String TEMPLATE_FILE_ADD = "report/KWR007_template_v3.xlsx";
    private static final String EXCEL_EXT = ".xlsx";
    private static final String PRINT_AREA = "";
    private static final String FORMAT_DATE = "yyyy/MM/dd";
    private static final int MAX_LINE_IN_PAGE = 40;
    private static final Integer HIERARCHY_LENGTH = 3;
    private static final int CHAR_LIMIT_ATTENDANCE = 12;


    @Override
    public void generate(FileGeneratorContext generatorContext, ArbitraryPeriodSummaryDto dataSource) {
        try {
            AsposeCellsReportContext reportContext = this.createContext(TEMPLATE_FILE_ADD);
            Workbook workbook = reportContext.getWorkbook();
            WorksheetCollection worksheets = workbook.getWorksheets();
            OutputSettingOfArbitrary ofArbitrary = dataSource.getOfArbitrary();
            String title = ofArbitrary != null ? ofArbitrary.getName().v() : "";

            Worksheet worksheetTemplate = worksheets.get(0);
            Worksheet worksheet = worksheets.get(1);

            worksheet.setName(title);

            settingPage(worksheet, dataSource, title);
            printContents(worksheetTemplate, worksheet, dataSource);
            worksheets.removeAt(0);
            worksheets.setActiveSheetIndex(0);
            reportContext.processDesigner();
            String fileName = title + "_" + GeneralDateTime.now().toString("yyyyMMddHHmmss");
            reportContext.saveAsExcel(this.createNewFile(generatorContext, fileName + EXCEL_EXT));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void settingPage(Worksheet worksheet,
                             ArbitraryPeriodSummaryDto dataSource, String title) {
        CompanyBsImport companyBsImport = dataSource.getCompanyInfo();
        String companyName = companyBsImport.getCompanyName();
        PageSetup pageSetup = worksheet.getPageSetup();
        pageSetup.setPaperSize(PaperSizeType.PAPER_A_4);
        pageSetup.setOrientation(PageOrientationType.LANDSCAPE);

        pageSetup.setHeader(0, "&7&\"ＭＳ ゴシック\"" + companyName);
        pageSetup.setHeader(1, "&12&\"ＭＳ ゴシック,Bold\"" + title);

        DateTimeFormatter fullDateTimeFormatter = DateTimeFormatter
                .ofPattern("yyyy/MM/dd  H:mm", Locale.JAPAN);
        pageSetup.setHeader(2,
                "&7&\"ＭＳ ゴシック\"" + LocalDateTime.now().format(fullDateTimeFormatter) + "\n" +
                        TextResource.localize("page") + " &P");
        pageSetup.setCenterHorizontally(true);

    }

    private void printContents(Worksheet worksheetTemplate, Worksheet worksheet, ArbitraryPeriodSummaryDto dataSource) {
        try {
            HorizontalPageBreakCollection pageBreaks = worksheet.getHorizontalPageBreaks();
            DetailOfArbitrarySchedule data = dataSource.getContent();
            if (data != null) {
                val query = dataSource.getQuery();
                List<AttendanceItemDisplayContents> contentsList = data.getContentsList();
                Map<Integer, CommonAttributesOfForms> mapIdAnAttribute =
                        contentsList.stream()
                                .filter(distinctByKey(AttendanceItemDisplayContents::getAttendanceItemId))
                                .collect(Collectors.toMap(AttendanceItemDisplayContents::getAttendanceItemId, AttendanceItemDisplayContents::getCommonAttributesOfForms));
                List<AttendanceDetailDisplayContents> detailDisplayContents = data.getDetailDisplayContents();
                List<AttendanceDetailDisplayContents> listTree = getDepWkpInfoTree(detailDisplayContents);
                List<WorkplaceTotalDisplayContent> totalDisplayContents = data.getTotalDisplayContents()
                        .stream().sorted(Comparator.comparing(WorkplaceTotalDisplayContent::getHierarchyCode)).collect(Collectors.toList());
                List<DisplayContent> totalAll = data.getTotalAll();
                List<CumulativeWorkplaceDisplayContent> cumulativeWorkplaceDisplayContents =
                        data.getCumulativeWorkplaceDisplayContents();
                DatePeriod period = dataSource.getPeriod();
                int count = 0;
                Cells cellsTemplate = worksheetTemplate.getCells();
                Cells cells = worksheet.getCells();
                int itemOnePage = 0;
                // C1_1
                printInfo(worksheetTemplate, worksheet, contentsList, period);
                count += 5;
                itemOnePage += 5;
                Integer pageBreakWplHierarchy = query.getPageBreakWplHierarchy();
                for (int q = 0; q < listTree.size(); q++) {
                    boolean isFist = true;
                    val subContent = listTree.get(q);
                    List<AttendanceDetailDisplayContents> contentsArrayList = new ArrayList<>();
                    toListChild(subContent, contentsArrayList);
                    List<AttendanceDetailDisplayContents> child = contentsArrayList
                            .stream().sorted(Comparator.comparing(AttendanceDetailDisplayContents::getLevel)).collect(Collectors.toList());

                    if (q != 0 && query.isPageBreakByWpl()) {
                        pageBreak(pageBreaks,count,cells);
                        count += 5;
                        itemOnePage = 5;
                    }
                    for (int i = 0; i < child.size(); i++) {
                        val content = child.get(i);
                        if (query.isDetail()) {
                            int wplHierarchy = content.getLevel();
                            if (i != 0 && query.isPageBreakByWpl() && pageBreakWplHierarchy == null) {
                                pageBreak(pageBreaks,count,cells);
                                count += 5;
                                itemOnePage = 5;
                            }
                            if (i != 0 && query.isPageBreakByWpl() && pageBreakWplHierarchy != null && (
                                    wplHierarchy <= pageBreakWplHierarchy)) {
                                pageBreak(pageBreaks,count,cells);
                                count += 5;
                                itemOnePage = 5;
                                isFist = true;
                            }
                            if (i != 0 && query.isPageBreakByWpl() && pageBreakWplHierarchy != null && (
                                    wplHierarchy > pageBreakWplHierarchy && isFist)) {
                                pageBreak(pageBreaks,count,cells);
                                count += 5;
                                itemOnePage = 5;
                                isFist = false;
                            }
                            if (q > 0 && (MAX_LINE_IN_PAGE - itemOnePage <= 2)) {
                                pageBreak(pageBreaks, count, cells);
                                count += 5;
                                itemOnePage = 5;
                            }
                            val listDisplaySid = content.getListDisplayedEmployees();
                            val listDisplayedEmployees = listDisplaySid.stream()
                                    .sorted(Comparator.comparing(DisplayedEmployee::getEmployeeCode)).collect(Collectors.toList());
                            cells.copyRow(cellsTemplate, itemOnePage <= 5 ? 5 : 16, count);
                            itemOnePage += 1;
                            //D1_1
                            cells.get(count, 0).setValue(TextResource.localize("KWR007_303")
                                    + content.getWorkplaceCd() + "　" + content.getWorkplaceName());
                            count += 1;
                            for (int j = 0; j < listDisplayedEmployees.size(); j++) {
                                val item = listDisplayedEmployees.get(j);
                                if (j % 2 == 0) {
                                    if (!checkLine(itemOnePage, MAX_LINE_IN_PAGE)) {
                                        pageBreak(pageBreaks,count,cells);
                                        count += 5;
                                        itemOnePage = 5;

                                        // Print workplace again if page break
                                        cells.copyRow(cellsTemplate, 5, count);
                                        itemOnePage += 1;
                                        cells.get(count, 0).setValue(TextResource.localize("KWR007_303")
                                                + content.getWorkplaceCd() + "　" + content.getWorkplaceName());
                                        count += 1;
                                    }
                                    cells.copyRows(cellsTemplate, 10, count, 2);

                                } else {
                                    if (!checkLine(itemOnePage, MAX_LINE_IN_PAGE)) {
                                        pageBreak(pageBreaks,count,cells);
                                        itemOnePage = 5;
                                        count += 5;

                                        // Print workplace again if page break
                                        cells.copyRow(cellsTemplate, 5, count);
                                        itemOnePage += 1;
                                        cells.get(count, 0).setValue(TextResource.localize("KWR007_303")
                                                + content.getWorkplaceCd() + "　" + content.getWorkplaceName());
                                        count += 1;
                                    }
                                    cells.copyRows(cellsTemplate, 8, count, 2);
                                }
                                // D2_1, D2_2
                                cells.get(count, 0).setValue(
                                        item.getEmployeeCode() + "　" + item.getEmployeeName());
                                itemOnePage += 2;
                                val contents = item.getContentList();
                                prinDetail(count, contents, cells, mapIdAnAttribute, query);
                                count += 2;
                            }
                        }

                        if (query.isWorkplaceTotal()) {
                            if (!query.isDetail()) {
                                cells.copyRow(cellsTemplate, 5, count);
                                itemOnePage += 1;
                                //D1_1
                                cells.get(count, 0).setValue(TextResource.localize("KWR007_303")
                                        + content.getWorkplaceCd() + "　" + content.getWorkplaceName());
                                count += 1;
                            }
                            val sumByWplOpt = totalDisplayContents
                                    .stream().filter(e -> e.getWorkplaceId().equals(content.getWorkplaceId())).findFirst();
                            if (sumByWplOpt.isPresent()) {
                                val sumByWpl = sumByWplOpt.get();
                                if (!checkLine(itemOnePage, MAX_LINE_IN_PAGE)) {
                                    pageBreak(pageBreaks,count,cells);
                                    itemOnePage = 5;
                                    count += 5;

                                    // #
                                    cells.copyRow(cellsTemplate, 5, count);
                                    itemOnePage += 1;
                                    cells.get(count, 0).setValue(TextResource.localize("KWR007_303")
                                            + content.getWorkplaceCd() + "　" + content.getWorkplaceName());
                                    count += 1;
                                }
                                cells.copyRows(cellsTemplate, 14, count, 2);
                                itemOnePage += 2;
                                // cells.clearContents(10, count, cells.getMaxRow(), cells.getMaxColumn());
                                cells.get(count, 0).setValue(TextResource.localize("KWR007_304"));
                                val listSum = sumByWpl.getListOfWorkplaces();
                                if (listSum != null) {
                                    prinDetail(count, listSum, cells, mapIdAnAttribute, query);
                                }
                                count += 2;
                            }
                        }
                    }
                }


                if (query.isCumulativeWorkplace()) {
                    for (CumulativeWorkplaceDisplayContent item : cumulativeWorkplaceDisplayContents) {
                        val listValue = item.getListOfWorkplaces();
                        if (!checkLine(itemOnePage, MAX_LINE_IN_PAGE)) {
                            pageBreak(pageBreaks,count,cells);
                            count += 5;
                            itemOnePage = 5;
                        }
                        cells.copyRows(cellsTemplate, 14, count, 2);
                        cells.get(count, 0).setValue(TextResource.localize("KWR007_305",String.valueOf(item.getHierarchy())));
                        itemOnePage += 2;
                        prinDetail(count, listValue, cells, mapIdAnAttribute, query);
                        count += 2;
                    }
                }
                if (query.isTotal()) {
                    if (!checkLine(itemOnePage, MAX_LINE_IN_PAGE)) {
                        pageBreak(pageBreaks,count,cells);
                        count += 5;
                    }
                    cells.copyRows(cellsTemplate, 14, count, 2);
                    cells.get(count, 0).setValue(TextResource.localize("KWR007_306"));
                    prinDetail(count, totalAll, cells, mapIdAnAttribute, query);
                }
                setTopBorder(cells, count - 1);
                PageSetup pageSetup = worksheet.getPageSetup();
                pageSetup.setPrintArea(PRINT_AREA + count);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private void toListChild(AttendanceDetailDisplayContents parent, List<AttendanceDetailDisplayContents> rs) {
        if(parent.getWorkplaceId()!=null)
        rs.add(parent);
        if (parent.getChildren() == null || parent.getChildren().size() == 0) return;
        val sub = parent.getChildren();
        for (AttendanceDetailDisplayContents aSub : sub) {
            toListChild(aSub, rs);
        }
    }

    private void printInfo(Worksheet worksheetTemplate, Worksheet worksheet,
                           List<AttendanceItemDisplayContents> contentsList, DatePeriod datePeriod) throws Exception {
        Cells cellsTemplate = worksheetTemplate.getCells();
        Cells cells = worksheet.getCells();
        cells.copyRows(cellsTemplate, 0, 0, 5);
        cells.clearContents(0, 0, cells.getMaxRow(), cells.getMaxColumn());
        cells.get(0, 0).setValue(TextResource.localize("KWR007_301")
                + datePeriod.start().toString(FORMAT_DATE) +"　"+ TextResource.localize("KWR007_307")
                +"　"+ datePeriod.end().toString(FORMAT_DATE));
        cells.get(1, 0).setValue(TextResource.localize("KWR007_302"));
        for (int i = 0; i < contentsList.size(); i++)
            if (i < 20) {
                cells.get(1, 1 + i).setValue(charLineBreak(contentsList.get(i).getAttendanceName(), CHAR_LIMIT_ATTENDANCE));
            } else if (i >= 20 && i < 40) {
                cells.get(3, 1 + i - 20).setValue(charLineBreak(contentsList.get(i).getAttendanceName(), CHAR_LIMIT_ATTENDANCE));
            }
    }

    /**
     * Format value
     */
    private String formatValue(Double valueDouble, CommonAttributesOfForms attributes, Boolean isZeroDisplay) {
        String rs = "";
        switch (attributes) {
            case TIME_OF_DAY:
                if (valueDouble != null) {
                    rs = convertToTime((int) valueDouble.intValue());
                }

                break;
            case DAYS:
                if (valueDouble != null) {
                    if (valueDouble != 0 || isZeroDisplay) {
                        DecimalFormat formatter2 = new DecimalFormat("#.#");
                        rs = formatter2.format(valueDouble) + TextResource.localize("KWR_1");
                    }
                }
                break;
            case TIME:
                if (valueDouble != null) {
                    val minute = (int) valueDouble.intValue();
                    if (minute != 0 || isZeroDisplay) {
                        rs = convertToTime(minute);
                    }
                }
                break;
            case AMOUNT_OF_MONEY:
                if (valueDouble != null) {
                    if (valueDouble != 0 || isZeroDisplay) {
                        DecimalFormat formatter3 = new DecimalFormat("#,###");
                        rs = formatter3.format((int) valueDouble.intValue()) + TextResource.localize("KWR_3");
                    }
                }
                break;
            case NUMBER_OF_TIMES:
                if (valueDouble != null) {
                    if (valueDouble != 0 || isZeroDisplay) {
                        DecimalFormat formatter1 = new DecimalFormat("#.#");
                        rs = formatter1.format(valueDouble) + TextResource.localize("KWR_2");
                    }
                }

                break;
        }
        return rs;
    }

    /**
     * Convert minute to HH:mm
     */
    private String convertToTime(int minute) {
        int minuteAbs = Math.abs(minute);
        if (minute < 0) {
            minuteAbs = Math.abs(minute +1440);
        }
        int hours = minuteAbs / 60;
        int minutes = minuteAbs % 60;
        return (minute < 0 ? "-" : "") + String.format("%d:%02d", hours, minutes);
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private boolean checkLine(int count, int maxItem) {
        return (maxItem - count) >= 2;
    }

    private List<AttendanceDetailDisplayContents> createTree(List<AttendanceDetailDisplayContents> lstHWkpInfo) {
        List<AttendanceDetailDisplayContents> lstReturn = new ArrayList<>();
        if (lstHWkpInfo.isEmpty())
            return lstReturn;
        // Higher hierarchyCode has shorter length
        int highestHierarchy = lstHWkpInfo.stream()
                .min((a, b) -> a.getHierarchyCode().length() - b.getHierarchyCode().length()).get().getHierarchyCode()
                .length();
        val totalDisplayContentComparator = Comparator.comparing(AttendanceDetailDisplayContents::getHierarchyCode);
        lstHWkpInfo = lstHWkpInfo.stream().sorted(totalDisplayContentComparator).collect(Collectors.toList());
        Iterator<AttendanceDetailDisplayContents> iteratorWkpHierarchy = lstHWkpInfo.iterator();
        // while have workplace
        while (iteratorWkpHierarchy.hasNext()) {
            // pop 1 item
            AttendanceDetailDisplayContents wkpHierarchy = iteratorWkpHierarchy.next();
            // convert
            AttendanceDetailDisplayContents dto = new AttendanceDetailDisplayContents(
                    wkpHierarchy.getWorkplaceId(),
                    wkpHierarchy.getWorkplaceCd(),
                    wkpHierarchy.getWorkplaceName(),
                    wkpHierarchy.getHierarchyCode(),
                    wkpHierarchy.getListDisplayedEmployees(),
                    wkpHierarchy.getLevel(),
                    new ArrayList<>());
            // build List
            this.pushToList(lstReturn, dto, wkpHierarchy.getHierarchyCode(), Strings.EMPTY, highestHierarchy);
        }
        return lstReturn;
    }

    private void pushToList(List<AttendanceDetailDisplayContents> lstReturn, AttendanceDetailDisplayContents dto, String hierarchyCode, String preCode,
                            int highestHierarchy) {
        if (hierarchyCode.length() == highestHierarchy) {
            // check duplicate code
            if (lstReturn.isEmpty()) {
                lstReturn.add(dto);
                return;
            }
            for (AttendanceDetailDisplayContents item : lstReturn) {
                if (!item.getWorkplaceId().equals(dto.getWorkplaceId())) {
                    lstReturn.add(dto);
                    break;
                }
            }
        } else {
            String searchCode = preCode.isEmpty() ? preCode + hierarchyCode.substring(0, highestHierarchy)
                    : preCode + hierarchyCode.substring(0, HIERARCHY_LENGTH);
            Optional<AttendanceDetailDisplayContents> optWorkplaceFindDto = lstReturn.stream()
                    .filter(item -> item.getHierarchyCode().equals(searchCode)).findFirst();
            if (!optWorkplaceFindDto.isPresent()) {
                return;
            }
            List<AttendanceDetailDisplayContents> currentItemChilds = optWorkplaceFindDto.get().getChildren();
            pushToList(currentItemChilds, dto, hierarchyCode.substring(HIERARCHY_LENGTH, hierarchyCode.length()),
                    searchCode, highestHierarchy);
        }
    }

    public List<AttendanceDetailDisplayContents> getDepWkpInfoTree(List<AttendanceDetailDisplayContents> listInfo) {
            List<String> listBase = new ArrayList<>();
            val listBase_1 = listInfo.stream().map(e->e.getHierarchyCode().substring(0, HIERARCHY_LENGTH)).distinct().collect(Collectors.toList());
            val listBase_2 = listInfo.stream().filter(x->x.getHierarchyCode().length()>=6).map(e->e.getHierarchyCode().substring(0,6)).collect(Collectors.toList());
            val listBase_3 = listInfo.stream().filter(x->x.getHierarchyCode().length()>6).map(AttendanceDetailDisplayContents::getHierarchyCode).collect(Collectors.toList());

            listBase.addAll(listBase_1);
            listBase.addAll(listBase_2);
            listBase.addAll(listBase_3);
            listBase = listBase.stream().distinct().collect(Collectors.toList());

            for (int i = 0; i < listBase.size() ; i++) {
                val hCode = listBase.get(i);
                val listSub = listInfo.stream().filter(e->e.getHierarchyCode()
                        .equals(hCode)).findFirst();
                if(!listSub.isPresent()){
                    listInfo.add(new AttendanceDetailDisplayContents(
                            null,
                            null,
                            null,
                            hCode
                            ,
                            Collections.emptyList(),
                            hCode.length()/3,
                            Collections.emptyList()
                    ));
                }
            }

        List<AttendanceDetailDisplayContents> listInfoHasHierarchyCode = new ArrayList<>();
        List<AttendanceDetailDisplayContents> listInfoHasNoHierarchyCode = new ArrayList<>();
        listInfo.forEach(e -> {
            if (e.getHierarchyCode().isEmpty()) {
                listInfoHasNoHierarchyCode.add(e);
            } else {
                listInfoHasHierarchyCode.add(e);
            }
        });
        List<AttendanceDetailDisplayContents> result = this.createTree(listInfoHasHierarchyCode);
        // convert list no hierarchy code to tree
        List<AttendanceDetailDisplayContents> noHierarchyCodeTree =
                listInfoHasNoHierarchyCode.stream().map(AttendanceDetailDisplayContents::toTreeDto).collect(Collectors.toList());
        // add list no hierarchy code to the end of the tree list
        result.addAll(noHierarchyCodeTree);
        return result;
    }

    private void prinDetail(int count, List<DisplayContent> contentList,
                            Cells cells, Map<Integer, CommonAttributesOfForms> mapIdAnAttribute, ArbitraryPeriodSummaryTableFileQuery query) {
        for (int k = 0; k < contentList.size(); k++)
            if (k < 20) {
                // Line 01:
                val itemLine1 = contentList.get(k);
                cells.get(count, 1 + k).setValue(formatValue(itemLine1.getValue()
                        , mapIdAnAttribute.getOrDefault(itemLine1.getAttendanceItemId(), null), query.isZeroDisplay()));
                Cell cell = cells.get(count, 1 + k);
                Style style =   cell.getStyle();
                style.setHorizontalAlignment(checkNumber( mapIdAnAttribute.getOrDefault(itemLine1.getAttendanceItemId(), null))? ColumnTextAlign.RIGHT.value:ColumnTextAlign.LEFT.value);
                cell.setStyle(style);
            } else if (k >= 20 && k < 40) {
                val itemLine2 = contentList.get(k);
                cells.get(count + 1, 1 + k - 20).setValue(formatValue(itemLine2.getValue()
                        , mapIdAnAttribute.getOrDefault(itemLine2.getAttendanceItemId(), null), query.isZeroDisplay()));

                Cell cell = cells.get(count + 1, 1 + k - 20);
                Style style =   cell.getStyle();
                style.setHorizontalAlignment(checkNumber( mapIdAnAttribute.getOrDefault(itemLine2.getAttendanceItemId(), null))? ColumnTextAlign.RIGHT.value:ColumnTextAlign.LEFT.value);
                cell.setStyle(style);
            }
    }
    private void pageBreak (HorizontalPageBreakCollection pageBreaks,int count,Cells cells) throws Exception {
        setTopBorderStyle(cells,count);
        pageBreaks.add(count);
        cells.copyRows(cells, 0, count, 5);
    }
    public boolean checkNumber(CommonAttributesOfForms attributes){
        return attributes == CommonAttributesOfForms.DAYS
                ||attributes == CommonAttributesOfForms.TIME_OF_DAY
                ||attributes == CommonAttributesOfForms.TIME
                ||attributes == CommonAttributesOfForms.AMOUNT_OF_MONEY
                ||attributes == CommonAttributesOfForms.NUMBER_OF_TIMES;
    }
    private void setTopBorderStyle(Cells cells,int index) {

        for (int i = 0; i < 21; i++) {
            val cell = cells.get(index - 1,i);
            Style style = cell.getStyle();
            style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
            cell.setStyle(style);
        }
    }

    private String charLineBreak(String source, int maxLength) {
        StringBuilder destination = new StringBuilder();
        for (int i = 1; i <= source.length(); i++) {
            if (i > maxLength) break;
            destination.append(source.charAt(i - 1));
            if(source.length()>4){
                if (i == 4) destination.append("\n");
                if(source.length()>8){
                    if (i == 8) destination.append("\n");
                }
            }
        }
        return destination.toString();
    }
    private void setTopBorder(Cells cells, int row) {
        for (int col = 0; col < 21; col++) {
            Cell cell = cells.get(row, col);
            Style style = cell.getStyle();
            style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
            cell.setStyle(style);
        }
    }

    private void printWorkplace(Cells cells, Cells cellsTemplate, int row, String wkpCode, String wkpName) throws Exception {
        cells.copyRow(cellsTemplate, 5, row);
        cells.get(row, 0).setValue(TextResource.localize("KWR007_303") + wkpCode + "　" + wkpName);
    }
}
