package nts.uk.file.at.infra.holidayconfirmationtable;

import com.aspose.cells.*;
import com.aspose.pdf.HorizontalAlignment;
import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.MngDataStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.OccurrenceDigClass;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.ManagementDataRemainUnit;
import nts.uk.ctx.sys.portal.dom.enums.MenuAtr;
import nts.uk.ctx.sys.portal.dom.standardmenu.StandardMenu;
import nts.uk.ctx.sys.portal.dom.standardmenu.StandardMenuRepository;
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
public class AsposeHolidayConfirmationTableGenerator extends AsposeCellsReportGenerator implements HolidayConfirmationTableGenerator {
    @Inject
    private StandardMenuRepository standardMenuRepo;

    private static final String TEMPLATE_FILE = "report/KDR004_template.xlsx";
    private static final String FILE_EXTENSION = ".xlsx";

    private static final int SUB_HEADER_ROW = 0;
    private static final String USER_GUIDE_COL = "J";
    private static final String EXPIRATION_COL = "Q";

    private static final String ROUND_OPENING_BRACKET = "(";
    private static final String ROUND_CLOSING_BRACKET = ")";
    private static final String SQUARE_OPENING_BRACKET = "[";
    private static final String SQUARE_CLOSING_BRACKET = "]";

    private static final int HEADER_ROW = 1;
    private static final int COLUMN_ER = 0;
    private static final int COLUMN_EMP = 1;
    private static final int COLUMN_CARRY_FORWARD = 2;
    private static final int COLUMN_OCCURRENCE = 3;
    private static final int COLUMN_USED = 4;
    private static final int COLUMN_REMAINING = 5;
    private static final int COLUMN_UNDIGESTED = 6;
    private static final int COLUMN_LINK = 7;

    private static final int TEMPLATE_ROWS = 13;
    private static final int MAX_ROW_PER_PAGE = 51;

