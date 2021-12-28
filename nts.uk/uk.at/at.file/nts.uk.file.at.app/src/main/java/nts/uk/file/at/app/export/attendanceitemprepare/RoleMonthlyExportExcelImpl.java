package nts.uk.file.at.app.export.attendanceitemprepare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.function.app.find.monthlycorrection.fixedformatmonthly.BusinessTypeSortedMonDto;
import nts.uk.ctx.at.function.app.find.monthlycorrection.fixedformatmonthly.BusinessTypeSortedMonFinder;
import nts.uk.ctx.at.function.app.find.monthlycorrection.fixedformatmonthly.DisplayTimeItemDto;
import nts.uk.ctx.at.function.app.find.monthlycorrection.fixedformatmonthly.MonthlyActualResultsDto;
import nts.uk.ctx.at.function.app.find.monthlycorrection.fixedformatmonthly.MonthlyRecordWorkTypeDto;
import nts.uk.ctx.at.function.app.find.monthlycorrection.fixedformatmonthly.MonthlyRecordWorkTypeFinder;
import nts.uk.ctx.at.function.app.find.monthlycorrection.fixedformatmonthly.OrderReferWorkTypeDto;
import nts.uk.ctx.at.function.app.find.monthlycorrection.fixedformatmonthly.SheetCorrectedMonthlyDto;
import nts.uk.ctx.at.record.app.find.dailyperformanceformat.BusinessTypesFinder;
import nts.uk.ctx.at.record.app.find.dailyperformanceformat.dto.BusinessTypeDto;
import nts.uk.ctx.at.record.app.find.workrecord.authfuncrest.EmployeeRoleDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemAuthority;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemName;
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
public class RoleMonthlyExportExcelImpl  {

    //sheet1
    @Inject
    private AttItemNameByAuth attItemNameByAuth;
    //sheet 2
    @Inject
    private ControlOfAttMonthlyRepoExcel controlOfItemlExcel;
    //sheet 3
    @Inject
    private BusinessTypesFinder findAll;
    @Inject
    private MonthlyRecordWorkTypeFinder fiderRecordMonthly;
    //sheet 4
    @Inject
    private BusinessTypeSortedMonFinder businessFinder;
    //shet 14
    @Inject
    OperationExcelRepo operationExcelRepo;

    @Inject
    PerAuthFormatExport perAuthFormatExport;
    
    public List<SheetData> extraSheets(MasterListExportQuery query, List<EmployeeRoleDto> listEmployeeRoleDto, Map<Integer, AttItemName> mapAttNameMonthlys, List<AttItemName> listAttItemNameNoAuth, int mode) {

    	String companyId = AppContexts.user().companyId();
        Map<String, List<AttItemName>> authSeting = new HashMap<>();
        authSeting.putAll(attItemNameByAuth.getAllMonthlyByComp(companyId));
    	Map<Integer, ControlOfAttMonthlyDtoExcel> listConItem = new HashMap<>();
        List<BusinessTypeDto> listBzMonthly = new ArrayList<>();   
        List<MonthlyRecordWorkTypeDto> listMonthlyRecord = new ArrayList<>();
        Map<String, MonthlyRecordWorkTypeDto> mapListRecordMonthly = new HashMap<>();
        List<OrderReferWorkTypeDto> listOrderReferWorkType = new ArrayList<>();

        initSheet2(listConItem,companyId);
        initSheet3(listBzMonthly,listMonthlyRecord,mapListRecordMonthly);
        initSheet4(listOrderReferWorkType);
        // add the work place sheet
        List<SheetData> sheetDatas = new ArrayList<>();
        SheetData sheet1 = new SheetData(getMasterDatas(query,listEmployeeRoleDto,authSeting,listAttItemNameNoAuth),
                getHeaderColumns(query), null, null, TextResource.localize("KDW006_145"), MasterListMode.NONE);
        sheetDatas.add(sheet1);
        SheetData sheet2 = new SheetData(getMasterDatasSheet2(query,listAttItemNameNoAuth,listConItem,mapListRecordMonthly),
                getHeaderColumnsSheet2(query), null, null, TextResource.localize("KDW006_146"), MasterListMode.NONE);
        sheetDatas.add(sheet2);
        SheetData sheet3 = new SheetData(getMasterDatasSheet3(query,listBzMonthly,mapListRecordMonthly,mapAttNameMonthlys,companyId,mode),
                getHeaderColumnsSheet3(query,mode), null, null, TextResource.localize("KDW006_147"), MasterListMode.NONE);
        sheetDatas.add(sheet3);
        if(mode==1){
        	 SheetData sheet4 = new SheetData(getMasterDatasSheet4(query,listOrderReferWorkType,mapAttNameMonthlys),
                     getHeaderColumnsSheet4(query), null, null, TextResource.localize("KDW006_148"), MasterListMode.NONE);
             sheetDatas.add(sheet4);
        }
       
        return sheetDatas;
    }


