package nts.uk.file.at.app.export.attendanceitemprepare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.app.find.dailyperformanceformat.BusinessTypeSortedFinder;
import nts.uk.ctx.at.record.app.find.dailyperformanceformat.BusinessTypesFinder;
import nts.uk.ctx.at.record.app.find.dailyperformanceformat.dto.BusinessTypeDto;
import nts.uk.ctx.at.record.app.find.dailyperformanceformat.dto.BusinessTypeSortedDto;
import nts.uk.ctx.at.record.app.find.workrecord.authfuncrest.EmployeeRoleDto;
import nts.uk.ctx.at.record.app.find.workrecord.authfuncrest.EmploymentRoleFinder;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.BusinessTypeFormatMonthly;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.repository.BusinessTypeFormatMonthlyRepository;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.FormatPerformance;
import nts.uk.ctx.at.shared.app.find.scherec.attitem.AttItemFinder;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemAuthority;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemName;
import nts.uk.ctx.at.shared.dom.workrule.businesstype.BusinessTypeCode;
import nts.uk.ctx.bs.employee.app.find.employment.EmploymentFinder;
import nts.uk.ctx.bs.employee.app.find.employment.dto.EmploymentDto;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.masterlist.data.ColumnTextAlign;
import nts.uk.shr.infra.file.report.masterlist.data.MasterCellStyle;
import nts.uk.shr.infra.file.report.masterlist.data.MasterData;
import nts.uk.shr.infra.file.report.masterlist.data.MasterHeaderColumn;
import nts.uk.shr.infra.file.report.masterlist.data.SheetData;
import nts.uk.shr.infra.file.report.masterlist.webservice.MasterListExportQuery;
import nts.uk.shr.infra.file.report.masterlist.webservice.MasterListMode;

/**
*
* @author Hoidd
*
*/
@Stateless
public class RoleDailyExportExcelImpl {

    //sheet1
//    @Inject
//    private AtItemNameAdapter atItemNameAdapter;
    
    @Inject
    private AttItemNameByAuth attItemNameByAuth;
    
    @Inject
    private EmploymentRoleFinder employmentRoleFinder;
    
    @Inject
    private AttItemFinder attfinder; //sheet 2 dung chung
    
    //sheet 2
    @Inject
    private ControlOfAttItemsRepoExcel controlOfItemlExcel;
    //sheet 3
    @Inject
    private BusinessTypesFinder findAll;
    @Inject
    private BusinessTypeFormatMonthlyRepository finAllMonthly;
    @Inject
    private BusinessDailyRepo businessDailyRepo;
    //sheet5
    @Inject
    private BusinessTypeSortedFinder businessTypeSortedFinder;
    //sheet 6
    @Inject
    private EmploymentFinder finderEmp;
    @Inject
    private WorkTypeGroupExcel workTypeGroup;
    //shet 6,7
    @Inject
    ErrorAlarmWorkRecordExportImpl errorAlarmWorkRecordExportImpl;
    @Inject
    RoleMonthlyExportExcelImpl roleMonthlyExportExcelImpl;
    //shet 8
    @Inject
    OperationExcelRepo operationExcelRepo;
    
    @Inject
    PerAuthFormatExport perAuthFormatExport;
    
    

