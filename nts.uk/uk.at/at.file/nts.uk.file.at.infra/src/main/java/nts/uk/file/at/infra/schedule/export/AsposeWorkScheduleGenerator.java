package nts.uk.file.at.infra.schedule.export;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.aspose.cells.BackgroundType;
import com.aspose.cells.BorderType;
import com.aspose.cells.Cell;
import com.aspose.cells.CellBorderType;
import com.aspose.cells.Cells;
import com.aspose.cells.Color;
import com.aspose.cells.PageSetup;
import com.aspose.cells.Style;
import com.aspose.cells.TextAlignmentType;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.pdf.HorizontalAlignment;
import com.aspose.pdf.VerticalAlignment;

import lombok.val;
import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.DayOfWeek;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.schedule.app.query.schedule.shift.management.shifttable.GetHolidaysByPeriod;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.holiday.PublicHoliday;
import nts.uk.ctx.bs.employee.pub.employee.export.PersonEmpBasicInfoPub;
import nts.uk.ctx.bs.employee.pub.employee.export.dto.PersonEmpBasicInfoDto;
import nts.uk.file.at.app.schedule.export.WorkPlaceScheDataSource;
import nts.uk.file.at.app.schedule.export.WorkPlaceScheGenerator;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportGenerator;

/**
 * @author anhnm
 *
 */
@Stateless
public class AsposeWorkScheduleGenerator extends AsposeCellsReportGenerator implements WorkPlaceScheGenerator {
    
    @Inject
    private GetHolidaysByPeriod getHolidayByPeriod;
    
    @Inject
    private PersonEmpBasicInfoPub personEmpBasicInfoPub;
    
    private static final String EXTENSION_FILE = ".xlsx";
    
    private static final String DATE_FORMAT = "yyyy/MM/dd";
    
    private final Color SUNDAY = Color.fromArgb(255, 0, 0);
    
    private final Color SATURDAY = Color.fromArgb(0, 0, 255);
    
    private final Color WEEKDAY = Color.fromArgb(64, 64, 64);

    @Override
    public void generate(FileGeneratorContext generatorContext, WorkPlaceScheDataSource dataSource) {
        val reportContext = this.createEmptyContext("WorkSchedule");
        String fileName = TextResource.localize("KDL055_27") + EXTENSION_FILE;
        Workbook workbook = reportContext.getWorkbook(); 
        Worksheet worksheet = workbook.getWorksheets().get(0);
        worksheet.setName(TextResource.localize("KDL055_28"));
        DatePeriod period = new DatePeriod(
                GeneralDate.fromString(dataSource.getStartDate(), DATE_FORMAT), 
                GeneralDate.fromString(dataSource.getEndDate(), DATE_FORMAT));
        List<PersonEmpBasicInfoDto> listInfo = personEmpBasicInfoPub.getPerEmpBasicInfo(dataSource.getEmployeeIDs());
        // sort employee list by employeeCode
        listInfo.sort(new Comparator<PersonEmpBasicInfoDto>() {
            @Override
            public int compare(PersonEmpBasicInfoDto o1, PersonEmpBasicInfoDto o2) {
                return o1.getEmployeeCode().compareTo(o2.getEmployeeCode());
            }
        });
        List<PublicHoliday> holidayList = this.getHolidayByPeriod.get(period);
        
        Style style = workbook.createStyle();
        style.getFont().setName("Meiryo UI");
        style.getFont().setSize(8);
        style.setVerticalAlignment(VerticalAlignment.Center);
        workbook.setDefaultStyle(style);
        worksheet.getCells().setStandardWidthPixels(86);
        worksheet.getCells().setStandardHeightPixels(20);
        this.printPage(worksheet);
        this.printData(worksheet, listInfo, period, holidayList.stream().map(x -> x.getDate()).collect(Collectors.toList()));
        
        reportContext.processDesigner();
        reportContext.saveAsExcel(this.createNewFile(generatorContext, fileName));
    }