    private void initSheet4(List<OrderReferWorkTypeDto> listOrderReferWorkType) {
        BusinessTypeSortedMonDto bzTypeSortMon =  businessFinder.getBusinessTypeSortedMon();
        if(bzTypeSortMon!=null){
            listOrderReferWorkType.addAll(bzTypeSortMon.getListOrderReferWorkType());
            listOrderReferWorkType.sort(Comparator.comparing(OrderReferWorkTypeDto::getOrder));
        }
    }

    private void initSheet3(List<BusinessTypeDto> listBzMonthly, List<MonthlyRecordWorkTypeDto> listMonthlyRecord, Map<String, MonthlyRecordWorkTypeDto> mapListRecordMonthly) {
        listBzMonthly.addAll(findAll.findAll());
        listMonthlyRecord.addAll(fiderRecordMonthly.getListMonthlyRecordWorkType());
        mapListRecordMonthly.putAll(listMonthlyRecord.stream().collect(Collectors.toMap(MonthlyRecordWorkTypeDto::getBusinessTypeCode,
                Function.identity())));
    }

    private void initSheet2(Map<Integer, ControlOfAttMonthlyDtoExcel> listConItem, String companyId) {
         listConItem.putAll(controlOfItemlExcel.getAllByCompanyId(companyId));
    }