    public List<SheetData> extraSheets(MasterListExportQuery query) {
    	String companyId = AppContexts.user().companyId();
      //sheet 1
        List<EmployeeRoleDto> listEmployeeRoleDto = new ArrayList<>();
        List<AttItemName> listAttItemNameNoAuth= new ArrayList<>();
        //sheet 2
        Map<String,List<AttItemName>> authSeting = new HashMap<>();
        Map<Integer, ControlOfAttendanceItemsDtoExcel> listConItem = new HashMap<>();
        //sheet 3
        List<BusinessTypeDto> listBusinessType= new ArrayList<>();
        List<BusinessTypeFormatMonthly> listBusinessMonthly= new ArrayList<>();
        List<AttItemName> listAttItemNameMonthly= new ArrayList<>();
        Map<Integer, AttItemName> mapAttNameMonthlys = new HashMap<>();
        Map<BusinessTypeCode,List<BusinessTypeFormatMonthly>> mapMonthlyBz = new HashMap<>();
        Map<String,BusinessTypeDto> mapBz = new HashMap<>();
        Map<String, Map<Integer, List<BusinessDailyExcel>>> maplistBzDaily = new HashMap<>();
        Map<Integer, AttItemName> mapAttNameDailys = new HashMap<>();
        //sheet 5
        List<BusinessTypeSortedDto> listBzTypeSort = new ArrayList<>();
        //sheet 6
        //sheet 8
        Optional<FormatPerformance> formatPerformance = operationExcelRepo.getFormatPerformanceById(companyId);
        int mode = 0;
        if(formatPerformance.isPresent()){
        	 mode = formatPerformance.get().getSettingUnitType().value;
        }
        List<EmploymentDto> listEmp = new ArrayList<>();
        Map<String, Map<Integer, List<WorkTypeDtoExcel>>> mapTypeByEmpAndGroup = new HashMap<>();
        initSheet1(listEmployeeRoleDto,listAttItemNameNoAuth,authSeting,companyId);
        initSheet2(listConItem,companyId);
        initSheet3(listBusinessType,listBusinessMonthly,mapMonthlyBz,listAttItemNameMonthly,mapAttNameMonthlys,maplistBzDaily,mapAttNameDailys,listAttItemNameNoAuth,companyId,mapBz);
        initSheet5(listBzTypeSort);
        initSheet6(listEmp,mapTypeByEmpAndGroup);
	    List<String> headerSheet6 = addTextHeader();
	    
        // add the work place sheet
        List<SheetData> sheetDatas = new ArrayList<>();
        SheetData sheet1 = new SheetData(getMasterDatas(query,listEmployeeRoleDto,authSeting,listAttItemNameNoAuth),
                getHeaderColumns(query), null, null, TextResource.localize("KDW006_138"), MasterListMode.NONE);
        SheetData sheet2 = new SheetData(getMasterDatasSheet2(query,listAttItemNameNoAuth,listConItem),
                getHeaderColumnsSheet2(query), null, null, TextResource.localize("KDW006_139"), MasterListMode.NONE);
        SheetData sheet3 = new SheetData(getMasterDatasSheet3(query,listBusinessType,mapMonthlyBz,
        		mapAttNameMonthlys,maplistBzDaily,mapAttNameDailys,companyId,mapAttNameMonthlys,mode,mapBz),
                getHeaderColumnsSheet3(query,mode), null, null, TextResource.localize("KDW006_142"), MasterListMode.NONE);

        SheetData sheet6 = new SheetData(getMasterDatasSheet6(query,mapTypeByEmpAndGroup,headerSheet6),
                getHeaderColumnsSheet6(query,headerSheet6), null, null, TextResource.localize("KDW006_144"), MasterListMode.NONE);

        sheetDatas.add(sheet1);
        sheetDatas.add(sheet2);
        sheetDatas.addAll(errorAlarmWorkRecordExportImpl.extraSheets(query));
        sheetDatas.add(sheet3);
        if(mode==1){
        	SheetData sheet5 = new SheetData(getMasterDatasSheet5(query,listBzTypeSort),
        			getHeaderColumnsSheet5(query), null, null, TextResource.localize("KDW006_143"), MasterListMode.NONE);
        	sheetDatas.add(sheet5);
        }
        sheetDatas.add(sheet6);
        sheetDatas.addAll(errorAlarmWorkRecordExportImpl.addSheet3(query)); //add sheet3 separately because of sheets' order
        sheetDatas.addAll(roleMonthlyExportExcelImpl.extraSheets(query,listEmployeeRoleDto,mapAttNameMonthlys,listAttItemNameMonthly,mode));

        return sheetDatas;
    }
    
	private void initSheet6(List<EmploymentDto> listEmp, Map<String, Map<Integer, List<WorkTypeDtoExcel>>> mapTypeByEmpAndGroup) {
        listEmp.addAll(finderEmp.findAll());
        listEmp.sort(Comparator.comparing(EmploymentDto::getCode));
        mapTypeByEmpAndGroup.putAll(workTypeGroup.getAllWorkType());
    }


    private void initSheet5(List<BusinessTypeSortedDto> listBzTypeSort) {
        listBzTypeSort.addAll(businessTypeSortedFinder.findAll());
        listBzTypeSort.sort(Comparator.comparing(BusinessTypeSortedDto::getOrder));
        
    }


    private void initSheet3(List<BusinessTypeDto> listBusinessType, List<BusinessTypeFormatMonthly> listBusinessMonthly, Map<BusinessTypeCode, 
    		List<BusinessTypeFormatMonthly>> mapMonthlyBz, List<AttItemName> listAttItemNameMonthly, Map<Integer, AttItemName> mapAttNameMonthlys, Map<String, Map<Integer, List<BusinessDailyExcel>>> maplistBzDaily, Map<Integer, AttItemName> mapAttNameDailys, List<AttItemName> listAttItemNameNoAuth, String companyId, Map<String, BusinessTypeDto> mapBz) {
        listBusinessType.addAll(findAll.findAll()); //
        listBusinessMonthly.addAll(finAllMonthly.getMonthlyDetailByCompanyId(companyId));
        listBusinessMonthly.sort(Comparator.comparing(BusinessTypeFormatMonthly::getBusinessTypeCode));
        listBusinessType.sort(Comparator.comparing(BusinessTypeDto::getBusinessTypeCode));
        mapMonthlyBz.putAll(listBusinessMonthly.stream().collect(
                  Collectors.groupingBy(BusinessTypeFormatMonthly::getBusinessTypeCode)
                ));
        mapBz.putAll(listBusinessType.stream().collect(Collectors.toMap(BusinessTypeDto::getBusinessTypeCode,
                Function.identity())));
        listAttItemNameMonthly.addAll(attfinder.getMonthlyAttItemByIdAndAtr(null));
        listAttItemNameMonthly.sort(Comparator.comparing(AttItemName::getAttendanceItemDisplayNumber));
        mapAttNameMonthlys.putAll(listAttItemNameMonthly.stream().collect(Collectors.toMap(AttItemName::getAttendanceItemId,
                        Function.identity())));
        
        //Daily
        maplistBzDaily.putAll(businessDailyRepo.getAllByComp(companyId));
        mapAttNameDailys.putAll(listAttItemNameNoAuth.stream().collect(Collectors.toMap(AttItemName::getAttendanceItemId,
                Function.identity())));
        
    }


    private void initSheet2(Map<Integer, ControlOfAttendanceItemsDtoExcel> listConItem, String companyId) {
    	listConItem.clear();
         listConItem.putAll(controlOfItemlExcel.getAllByCompanyId(companyId));
    }


