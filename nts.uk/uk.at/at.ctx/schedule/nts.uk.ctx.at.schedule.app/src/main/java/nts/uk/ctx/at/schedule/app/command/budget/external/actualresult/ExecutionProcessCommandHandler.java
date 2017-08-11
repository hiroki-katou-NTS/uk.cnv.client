/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.app.command.budget.external.actualresult;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateful;
import javax.inject.Inject;

import nts.arc.i18n.custom.IInternationalization;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.arc.layer.infra.file.storage.StoredFileStreamService;
import nts.arc.primitive.PrimitiveValueUtil;
import nts.arc.task.AsyncTask;
import nts.arc.task.AsyncTaskService;
import nts.arc.task.data.TaskDataSetter;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.gul.collection.CollectionUtil;
import nts.gul.csv.NtsCsvReader;
import nts.gul.csv.NtsCsvRecord;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.schedule.app.command.budget.external.actualresult.dto.ExecutionInfor;
import nts.uk.ctx.at.schedule.app.command.budget.external.actualresult.dto.ExternalBudgetDailyDto;
import nts.uk.ctx.at.schedule.app.command.budget.external.actualresult.dto.ExternalBudgetErrorDto;
import nts.uk.ctx.at.schedule.app.command.budget.external.actualresult.dto.ExternalBudgetLogDto;
import nts.uk.ctx.at.schedule.app.command.budget.external.actualresult.dto.ExternalBudgetTimeDto;
import nts.uk.ctx.at.schedule.dom.budget.external.ExternalBudget;
import nts.uk.ctx.at.schedule.dom.budget.external.ExternalBudgetRepository;
import nts.uk.ctx.at.schedule.dom.budget.external.UnitAtr;
import nts.uk.ctx.at.schedule.dom.budget.external.actualresult.CompletionState;
import nts.uk.ctx.at.schedule.dom.budget.external.actualresult.ExtBudgetMoney;
import nts.uk.ctx.at.schedule.dom.budget.external.actualresult.ExtBudgetNumberPerson;
import nts.uk.ctx.at.schedule.dom.budget.external.actualresult.ExtBudgetNumericalVal;
import nts.uk.ctx.at.schedule.dom.budget.external.actualresult.ExtBudgetTime;
import nts.uk.ctx.at.schedule.dom.budget.external.actualresult.ExtBudgetUnitPrice;
import nts.uk.ctx.at.schedule.dom.budget.external.actualresult.ExternalBudgetDaily;
import nts.uk.ctx.at.schedule.dom.budget.external.actualresult.ExternalBudgetDailyRepository;
import nts.uk.ctx.at.schedule.dom.budget.external.actualresult.ExternalBudgetErrorRepository;
import nts.uk.ctx.at.schedule.dom.budget.external.actualresult.ExternalBudgetLog;
import nts.uk.ctx.at.schedule.dom.budget.external.actualresult.ExternalBudgetLogRepository;
import nts.uk.ctx.at.schedule.dom.budget.external.actualresult.ExternalBudgetTimeZone;
import nts.uk.ctx.at.schedule.dom.budget.external.actualresult.ExternalBudgetTimeZoneRepository;
import nts.uk.ctx.at.schedule.dom.budget.external.actualresult.WorkplaceAdapter;
import nts.uk.ctx.at.schedule.dom.budget.external.actualresult.service.ExtBudgetFileCheckService;
import nts.uk.ctx.at.schedule.dom.budget.external.actualresult.service.FileUltil;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class ExecutionProcessCommandHandler.
 */
@Stateful
public class ExecutionProcessCommandHandler extends CommandHandlerWithResult<ExecutionProcessCommand, ExecutionInfor> {
    
    /** The managed task service. */
    @Inject
    private AsyncTaskService managedTaskService;
    
    /** The internationalization. */
    @Inject
    private IInternationalization internationalization;
    
    /** The file check service. */
    @Inject
    private ExtBudgetFileCheckService fileCheckService;
    
    /** The external budget repo. */
    @Inject
    private ExternalBudgetRepository externalBudgetRepo;
    
    /** The ext budget daily repo. */
    @Inject
    private ExternalBudgetDailyRepository extBudgetDailyRepo;
    
    /** The ext budget time repo. */
    @Inject
    private ExternalBudgetTimeZoneRepository extBudgetTimeRepo;
    