    private void printData(Worksheet worksheet, List<PersonEmpBasicInfoDto> infos, DatePeriod datePeriod, List<GeneralDate> holidayList) {
        Cells cells = worksheet.getCells();
        
        // EX1_1
        Cell cellA1 = cells.get("A1");
        cellA1.setValue(TextResource.localize("KDL055_29"));
        this.setHeaderStyle(cellA1);
        this.setBorder(cellA1);
        cells.setColumnWidthPixel(0, 119);
        
        // EX1_2
        Cell cellB2 = cells.get("B1");
        cellB2.setValue(TextResource.localize("KDL055_30"));
        this.setHeaderStyle(cellB2);
        this.setBorder(cellB2);
        cells.setColumnWidthPixel(1, 119);
        
        // EX1_3
        List<GeneralDate> period = datePeriod.datesBetween();
        for(int i = 0; i < period.size(); i++) {
            Cell cell = cells.get(0, i + 2);
            cell.setValue(period.get(i).date());
            this.setHeaderDateStyle(cell, period.get(i), holidayList);
            this.setBorder(cell);
        }
        
        for (int i = 0; i < infos.size(); i++) {
            // EX2_1
            Cell employeeCodeCell = cells.get(i + 1, 0);
            employeeCodeCell.setValue(infos.get(i).getEmployeeCode());
            Style styleCode = employeeCodeCell.getStyle();
            styleCode.setHorizontalAlignment(HorizontalAlignment.Left);
            this.setBorder(employeeCodeCell);
            
            // EX2_2
            Cell nameCell = cells.get(i + 1, 1);
            nameCell.setValue(infos.get(i).getBusinessName());
            Style styleName = nameCell.getStyle();
            styleName.setHorizontalAlignment(HorizontalAlignment.Left);
            this.setBorder(nameCell);
            
            for(int j = 0; j < period.size(); j++) {
                Cell cell = cells.get(i + 1, j + 2);
                this.setBorder(cell);
                Style style = cell.getStyle();
                style.setHorizontalAlignment(TextAlignmentType.CENTER);
                style.setVerticalAlignment(TextAlignmentType.CENTER);
                cell.setStyle(style);
            }
        }
    }

    private void setBorder(Cell cellA1) {
        Style style = cellA1.getStyle();
        style.getBorders().getByBorderType(BorderType.TOP_BORDER).setLineStyle(CellBorderType.THIN);
        style.getBorders().getByBorderType(BorderType.BOTTOM_BORDER).setLineStyle(CellBorderType.THIN);
        style.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(CellBorderType.THIN);
        style.getBorders().getByBorderType(BorderType.LEFT_BORDER).setLineStyle(CellBorderType.THIN);
        cellA1.setStyle(style);
    }

    private void setHeaderDateStyle(Cell cell, GeneralDate generalDate, List<GeneralDate> holidayList) {
        this.setHeaderStyle(cell);
        
        Style style = cell.getStyle();
        style.setCustom("yyyy/MM/dd");
        if (holidayList.contains(generalDate) || generalDate.dayOfWeekEnum().equals(DayOfWeek.SUNDAY)) {
            style.getFont().setColor(SUNDAY);
        } else if (generalDate.dayOfWeekEnum().equals(DayOfWeek.SATURDAY)) {
            style.getFont().setColor(SATURDAY);
        } else {
            style.getFont().setColor(WEEKDAY);
        }
        cell.setStyle(style);
        
        // TODO: set rule type of cell to Date with format dd/MM/yyyy
        
    }

    private void setHeaderStyle(Cell cellA1) {
        Style style = cellA1.getStyle();
        style.setHorizontalAlignment(HorizontalAlignment.Center);
        style.setPattern(BackgroundType.SOLID);
        style.setForegroundColor(Color.fromArgb(204, 255, 255));
        
        cellA1.setStyle(style);
    }

    private void printPage(Worksheet worksheet) {
        PageSetup pageSetup = worksheet.getPageSetup();
        pageSetup.setFirstPageNumber(1);
    }

}