    private void initSheet1(List<EmployeeRoleDto> listEmployeeRoleDto, List<AttItemName> listAttItemNameNoAuth, Map<String, List<AttItemName>> authSeting, String companyId) {
    	listEmployeeRoleDto.addAll(employmentRoleFinder.findEmploymentRoles());
        listEmployeeRoleDto.sort(Comparator.comparing(EmployeeRoleDto::getRoleCode));
        listAttItemNameNoAuth.addAll(attfinder.getDailyAttItemByIdAndAtr(null));//sheet 3 dung chung
        listAttItemNameNoAuth.sort(Comparator.comparing(AttItemName::getAttendanceItemDisplayNumber));
        authSeting.putAll(attItemNameByAuth.getAllByComp(companyId));
    }

    public List<MasterHeaderColumn> getHeaderColumns(MasterListExportQuery query) {
        
        List<MasterHeaderColumn> columns = new ArrayList<>();
        columns.add(new MasterHeaderColumn("コード", TextResource.localize("KDW006_106"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn("名称", TextResource.localize("KDW006_107"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn("コード2", TextResource.localize("KDW006_106"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn("項目", TextResource.localize("KDW006_88"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn("利用区分", TextResource.localize("KDW006_149"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn("本人修正設定", TextResource.localize("KDW006_150"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn("本人以外修正設定", TextResource.localize("KDW006_151"),
                ColumnTextAlign.LEFT, "", true));
        return columns;
    }
    
    public List<MasterData> getMasterDatas(MasterListExportQuery query, List<EmployeeRoleDto> listEmployeeRoleDto, Map<String, List<AttItemName>> authSeting, List<AttItemName> listAttItemNameNoAuth) {
        
        List<String> listAuthSetingCode = new ArrayList<String>(authSeting.keySet());
		Map<String, EmployeeRoleDto> mapListEmployeeRoleDto =
				listEmployeeRoleDto.stream().collect(Collectors.toMap(EmployeeRoleDto::getRoleId,
        	                                              Function.identity()));
		if(!CollectionUtil.isEmpty(listAuthSetingCode)){
			for (String key : listAuthSetingCode) {
				if(mapListEmployeeRoleDto.get(key)==null){
					listEmployeeRoleDto.add(new EmployeeRoleDto(key,"",TextResource.localize("KDW006_226")));
				}
			}
		}
        List<MasterData> datas = new ArrayList<>();
        if (CollectionUtil.isEmpty(listEmployeeRoleDto)) {
            return null;
        } else {
            listEmployeeRoleDto.stream().forEach(c -> {
                List<AttItemName> attItemNamesAuthSet = authSeting.get(c.getRoleId());
                if(CollectionUtil.isEmpty(attItemNamesAuthSet)){
                	return;
                }
                Map<Integer, AttItemName> result =
                        attItemNamesAuthSet.stream().collect(Collectors.toMap(AttItemName::getAttendanceItemId,
                                                                  Function.identity()));
                attItemNamesAuthSet.sort(Comparator.comparing(AttItemName::getAttendanceItemDisplayNumber));
                Map<String, Object> data = new HashMap<>();
                Map<String, Object> dataChild = new HashMap<>();
                putDataEmpty(data);
                putDataEmpty(dataChild);
                data.put("コード", c.getRoleCode());
                data.put("名称", c.getRoleName());
                if(!CollectionUtil.isEmpty(listAttItemNameNoAuth)){
                	
                    if(listAttItemNameNoAuth.size()==1){
                        
                        AttItemName  attItemName = listAttItemNameNoAuth.get(0);
                        data.put("コード2", attItemName.getAttendanceItemDisplayNumber());
                        data.put("項目", attItemName.getAttendanceItemName());
                        //putAthSeting
                        AttItemAuthority attItemAuthor = result.get(attItemName.getAttendanceItemId()).getAuthority();
                        putAuthor(data,attItemAuthor,attItemName.getUserCanUpdateAtr());
                        alignMasterDataSheetRoleDaily(data);
                        datas.add(alignMasterDataSheetRoleDaily(data));
                        putDataEmpty(data);
                    }else {
                        AttItemName  attItemName = listAttItemNameNoAuth.get(0);
                        data.put("コード2", attItemName.getAttendanceItemDisplayNumber());
                        data.put("項目", attItemName.getAttendanceItemName());
                        //putAthSeting
                        if(result.get(attItemName.getAttendanceItemId())!=null  ){
                            AttItemAuthority attItemAuthor = result.get(attItemName.getAttendanceItemId()).getAuthority();
                            putAuthor(data,attItemAuthor,attItemName.getUserCanUpdateAtr());
                        }
                        datas.add(alignMasterDataSheetRoleDaily(data));
                        putDataEmpty(data);
                        for(int i = 1; i < listAttItemNameNoAuth.size() ; i++){
                            putDataEmpty(dataChild);
                            attItemName = new AttItemName();
                            attItemName = listAttItemNameNoAuth.get(i);
                            dataChild.put("コード2", attItemName.getAttendanceItemDisplayNumber());
                            dataChild.put("項目", attItemName.getAttendanceItemName());
                            //putAthSeting
                            if(result.get(attItemName.getAttendanceItemId())!=null){
                                AttItemAuthority attItemAuthor = result.get(attItemName.getAttendanceItemId()).getAuthority();
                                putAuthor(dataChild,attItemAuthor,attItemName.getUserCanUpdateAtr());
                            }
                            datas.add(alignMasterDataSheetRoleDaily(dataChild));
                        }
                    }
                    
                }
            });
        }
        return datas;
    }
    
    private void putAuthor(Map<String, Object> data, AttItemAuthority attItemAuthor, int isCanUpDate) {
        if(attItemAuthor.isToUse()){
        data.put("利用区分", "○");
            if(isCanUpDate == 1){
                if(attItemAuthor.isYouCanChangeIt()){
                    data.put("本人修正設定", "○" );
                }else {
                    data.put("本人修正設定", "-" );
                }
                if(attItemAuthor.isCanBeChangedByOthers()){
                    data.put("本人以外修正設定", "○" );
                }else {
                    data.put("本人以外修正設定", "-" );
                }
            }
        }else {
            data.put("利用区分", "-");
        }
        
    }

    private void putDataEmpty(Map<String, Object> data){
        data.put("コード","");
        data.put("名称","");
        data.put("コード2","");
        data.put("項目","");
        data.put("利用区分","");
        data.put("本人修正設定","");
        data.put("本人以外修正設定","");
    }
    private MasterData alignMasterDataSheetRoleDaily(Map<String, Object> data) {
        MasterData masterData = new MasterData(data, null, "");
        masterData.cellAt("コード").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("名称").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("コード2").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("項目").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("利用区分").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("本人修正設定").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("本人以外修正設定").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        return masterData;
    }
    
    //sheet 2
    public List<MasterHeaderColumn> getHeaderColumnsSheet2(MasterListExportQuery query) {
        
        List<MasterHeaderColumn> columns = new ArrayList<>();
        columns.add(new MasterHeaderColumn("コード", TextResource.localize("KDW006_106"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn("項目", TextResource.localize("KDW006_88"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn("表示名称", TextResource.localize("KDW002_65"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn("改行位置", TextResource.localize("KDW002_67"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn("ヘッダー色", TextResource.localize("KDW006_152"),
                ColumnTextAlign.LEFT, "", true));
//        columns.add(new MasterHeaderColumn("丸め単位", TextResource.localize("KDW006_153"),
//                ColumnTextAlign.LEFT, "", true));
        return columns;
    }
    
    public List<MasterData> getMasterDatasSheet2(MasterListExportQuery query, List<AttItemName> listAttItemNameNoAuth, Map<Integer, ControlOfAttendanceItemsDtoExcel> listConItem) {
		List<Integer> listIdControlItem = new ArrayList<Integer>(listConItem.keySet());
        List<MasterData> datas = new ArrayList<>();
        if (CollectionUtil.isEmpty(listIdControlItem)) {
            return null;
        } else {
        	if (!CollectionUtil.isEmpty(listAttItemNameNoAuth)) {
                listAttItemNameNoAuth.stream().forEach(c -> {
                    ControlOfAttendanceItemsDtoExcel controlItem = listConItem.get(c.getAttendanceItemId());
                    Map<String, Object> data = new HashMap<>();
                    if(controlItem!=null){
                    	putDataEmptySheet2(data);
                        data.put("コード", c.getAttendanceItemDisplayNumber());
                        data.put("項目", c.getOldName());
                        data.put("表示名称", c.getAttendanceItemName());
                        data.put("改行位置", c.getNameLineFeedPosition());
                        String color =controlItem.getHeaderBgColorOfDailyPer();
                        if(color!=null){
                        	data.put("ヘッダー色", color.replace("#", ""));
                        }
//    	                Float inputUnit = controlItem.getInputUnitOfTimeItem()==null ? 0 : controlItem.getInputUnitOfTimeItem();

//    	                switch (c.getAttendanceAtr()) {
//    	                	case 5:	//DailyAttendanceAtr.Time
//    	                		data.put("丸め単位", inputUnit % 1 == 0 ? (int) Math.floor(inputUnit) + TextResource.localize("KDW006_154") :
//    	                			inputUnit + TextResource.localize("KDW006_154"));
//    	                		break;
//    	                	case 2: //DailyAttendanceAtr.NumberOfTime
//    	                		data.put("丸め単位", inputUnit % 1 == 0 ? (int) Math.floor(inputUnit) + TextResource.localize("KDW006_230") :
//    	                			inputUnit + TextResource.localize("KDW006_230"));
//    	                		break;
//    	                	case 3: //DailyAttendanceAtr.AmountOfMoney
//    	                		data.put("丸め単位", inputUnit % 1 == 0 ? (int) Math.floor(inputUnit) + TextResource.localize("KDW006_231"):
//    	                			inputUnit + TextResource.localize("KDW006_231"));
//    	                		break;
//    	                	default:
//    	                		data.put("丸め単位","");
//    	                		break;
//    	                }

                        datas.add(alignMasterDataSheet2(data));
                    }
                });
        	}
        }
        return datas;
    }
    
    private void putDataEmptySheet2(Map<String, Object> data){
        data.put("コード","");
        data.put("項目","");
        data.put("表示名称","");
        data.put("改行位置","");
        data.put("ヘッダー色","");
//        data.put("丸め単位","");
    }
    private MasterData alignMasterDataSheet2(Map<String, Object> data) {
        MasterData masterData = new MasterData(data, null, "");
        masterData.cellAt("コード").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("項目").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("表示名称").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("改行位置").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT));
        masterData.cellAt("ヘッダー色").setStyle(MasterCellStyle.build().backgroundColor((data.get("ヘッダー色").toString())).horizontalAlign(ColumnTextAlign.LEFT));
//        masterData.cellAt("丸め単位").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT));
        return masterData;
    }
    
    //sheet 3
public List<MasterHeaderColumn> getHeaderColumnsSheet3(MasterListExportQuery query, int mode) {
        
        List<MasterHeaderColumn> columns = new ArrayList<>();
        columns.add(new MasterHeaderColumn("コード", TextResource.localize("KDW006_106"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn("名称", TextResource.localize("KDW006_107"),
                ColumnTextAlign.LEFT, "", true));
        if(mode==0){
        	columns.add(new MasterHeaderColumn("このフォーマットを初期設定にする", TextResource.localize("KDW006_225"),
                    ColumnTextAlign.LEFT, "", true));
        }
        columns.add(new MasterHeaderColumn("日次項目 Sheet選択", TextResource.localize("KDW006_187"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn("日次項目 名称", TextResource.localize("KDW006_188"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn("日次項目 項目", TextResource.localize("KDW006_189"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn("月次項目", TextResource.localize("KDW006_190"),
                ColumnTextAlign.LEFT, "", true));
        return columns;
    }
    
    public List<MasterData> getMasterDatasSheet3(MasterListExportQuery query, List<BusinessTypeDto> listBusinessType, Map<BusinessTypeCode, List<BusinessTypeFormatMonthly>> mapMonthlyBz,
    		Map<Integer, AttItemName> mapAttNameMonthlys, Map<String, Map<Integer, List<BusinessDailyExcel>>> maplistBzDaily, Map<Integer, AttItemName> mapAttNameDailys, String companyId, Map<Integer, AttItemName> mapAttNameMonthlys2, int mode, Map<String, BusinessTypeDto> mapBz) {
    	if(mode==1){
    		List<String> listBusinessTypeDailyCode = new ArrayList<String>(maplistBzDaily.keySet());
    		
	        List<MasterData> datas = new ArrayList<>();
	        if (CollectionUtil.isEmpty(listBusinessTypeDailyCode)) {
	            return null;
	        } else {
	        	Collections.sort(listBusinessTypeDailyCode);
	        	listBusinessTypeDailyCode.stream().forEach(c -> {
	                Map<String, Object> data = new HashMap<>();
	                putDataEmptySheet3(data);
	                BusinessTypeDto bzType = mapBz.get(c);
	                String name = "";
	                if(bzType==null){
	                	name = TextResource.localize("KDW006_226");
	                }else{
	                	name = bzType.getBusinessTypeName();
	                }
	                data.put("コード", c);
	                data.put("名称", name);
	                BusinessTypeCode businessTypeCode = new BusinessTypeCode(c);
	                List<BusinessTypeFormatMonthly> businessTypeFormatMonthly = mapMonthlyBz.get(businessTypeCode);
	                List<Integer> keyMonthly = Collections.emptyList();
	                if(!CollectionUtil.isEmpty(businessTypeFormatMonthly)){
	                	businessTypeFormatMonthly.sort(Comparator.comparing(BusinessTypeFormatMonthly::getOrder));
	                	keyMonthly =
	                        businessTypeFormatMonthly.stream()
	                                  .map(BusinessTypeFormatMonthly::getAttendanceItemId)
	                                  .collect(Collectors.toList());
	                }
	                List<String> codeAndNameMonthly = new ArrayList<>();
	                AttItemName attenNameMonthly = new AttItemName();
	                for (Integer key : keyMonthly) {
	                    attenNameMonthly =!CollectionUtil.isEmpty(businessTypeFormatMonthly)? mapAttNameMonthlys.get(key):null;
	                    if(attenNameMonthly!=null)
	                    codeAndNameMonthly.add(attenNameMonthly.getAttendanceItemDisplayNumber()+attenNameMonthly.getAttendanceItemName());
	                }
	                String listCodeAndNameMonthly = String.join(",", codeAndNameMonthly);
	                data.put("月次項目", listCodeAndNameMonthly);
	                //put Daily
	                Map<Integer, List<BusinessDailyExcel>> mapListBzDaily = maplistBzDaily.get(c);
	                List<Integer> listKeyBzDailyBySheetNo  = new ArrayList<>();
	                if(mapListBzDaily!=null){
	                    listKeyBzDailyBySheetNo = new ArrayList<Integer>(mapListBzDaily.keySet());
	                }
	                if(!CollectionUtil.isEmpty(listKeyBzDailyBySheetNo)){
	                	Collections.sort(listKeyBzDailyBySheetNo);
	                }
	                if(!CollectionUtil.isEmpty(listKeyBzDailyBySheetNo)){
	                    int check = 0;
	                    for (int keyBySheetNo : listKeyBzDailyBySheetNo) {
	                        List<BusinessDailyExcel> listBzDailyExcel = mapListBzDaily.get(keyBySheetNo);
	                        if(CollectionUtil.isEmpty(listBzDailyExcel)){
	                        	continue;
	                        }
	                        listBzDailyExcel.sort(Comparator.comparing(BusinessDailyExcel::getOrder));
	                        Map<String, Object> dataChil = new HashMap<>();
	                        putDataEmptySheet3(dataChil);
	                        if(check==0){
	                            data.put("日次項目 Sheet選択", keyBySheetNo);
	                            data.put("日次項目 名称", listBzDailyExcel==null?"":listBzDailyExcel.get(0).getSheetName());
	                            List<Integer> keyDaily = Collections.emptyList();
	                            if(!CollectionUtil.isEmpty(listBzDailyExcel)){
	                                keyDaily =
	                                        listBzDailyExcel.stream()
	                                              .map(BusinessDailyExcel::getAttItemId)
	                                              .collect(Collectors.toList());
	                            }
	                            List<String> codeAndNameDaily = new ArrayList<>();
	                            String duplicateStringName = "";
	                            for (Integer key : keyDaily) {
	                                AttItemName attenNameDaily =!CollectionUtil.isEmpty(listBzDailyExcel)? mapAttNameDailys.get(key):null;
	                                if(attenNameDaily!=null && !duplicateStringName.equals(attenNameDaily.getAttendanceItemDisplayNumber()+attenNameDaily.getAttendanceItemName())){
	                                    duplicateStringName = attenNameDaily.getAttendanceItemDisplayNumber()+attenNameDaily.getAttendanceItemName();
	                                    codeAndNameDaily.add(duplicateStringName);
	                                }
	                            }
	                            String listItemDaily = String.join(",", codeAndNameDaily);
	                            data.put("日次項目 項目", listItemDaily);
	                            datas.add(alignMasterDataSheet3(data));
	                            check++;
	                        }else {
	                            dataChil.put("日次項目 Sheet選択", keyBySheetNo);
	                            List<BusinessDailyExcel> value = listBzDailyExcel;
	                            dataChil.put("日次項目 名称", value.get(0).getSheetName()==null?"":value.get(0).getSheetName());
	                            List<Integer> keyDaily = Collections.emptyList();
	                            if(!CollectionUtil.isEmpty(value)){
	                                keyDaily =
	                                        value.stream()
	                                              .map(BusinessDailyExcel::getAttItemId)
	                                              .collect(Collectors.toList());
	                            }
	                            List<String> codeAndNameDaily = new ArrayList<>();
	                            String duplicateStringName = "";
	                            for (Integer key : keyDaily) {
	                                AttItemName attenNameDaily =!CollectionUtil.isEmpty(listBzDailyExcel)? mapAttNameDailys.get(key):null;
	                                if(attenNameDaily!=null && !duplicateStringName.equals(attenNameDaily.getAttendanceItemDisplayNumber()+attenNameDaily.getAttendanceItemName())){
	                                    duplicateStringName = attenNameDaily.getAttendanceItemDisplayNumber()+attenNameDaily.getAttendanceItemName();
	                                    codeAndNameDaily.add(duplicateStringName);
	                                }
	                            }
	                            String listItemDaily = String.join(",", codeAndNameDaily);
	                            dataChil.put("日次項目 項目", listItemDaily);
	                            datas.add(alignMasterDataSheet3(dataChil));
	                        }
	                    }
	                }else {
	                    datas.add(alignMasterDataSheet3(data));
	                }
	                
	            });
	        }
	        return datas;
        }else {
        	List<MasterData> datas = new ArrayList<>();
        	Map<String, Map<Integer, List<PerAuthFormatItem>>> mapDaiPerAuth= perAuthFormatExport.getAllDailyByComp(companyId);
        	Map<String, List<PerAuthFormatItem>> mapMonPerAuth = perAuthFormatExport.getAllDaiMonthlyByComp(companyId);
        	List<String> keyDaiCode = new ArrayList<String>(mapDaiPerAuth.keySet());
        	//putDaily
        	if(CollectionUtil.isEmpty(keyDaiCode)){
        		return null;
        	}
        	Collections.sort(keyDaiCode);
        	keyDaiCode.stream().forEach(x->{
        		Map<Integer, List<PerAuthFormatItem>> mapAttItem = mapDaiPerAuth.get(x);
        		Map<String, Object> data = new HashMap<>();
                putDataEmptySheet3ModeAuth(data);
        		data.put("コード", x);
        		Map.Entry<Integer,List<PerAuthFormatItem>> entry = mapAttItem.entrySet().iterator().next();
        		PerAuthFormatItem daiFirst = entry.getValue().get(0);
        		data.put("名称", daiFirst.getDailyName()==null?TextResource.localize("KDW006_226"):daiFirst.getDailyName());
        		data.put("このフォーマットを初期設定にする", daiFirst.getAvailability()==1?"○":"-");
        		//put Monthly
        		List<PerAuthFormatItem> listMon = mapMonPerAuth.get(x);
        		if(!CollectionUtil.isEmpty(listMon)){
        			listMon.sort(Comparator.comparing(PerAuthFormatItem::getDisplayOder));
        		}
        		
        		String nameAndCodeMon = "";
        		List<String> listResult = new ArrayList<>();
                for (PerAuthFormatItem key : listMon) {
                	AttItemName attMon = mapAttNameMonthlys.get(key.getAttId());
                    if(attMon!=null){
                    	listResult.add(attMon.getAttendanceItemDisplayNumber()+attMon.getAttendanceItemName());
                    }
                }
                if(!CollectionUtil.isEmpty(listResult)){
                	nameAndCodeMon = String.join(",", listResult);
                }
                
        		data.put("月次項目", nameAndCodeMon);
        		List<Integer> sheetNos = new ArrayList<Integer>(mapAttItem.keySet());
            	if(CollectionUtil.isEmpty(sheetNos)){
            		return;
            	}
        		for(int i = 0 ; i < sheetNos.size();i++){
        			List<PerAuthFormatItem> listDaily = mapAttItem.get(sheetNos.get(i));
        			if(CollectionUtil.isEmpty(listDaily)){
        				continue;
        			}
        			listDaily.sort(Comparator.comparing(PerAuthFormatItem::getDisplayOder));
        			 //get name dailyAtt
                    List<String> result = new ArrayList<>();
                    listDaily.stream().forEach(z->{
                    	AttItemName att = mapAttNameDailys.get(z.getAttId());
                    	if(att!=null){
                    		result.add(att.getAttendanceItemDisplayNumber()+att.getAttendanceItemName());
                    	}
                    });
        			if(i ==0){
	        			data.put("日次項目 Sheet選択", sheetNos.get(i));
	        			data.put("日次項目 名称", listDaily.get(0).getSheetName());
                    	if(!CollectionUtil.isEmpty(result)){
                    		String codeAndNameAttDai = String.join(",", result);
                    		data.put("日次項目 項目", codeAndNameAttDai);
                    	}
	        			datas.add(alignMasterDataSheet3ModeAuth(data));

        			}else {
            			Map<String, Object> dataChild = new HashMap<>();
                        putDataEmptySheet3ModeAuth(dataChild);
                        dataChild.put("日次項目 Sheet選択", sheetNos.get(i));
                        dataChild.put("日次項目 名称", listDaily.get(0).getSheetName());
                    	if(!CollectionUtil.isEmpty(result)){
                      		 String codeAndNameAttDai = String.join(",", result);
                      		 dataChild.put("日次項目 項目", codeAndNameAttDai);
                    	}
                       
                        datas.add(alignMasterDataSheet3ModeAuth(dataChild));
					}
        			
        		}
        		
        	});
        	
        	return datas;
		}
    }
    
    private MasterData alignMasterDataSheet3ModeAuth(Map<String, Object> data) {
        MasterData masterData = new MasterData(data, null, "");
        masterData.cellAt("コード").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("名称").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("このフォーマットを初期設定にする").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("日次項目 Sheet選択").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT));
        masterData.cellAt("日次項目 名称").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("日次項目 項目").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("月次項目").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        return masterData;
	}

	private void putDataEmptySheet3ModeAuth(Map<String, Object> data) {
    	data.put("コード","");
        data.put("名称","");
        data.put("このフォーマットを初期設定にする","");
        data.put("日次項目 Sheet選択","");
        data.put("日次項目 名称","");
        data.put("日次項目 項目","");
        data.put("月次項目","");
		
	}

	private void putDataEmptySheet3(Map<String, Object> data){
        data.put("コード","");
        data.put("名称","");
        data.put("日次項目 Sheet選択","");
        data.put("日次項目 名称","");
        data.put("日次項目 項目","");
        data.put("月次項目","");
    }
    private MasterData alignMasterDataSheet3(Map<String, Object> data) {
        MasterData masterData = new MasterData(data, null, "");
        masterData.cellAt("コード").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("名称").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("日次項目 Sheet選択").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT));
        masterData.cellAt("日次項目 名称").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("日次項目 項目").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("月次項目").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        return masterData;
    }
public List<MasterHeaderColumn> getHeaderColumnsSheet5(MasterListExportQuery query) {
        
        List<MasterHeaderColumn> columns = new ArrayList<>();
        columns.add(new MasterHeaderColumn("コード", TextResource.localize("KDW006_106"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn("名称", TextResource.localize("KDW006_90"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn("順", TextResource.localize("KDW006_191"),
                ColumnTextAlign.LEFT, "", true));
        return columns;
    }
    
    public List<MasterData> getMasterDatasSheet5(MasterListExportQuery query, List<BusinessTypeSortedDto> listBzTypeSort) {
        
        List<MasterData> datas = new ArrayList<>();
        if (CollectionUtil.isEmpty(listBzTypeSort)) {
            return null;
        } else {
            listBzTypeSort.stream().forEach(c -> {
                
                Map<String, Object> data = new HashMap<>();
                putDataEmptySheet5(data);
                BusinessTypeSortedDto bzSort = c;
                data.put("コード", bzSort.getDislayNumber());
                data.put("名称", bzSort.getAttendanceItemName());
                data.put("順", bzSort.getOrder());
                datas.add(alignMasterDataSheet5(data));
                
            });
        }
        return datas;
    }
    
    private void putDataEmptySheet5(Map<String, Object> data){
        data.put("コード","");
        data.put("名称","");
        data.put("順","");
    }
    private MasterData alignMasterDataSheet5(Map<String, Object> data) {

        MasterData masterData = new MasterData(data, null, "");
        masterData.cellAt("コード").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("名称").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("順").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT));
        return masterData;
    }
    
    //sheet 6
    public List<MasterHeaderColumn> getHeaderColumnsSheet6(MasterListExportQuery query, List<String> headerSheet6) {
        List<MasterHeaderColumn> columns = new ArrayList<>();
    	for (String string : headerSheet6) {
    		columns.add(new MasterHeaderColumn(string, string,
                    ColumnTextAlign.LEFT, "", true));
    	}
        
        return columns;
    }
    
    public List<MasterData> getMasterDatasSheet6(MasterListExportQuery query, Map<String, Map<Integer, List<WorkTypeDtoExcel>>> mapTypeByEmpAndGroup, List<String> headerSheet6) {
        List<String> listEmpCode = new ArrayList<String>(mapTypeByEmpAndGroup.keySet());
        Collections.sort(listEmpCode);
        List<MasterData> datas = new ArrayList<>();
        if (CollectionUtil.isEmpty(listEmpCode)) {
            return null;
        } else {
            for (String empCode : listEmpCode) {
                Map<String, Object> data = new HashMap<>();
                putDataEmptySheet6(data,headerSheet6);
                Map<Integer, List<WorkTypeDtoExcel>> mapbyGroupNo = mapTypeByEmpAndGroup.get(empCode);
                List<Integer> listGroupNo = new ArrayList<Integer>(mapbyGroupNo.keySet());
                Collections.sort(listGroupNo);
                for (Integer GroupNo : listGroupNo) {
                    List<WorkTypeDtoExcel> groupWorkType = mapbyGroupNo.get(GroupNo);
                    groupWorkType.sort(Comparator.comparing(WorkTypeDtoExcel::getWorkTypeCode));
                    if(!CollectionUtil.isEmpty(groupWorkType)){
                    
                        List<String> listString = new ArrayList<>();
						for (WorkTypeDtoExcel workTypeDtoExcel : groupWorkType) {
							if(workTypeDtoExcel.getWorkTypeCode()!=null&&!("".equals(workTypeDtoExcel.getWorkTypeCode()))){
								listString.add(workTypeDtoExcel.getWorkTypeCode()+(workTypeDtoExcel.getWorkTypeName()==null?TextResource.localize("KDW006_226"):workTypeDtoExcel.getWorkTypeName()));
							}
						}
                        String listWorkTypeName = "";
                        if(!CollectionUtil.isEmpty(listString)){
                        	listString.removeIf(item -> item == null || "".equals(item));
                        	listWorkTypeName = String.join(",", listString);
                        }
                        switch (GroupNo) {
						case 1:
						case 2:
						case 3:
						case 4:
							data.put(headerSheet6.get(GroupNo+1),listWorkTypeName);
							break;
						case 5:
							data.put(headerSheet6.get(6),groupWorkType.get(0).getGroupName());
							data.put(headerSheet6.get(7),listWorkTypeName);
							break;
						case 6:
							data.put(headerSheet6.get(8),groupWorkType.get(0).getGroupName());
							data.put(headerSheet6.get(9),listWorkTypeName);
							break;
						case 7:
							data.put(headerSheet6.get(10),groupWorkType.get(0).getGroupName());
							data.put(headerSheet6.get(11),listWorkTypeName);
							break;
						case 8:
							data.put(headerSheet6.get(12),groupWorkType.get(0).getGroupName());
							data.put(headerSheet6.get(13),listWorkTypeName);
							break;
						case 9:
							data.put(headerSheet6.get(14),groupWorkType.get(0).getGroupName());
							data.put(headerSheet6.get(15),listWorkTypeName);
							break;
						case 10:
							data.put(headerSheet6.get(16),groupWorkType.get(0).getGroupName());
							data.put(headerSheet6.get(17),listWorkTypeName);
							break;
						default:
							break;
						}
                        
                    }
                    
                }
                data.put("コード",empCode);
                data.put("名称",mapbyGroupNo.get(listGroupNo.get(0)).get(0).getNameEmp()==null?TextResource.localize("KDW006_226"):mapbyGroupNo.get(listGroupNo.get(0)).get(0).getNameEmp());
                datas.add(alignMasterDataSheet6(data,headerSheet6));
            }
        }
        return datas;
    }
    
    private void putDataEmptySheet6(Map<String, Object> data, List<String> headerSheet6){
    	for (String string : headerSheet6) {
    		data.put(string,"");
		}
    }
    private MasterData alignMasterDataSheet6(Map<String, Object> data, List<String> headerSheet6) {
        MasterData masterData = new MasterData(data, null, "");
	for (String string : headerSheet6) {
		masterData.cellAt(string).setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
	}
        return masterData;
    }
    private List<String> addTextHeader() {
	  	String column1 = TextResource.localize("KDW006_106");
	    String column2 = TextResource.localize("KDW006_90");
	    String column3 = TextResource.localize("KDW006_192");
	    String column4 = TextResource.localize("KDW006_193");
	    String column5 = TextResource.localize("KDW006_194");
	    String column6 = TextResource.localize("KDW006_195");
	    String column7 = TextResource.localize("KDW006_196");
	    String column8 = TextResource.localize("KDW006_197");
	    String column9 = TextResource.localize("KDW006_198");
	    String column10 = TextResource.localize("KDW006_199"); 
	    String column11 = TextResource.localize("KDW006_200");
	    String column12 = TextResource.localize("KDW006_201");
	    String column13 = TextResource.localize("KDW006_202");
	    String column14 = TextResource.localize("KDW006_203");
	    String column15 = TextResource.localize("KDW006_204");
	    String column16 = TextResource.localize("KDW006_205");
	    String column17 = TextResource.localize("KDW006_206");
	    String column18 = TextResource.localize("KDW006_207");
		return Arrays.asList(column1,column2,column3,column4,column5,column6,column7,column8,column9,column10,column11,column12,column13,column14,column15,column16,column17,column18);
	}
}