    @Override
    public void generate(FileGeneratorContext generatorContext, Kdr004DataSource dataSource) {
        List<StandardMenu> menus = standardMenuRepo.findAll(dataSource.getCompanyInfo().getCompanyId());
        String menuName = menus.stream().filter(i->i.getSystem().value == 1 && i.getMenuAtr() == MenuAtr.Menu && i.getProgramId().equals("KDR004"))
                .findFirst().map(i -> i.getDisplayName().v()).orElse(TextResource.localize("KDR004_100"));
        try (AsposeCellsReportContext reportContext = this.createContext(TEMPLATE_FILE)) {
            this.setPageHeader(reportContext, dataSource, menuName);
            Workbook workbook = reportContext.getWorkbook();
            WorksheetCollection worksheets = workbook.getWorksheets();
            Worksheet sheet = worksheets.get(0);
            sheet.setName(menuName);
            this.printSubHeader(sheet.getCells(), dataSource);
            this.printHeader(sheet.getCells());
            this.printData(sheet, dataSource);
            reportContext.processDesigner();
            reportContext.saveAsExcel(this.createNewFile(generatorContext, this.getReportName(menuName + FILE_EXTENSION)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    private void setPageHeader(AsposeCellsReportContext reportContext, Kdr004DataSource dataSource, String menuName) {
        reportContext.setHeader(0, "&7&\"ＭＳ ゴシック\"" + dataSource.getCompanyInfo().getCompanyName());
        reportContext.setHeader(1, "&12&\"ＭＳ ゴシック,Bold\"" + menuName);
        DateTimeFormatter fullDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd  HH:mm");
        reportContext.setHeader(2, "&7&\"ＭＳ ゴシック\"" + LocalDateTime.now().format(fullDateTimeFormatter) + "\npage&P ");
    }

    private void printSubHeader(Cells cells, Kdr004DataSource dataSource) {
        cells.get(USER_GUIDE_COL + (SUB_HEADER_ROW + 1)).setValue(TextResource.localize("KDR004_20"));
        cells.get(EXPIRATION_COL + (SUB_HEADER_ROW + 1)).setValue(TextResource.localize("KDR004_123") + dataSource.getComSubstVacationSetting().map(i -> i.getSetting().getExpirationDate().nameId).orElse(""));
    }

    private void printHeader(Cells cells) {
        cells.get(HEADER_ROW, COLUMN_EMP).setValue(TextResource.localize("KDR004_21"));
        cells.get(HEADER_ROW, COLUMN_CARRY_FORWARD).setValue(TextResource.localize("KDR004_22"));
        cells.get(HEADER_ROW, COLUMN_OCCURRENCE).setValue(TextResource.localize("KDR004_23"));
        cells.get(HEADER_ROW, COLUMN_USED).setValue(TextResource.localize("KDR004_24"));
        cells.get(HEADER_ROW, COLUMN_REMAINING).setValue(TextResource.localize("KDR004_25"));
        cells.get(HEADER_ROW, COLUMN_UNDIGESTED).setValue(TextResource.localize("KDR004_26"));
        cells.get(HEADER_ROW, 7).setValue(TextResource.localize("KDR004_27"));
        cells.get(HEADER_ROW, 9).setValue(TextResource.localize("KDR004_28"));
        cells.get(HEADER_ROW, 10).setValue(TextResource.localize("KDR004_29"));
        cells.get(HEADER_ROW, 11).setValue(TextResource.localize("KDR004_30"));
        cells.get(HEADER_ROW, 12).setValue(TextResource.localize("KDR004_31"));
        cells.get(HEADER_ROW, 13).setValue(TextResource.localize("KDR004_32"));
        cells.get(HEADER_ROW, 14).setValue(TextResource.localize("KDR004_33"));
        cells.get(HEADER_ROW, 15).setValue(TextResource.localize("KDR004_34"));
        cells.get(HEADER_ROW, 16).setValue(TextResource.localize("KDR004_35"));
        cells.get(HEADER_ROW, 17).setValue(TextResource.localize("KDR004_36"));
        cells.get(HEADER_ROW, 18).setValue(TextResource.localize("KDR004_37"));
    }

    private void printData(Worksheet sheet, Kdr004DataSource dataSource) {
        Cells cells = sheet.getCells();
        Map<String, List<HolidayConfirmationTableContent>> mapData = dataSource.getContents().stream().collect(Collectors.groupingBy(HolidayConfirmationTableContent::getHierarchicalCode));
        int row = 3, count = 0;
        List<Integer> wkpIndexes = new ArrayList<>();
        List<Integer> empIndexes = new ArrayList<>();
        List<String> wkpCodes = mapData.keySet().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        for (String wkpCode : wkpCodes) {
            // copy for next workplace
            cells.insertRow(row);
            try {
                cells.copyRows(cells, row + 1, row, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // page break by workplace
            if (dataSource.getPageBreak() == 2 && wkpCodes.indexOf(wkpCode) != 0) {
                sheet.getHorizontalPageBreaks().add(row);
                count = 0;
            }
            List<HolidayConfirmationTableContent> contents = mapData.get(wkpCode);
            contents.sort(Comparator.comparing(HolidayConfirmationTableContent::getEmployeeCode));
            cells.get(row, 0).setValue(TextResource.localize("KDR004_38") + wkpCode + "　" + contents.get(0).getWorkplaceName());
            wkpIndexes.add(row);
            row++;
            count++;

            for (HolidayConfirmationTableContent content : contents) {
                // page break by employee
                if (dataSource.getPageBreak() == 1) {
                    if (contents.indexOf(content) != 0) {
                        int lastWkpRow = wkpIndexes.get(wkpIndexes.size() - 1);
                        this.insertWkpRowAndPageBreak(sheet, cells, wkpIndexes, lastWkpRow, row);
                        row++;
                    } else {
                        if (wkpCodes.indexOf(wkpCode) != 0) {
                            int lastWkpRow = wkpIndexes.get(wkpIndexes.size() - 1);
                            sheet.getHorizontalPageBreaks().add(lastWkpRow);
                        }
                    }
                    count = 1;
                }

                if (dataSource.isLinking()) {
                    // print linked content
                    this.prepareLinkingData(
                            content.getHolidayAcquisitionInfo().get().getLinkingInfos(),
                            content.getHolidayAcquisitionInfo().get().getOccurrenceAcquisitionDetails()
                    );
                    int col = 9;
                    int size = content.getHolidayAcquisitionInfo().get().getLinkingInfos().size();
                    int loops = size > 0 && size % 10 == 0 ? (size / 10) : (size / 10 + 1);

                    for (int loop = 0; loop < loops; loop++) {
                        cells.insertRows(row, 2);
                        try {
                            cells.copyRows(cells, loop == loops - 1 ? row + 7 : row + 5, row, 2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (loop == 0) {
                            this.printCommonContent(cells, row, content, dataSource.isHolidayMng());
                            empIndexes.add(row);
                            cells.get(row, 7).setValue(TextResource.localize("KDR004_39"));
                        }
                        cells.get(row, 8).setValue(TextResource.localize("KDR004_41"));
                        cells.get(row + 1, 8).setValue(TextResource.localize("KDR004_42"));
                        for (int i = 0; i < 10; i++) {
                            int idx = loop * 10 + i;
                            if (idx < size) {
                                LinkingInfo linkingInfo = content.getHolidayAcquisitionInfo().get().getLinkingInfos().get(idx);
                                if (linkingInfo.getUseDateNumber() != 0.1) { // check dummy number in preparing data
                                    if (i > 0) {
                                        LinkingInfo prevLinkingInfo = content.getHolidayAcquisitionInfo().get().getLinkingInfos().get(idx - 1);
                                        this.fillLinkingData(
                                                cells,
                                                linkingInfo,
                                                prevLinkingInfo,
                                                row,
                                                col + i,
                                                dataSource.getHowToPrintDate(),
                                                content.getHolidayAcquisitionInfo().get().getOccurrenceAcquisitionDetails()
                                        );
                                    } else {
                                        String value = this.formatDate(
                                                OccurrenceDigClass.OCCURRENCE,
                                                linkingInfo.getOccurrenceDate(),
                                                linkingInfo.getUseDateNumber(),
                                                dataSource.getHowToPrintDate(),
                                                content.getHolidayAcquisitionInfo().get().getOccurrenceAcquisitionDetails()
                                        );
                                        this.setValue(cells, row, col + i, value);

                                        String value2 = this.formatDate(
                                                OccurrenceDigClass.DIGESTION,
                                                linkingInfo.getUseDate(),
                                                linkingInfo.getUseDateNumber(),
                                                dataSource.getHowToPrintDate(),
                                                content.getHolidayAcquisitionInfo().get().getOccurrenceAcquisitionDetails()
                                        );
                                        this.setValue(cells, row + 1, col + i, value2);
                                    }
                                }
                            } else {
                                break;
                            }
                        }
                        row += 2;
                        if (count + 2 > MAX_ROW_PER_PAGE) {
                            // add page break at the end of page
                            row = this.checkPageBreakEndPage(sheet, cells, wkpIndexes, empIndexes, row);
                            count = row - wkpIndexes.get(wkpIndexes.size() - 1);
                        } else {
                            count += 2;
                        }
                    }
                }

                this.prepareNoLinkingData(
                        content.getHolidayAcquisitionInfo().get().getOccurrenceAcquisitionDetails(),
                        content.getHolidayAcquisitionInfo().get().getLinkingInfos()
                );
                int col = 9;
                int size = content.getHolidayAcquisitionInfo().get().getOccurrenceAcquisitionDetails().size();
                int loops = size > 0 && size % 10 == 0 ? (size / 10) : (size / 10 + 1);

                for (int loop = 0; loop < loops; loop++) {
                    cells.insertRows(row, 2);
                    try {
                        cells.copyRows(cells, loop == loops - 1 ? row + 13 : row + 11, row, 2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (loop == 0) {
                        if (!dataSource.isLinking()) {
                            this.printCommonContent(cells, row, content, dataSource.isHolidayMng());
                            empIndexes.add(row);
                        }
                        cells.get(row, 7).setValue(TextResource.localize("KDR004_40"));
                    }
                    cells.get(row, 8).setValue(TextResource.localize("KDR004_41"));
                    cells.get(row + 1, 8).setValue(TextResource.localize("KDR004_42"));
                    for (int i = 0; i < 10; i++) {
                        int idx = loop * 10 + i;
                        if (idx < size) {
                            OccurrenceAcquisitionDetail acquisitionDetail = content.getHolidayAcquisitionInfo().get().getOccurrenceAcquisitionDetails().get(idx);
                            if (acquisitionDetail.getOccurrenceDigCls() == OccurrenceDigClass.OCCURRENCE) {
                                String value = this.formatNoLinkedDate(acquisitionDetail, dataSource.getHowToPrintDate());
                                this.setValue(cells, row, col + i, value);
                            } else {
                                String value = this.formatNoLinkedDate(acquisitionDetail, dataSource.getHowToPrintDate());
                                this.setValue(cells, row + 1, col + i, value);
                            }
                        } else {
                            break;
                        }
                    }
                    // if there is only 1 block => draw thin border
                    if (loops > 1) {
                        this.drawBorder(cells, row);
                    }
                    row += 2;
                    if (count + 2 > MAX_ROW_PER_PAGE) {
                        // add page break at the end of page
                        row = this.checkPageBreakEndPage(sheet, cells, wkpIndexes, empIndexes, row);
                        count = row - wkpIndexes.get(wkpIndexes.size() - 1);
                    } else {
                        count += 2;
                    }
                }
            }
        }
        if (!dataSource.isLinking()) {
            cells.deleteColumn(COLUMN_LINK);
        }

        // remove template rows
        cells.deleteRows(row, TEMPLATE_ROWS);

        // set print area
        PageSetup pageSetup = sheet.getPageSetup();
        pageSetup.setPrintArea("A1:" + (dataSource.isLinking() ? "S" : "R") + row);
    }

    private void printCommonContent(Cells cells, int row, HolidayConfirmationTableContent content, boolean holidayMng) {
        cells.get(row, COLUMN_EMP).setValue(content.getEmployeeCode() + "　" + content.getEmployeeName());
        if (!content.getHolidayAcquisitionInfo().isPresent()) return;
        cells.get(row, COLUMN_ER).setValue(content.getHolidayAcquisitionInfo().get().isError() ? "ER" : null);
        if (holidayMng) {
            Style negativeStyle = cells.get(row, COLUMN_CARRY_FORWARD).getStyle();
            negativeStyle.getFont().setColor(Color.getRed());

            cells.get(row, COLUMN_CARRY_FORWARD).setValue(String.format("%.1f", content.getHolidayAcquisitionInfo().get().getCarryForwardNumber()));
            if (content.getHolidayAcquisitionInfo().get().getCarryForwardNumber() < 0) {
                cells.get(row, COLUMN_CARRY_FORWARD).setStyle(negativeStyle);
            }

            cells.get(row, COLUMN_OCCURRENCE).setValue(String.format("%.1f", content.getHolidayAcquisitionInfo().get().getOccurrencesNumber()));
            if (content.getHolidayAcquisitionInfo().get().getOccurrencesNumber() < 0) {
                cells.get(row, COLUMN_OCCURRENCE).setStyle(negativeStyle);
            }

            cells.get(row, COLUMN_USED).setValue(String.format("%.1f", content.getHolidayAcquisitionInfo().get().getUsedNumber()));
            if (content.getHolidayAcquisitionInfo().get().getUsedNumber() < 0) {
                cells.get(row, COLUMN_USED).setStyle(negativeStyle);
            }

            cells.get(row, COLUMN_REMAINING).setValue(String.format("%.1f", content.getHolidayAcquisitionInfo().get().getRemainingNumber()));
            if (content.getHolidayAcquisitionInfo().get().getRemainingNumber() < 0) {
                cells.get(row, COLUMN_REMAINING).setStyle(negativeStyle);
            }

            cells.get(row, COLUMN_UNDIGESTED).setValue(String.format("%.1f", content.getHolidayAcquisitionInfo().get().getUndigestedNumber()));
            if (content.getHolidayAcquisitionInfo().get().getUndigestedNumber() != 0) {
                cells.get(row, COLUMN_UNDIGESTED).setStyle(negativeStyle);
            }
        }
    }

    /**
     *
     * @param cls
     * @param date
     * @param usedNumber
     * @param howToPrintDate 1: YYYY/MM/DD, 0: MM/DD
     * @param occurrenceAcquisitionDetails
     * @return
     */
    private String formatDate(OccurrenceDigClass cls, GeneralDate date, double usedNumber, int howToPrintDate, List<OccurrenceAcquisitionDetail> occurrenceAcquisitionDetails) {
        StringBuilder formattedDate = new StringBuilder();
        if (howToPrintDate == 0) {
            formattedDate.append(date.toString("MM/dd"));
        } else {
            formattedDate.append(date.toString("yy/MM/dd"));
        }
        Optional<OccurrenceAcquisitionDetail> occurrenceAcquisitionDetail = occurrenceAcquisitionDetails.stream()
                .filter(o -> o.getOccurrenceDigCls() == cls && o.getDate().getDayoffDate().isPresent() && o.getDate().getDayoffDate().get().equals(date))
                .findFirst();
        this.formatDateCommon(formattedDate, usedNumber != 1.0, occurrenceAcquisitionDetail.orElse(null));
        return formattedDate.toString();
    }

    /**
     *
     * @param detail
     * @param howToPrintDate 1: YYYY/MM/DD, 0: MM/DD
     * @return
     */
    private String formatNoLinkedDate(OccurrenceAcquisitionDetail detail, int howToPrintDate) {
        StringBuilder formattedDate = new StringBuilder();
        if (howToPrintDate == 0) {
            formattedDate.append(detail.getDate().getDayoffDate().get().toString("MM/dd"));
        } else {
            formattedDate.append(detail.getDate().getDayoffDate().get().toString("yy/MM/dd"));
        }
        this.formatDateCommon(formattedDate, detail.getOccurrencesUseNumber().getDay().v() != 1.0, detail);
        return formattedDate.toString();
    }

    public void formatDateCommon(StringBuilder formattedDate, boolean isHalfDay, OccurrenceAcquisitionDetail detail) {
        int spaceLeft = 2, spaceRight = 3;
        if (isHalfDay) {
            formattedDate.append(TextResource.localize("KDR004_120"));
            spaceRight += -1;
        }
        if (detail != null) {
            if (detail.getStatus() == MngDataStatus.SCHEDULE || detail.getStatus() == MngDataStatus.NOTREFLECTAPP) {
                formattedDate.insert(0, ROUND_OPENING_BRACKET);
                formattedDate.append(ROUND_CLOSING_BRACKET);
                spaceLeft += -1;
                spaceRight += -1;
            }
            if (detail.getExpiringThisMonth().isPresent() && detail.getExpiringThisMonth().get()) {
                formattedDate.insert(0, SQUARE_OPENING_BRACKET);
                formattedDate.append(SQUARE_CLOSING_BRACKET);
                spaceLeft += -1;
                spaceRight += -1;
            }
        }
        if (spaceLeft > 0) formattedDate.insert(0, spaceLeft == 1 ? " " : "  ");
        if (spaceRight > 0) formattedDate.append(spaceRight == 1 ? " " : spaceRight == 2 ? "  " : "   ");
    }

    private void setValue(Cells cells, int row, int col, String value) {
        cells.get(row, col).setValue(value);

        if (!value.contains(ROUND_OPENING_BRACKET)
                && !value.contains(SQUARE_OPENING_BRACKET)
                && !value.contains(TextResource.localize("KDR004_120"))) {
            Style style = cells.get(row, col).getStyle();
            style.setHorizontalAlignment(TextAlignmentType.CENTER);
            cells.get(row, col).setStyle(style);
        }

    }

    private void drawBorder(Cells cells, int row) {
        Style style1 = cells.get(row, COLUMN_LINK).getStyle();
        style1.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(CellBorderType.THIN);
        cells.get(row, COLUMN_LINK).setStyle(style1);
        Style style2 = cells.get(row + 1, COLUMN_LINK).getStyle();
        style2.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(CellBorderType.THIN);
        cells.get(row + 1, COLUMN_LINK).setStyle(style2);
    }

    private void insertWkpRowAndPageBreak(Worksheet sheet, Cells cells, List<Integer> indexes, int fromRow, int toRow) {
        cells.insertRow(toRow);
        try {
            cells.copyRows(cells, fromRow, toRow, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sheet.getHorizontalPageBreaks().add(toRow); // always add page break before insert row
        indexes.add(toRow);
    }

    private int checkPageBreakEndPage(Worksheet sheet, Cells cells, List<Integer> wkpIndexes, List<Integer> empIndexes, int currentRow) {
        int lastEmpRow = empIndexes.get(empIndexes.size() - 1);
        int lastWkpRow = wkpIndexes.get(wkpIndexes.size() - 1);
        if (lastEmpRow == lastWkpRow + 1) {
            sheet.getHorizontalPageBreaks().add(lastWkpRow);
            return currentRow;
        } else {
            this.insertWkpRowAndPageBreak(sheet, cells, wkpIndexes, lastWkpRow, lastEmpRow);
            empIndexes.set(empIndexes.size() - 1, lastEmpRow + 1);
            return currentRow + 1;
        }
    }

    private void fillLinkingData(Cells cells, LinkingInfo current, LinkingInfo prev, int row, int col, int howToPrintDate, List<OccurrenceAcquisitionDetail> details) {
        if (current.getOccurrenceDate().equals(prev.getOccurrenceDate())
                && current.getUseDateNumber() == 0.5
                && prev.getUseDateNumber() == 0.5) {
            cells.merge(row, col - 1, 1, 2);
            String value = this.formatDate(
                    OccurrenceDigClass.OCCURRENCE,
                    current.getOccurrenceDate(),
                    1.0,
                    howToPrintDate,
                    details
            );
            cells.get(row, col - 1).setValue(value);
            Style style = cells.get(row, col - 1).getStyle();
            style.setHorizontalAlignment(HorizontalAlignment.Center);
            cells.get(row, col - 1).setStyle(style);
        } else {
            String value = this.formatDate(
                    OccurrenceDigClass.OCCURRENCE,
                    current.getOccurrenceDate(),
                    current.getUseDateNumber(),
                    howToPrintDate,
                    details
            );
            this.setValue(cells, row, col, value);
        }
        if (current.getUseDate().equals(prev.getUseDate())
                && current.getUseDateNumber() == 0.5
                && prev.getUseDateNumber() == 0.5) {
            cells.merge(row + 1, col - 1, 1, 2);
            String value = this.formatDate(
                    OccurrenceDigClass.DIGESTION,
                    current.getUseDate(),
                    1.0,
                    howToPrintDate,
                    details
            );
            cells.get(row + 1, col - 1).setValue(value);
            Style style = cells.get(row + 1, col - 1).getStyle();
            style.setHorizontalAlignment(HorizontalAlignment.Center);
            cells.get(row + 1, col - 1).setStyle(style);
        } else {
            String value = this.formatDate(
                    OccurrenceDigClass.DIGESTION,
                    current.getUseDate(),
                    current.getUseDateNumber(),
                    howToPrintDate,
                    details
            );
            this.setValue(cells, row + 1, col, value);
        }
    }

    private void prepareLinkingData(List<LinkingInfo> linkingInfors, List<OccurrenceAcquisitionDetail> details) {
        // sort before check
        linkingInfors.sort(Comparator.comparing(LinkingInfo::getOccurrenceDate));
        // check cell at the end of line, if it can be merged with next cell, add dummy data to list to leave it blank
        ListIterator<LinkingInfo> iterator = linkingInfors.listIterator();
        while (iterator.hasNext()) {
            int index = iterator.nextIndex();
            LinkingInfo info = iterator.next();
            if (index % 10 == 9 && info.getUseDateNumber() == 0.5) {
                if (iterator.hasNext()) {
                    LinkingInfo nextInfo = iterator.next();
                    if (info.getOccurrenceDate().equals(nextInfo.getOccurrenceDate()) || info.getUseDate().equals(nextInfo.getUseDate())) {
                        iterator.add(new LinkingInfo(
                                info.getOccurrenceDate(),
                                info.getUseDate(),
                                0.1 // a number to know when to skip
                        ));
                    }
                }
            }
        }
        // sort by occurence date after check
        linkingInfors.sort(Comparator.comparing(LinkingInfo::getOccurrenceDate).thenComparing(LinkingInfo::getUseDateNumber));
    }

    private void prepareNoLinkingData(List<OccurrenceAcquisitionDetail> details, List<LinkingInfo> linkingInfors) {
        if (!linkingInfors.isEmpty()) {
            // remove linked date
            ListIterator<OccurrenceAcquisitionDetail> iterator = details.listIterator();
            while (iterator.hasNext()) {
                OccurrenceAcquisitionDetail detail = iterator.next();
                double linkingUsedDays;
                if (detail.getOccurrenceDigCls() == OccurrenceDigClass.OCCURRENCE) {
                    linkingUsedDays = linkingInfors
                            .stream().filter(i -> i.getOccurrenceDate().equals(detail.getDate().getDayoffDate().get()))
                            .mapToDouble(LinkingInfo::getUseDateNumber).sum();
                } else {
                    linkingUsedDays = linkingInfors
                            .stream().filter(i -> i.getUseDate().equals(detail.getDate().getDayoffDate().get()))
                            .mapToDouble(LinkingInfo::getUseDateNumber).sum();
                }
                if (linkingUsedDays >= detail.getOccurrencesUseNumber().getDay().v()) {
                    iterator.remove();
                } else if (linkingUsedDays > 0) {
                    detail.getOccurrencesUseNumber().setDay(new ManagementDataRemainUnit(detail.getOccurrencesUseNumber().getDay().v() - linkingUsedDays));
                    iterator.set(detail);
                }
            }
        }
        details.sort(Comparator.comparing(i -> i.getDate().getDayoffDate().get()));
    }

}
