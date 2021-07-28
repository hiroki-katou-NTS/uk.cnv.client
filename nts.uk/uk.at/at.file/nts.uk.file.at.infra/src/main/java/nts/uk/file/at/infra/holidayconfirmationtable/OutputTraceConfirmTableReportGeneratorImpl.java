package nts.uk.file.at.infra.holidayconfirmationtable;

import com.aspose.cells.*;
import com.aspose.pdf.HorizontalAlignment;
import lombok.val;
import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.function.app.query.arbitraryperiodsummarytable.*;
import nts.uk.ctx.at.function.app.query.holidayconfirmationtable.DisplayContentsOfSubLeaveConfirmationTable;
import nts.uk.ctx.at.function.app.query.holidayconfirmationtable.LinkingInformation;
import nts.uk.ctx.at.function.app.query.holidayconfirmationtable.OccurrenceAcquisitionDetails;
import nts.uk.ctx.at.function.dom.arbitraryperiodsummarytable.OutputSettingOfArbitrary;
import nts.uk.ctx.at.function.dom.outputitemsofworkstatustable.enums.CommonAttributesOfForms;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.MngDataStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.OccurrenceDigClass;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.ManagementDataRemainUnit;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.MngHistDataAtr;
import nts.uk.ctx.sys.gateway.dom.adapter.company.CompanyBsImport;
import nts.uk.ctx.sys.portal.dom.enums.MenuAtr;
import nts.uk.ctx.sys.portal.dom.standardmenu.StandardMenu;
import nts.uk.ctx.sys.portal.dom.standardmenu.StandardMenuRepository;
import nts.uk.file.at.app.export.arbitraryperiodsummarytable.ArbitraryPeriodSummaryDto;
import nts.uk.file.at.app.export.holidayconfirmationtable.*;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportContext;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportGenerator;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Stateless
public class OutputTraceConfirmTableReportGeneratorImpl extends AsposeCellsReportGenerator
        implements OutputTraceConfirmTableReportGenerator {
    @Inject
    private StandardMenuRepository standardMenuRepo;
    private static final String TEMPLATE_FILE_EXT = "report/KDR003_template.xlsx";
    private static final String EXCEL_EXT = ".xlsx";
    private static final int HEADER_ROW = 1;
    private static final int COLUMN_ER = 0;
    private static final int COLUMN_EMP = 1;
    private static final int COLUMN_CARRY_FORWARD = 2;
    private static final int COLUMN_OCCURRENCE = 3;
    private static final int COLUMN_USED = 4;
    private static final int COLUMN_REMAINING = 5;
    private static final int COLUMN_UNDIGESTED = 6;
    private static final int SUB_HEADER_ROW = 0;
    private static final String USER_GUIDE_COL = "J";
    private static final String EXPIRATION_COL = "Q";
    private static final int TEMPLATE_ROWS = 25;
    private static final int MAX_ROW_PER_PAGE = 51;

    @Override
    public void generate(FileGeneratorContext generatorContext, OutputTraceConfirmTableDataSource dataSource) {
        List<StandardMenu> menus = standardMenuRepo.findBySystem(dataSource.getCompanyInfo().getCompanyId(), 1);
        String menuName = menus.stream().filter(i -> i.getMenuAtr() == MenuAtr.Menu && i.getProgramId().equals("KDR003"))
                .findFirst().map(i -> i.getDisplayName().v()).orElse(TextResource.localize("KDR003_100"));
        try (AsposeCellsReportContext reportContext = this.createContext(TEMPLATE_FILE_EXT)) {
            this.setPageHeader(reportContext, dataSource, menuName);
            Workbook workbook = reportContext.getWorkbook();
            WorksheetCollection worksheets = workbook.getWorksheets();
            Worksheet sheet = worksheets.get(0);
            sheet.setName(menuName);
            this.printSubHeader(sheet.getCells(), dataSource);
            this.printHeader(sheet.getCells());
            this.printData(sheet, dataSource);
            reportContext.processDesigner();
            reportContext.saveAsExcel(this.createNewFile(generatorContext, this.getReportName(menuName + EXCEL_EXT)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    private void setPageHeader(AsposeCellsReportContext reportContext, OutputTraceConfirmTableDataSource dataSource, String menuName) {
        reportContext.setHeader(0, "&7&\"MS ゴシック\"" + dataSource.getCompanyInfo().getCompanyName());
        reportContext.setHeader(1, "&12&\"MS ゴシック,Bold\"" + menuName);
        DateTimeFormatter fullDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd  HH:mm");
        reportContext.setHeader(2, "&7&\"MS ゴシック\"" + LocalDateTime.now().format(fullDateTimeFormatter) + "\npage&P ");
    }

    private void printSubHeader(Cells cells, OutputTraceConfirmTableDataSource dataSource) {
        val st = dataSource.getMngUnit() != null && dataSource.getMngUnit() == 2 ? TextResource.localize("KDR003_20") : TextResource.localize("KDR003_19");
        cells.get(USER_GUIDE_COL + (SUB_HEADER_ROW + 1)).setValue(st);
        cells.get(EXPIRATION_COL + (SUB_HEADER_ROW + 1)).setValue(TextResource.localize("KDR003_123") + dataSource.getComSubstVacation().getCompensatoryAcquisitionUse().getExpirationTime().nameId);
    }

    private void printHeader(Cells cells) {
        cells.get(HEADER_ROW, COLUMN_EMP).setValue(TextResource.localize("KDR003_21"));
        cells.get(HEADER_ROW, COLUMN_CARRY_FORWARD).setValue(TextResource.localize("KDR003_22"));
        cells.get(HEADER_ROW, COLUMN_OCCURRENCE).setValue(TextResource.localize("KDR003_23"));
        cells.get(HEADER_ROW, COLUMN_USED).setValue(TextResource.localize("KDR003_24"));
        cells.get(HEADER_ROW, COLUMN_REMAINING).setValue(TextResource.localize("KDR003_25"));
        cells.get(HEADER_ROW, COLUMN_UNDIGESTED).setValue(TextResource.localize("KDR003_26"));
        cells.get(HEADER_ROW, 7).setValue(TextResource.localize("KDR003_27"));
        cells.get(HEADER_ROW, 9).setValue(TextResource.localize("KDR003_28"));
        cells.get(HEADER_ROW, 10).setValue(TextResource.localize("KDR003_29"));
        cells.get(HEADER_ROW, 11).setValue(TextResource.localize("KDR003_30"));
        cells.get(HEADER_ROW, 12).setValue(TextResource.localize("KDR003_31"));
        cells.get(HEADER_ROW, 13).setValue(TextResource.localize("KDR003_32"));
        cells.get(HEADER_ROW, 14).setValue(TextResource.localize("KDR003_33"));
        cells.get(HEADER_ROW, 15).setValue(TextResource.localize("KDR003_34"));
        cells.get(HEADER_ROW, 16).setValue(TextResource.localize("KDR003_35"));
        cells.get(HEADER_ROW, 17).setValue(TextResource.localize("KDR003_36"));
        cells.get(HEADER_ROW, 18).setValue(TextResource.localize("KDR003_37"));
    }

    private void printData(Worksheet sheet, OutputTraceConfirmTableDataSource dataSource) {
        Cells cells = sheet.getCells();
        Map<String, List<DisplayContentsOfSubLeaveConfirmationTable>> mapData = dataSource.getContents().stream().collect(Collectors.groupingBy(DisplayContentsOfSubLeaveConfirmationTable::getHierarchyCode));
        int row = 3, count = 0;
        List<Integer> wkpIndexes = new ArrayList<>();
        List<Integer> empIndexes = new ArrayList<>();
        List<String> wkpCodes = mapData.keySet().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        boolean isTime = dataSource.getMngUnit() == 2;
        for (String wkpCode : wkpCodes) {
            // copy for next workplace
            cells.insertRow(row);
            try {
                cells.copyRows(cells, row + 1, row, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // page break by workplace
            if (dataSource.getQuery().getPageBreak() == 2 && wkpCodes.indexOf(wkpCode) != 0) {
                sheet.getHorizontalPageBreaks().add(row);
                count = 0;
            }
            List<DisplayContentsOfSubLeaveConfirmationTable> contents = mapData.get(wkpCode);
            contents.sort(Comparator.comparing(DisplayContentsOfSubLeaveConfirmationTable::getEmployeeCode));
            cells.get(row, 0).setValue(TextResource.localize("KDR003_38") + wkpCode + " " + contents.get(0).getWorkplaceName());
            wkpIndexes.add(row);
            row++;
            count++;

            for (DisplayContentsOfSubLeaveConfirmationTable content : contents) {

                // page break by employee
                if (dataSource.getQuery().getPageBreak() == 1) {
                    if (contents.indexOf(content) != 0) {
                        cells.insertRow(row);
                        sheet.getHorizontalPageBreaks().add(row);
                        int lastWkpRow = wkpIndexes.get(wkpIndexes.size() - 1);
                        try {
                            cells.copyRows(cells, lastWkpRow, row, 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        wkpIndexes.add(row);
                        row++;
                    } else {
                        if (wkpCodes.indexOf(wkpCode) != 0) {
                            int lastWkpRow = wkpIndexes.get(wkpIndexes.size() - 1);
                            sheet.getHorizontalPageBreaks().add(lastWkpRow);
                        }
                    }
                    count = 1;
                }
                val isPresent = content.getObservationOfExitLeave().isPresent();
                if (dataSource.isLinking()) {
                    // print linked content
                    if(isPresent){
                        content.getObservationOfExitLeave().get().getListTyingInformation().sort(Comparator.comparing(LinkingInformation::getOccurrenceDate));
                    }
                    int col = 9;
                    int size = isPresent ? content.getObservationOfExitLeave().get().getListTyingInformation().size():1;
                    int loops = size > 0 && size % 10 == 0 ? (size / 10) : (size / 10 + 1);
                    for (int loop = 0; loop < loops; loop++) {
                        cells.insertRows(row, 2);
                        try {
                            cells.copyRows(cells, loop == loops - 1 ? row + 7 : row + 5, row, 2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (loop == 0) {
                            this.printCommonContent(cells, row, content, dataSource.getMngUnit());
                            empIndexes.add(row);
                            cells.get(row, 7).setValue(TextResource.localize("KDR003_39"));
                        }
                        cells.get(row, 8).setValue(TextResource.localize("KDR003_41"));
                        cells.get(row + 1, 8).setValue(TextResource.localize("KDR003_42"));
                        if(isPresent){
                            for (int i = 0; i < 10; i++) {
                                int idx = loop * 10 + i;
                                if (idx < size) {
                                    LinkingInformation linkingInfo = content.getObservationOfExitLeave().get().getListTyingInformation().get(idx);
                                    if (i > 0) {
                                        LinkingInformation prevLinkingInfo = content.getObservationOfExitLeave().get().getListTyingInformation().get(idx - 1);
                                        if (linkingInfo.getOccurrenceDate().equals(prevLinkingInfo.getOccurrenceDate())
                                                && linkingInfo.getDateOfUse().v() == 0.5
                                                && prevLinkingInfo.getDateOfUse().v() == 0.5) {
                                            cells.merge(row, col + i - 1, 1, 2);
                                            String value = this.formatDate(
                                                    OccurrenceDigClass.OCCURRENCE,
                                                    linkingInfo.getOccurrenceDate(),
                                                    1.0,
                                                    dataSource.getQuery().getHowToPrintDate(),
                                                    content.getObservationOfExitLeave().get().getOccurrenceAcquisitionDetailsList()
                                            );
                                            cells.get(row, col + i - 1).setValue(value);
                                            Style style = cells.get(row, col + i - 1).getStyle();
                                            style.setHorizontalAlignment(HorizontalAlignment.Center);
                                            cells.get(row, col + i - 1).setStyle(style);
                                        } else {
                                            val value = this.formatDate(
                                                    OccurrenceDigClass.OCCURRENCE,
                                                    linkingInfo.getOccurrenceDate(),
                                                    linkingInfo.getDateOfUse().v(),
                                                    dataSource.getQuery().getHowToPrintDate(),
                                                    content.getObservationOfExitLeave().get().getOccurrenceAcquisitionDetailsList()
                                            );
                                            this.setValue(cells, row, col + i, value);
                                        }
                                        if (linkingInfo.getYmd().equals(prevLinkingInfo.getYmd())
                                                && linkingInfo.getDateOfUse().v() == 0.5
                                                && prevLinkingInfo.getDateOfUse().v() == 0.5) {
                                            cells.merge(row + 1, col + i - 1, 1, 2);
                                            String value = this.formatDate(
                                                    OccurrenceDigClass.DIGESTION,
                                                    linkingInfo.getYmd(),
                                                    linkingInfo.getDateOfUse().v(),
                                                    dataSource.getQuery().getHowToPrintDate(),
                                                    content.getObservationOfExitLeave().get().getOccurrenceAcquisitionDetailsList()
                                            );
                                            cells.get(row + 1, col + i - 1).setValue(value);
                                            Style style = cells.get(row + 1, col + i - 1).getStyle();
                                            style.setHorizontalAlignment(HorizontalAlignment.Center);
                                            cells.get(row + 1, col + i - 1).setStyle(style);
                                        } else {
                                            val value = this.formatDate(
                                                    OccurrenceDigClass.DIGESTION,
                                                    linkingInfo.getYmd(),
                                                    linkingInfo.getDateOfUse().v(),
                                                    dataSource.getQuery().getHowToPrintDate(),
                                                    content.getObservationOfExitLeave().get().getOccurrenceAcquisitionDetailsList()
                                            );
                                            this.setValue(cells, row + 1, col + i, value);
                                        }
                                    } else {
                                        val value = this.formatDate(
                                                OccurrenceDigClass.OCCURRENCE,
                                                linkingInfo.getOccurrenceDate(),
                                                linkingInfo.getDateOfUse().v(),
                                                dataSource.getQuery().getHowToPrintDate(),
                                                content.getObservationOfExitLeave().get().getOccurrenceAcquisitionDetailsList()
                                        );
                                        this.setValue(cells, row, col + i, value);
                                        val value2 = this.formatDate(
                                                OccurrenceDigClass.DIGESTION,
                                                linkingInfo.getYmd(),
                                                linkingInfo.getDateOfUse().v(),
                                                dataSource.getQuery().getHowToPrintDate(),
                                                content.getObservationOfExitLeave().get().getOccurrenceAcquisitionDetailsList()
                                        );
                                        this.setValue(cells, row + 1, col + i, value2);
                                    }
                                } else {
                                    break;
                                }
                            }
                        }
                        if (loops == 1) {
                            Style style1 = cells.get(row, 7).getStyle();
                            style1.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(CellBorderType.THIN);
                            cells.get(row, 7).setStyle(style1);
                            Style style2 = cells.get(row + 1, 7).getStyle();
                            style2.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(CellBorderType.THIN);
                            cells.get(row + 1, 7).setStyle(style2);
                        }
                        row += 2;
                        if (count + 2 > MAX_ROW_PER_PAGE) {
                            // add page break at the end of page
                            int lastEmpRow = empIndexes.get(empIndexes.size() - 1);
                            int lastWkpRow = wkpIndexes.get(wkpIndexes.size() - 1);
                            if (lastEmpRow == lastWkpRow + 1) {
                                sheet.getHorizontalPageBreaks().add(lastWkpRow);
                            } else {
                                cells.insertRow(lastEmpRow);
                                sheet.getHorizontalPageBreaks().add(lastEmpRow); // always add page break before insert row
                                try {
                                    cells.copyRows(cells, lastWkpRow, lastEmpRow, 1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                wkpIndexes.add(lastEmpRow);
                                empIndexes.set(empIndexes.size() - 1, lastEmpRow + 1);
                                row++;
                            }
                            count = row - wkpIndexes.get(wkpIndexes.size() - 1);
                        } else {
                            count += 2;
                        }
                    }
                }
                if (!content.getObservationOfExitLeave().get().getListTyingInformation().isEmpty()) {
                    // remove linked date
                    ListIterator<OccurrenceAcquisitionDetails> iterator = content.getObservationOfExitLeave().get().getOccurrenceAcquisitionDetailsList().listIterator();
                    while (iterator.hasNext()) {
                        OccurrenceAcquisitionDetails detail = iterator.next();
                        double linkingUsedDays;
                        if (detail.getOccurrenceDigClass() == OccurrenceDigClass.OCCURRENCE) {
                            linkingUsedDays = content.getObservationOfExitLeave().get().getListTyingInformation()
                                    .stream().filter(i -> i.getOccurrenceDate().equals(detail.getDate().getDayoffDate()
                                            .get())).mapToDouble(e -> e.getDateOfUse().v()).sum();
                        } else {
                            linkingUsedDays = content.getObservationOfExitLeave().get().getListTyingInformation()
                                    .stream().filter(i -> i.getYmd().equals(detail.getDate().getDayoffDate().get())).mapToDouble(e -> e.getDateOfUse().v()).sum();
                        }
                        if (linkingUsedDays >= detail.getNumberConsecuVacation().getDay().v()) {
                            iterator.remove();
                        } else if (linkingUsedDays > 0) {
                            detail.getNumberConsecuVacation().setDay(new ManagementDataRemainUnit(detail.getNumberConsecuVacation().getDay().v() - linkingUsedDays));
                            iterator.set(detail);
                        }
                    }
                }
                if(isPresent){
                    content.getObservationOfExitLeave().get().getOccurrenceAcquisitionDetailsList().sort(Comparator.comparing(i -> i.getDate().getDayoffDate().get()));
                }
                int col = 9;
                int size = isPresent? content.getObservationOfExitLeave().get().getOccurrenceAcquisitionDetailsList().size():1;
                int loops = size > 0 && size % 10 == 0 ? (size / 10) : (size / 10 + 1);

                for (int loop = 0; loop < loops; loop++) {
                    cells.insertRows(row, isTime ? 4 : 2);
                    try {
                        if (isTime) {
                            cells.copyRows(cells, loop == loops - 1 ? row + 25 : row + 21, row, 4);
                        } else {
                            cells.copyRows(cells, loop == loops - 1 ? row + 13 : row + 11, row, 2);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (loop == 0) {
                        if (!dataSource.isLinking()) {
                            this.printCommonContent(cells, row, content, dataSource.getMngUnit());
                            empIndexes.add(row);
                        }

                    }
                    cells.get(row, 7).setValue(TextResource.localize("KDR003_40"));
                    if (isTime) {
                        cells.merge(row, 8, 2, 1, true);
                        cells.merge(row + 2, 8, 2, 1, true);
                    }
                    cells.get(row, 8).setValue(TextResource.localize("KDR003_41"));
                    cells.get(isTime ? row + 2 : row + 1, 8).setValue(TextResource.localize("KDR003_42"));
                    if(isPresent)
                    for (int i = 0; i < 10; i++) {
                        int idx = loop * 10 + i;
                        if (idx < size) {
                            OccurrenceAcquisitionDetails acquisitionDetail = content.getObservationOfExitLeave().get().getOccurrenceAcquisitionDetailsList().get(idx);
                            if (!isTime) {
                                if (acquisitionDetail.getOccurrenceDigClass() == OccurrenceDigClass.OCCURRENCE) {
                                    val value = this.formatNoLinkedDate(acquisitionDetail, dataSource.getQuery().getHowToPrintDate());
                                    this.setValue(cells, row, col + i, value);
                                } else {
                                    val value = this.formatNoLinkedDate(acquisitionDetail, dataSource.getQuery().getHowToPrintDate());
                                    this.setValue(cells, row + 1, col + i, value);
                                }
                            } else {
                                Optional<AttendanceTime> timeOpt = acquisitionDetail.getNumberConsecuVacation().getTime();
                                if (acquisitionDetail.getOccurrenceDigClass() == OccurrenceDigClass.OCCURRENCE) {
                                    val value1 = this.formatNoLinkedDate(acquisitionDetail, dataSource.getQuery().getHowToPrintDate());
                                    this.setValue(cells, row, col + i, value1);
                                    val value2 = timeOpt.isPresent() ?
                                            formatNoLinkedTime(acquisitionDetail, timeOpt.get().valueAsMinutes()) : null;
                                    this.setValue(cells, row + 1, col + i, value2);

                                } else {
                                    val value3 = this.formatNoLinkedDate(acquisitionDetail, dataSource.getQuery().getHowToPrintDate());
                                    this.setValue(cells, row + 2, col + i, value3);

                                    val value4 = timeOpt.isPresent() ?
                                            formatNoLinkedTime(acquisitionDetail, timeOpt.get().valueAsMinutes()) : null;
                                    this.setValue(cells, row + 3, col + i, value4);

                                }

                            }

                        } else {
                            break;
                        }
                    }
                    val countTimeRow = isTime ? 4 : 2;
                    row += countTimeRow;
                    if (count + countTimeRow > MAX_ROW_PER_PAGE) {
                        // add page break at the end of page
                        int lastEmpRow = empIndexes.get(empIndexes.size() - 1);
                        int lastWkpRow = wkpIndexes.get(wkpIndexes.size() - 1);
                        if (lastEmpRow == lastWkpRow + 1) {
                            sheet.getHorizontalPageBreaks().add(lastWkpRow);
                        } else {
                            cells.insertRow(lastEmpRow);
                            try {
                                cells.copyRows(cells, lastWkpRow, lastEmpRow, 1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            sheet.getHorizontalPageBreaks().add(lastEmpRow); // always add page break before insert row
                            wkpIndexes.add(lastEmpRow);
                            empIndexes.set(empIndexes.size() - 1, lastEmpRow + 1);
                            row++;
                        }
                        count = row - wkpIndexes.get(wkpIndexes.size() - 1);
                    } else {
                        count += countTimeRow;
                    }
                }
            }

        }
        if (!dataSource.isLinking()) {
            cells.deleteColumn(7);
        }
        // remove template rows
        cells.deleteRows(row, TEMPLATE_ROWS);
        // set print area
        PageSetup pageSetup = sheet.getPageSetup();
        pageSetup.setPrintArea("A1:" + (dataSource.isLinking() ? "S" : "R") + row);
    }

    private void printCommonContent(Cells cells, int row, DisplayContentsOfSubLeaveConfirmationTable content, Integer mngUnit) {
        cells.get(row, COLUMN_EMP).setValue(content.getEmployeeCode() + " " + content.getEmployeeName());
        if(content.getObservationOfExitLeave().isPresent()){
            cells.get(row, COLUMN_ER).setValue(content.getObservationOfExitLeave().get().isEr() ? TextResource.localize("KDR003_122") : null);
            Double carry = null;
            Double occ = null;
            Double numofUse = null;
            Double numberOfRemaining = null;
            Double undeterminedNumber = null;
            if (content.getObservationOfExitLeave().isPresent() && mngUnit != null && mngUnit == 1) {
                carry = content.getObservationOfExitLeave().get().getNumberCarriedForward().getLeaveRemainingDayNumber().v();
                occ = content.getObservationOfExitLeave().get().getTotalNumberOfSubstituteHolidays().getNumberOfDate().v();
                numofUse = content.getObservationOfExitLeave().get().getNumOfUse().getNumOfDate().v();
                numberOfRemaining = content.getObservationOfExitLeave().get().getNumberOfRemaining().getNumberOfDate().v();
                undeterminedNumber = content.getObservationOfExitLeave().get().getUndeterminedNumber().getNumOfDate().v();
            } else if (content.getObservationOfExitLeave().isPresent() && mngUnit != null && mngUnit == 2) {
                val carryTimeOpt = content.getObservationOfExitLeave().get().getNumberCarriedForward().getTime();
                val occOpt = content.getObservationOfExitLeave().get().getTotalNumberOfSubstituteHolidays().getTime();
                val numofUseOpt = content.getObservationOfExitLeave().get().getNumOfUse().getTime();
                val numberOfRemainingOpt = content.getObservationOfExitLeave().get().getNumberOfRemaining().getTime();
                val undeterminedNumberOpt = content.getObservationOfExitLeave().get().getUndeterminedNumber().getTime();
                if (carryTimeOpt.isPresent()) {
                    carry = carryTimeOpt.get().v().doubleValue();
                }
                if (occOpt.isPresent()) {
                    occ = occOpt.get().v().doubleValue();
                }
                if (numofUseOpt.isPresent()) {
                    numofUse = numofUseOpt.get().v().doubleValue();
                }
                if (numberOfRemainingOpt.isPresent()) {
                    numberOfRemaining = numberOfRemainingOpt.get().v().doubleValue();
                }
                if (undeterminedNumberOpt.isPresent()) {
                    undeterminedNumber = undeterminedNumberOpt.get().v().doubleValue();
                }
            }
            if (mngUnit!= null && mngUnit == 2) {
                cells.get(row, COLUMN_CARRY_FORWARD).setValue(carry == null ? "" : convertToTime(carry.intValue()));
                cells.get(row, COLUMN_OCCURRENCE).setValue(occ == null ? "" : convertToTime(occ.intValue()));
                cells.get(row, COLUMN_USED).setValue(numofUse == null ? "" : convertToTime(numofUse.intValue()));

                cells.get(row, COLUMN_REMAINING).setValue(numberOfRemaining == null ? "" : convertToTime(numberOfRemaining.intValue()));
            } else if(mngUnit!= null && mngUnit == 1) {
                cells.get(row, COLUMN_CARRY_FORWARD).setValue(String.format("%.1f", carry));
                cells.get(row, COLUMN_OCCURRENCE).setValue(String.format("%.1f", occ));

                cells.get(row, COLUMN_USED).setValue(String.format("%.1f", numofUse));

                cells.get(row, COLUMN_REMAINING).setValue(String.format("%.1f", numberOfRemaining));
            }
            if (numberOfRemaining != null && numberOfRemaining < 0) {
                Style remainingStyle = cells.get(row, COLUMN_REMAINING).getStyle();
                Font remainingFont = remainingStyle.getFont();
                remainingFont.setColor(Color.getRed());
                cells.get(row, COLUMN_REMAINING).setStyle(remainingStyle);
            }
            cells.get(row, COLUMN_UNDIGESTED).setValue(String.format("%.1f", undeterminedNumber));
            if (undeterminedNumber != null && undeterminedNumber != 0) {
                Style remainingStyle = cells.get(row, COLUMN_UNDIGESTED).getStyle();
                Font remainingFont = remainingStyle.getFont();
                remainingFont.setColor(Color.getRed());
                cells.get(row, COLUMN_UNDIGESTED).setStyle(remainingStyle);
            }
        }
    }

    /**
     * @param date
     * @param howToPrintDate 1: YYYY/MM/DD, 0: MM/DD
     * @return
     */
    private String formatDate(OccurrenceDigClass cls, GeneralDate date, double usedNumber, int howToPrintDate, List<OccurrenceAcquisitionDetails> occurrenceAcquisitionDetails) {
        StringBuilder formattedDate = new StringBuilder();
        if (howToPrintDate == 0) {
            formattedDate.append(date.toString("MM/dd"));
        } else {
            formattedDate.append(date.toString("yy/MM/dd"));
        }
        if (usedNumber != 1.0) {
            formattedDate.append(TextResource.localize("KDR003_120"));
        }
        occurrenceAcquisitionDetails.stream()
                .filter(o -> o.getOccurrenceDigClass() == cls && o.getDate().getDayoffDate().isPresent() && o.getDate().getDayoffDate().get().equals(date))
                .findFirst().ifPresent(detail -> {
            if (detail.getStatus() == MngHistDataAtr.SCHEDULE || detail.getStatus() == MngHistDataAtr.NOTREFLECT) {
                formattedDate.insert(0, "(");
                formattedDate.append(")");
            }
            if (detail.getIsExpiredInCurrentMonth().isPresent() && detail.getIsExpiredInCurrentMonth().get()) {
                formattedDate.insert(0, "[");
                formattedDate.append("]");
            }
        });
        return formattedDate.toString();
    }

    /**
     * @param detail
     * @param howToPrintDate 1: YYYY/MM/DD, 0: MM/DD
     * @return
     */
    private String formatNoLinkedDate(OccurrenceAcquisitionDetails detail, int howToPrintDate) {
        StringBuilder formattedDate = new StringBuilder();
        if (howToPrintDate == 0) {
            formattedDate.append(detail.getDate().getDayoffDate().get().toString("MM/dd"));
        } else {
            formattedDate.append(detail.getDate().getDayoffDate().get().toString("yy/MM/dd"));
        }
        if (detail.getNumberConsecuVacation().getDay().v() != 1.0) {
            formattedDate.append(TextResource.localize("KDR003_120"));
        }
        if (detail.getStatus() == MngHistDataAtr.SCHEDULE || detail.getStatus() == MngHistDataAtr.NOTREFLECT) {
            formattedDate.insert(0, "(");
            formattedDate.append(")");
        }
        if (detail.getIsExpiredInCurrentMonth().isPresent() && detail.getIsExpiredInCurrentMonth().get()) {
            formattedDate.insert(0, "[");
            formattedDate.append("]");
        }
        return formattedDate.toString();
    }

    private String formatNoLinkedTime(OccurrenceAcquisitionDetails detail, int value) {
        StringBuilder formattedDate = new StringBuilder();
        formattedDate.append(convertToTime(value));
        if (detail.getNumberConsecuVacation().getDay().v() != 1.0) {
            formattedDate.append(TextResource.localize("KDR003_120"));
        }
        if (detail.getStatus() == MngHistDataAtr.SCHEDULE || detail.getStatus() == MngHistDataAtr.NOTREFLECT) {
            formattedDate.insert(0, "(");
            formattedDate.append(")");
        }
        if (detail.getIsExpiredInCurrentMonth().isPresent() && detail.getIsExpiredInCurrentMonth().get()) {
            formattedDate.insert(0, "[");
            formattedDate.append("]");
        }
        return formattedDate.toString();
    }

    /**
     * Convert minute to HH:mm
     */
    private String convertToTime(int minute) {
        int minuteAbs = Math.abs(minute);
        if (minute < 0) {
            minuteAbs = Math.abs(minute + 1440);
        }
        int hours = minuteAbs / 60;
        int minutes = minuteAbs % 60;
        return (minute < 0 ? "-" : "") + String.format("%d:%02d", hours, minutes);
    }

    private void setValue(Cells cells, int row, int col, String value) {
        cells.get(row, col).setValue(value);
        if (!value.contains("(") && !value.contains("[")&&
                !value.contains(TextResource.localize("KDR003_120"))) {
            Style style = cells.get(row, col).getStyle();
            style.setHorizontalAlignment(TextAlignmentType.CENTER);
            cells.get(row, col).setStyle(style);
        }
    }
}
