package nts.uk.file.at.infra.schedule.filemanagement;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.CellsException;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.layer.infra.file.storage.StoredFileStreamService;
import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.CharType;
import nts.arc.primitive.constraint.StringCharType;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.dom.importschedule.CapturedRawData;
import nts.uk.ctx.at.schedule.dom.importschedule.CapturedRawDataOfCell;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterImportCode;
import nts.uk.ctx.bs.employee.dom.setting.code.EmployeeCEMethodAttr;
import nts.uk.ctx.bs.employee.pub.employee.employeeInfo.setting.code.EmployeeCodeEditSettingExport;
import nts.uk.file.at.app.schedule.filemanagement.CheckFileService;
import nts.uk.file.at.app.schedule.filemanagement.WorkPlaceScheCheckFileParam;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportContext;

@Stateless
public class AsposeWorkScheFileCheck implements CheckFileService {

    @Inject
    private StoredFileStreamService fileStreamService;
    
    private final String[] dateFormats = {"yyyy/MM/dd", "yyyy/M/d","yyyy-MM-dd'T'HH:mm:ss", "yyyy年M月d日", "yyyy年MM月dd日"};

    public CapturedRawData processingFile(WorkPlaceScheCheckFileParam checkFileParam, EmployeeCodeEditSettingExport setting)
            throws Exception {

        // get file uploaded
        InputStream inputStream = this.fileStreamService.takeOutFromFileId(checkFileParam.getFileID());

        @SuppressWarnings("resource")
        AsposeCellsReportContext designer = new AsposeCellsReportContext(inputStream);
        Workbook workbook = designer.getWorkbook();

        this.checkFileError(checkFileParam, workbook);

        CapturedRawData outputDto = this.processData(checkFileParam, workbook, setting);
        designer.close();
        return outputDto;
    }

    private CapturedRawData processData(WorkPlaceScheCheckFileParam checkFileParam, Workbook workbook,
            EmployeeCodeEditSettingExport setting) {
        int rowStart = 0;
        int columnStart = 0;
        boolean isColumnNameExist = true;
        
        List<GeneralDate> listDate = new ArrayList<GeneralDate>();
        List<CapturedRawDataOfCell> contents = new ArrayList<CapturedRawDataOfCell>();
        List<String> employeeCodes = new ArrayList<String>();

        // get sheet
        Worksheet worksheet;
        if (checkFileParam.isCaptureCheckSheet() && !StringUtils.isBlank(checkFileParam.getCaptureSheet())) {
            worksheet = workbook.getWorksheets().get(checkFileParam.getCaptureSheet());
        } else {
            worksheet = workbook.getWorksheets().get(0);
        }

        // get cell range start
        Cells cells = worksheet.getCells();
        Cell cellStart = null;
        if (checkFileParam.isCaptureCheckCell() && !StringUtils.isBlank(checkFileParam.getCaptureCell())) {
            try {
                cellStart = cells.get(checkFileParam.getCaptureCell());
            } catch (CellsException e) {
                throw new BusinessException("Msg_2192");
            }
            
            if (cellStart != null) {
                rowStart = cellStart.getRow();
                columnStart = cellStart.getColumn();
            } else {
                throw new BusinessException("Msg_2192");
            }
            
            if (!cellStart.getStringValue().equals(TextResource.localize("KDL055_29"))) {
                throw new BusinessException("Msg_2208");
            }
        }
        
        // check cell empName exist
        Cell temp = cells.get(rowStart, columnStart + 1);
        if (!temp.getStringValue().equals(TextResource.localize("KDL055_30"))) {
            isColumnNameExist = false;
        }

        // loop for header
        int countHead = 0;
        for (int i = columnStart + (isColumnNameExist ? 2 : 1); i < columnStart + (isColumnNameExist ? 66 : 61); i++) {
            String formatTmp = "";
            Cell cellDate = cells.get(rowStart, i);

            if (cellDate.getValue() == null) {
                break;
            }
            
            String cellValueDisplay = cellDate.getValue().toString();

            // Check if end of dates
            if (StringUtils.isBlank(cellValueDisplay)) {
                break;
            }

            // check date format
            GeneralDate date = null;
            boolean isValid = true;
            for (String format : dateFormats) {
                try {
                    date = GeneralDate.fromString(cellValueDisplay, format);
                    isValid = true;
                    formatTmp = format;
                    break;
                } catch (Exception e) {
                    isValid = false;
                }
            }
            if (!isValidDate(cellValueDisplay, isValid, formatTmp)) {
                throw new BusinessException("Msg_1796");
            }

            // check if date duplicate
            if (listDate.contains(date)) {
                throw new BusinessException("Msg_1798");
            }
            
            // add date to list
            listDate.add(date);

            // check number of date > 62
            if (i == (columnStart + (isColumnNameExist ? 64 : 63))) {
                throw new BusinessException("Msg_1799");
            }

            countHead++;
        }

        // loop for row data
        for (int i = rowStart + 1; i < (rowStart + 1001); i++) {
            Cell cellEmployeeCode = cells.get(i, columnStart);
//            Cell cellEmployeeName = cells.get(i, columnStart + 1);

            String employeeCodeDisplay = cellEmployeeCode.getStringValue();
//            String employeeNameDisplay = cellEmployeeName.getStringValue();

            // stop when end of rows
            if (StringUtils.isBlank(employeeCodeDisplay)) {
                break;
            }

            String employeeCode = this.validateCodePrimitive(employeeCodeDisplay, setting);
            
            // check duplicate employee
            if (employeeCodes.contains(employeeCode)) {
                throw new BusinessException("Msg_1797");
            }
            employeeCodes.add(employeeCode);
            
            // loop for data of each row
            for (int j = 1; j <= countHead; j++) {
                Cell rowItem = cells.get(i, columnStart + (isColumnNameExist ? 1 : 0) + j);
                GeneralDate ymd = listDate.get(j - 1);
                ShiftMasterImportCode shiftCode = null;
                String itemValue = rowItem.getStringValue();
                if (!StringUtils.isBlank(itemValue)) {
                    try {
                        shiftCode = new ShiftMasterImportCode(itemValue);
                        shiftCode.validate();
                    } catch (Exception e) {
                        throw new BusinessException("Msg_2173");
                    }
                    
                    CapturedRawDataOfCell content = new CapturedRawDataOfCell(employeeCode, ymd, shiftCode);
                    contents.add(content);
                }
                
            }
            
            // check if employee > 999
            if (i == (rowStart + 1000)) {
                throw new BusinessException("Msg_1800");
            }
        }

        return new CapturedRawData(contents, employeeCodes);
    }