    /** The ext budget log repo. */
    @Inject
    private ExternalBudgetLogRepository extBudgetLogRepo;
    
    /** The ext budget error repo. */
    @Inject
    private ExternalBudgetErrorRepository extBudgetErrorRepo;
    
    /** The file stream service. */
    @Inject
    private StoredFileStreamService fileStreamService;
    
    /** The workplace adapter. */
    @Inject
    private WorkplaceAdapter workplaceAdapter;
    
    /** The Constant FORMAT_DATES. */
    private static final List<String> FORMAT_DATES = Arrays.asList("yyyyMMdd", "yyyy/M/d", "yyyy/MM/dd");
    
    /** The Constant DEFAULT_VALUE. */
    private static final Integer DEFAULT_VALUE = 0;
    
    /** The Constant INDEX_COLUMN_CODE. */
    private static final Integer INDEX_COLUMN_CODE = 0;
    
    /** The Constant INDEX_COLUMN_DATE. */
    private static final Integer INDEX_COLUMN_DATE = 1;
    
    /** The Constant INDEX_BEGIN_COL_VALUE. */
    private static final Integer INDEX_BEGIN_COL_VALUE = 2;
    
    /** The Constant MAX_COLMN. */
    private static final Integer MAX_COLMN = 51;
    
    /** The Constant TOTAL_RECORD. */
    private static final String TOTAL_RECORD = "TOTAL_RECORD";
    
    /** The Constant SUCCESS_CNT. */
    private static final String SUCCESS_CNT = "SUCCESS_CNT";
    
    /** The Constant FAIL_CNT. */
    private static final String FAIL_CNT = "FAIL_CNT";
    
    /* (non-Javadoc)
     * @see nts.arc.layer.app.command.CommandHandlerWithResult#handle(nts.arc.layer.app.command.CommandHandlerContext)
     */
    @Override
    protected ExecutionInfor handle(CommandHandlerContext<ExecutionProcessCommand> context) {
        ExecutionProcessCommand command = context.getCommand();
        TaskDataSetter setter = new TaskDataSetter();
        // GUID
        String executeId = IdentifierUtil.randomUniqueId();
        
        // find all message JP before import
        Map<String, String> mapStringJP = findAllStringJP();
        AsyncTask task = AsyncTask.builder().withContexts().keepsTrack(true).build(() -> {
            // valid file format
            this.fileCheckService.validFileFormat(command.getFileId(), command.getEncoding(), command.getStartLine());
            
            // get input stream by file id
            InputStream inputStream = this.fileStreamService.takeOutFromFileId(command.getFileId());
            
            setter.setData(TOTAL_RECORD, DEFAULT_VALUE);
            setter.setData(SUCCESS_CNT, DEFAULT_VALUE);
            setter.setData(FAIL_CNT, DEFAULT_VALUE);
            
            // register table LOG with status: IN_COMPLETE 
            String employeeId = AppContexts.user().employeeId();
            GeneralDateTime dateTimeCurrent = GeneralDateTime.now();
            ExternalBudgetLogDto extBudgetLogDto = ExternalBudgetLogDto.builder()
                    .executionId(executeId)
                    .employeeId(employeeId)
                    .startDateTime(dateTimeCurrent)
                    .endDateTime(dateTimeCurrent) // begin import, do not have end date?
                    .extBudgetCode(command.getExternalBudgetCode())
                    .extBudgetFileName(command.getFileName())
                    .completionState(CompletionState.INCOMPLETE)
                    .build();
            this.extBudgetLogRepo.add(extBudgetLogDto.toDomain());
            
            String companyId = AppContexts.user().companyId();
            Optional<ExternalBudget> extBudgetOptional = this.externalBudgetRepo.find(companyId,
                    command.getExternalBudgetCode());
            if (!extBudgetOptional.isPresent()) {
                throw new RuntimeException("Not external budget setting.");
            }
            
            // initial import process
            ImportProcess importProcess = new ImportProcess();
            importProcess.executeId = executeId;
            importProcess.inputStream = inputStream;
            importProcess.externalBudget = extBudgetOptional.get();
            importProcess.extractCondition = command;
            importProcess.mapStringJP = mapStringJP;
            
            // begin process input file
            this.processInput(importProcess, setter);
        });
        task.setDataSetter(setter);
        this.managedTaskService.execute(task);
        return ExecutionInfor.builder()
                .taskId(task.getId())
                .executeId(executeId)
                .build();
    }
    