    public List<MasterHeaderColumn> getHeaderColumns(MasterListExportQuery query) {
        
        List<MasterHeaderColumn> columns = new ArrayList<>();
        columns.add(new MasterHeaderColumn("コード", TextResource.localize("KDW006_106"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn("名称", TextResource.localize("KDW006_90"),
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
                Map<String, Object> data = new HashMap<>();
                Map<String, Object> dataChild = new HashMap<>();
                putDataEmpty(data);
                putDataEmpty(dataChild);
                data.put("コード", c.getRoleCode());
                data.put("名称", c.getRoleName());
                List<AttItemName> attItemNamesAuthSet = authSeting.get(c.getRoleId());
                if(CollectionUtil.isEmpty(attItemNamesAuthSet)){
                	return;
                }
                Map<Integer, AttItemName> mapListItemNameAuthSet =
                        attItemNamesAuthSet.stream().collect(Collectors.toMap(AttItemName::getAttendanceItemId,
                                                                  Function.identity()));
                attItemNamesAuthSet.sort(Comparator.comparing(AttItemName::getAttendanceItemDisplayNumber));
                if(!CollectionUtil.isEmpty(listAttItemNameNoAuth)){
                    if(listAttItemNameNoAuth.size()==1){
                        AttItemName  attItemName = listAttItemNameNoAuth.get(0);
                        data.put("コード2", attItemName.getAttendanceItemDisplayNumber());
                        data.put("項目", attItemName.getAttendanceItemName());
                        //putAthSeting
                        AttItemAuthority attItemAuthor = mapListItemNameAuthSet.get(attItemName.getAttendanceItemId()).getAuthority();
                        putAuthor(data,attItemAuthor,attItemName.getUserCanUpdateAtr());
                        alignMasterDataSheetRoleDaily(data);
                        datas.add(alignMasterDataSheetRoleDaily(data));
                        putDataEmpty(data);
                    }else {
                        AttItemName  attItemName = listAttItemNameNoAuth.get(0);
                        data.put("コード2", attItemName.getAttendanceItemDisplayNumber());
                        data.put("項目", attItemName.getAttendanceItemName());
                        //putAthSeting
                        if(mapListItemNameAuthSet.get(attItemName.getAttendanceItemId())!=null  ){
                            AttItemAuthority attItemAuthor = mapListItemNameAuthSet.get(attItemName.getAttendanceItemId()).getAuthority();
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
                            if(mapListItemNameAuthSet.get(attItemName.getAttendanceItemId())!=null){
                                AttItemAuthority attItemAuthor = mapListItemNameAuthSet.get(attItemName.getAttendanceItemId()).getAuthority();
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
    
    public List<MasterData> getMasterDatasSheet2(MasterListExportQuery query, List<AttItemName> listAttItemNameNoAuth, Map<Integer, ControlOfAttMonthlyDtoExcel> listConItem, Map<String, MonthlyRecordWorkTypeDto> mapListRecordMonthly) {
    	List<Integer> listIdControlItem = new ArrayList<Integer>(listConItem.keySet());
    	List<MasterData> datas = new ArrayList<>();
        if (CollectionUtil.isEmpty(listIdControlItem)) {
            return null;
        } else {
        	if (!CollectionUtil.isEmpty(listAttItemNameNoAuth)){
        		listAttItemNameNoAuth.stream().forEach(c -> {
                    ControlOfAttMonthlyDtoExcel controlItem = listConItem.get(c.getAttendanceItemId());
                   
                    if(controlItem!=null){
                    	 Map<String, Object> data = new HashMap<>();
                         putDataEmptySheet2(data);
                         data.put("コード", c.getAttendanceItemDisplayNumber());
                         data.put("項目", c.getOldName());
                         data.put("表示名称", c.getAttendanceItemName());
                         data.put("改行位置", c.getNameLineFeedPosition());
                        if(controlItem.getHeaderBgColorOfMonthlyPer()!=null){
                            data.put("ヘッダー色", controlItem.getHeaderBgColorOfMonthlyPer().replace("#", ""));
                        }
//                        Float inputUnit = controlItem.getInputUnitOfTimeItem()==null ? 0 : controlItem.getInputUnitOfTimeItem();
    	                
//    	                switch (c.getAttendanceAtr()) {
//    	                	case 1:	//MonthlyAttendanceItemAtr.Time
//    	                		data.put("丸め単位", inputUnit % 1 == 0 ? (int) Math.floor(inputUnit) + TextResource.localize("KDW006_154") :
//    	                			inputUnit + TextResource.localize("KDW006_154"));
//    	                		break;
//    	                	case 2: //MonthlyAttendanceItemAtr.NumberOfTime
//    	                		data.put("丸め単位", inputUnit % 1 == 0 ? (int) Math.floor(inputUnit) + TextResource.localize("KDW006_230") :
//    	                			inputUnit + TextResource.localize("KDW006_230"));
//    	                		break;
//    	                	case 4: //MonthlyAttendanceItemAtr.AmountOfMoney
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
        data.put("表示名称", "");
        data.put("改行位置", "");
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
        columns.add(new MasterHeaderColumn("名称", TextResource.localize("KDW006_90"),
        		ColumnTextAlign.LEFT, "", true));
        if(mode==0){
        	columns.add(new MasterHeaderColumn("このフォーマットを初期設定にする", TextResource.localize("KDW006_225"),
        			ColumnTextAlign.LEFT, "", true));
        }
        columns.add(new MasterHeaderColumn("Sheet選択", TextResource.localize("KDW006_208"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn("名称SheetName", TextResource.localize("KDW006_90"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn("月次項目 項目", TextResource.localize("KDW006_209"),
                ColumnTextAlign.LEFT, "", true));
        return columns;
    }
    
    public List<MasterData> getMasterDatasSheet3(MasterListExportQuery query, List<BusinessTypeDto> listBzMonthly, Map<String, MonthlyRecordWorkTypeDto> mapListRecordMonthly, Map<Integer, AttItemName> mapAttNameMonthlys, String companyId, int mode) {
    	if(mode==1){
	        List<MasterData> datas = new ArrayList<>();
	        List<String> listWorkTypeCode =  new ArrayList<String>(mapListRecordMonthly.keySet());
	        Map<String, BusinessTypeDto> maplistBzMonthly =
	        		listBzMonthly.stream().collect(Collectors.toMap(BusinessTypeDto::getBusinessTypeCode,
	        	                                              Function.identity()));
	       
	        if (CollectionUtil.isEmpty(listWorkTypeCode)) {
	            return null;
	        } else {
	        	Collections.sort(listWorkTypeCode);
	        	listWorkTypeCode.stream().forEach(c -> {
	                Map<String, Object> data = new HashMap<>();
	                putDataEmptySheet3(data,mode);
	                MonthlyRecordWorkTypeDto montlhyRecord = mapListRecordMonthly.get(c);
	                BusinessTypeDto bzType =  maplistBzMonthly.get(c);
	                String name ="";
	                if(bzType==null){
	                	name = TextResource.localize("KDW006_226");
	                }else {
						name = bzType.getBusinessTypeName();
					}
	                data.put("コード", c);
	                data.put("名称", name);
	                if(montlhyRecord!=null &&montlhyRecord.getDisplayItem()!=null){
	                    MonthlyActualResultsDto monActualResult = montlhyRecord.getDisplayItem();
	                    List<SheetCorrectedMonthlyDto> listSheetCorrectedMonthly = monActualResult.getListSheetCorrectedMonthly();
	                    
	                    int check = 0;
	                    if(!CollectionUtil.isEmpty(listSheetCorrectedMonthly)){
	                    	listSheetCorrectedMonthly.sort(Comparator.comparing(SheetCorrectedMonthlyDto::getSheetNo));
	                        for(int i = 0 ; i < listSheetCorrectedMonthly.size() ; i++) {
	                            Map<String, Object> dataChil = new HashMap<>();
	                            putDataEmptySheet3(dataChil,mode);
	                            SheetCorrectedMonthlyDto sheetCorectMon = listSheetCorrectedMonthly.get(i);
	                            if(check==0){
	                                data.put("Sheet選択", sheetCorectMon.getSheetNo());
	                                data.put("名称SheetName", sheetCorectMon.getSheetName());
	                                //add last column
	                                List<String> codeAndNameMonthly = new ArrayList<>();
	                                AttItemName attenNameMonthly = new AttItemName();
	                                List<DisplayTimeItemDto> listDisplayItem = sheetCorectMon.getListDisplayTimeItem();
	                                if(!CollectionUtil.isEmpty(listDisplayItem)){
	                                    listDisplayItem.sort(Comparator.comparing(DisplayTimeItemDto::getDisplayOrder));
	                                    for (DisplayTimeItemDto displayTimeItemDto : listDisplayItem) {
	                                        attenNameMonthly = mapAttNameMonthlys.get(displayTimeItemDto.getItemDaily());
	                                        if(attenNameMonthly!=null)
	                                        codeAndNameMonthly.add(attenNameMonthly.getAttendanceItemDisplayNumber()+attenNameMonthly.getAttendanceItemName());
	                                    }
	                                }
	                                String listCodeAndNameMonthly = String.join(",", codeAndNameMonthly);
	                                data.put("月次項目 項目", listCodeAndNameMonthly);
	                                datas.add(alignMasterDataSheet3(data,mode));
	                                check++;
	                            }else {
	                                putDataEmptySheet3(dataChil,mode);
	                                dataChil.put("Sheet選択", sheetCorectMon.getSheetNo());
	                                dataChil.put("名称SheetName", sheetCorectMon.getSheetName());
	                                //add last column
	                                List<String> codeAndNameMonthly = new ArrayList<>();
	                                AttItemName attenNameMonthly = new AttItemName();
	                                List<DisplayTimeItemDto> listDisplayItem = sheetCorectMon.getListDisplayTimeItem();
	                                if(!CollectionUtil.isEmpty(listDisplayItem)){
	                                    listDisplayItem.sort(Comparator.comparing(DisplayTimeItemDto::getDisplayOrder));
	                                    for (DisplayTimeItemDto displayTimeItemDto : listDisplayItem) {
	                                        attenNameMonthly = mapAttNameMonthlys.get(displayTimeItemDto.getItemDaily());
	                                        if(attenNameMonthly!=null)
	                                        codeAndNameMonthly.add(attenNameMonthly.getAttendanceItemDisplayNumber()+attenNameMonthly.getAttendanceItemName());
	                                    }
	                                }
	                                String listCodeAndNameMonthly = String.join(",", codeAndNameMonthly);
	                                dataChil.put("月次項目 項目", listCodeAndNameMonthly);
	                                datas.add(alignMasterDataSheet3(dataChil,mode));
	                            }
	                        }
	                    }
	                }
	            });
	        }
	        return datas;
        }else {
        	List<MasterData> datas = new ArrayList<>();
        	Map<String, Map<Integer, List<PerAuthFormatItem>>> mapMonPerAuth= perAuthFormatExport.getAllMonByComp(companyId);
        	List<String> keyMonCode = new ArrayList<String>(mapMonPerAuth.keySet());
        	if(CollectionUtil.isEmpty(keyMonCode)){
        		return null;
        	}
        	Collections.sort(keyMonCode);
        	keyMonCode.stream().forEach(x->{
        		Map<Integer, List<PerAuthFormatItem>> mapAttItem = mapMonPerAuth.get(x);
        		Map<String, Object> data = new HashMap<>();
                putDataEmptySheet3(data,mode);
        		data.put("コード", x);
        		Map.Entry<Integer,List<PerAuthFormatItem>> entry = mapAttItem.entrySet().iterator().next();
        		PerAuthFormatItem monFirst = entry.getValue().get(0);
        		data.put("名称", monFirst.getDailyName()==null?TextResource.localize("KDW006_226"): monFirst.getDailyName());
        		data.put("このフォーマットを初期設定にする", monFirst.getAvailability()==1?"○":"-");
                
        		List<Integer> keySheetNo = new ArrayList<Integer>(mapAttItem.keySet());
            	if(CollectionUtil.isEmpty(keyMonCode)){
            		return;
            	}
        		Collections.sort(keySheetNo);
        		for(int i = 0 ; i < keySheetNo.size();i++){
        			List<PerAuthFormatItem> listMon = mapAttItem.get(keySheetNo.get(i));
        			listMon.sort(Comparator.comparing(PerAuthFormatItem::getDisplayOder));
        			 //get name dailyAtt
                    List<String> result = new ArrayList<>();
                    listMon.stream().forEach(z->{
                    	AttItemName att = mapAttNameMonthlys.get(z.getAttId());
                    	if(att!=null){
                    		result.add(att.getAttendanceItemDisplayNumber()+att.getAttendanceItemName());
                    	}
                    });
        			if(i ==0){
	        			data.put("Sheet選択", keySheetNo.get(i));
	        			data.put("名称SheetName", listMon.get(0).getSheetName());
                    	if(!CollectionUtil.isEmpty(result)){
                    		String codeAndNameAtt = String.join(",", result);
                    		data.put("月次項目 項目", codeAndNameAtt);
                    	}
	        			datas.add(alignMasterDataSheet3(data,mode));

        			}else {
            			Map<String, Object> dataChild = new HashMap<>();
                        putDataEmptySheet3(dataChild,mode);
                        dataChild.put("Sheet選択", keySheetNo.get(i));
                        dataChild.put("名称SheetName", listMon.get(0).getSheetName());
                    	if(!CollectionUtil.isEmpty(result)){
                      		 String codeAndNameAtt = String.join(",", result);
                      		 dataChild.put("月次項目 項目", codeAndNameAtt);
                    	}
                       
                        datas.add(alignMasterDataSheet3(dataChild,mode));
					}
        			
        		}
        		
        	});
        	
        	return datas;
		}
    }
    
    private void putDataEmptySheet3(Map<String, Object> data, int mode){
        data.put("コード","");
        data.put("名称","");
        if(mode==0){
        	data.put("このフォーマットを初期設定にする","");
        }
        data.put("Sheet選択","");
        data.put("名称SheetName","");
        data.put("月次項目 項目","");
    }
    private MasterData alignMasterDataSheet3(Map<String, Object> data, int mode) {
        MasterData masterData = new MasterData(data, null, "");
        masterData.cellAt("コード").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("名称").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        if(mode == 0){
        	masterData.cellAt("このフォーマットを初期設定にする").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        }
        masterData.cellAt("Sheet選択").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT));
        masterData.cellAt("名称SheetName").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("月次項目 項目").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        return masterData;
    }

    //SHEET 4
    public List<MasterHeaderColumn> getHeaderColumnsSheet4(MasterListExportQuery query) {
        
        List<MasterHeaderColumn> columns = new ArrayList<>();
        columns.add(new MasterHeaderColumn("コード", TextResource.localize("KDW006_106"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn("名称", TextResource.localize("KDW006_90"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn("順", TextResource.localize("KDW006_191"),
                ColumnTextAlign.LEFT, "", true));
        return columns;
    }

    public List<MasterData> getMasterDatasSheet4(MasterListExportQuery query, List<OrderReferWorkTypeDto> listOrderReferWorkType, Map<Integer, AttItemName> mapAttNameMonthlys) {
        
        List<MasterData> datas = new ArrayList<>();
        if (CollectionUtil.isEmpty(listOrderReferWorkType)) {
            return null;
        } else {
            listOrderReferWorkType.stream().forEach(c -> {
                
                Map<String, Object> data = new HashMap<>();
                putDataEmptySheet4(data);
                OrderReferWorkTypeDto oderBzSort = c;
                AttItemName attDtdo = mapAttNameMonthlys.get(c.getAttendanceItemID());
                String nameAtt =attDtdo!=null?attDtdo.getAttendanceItemName():"";
                data.put("コード", attDtdo!=null?attDtdo.getAttendanceItemDisplayNumber():"");
                data.put("名称", nameAtt);
                data.put("順", oderBzSort.getOrder());
                datas.add(alignMasterDataSheet4(data));
                
            });
        }
        return datas;
    }
    
    private void putDataEmptySheet4(Map<String, Object> data){
        data.put("コード","");
        data.put("名称","");
        data.put("順","");
    }
    private MasterData alignMasterDataSheet4(Map<String, Object> data) {
    
        MasterData masterData = new MasterData(data, null, "");
        masterData.cellAt("コード").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("名称").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("順").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT));
        return masterData;
    }
}