    private String validateCodePrimitive(String employeeCodeDisplay, EmployeeCodeEditSettingExport setting) {
        try {
            @SuppressWarnings("unused")
            EmployeeCodePrimitive employeeCodePrimitive = new EmployeeCodePrimitive(employeeCodeDisplay);
        } catch (Exception e) {
            throw new BusinessException("Msg_2172");
        }

        if (setting != null) {
            int maxLength = setting.getDigitNumb();
            EmployeeCEMethodAttr editMethod = EnumAdaptor.valueOf(setting.getCeMethodAtr(), EmployeeCEMethodAttr.class);

            // check length
            if (employeeCodeDisplay.length() > maxLength) {
                throw new BusinessException("Msg_2172");
            }

            // check edit method
            switch (editMethod) {
            case ZEROBEFORE:
                while (employeeCodeDisplay.length() < maxLength) {
                    employeeCodeDisplay = "0" + employeeCodeDisplay;
                }
                break;
            case ZEROAFTER:
                while (employeeCodeDisplay.length() < maxLength) {
                    employeeCodeDisplay = employeeCodeDisplay.concat("0");
                }
                break;
            case SPACEAFTER:
                while (employeeCodeDisplay.length() < maxLength) {
                    employeeCodeDisplay = employeeCodeDisplay.concat(" ");
                }
                break;
            case SPACEBEFORE:
                while (employeeCodeDisplay.length() < maxLength) {
                    employeeCodeDisplay = " " + employeeCodeDisplay;
                }
                break;
            default:
                break;
            }
        }
        
        return employeeCodeDisplay;
    }

    private void checkFileError(WorkPlaceScheCheckFileParam checkFileParam, Workbook workbook) {
        // 取り込みシートをチェックする
        if (checkFileParam.isCaptureCheckSheet() && !StringUtils.isBlank(checkFileParam.getCaptureSheet())) {
            Worksheet worksheet = workbook.getWorksheets().get(checkFileParam.getCaptureSheet());
            if (worksheet == null) {
                throw new BusinessException("Msg_1905");
            }
        }
    }

    @StringCharType(CharType.ALPHA_NUMERIC)
    private class EmployeeCodePrimitive extends StringPrimitiveValue<EmployeeCodePrimitive> {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public EmployeeCodePrimitive(String rawValue) {
            super(rawValue);
        }
    }
    
    public static boolean isValidDate(String dateStr, boolean isValid, String format) {
        if (!isValid) {
            return false;
        }
        
        switch (format) {
        case "yyyy/MM/dd":
        case "yyyy/M/d":
        case "yyyy年M月d日":
        case "yyyy年MM月dd日":
            String lastDayOfMonth = getLastDayOfMonth(dateStr, format);
            if (dateStr.length() < lastDayOfMonth.length()) {
                return true;
            } else {
                return dateStr.compareTo(lastDayOfMonth) < 1;
            }

        case "yyyy-MM-dd'T'HH:mm:ss":
            String[] pattern = dateStr.split("T")[0].split("-");
            GeneralDate generalDate = GeneralDate.fromString(dateStr, "yyyy-MM-dd'T'HH:mm:ss");
            if (pattern[2].equals(generalDate.toString("dd"))) {
                return true;
            }
            
            return false;
        default:
            return false;
        }
        
    }
    
    public static String getLastDayOfMonth(String date, String format) {
        return GeneralDate.fromString(date, format).localDate()
                .with(TemporalAdjusters.lastDayOfMonth())
                .format(DateTimeFormatter.ofPattern(format));
    }
}