    /**
     * Process input.
     *
     * @param importProcess the import process
     * @param setter the setter
     */
    private void processInput(ImportProcess importProcess, TaskDataSetter setter) {
        try {
            NtsCsvReader csvReader = FileUltil.newCsvReader(importProcess.extractCondition.getEncoding());
            List<NtsCsvRecord> csRecords = csvReader.parse(importProcess.inputStream);
            setter.updateData(TOTAL_RECORD, csRecords.size() - importProcess.extractCondition.getStartLine() + 1);
            Iterator<NtsCsvRecord> csvRecordIterator = csRecords.iterator();
            while(csvRecordIterator.hasNext()) {
                /** TODO: check has interruption, if is interrupt, update table LOG status interruption (中断)
                 * and end flow (stop process)
                 * this.updateLog(importProcess.executeId, CompletionState.INTERRUPTION);
                 */
                importProcess.stopLine = false;
                this.processLine(importProcess, csvRecordIterator.next());
                
                // respond status client
                Optional<ExternalBudgetLog> optional = this.extBudgetLogRepo
                        .findExtBudgetLogByExecuteId(importProcess.executeId);
                if (optional.isPresent()) {
                    ExternalBudgetLog log = optional.get();
                    setter.updateData(SUCCESS_CNT, log.getNumberSuccess());
                    setter.updateData(FAIL_CNT, log.getNumberFail());
                }
            }
            this.updateLog(importProcess.executeId, CompletionState.DONE);
            
            // close input stream
            importProcess.inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Process line.
     *
     * @param importProcess the import process
     * @param record the record
     */
    private void processLine(ImportProcess importProcess, NtsCsvRecord record) {
        importProcess.startLine++;
        if (importProcess.startLine < importProcess.extractCondition.getStartLine()) {
            return;
        }
        
        // get data cell from input csv
        List<String> result = new ArrayList<>();
        for (int i = 0; i < MAX_COLMN; i++) {
            Object header = record.getColumn(i);
            if (header != null) {
                result.add(header.toString());
            }
        }
        // check record has data?
        if (CollectionUtil.isEmpty(result)) {
            // update table log with status DONE(完了)
            this.updateLog(importProcess.executeId, CompletionState.DONE);
            return;
        }
        // validate input file csv
        this.validDataLine(importProcess, result);
        
        // insert data master
        this.insertValue(importProcess, result);
    }
    
    /**
     * Insert value.
     *
     * @param importProcess the import process
     * @param result the result
     */
    private void insertValue(ImportProcess importProcess, List<String> result) {
        if (importProcess.stopLine) {
            return;
        }
        switch (importProcess.externalBudget.getUnitAtr()) {
            case DAILY:
                this.addExtBudgetDaily(importProcess, result);
                break;
            case BYTIMEZONE:
                this.addExtBudgetTime(importProcess, result);
                break;
            default:
                throw new RuntimeException("Not unit atr suitable.");
        }
        boolean isUpdateCaseFail = false;
        int number;
        if (importProcess.stopLine) {
            isUpdateCaseFail = true;
            number = ++importProcess.failCnt;
        } else {
            number = ++importProcess.successCnt;
        }
        this.updateLog(isUpdateCaseFail, importProcess.executeId, number);
    }
    
    /**
     * Adds the ext budget daily.
     *
     * @param importProcess the import process
     * @param result the result
     */
    private void addExtBudgetDaily(ImportProcess importProcess, List<String> result) {
        String rawValue = result.get(INDEX_BEGIN_COL_VALUE);
        Long value = null;
        try {
            value = this.convertVal(rawValue);
        } catch (RuntimeException e) {
            value = Long.parseLong(rawValue);
        }
        ExternalBudgetDailyDto dto = ExternalBudgetDailyDto.builder()
                .budgetAtr(importProcess.externalBudget.getBudgetAtr())
                .workplaceId(importProcess.workplaceId)
                .extBudgetCode(importProcess.extractCondition.getExternalBudgetCode())
                .actualDate(importProcess.actualDate)
                .actualValue(value)
                .build();
        
        switch (importProcess.externalBudget.getBudgetAtr()) {
            case TIME:
                ExternalBudgetDaily<ExtBudgetTime> domainTime = dto.toDomain();
                this.saveDataDaily(importProcess, domainTime);
                break;
            case PEOPLE:
                ExternalBudgetDaily<ExtBudgetNumberPerson> domainPeople = dto.toDomain();
                this.saveDataDaily(importProcess, domainPeople);
                break;
            case MONEY:
                ExternalBudgetDaily<ExtBudgetMoney> domainMoney = dto.toDomain();
                this.saveDataDaily(importProcess, domainMoney);
                break;
            case NUMERICAL:
                ExternalBudgetDaily<ExtBudgetNumericalVal> domainNumerical = dto.toDomain();
                this.saveDataDaily(importProcess, domainNumerical);
                break;
            case PRICE:
                ExternalBudgetDaily<ExtBudgetUnitPrice> domainPrice = dto.toDomain();
                this.saveDataDaily(importProcess, domainPrice);
                break;
            default:
                throw new RuntimeException("Not budget atr suitable.");
        }
    }
    
    /**
     * Save data daily.
     *
     * @param <T> the generic type
     * @param importProcess the import process
     * @param domain the domain
     */
    private <T> void saveDataDaily (ImportProcess importProcess, ExternalBudgetDaily<T> domain) {
        if (!this.extBudgetDailyRepo.isExisted(domain.getWorkplaceId(), GeneralDate.legacyDate(domain.getActualDate()),
                domain.getExtBudgetCode().v())) {
            this.extBudgetDailyRepo.add(domain);
            return;
        }
        if (importProcess.extractCondition.getIsOverride()) {
            this.extBudgetDailyRepo.update(domain);
            return;
        }
        // insert table ERROR with message id: Msg_167
        ExternalBudgetErrorDto extBudgetErrorDto = ExternalBudgetErrorDto.builder()
                .executionId(importProcess.executeId)
                .lineNo(importProcess.startLine)
                .columnNo(DEFAULT_VALUE)
                .errorContent(importProcess.mapStringJP.get("Msg_167"))
                .build();
        this.extBudgetErrorRepo.add(extBudgetErrorDto.toDomain());
        
        // marker finish line.
        importProcess.stopLine = true;
    }
    
    /**
     * Adds the ext budget time.
     *
     * @param importProcess the import process
     * @param result the result
     */
    private void addExtBudgetTime(ImportProcess importProcess, List<String> result) {
        Map<Integer, Long> mapValue = new HashMap<>();
        List<String> lstValue = result.subList(INDEX_BEGIN_COL_VALUE, result.size());
        for (int i=0; i<lstValue.size(); i++) {
            String rawValue = lstValue.get(i);
            Long value = null;
            try {
                value = this.convertVal(rawValue);
            } catch (RuntimeException e) {
                value = Long.parseLong(rawValue);
            }
            mapValue.put(i, value);
        }
        ExternalBudgetTimeDto dto = ExternalBudgetTimeDto.builder()
                .budgetAtr(importProcess.externalBudget.getBudgetAtr())
                .workplaceId(importProcess.workplaceId)
                .extBudgetCode(importProcess.extractCondition.getExternalBudgetCode())
                .actualDate(importProcess.actualDate)
                .mapValue(mapValue)
                .build();
        switch (importProcess.externalBudget.getBudgetAtr()) {
            case TIME:
                ExternalBudgetTimeZone<ExtBudgetTime> domainTime = dto.toDomain();
                this.saveDataTime(importProcess, domainTime);
                break;
            case PEOPLE:
                ExternalBudgetTimeZone<ExtBudgetNumberPerson> domainPeople = dto.toDomain();
                this.saveDataTime(importProcess, domainPeople);
                break;
            case MONEY:
                ExternalBudgetTimeZone<ExtBudgetMoney> domainMoney = dto.toDomain();
                this.saveDataTime(importProcess, domainMoney);
                break;
            case NUMERICAL:
                ExternalBudgetTimeZone<ExtBudgetNumericalVal> domainNumerical = dto.toDomain();
                this.saveDataTime(importProcess, domainNumerical);
                break;
            case PRICE:
                ExternalBudgetTimeZone<ExtBudgetUnitPrice> domainPrice = dto.toDomain();
                this.saveDataTime(importProcess, domainPrice);
                break;
            default:
                throw new RuntimeException("Not budget atr suitable.");
        }
    }
    
    /**
     * Save data time.
     *
     * @param <T> the generic type
     * @param importProcess the import process
     * @param domain the domain
     */
    private <T> void saveDataTime (ImportProcess importProcess, ExternalBudgetTimeZone<T> domain) {
        if (!this.extBudgetTimeRepo.isExisted(domain.getWorkplaceId(), GeneralDate.legacyDate(domain.getActualDate()),
                domain.getExtBudgetCode().v())) {
            this.extBudgetTimeRepo.add(domain);
            return;
        }
        if (importProcess.extractCondition.getIsOverride()) {
            this.extBudgetTimeRepo.update(domain);
            return;
        }
        // insert table ERROR with message id: Msg_167
        ExternalBudgetErrorDto extBudgetErrorDto = ExternalBudgetErrorDto.builder()
                .executionId(importProcess.executeId)
                .lineNo(importProcess.startLine)
                .columnNo(DEFAULT_VALUE)
                .errorContent(importProcess.mapStringJP.get("Msg_167"))
                .build();
        this.extBudgetErrorRepo.add(extBudgetErrorDto.toDomain());
        
        // marker finish line.
        importProcess.stopLine = true;
    }
    
    /**
     * Valid data line.
     *
     * @param importProcess the import process
     * @param result the result
     */
    private void validDataLine(ImportProcess importProcess, List<String> result) {
        this.validUnitAtr(importProcess, result.size());
        
        // check column 2 (２列目) is date ?
        this.validDateFormat(importProcess, result.get(INDEX_COLUMN_DATE));
        
        // Check column 1 (1列目) is workplace code?
        this.validWorkplaceCode(importProcess, result.get(INDEX_COLUMN_CODE));
        
        // Check actual value by primitive
        this.validActualVal(importProcess, result);
        
        // finish process of line.
        if (importProcess.stopLine) {
            this.updateLog(true, importProcess.executeId, importProcess.failCnt);
            return;
        }
    }
    
    /**
     * Valid unit atr.
     *
     * @param importProcess the import process
     * @param numberColInput the number col input
     */
    private void validUnitAtr(ImportProcess importProcess, int numberColInput) {
        UnitAtr unitAtr = importProcess.externalBudget.getUnitAtr();
        int numberCol = 0;
        switch (unitAtr) {
            case DAILY:
                numberCol = 3;
                break;
            case BYTIMEZONE:
                numberCol = 50;
                break;
            default:
                throw new RuntimeException("Not unit atr suitable.");
        }
        if (numberColInput == numberCol) {
            return;
        }
        int numberColError = 0;
        String messageIdError = "";
        if (numberColInput < numberCol) {
            numberColError = numberColInput;
            messageIdError = "Msg_163";
        } else if (numberColInput > numberCol) {
            numberColError = ++numberCol;
            messageIdError = "Msg_162";
        }
        ExternalBudgetErrorDto extBudgetErrorDto = ExternalBudgetErrorDto.builder()
                .executionId(importProcess.executeId)
                .lineNo(importProcess.startLine)
                .columnNo(numberColError)
                .errorContent(importProcess.mapStringJP.get(messageIdError))
                .build();
        this.extBudgetErrorRepo.add(extBudgetErrorDto.toDomain());
        
        importProcess.failCnt++;
        // marker finish process line
        importProcess.stopLine = true;
    }
    
    /**
     * Valid date format.
     *
     * @param importProcess the import process
     * @param inputDate the input date
     */
    private void validDateFormat(ImportProcess importProcess, String inputDate) {
        // finish process of line.
        if (importProcess.stopLine) {
            return;
        }
        Boolean isInValidDateFormat = null;
        for (String formatDate : FORMAT_DATES) {
            try {
                isInValidDateFormat = false;
                importProcess.actualDate = new SimpleDateFormat(formatDate).parse(inputDate);
                break;
            } catch (ParseException e) {
                isInValidDateFormat = true;
            }
        }
        if (isInValidDateFormat) {
            int idxColReal = INDEX_COLUMN_DATE + 1;
            ExternalBudgetErrorDto extBudgetErrorDto = ExternalBudgetErrorDto.builder()
                    .executionId(importProcess.executeId)
                    .lineNo(importProcess.startLine)
                    .columnNo(idxColReal)
                    .acceptedDate(inputDate)
                    .errorContent("Invalid format date.") // Not has message id
                    .build();
            this.extBudgetErrorRepo.add(extBudgetErrorDto.toDomain());
            
            importProcess.failCnt++;
            // marker finish process line
            importProcess.stopLine = true;
        }
    }
    
    /**
     * Valid workplace code.
     *
     * @param importProcess the import process
     * @param workplaceCode the workplace code
     */
    private void validWorkplaceCode(ImportProcess importProcess, String workplaceCode) {
        // finish process of line.
        if (importProcess.stopLine) {
            return;
        }
        List<String> lstWpkId = this.workplaceAdapter.findWpkIdList(workplaceCode, importProcess.actualDate);
        if (!CollectionUtil.isEmpty(lstWpkId)) {
            importProcess.workplaceId = lstWpkId.get(DEFAULT_VALUE);
            return;
        }
        // insert error
        ExternalBudgetErrorDto extBudgetErrorDto = ExternalBudgetErrorDto.builder()
                .executionId(importProcess.executeId)
                .lineNo(importProcess.startLine)
                .columnNo(INDEX_COLUMN_CODE)
                .workplaceCode(workplaceCode)
                .errorContent(importProcess.mapStringJP.get("Msg_164"))
                .build();
        this.extBudgetErrorRepo.add(extBudgetErrorDto.toDomain());
        
        importProcess.failCnt++;
        importProcess.stopLine = true;
    }
    
    /**
     * Valid actual val.
     *
     * @param importProcess the import process
     * @param lstValue the lst value
     */
    private void validActualVal(ImportProcess importProcess, List<String> lstValue) {
        // finish process of line.
        if (importProcess.stopLine) {
            return;
        }
        for (int i = INDEX_BEGIN_COL_VALUE; i < lstValue.size(); i++) {
            this.validValByPrimitive(importProcess, i + 1, lstValue.get(i));
        }
    }
    
    /**
     * Valid val by primitive.
     *
     * @param importProcess the import process
     * @param columnNo the column no
     * @param value the value
     */
    private void validValByPrimitive (ImportProcess importProcess, int columnNo, String value) {
        String itemName = importProcess.mapStringJP.get("KSU006_18");
        try {
            switch (importProcess.externalBudget.getBudgetAtr()) {
                case TIME:
                    // convert HH:mm -> minute
                    Long valueTime = this.convertVal(value);
                    PrimitiveValueUtil.createWithValidate(() -> new ExtBudgetTime(valueTime), (ex) ->{
                        this.logError(importProcess, columnNo, value, ex.getErrorMessage(itemName));
                    });
                    break;
                case PEOPLE:
                    Integer valuePeople= Integer.parseInt(value);
                    PrimitiveValueUtil.createWithValidate(() -> new ExtBudgetNumberPerson(valuePeople), (ex) ->{
                        this.logError(importProcess, columnNo, value, ex.getErrorMessage(itemName));
                    });
                    break;
                case MONEY:
                    Integer valueMoney= Integer.parseInt(value);
                    PrimitiveValueUtil.createWithValidate(() -> new ExtBudgetMoney(valueMoney), (ex) ->{
                        this.logError(importProcess, columnNo, value, ex.getErrorMessage(itemName));
                    });
                    break;
                case NUMERICAL:
                    Integer valueNumerical= Integer.parseInt(value);
                    PrimitiveValueUtil.createWithValidate(() -> new ExtBudgetNumericalVal(valueNumerical), (ex) ->{
                        this.logError(importProcess, columnNo, value, ex.getErrorMessage(itemName));
                    });
                    break;
                case PRICE:
                    Integer valuePrice= Integer.parseInt(value);
                    PrimitiveValueUtil.createWithValidate(() -> new ExtBudgetUnitPrice(valuePrice), (ex) ->{
                        this.logError(importProcess, columnNo, value, ex.getErrorMessage(itemName));
                    });
                    break;
                default:
                    throw new RuntimeException("Not budget atr suitable.");
            }
        } catch (Exception e) {
            this.logError(importProcess, columnNo, value, "Invalid format number.");
        }
    }
    
    /**
     * Convert val.
     *
     * @param value the value
     * @return the long
     */
    private Long convertVal(String value) {
        String CHARACTER_COLON = ":";
        if (!value.contains(CHARACTER_COLON)) {
            throw new RuntimeException("Actual value time invalid format.");
        }
        String[] arr = value.split(CHARACTER_COLON);
        Integer HOUR = 60;
        Long numberHour = Long.parseLong(arr[0]);
        Long numberminute = Long.parseLong(arr[1]);
        return numberHour * HOUR + numberminute;
    }
    
    /**
     * Log error.
     *
     * @param importProcess the import process
     * @param columnNo the column no
     * @param value the value
     * @param errContent the err content
     */
    private void logError(ImportProcess importProcess, int columnNo, String value, String errContent) {
        ExternalBudgetErrorDto extBudgetErrorDto = ExternalBudgetErrorDto.builder()
                .executionId(importProcess.executeId)
                .lineNo(importProcess.startLine)
                .columnNo(columnNo)
                .actualValue(value)
                .errorContent(errContent)
                .build();
        this.extBudgetErrorRepo.add(extBudgetErrorDto.toDomain());
        
        if (!importProcess.stopLine) {
            importProcess.failCnt++;
        }
        
        // marker finish line.
        importProcess.stopLine = true;
    }
    
    /**
     * Update log.
     *
     * @param executeId the execute id
     * @param status the status
     */
    private void updateLog(String executeId, CompletionState status) {
        Optional<ExternalBudgetLog> optional = this.extBudgetLogRepo.findExtBudgetLogByExecuteId(executeId);
        if (optional.isPresent()) {
            ExternalBudgetLogDto extBudgetLogDto = ExternalBudgetLogDto.copy(optional.get());
            extBudgetLogDto.completionState = status;
            extBudgetLogDto.endDateTime = GeneralDateTime.now();
            this.extBudgetLogRepo.update(extBudgetLogDto.toDomain());
        }
    }
    
    /**
     * Update log.
     *
     * @param isUpdateCaseFail the is update case fail
     * @param executeId the execute id
     * @param number the number
     */
    private void updateLog(boolean isUpdateCaseFail, String executeId, Integer number) {
        Optional<ExternalBudgetLog> optional = this.extBudgetLogRepo.findExtBudgetLogByExecuteId(executeId);
        if (optional.isPresent()) {
            ExternalBudgetLogDto extBudgetLogDto = ExternalBudgetLogDto.copy(optional.get());
            if (isUpdateCaseFail) {
                extBudgetLogDto.numberFail = number;
            } else {
                extBudgetLogDto.numberSuccess = number;
            }
            this.extBudgetLogRepo.update(extBudgetLogDto.toDomain());
        }
    }
    
    /**
     * Find all string JP.
     *
     * @return the map
     */
    private Map<String, String> findAllStringJP() {
        Map<String, String> mapMessage = new HashMap<>();
        String nameId = "KSU006_18";
        Optional<String> optional = this.internationalization.getItemName(nameId);
        mapMessage.put(nameId, optional.isPresent() ? optional.get() : (nameId + " is not found."));
        
        List<String> lstMsgId = Arrays.asList("Msg_162", "Msg_163", "Msg_164", "Msg_167");
        for (String msgId : lstMsgId) {
            mapMessage.put(msgId, this.getMessageById(msgId));
        }
        return mapMessage;
    }
    
    /**
     * Gets the message by id.
     *
     * @param messageId the message id
     * @return the message by id
     */
    private String getMessageById(String messageId) {
        String errorContent = messageId + " is not found.";
        Optional<String> optional = this.internationalization.getRawMessage(messageId);
        if (optional.isPresent()) {
            errorContent = optional.get();
        }
        return errorContent;
    }
    
    /**
     * The Class ImportProcess.
     */
    class ImportProcess {
        
        /** The execute id. */
        String executeId = "";
        
        /** The start line. */
        int startLine = 0;
        
        /** The success cnt. */
        int successCnt = 0;
        
        /** The fail cnt. */
        int failCnt = 0;
        
        /** The stop line. */
        boolean stopLine;
        
        /** The input stream. */
        InputStream inputStream;
        
        /** The external budget. */
        ExternalBudget externalBudget;
        
        /** The extract condition. */
        ExecutionProcessCommand extractCondition;
        
        /** The map string JP. */
        Map<String, String> mapStringJP;
        
        /** The workplace id. */
        String workplaceId;
        
        /** The actual date. */
        Date actualDate;
    }
}
