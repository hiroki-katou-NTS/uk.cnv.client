package nts.uk.file.at.app.export.attendanceitemprepare;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.confirmemployment.RestrictConfirmEmployment;
import nts.uk.ctx.at.record.dom.workrecord.authormanage.DailyPerformFuncRepo;
import nts.uk.ctx.at.record.dom.workrecord.authormanage.DailyPerformanceFunction;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.ApprovalProcess;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.DaiPerformanceFun;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.IdentityProcess;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.MonPerformanceFun;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.YourselfConfirmError;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.masterlist.annotation.DomainID;
import nts.uk.shr.infra.file.report.masterlist.data.ColumnTextAlign;
import nts.uk.shr.infra.file.report.masterlist.data.MasterCellStyle;
import nts.uk.shr.infra.file.report.masterlist.data.MasterData;
import nts.uk.shr.infra.file.report.masterlist.data.MasterHeaderColumn;
import nts.uk.shr.infra.file.report.masterlist.data.MasterListData;
import nts.uk.shr.infra.file.report.masterlist.data.SheetData;
import nts.uk.shr.infra.file.report.masterlist.webservice.MasterListExportQuery;
import nts.uk.shr.infra.file.report.masterlist.webservice.MasterListMode;
/**
*
* @author HoiDD
*
*/
@Stateless
@DomainID(value = "OperationSetting")
public class OperationExcelRepoImpl implements MasterListData {

    @Inject
    OperationExcelRepo operationExcelRepo;
    
    @Inject
    RoleDailyExportExcelImpl roleDailyExportExcelImpl;

    @Inject
    RoleMonthlyExportExcelImpl roleMonthlyExportExcelImpl;
	
    @Inject
	private DailyPerformFuncRepo dailyPerfFunctionRepo;
    
    @Override
    public String mainSheetName() {
        return TextResource.localize("KDW006_91");
    }

	@Override
	public MasterListMode mainSheetMode(){
		return MasterListMode.NONE;
	}

    @Override
    public List<SheetData> extraSheets(MasterListExportQuery query) {
    	List<DailyPerformanceFunction> listDailyPerformanceFunction= dailyPerfFunctionRepo.getAll();
    	listDailyPerformanceFunction.sort(Comparator.comparing(DailyPerformanceFunction::getDisplayOrder));
    	List<String> header = new ArrayList<>();
    	header.add("コード");
    	header.add("名称");
    	listDailyPerformanceFunction.stream().forEach(x->{
    		header.add(x.getDisplayName().v());
    	});
        //sheet 1  共通_機能制限
        List<SheetData> sheetDatas = new ArrayList<>();
        //Sheet 2 共通_権限別機能制限
        SheetData sheetRole = new SheetData(getMasterDatasSheetRoled(query, header),
                getHeaderColumnsSheetRole(query, header), null, null,TextResource.localize("KDW006_105"), MasterListMode.NONE);
        sheetDatas.add(sheetRole);
        sheetDatas.addAll(roleDailyExportExcelImpl.extraSheets(query));
        return sheetDatas;
    }

    @Override
    public List<MasterHeaderColumn> getHeaderColumns(MasterListExportQuery query) {
        List<MasterHeaderColumn> columns = new ArrayList<>();
        columns.add(new MasterHeaderColumn("項目", TextResource.localize("KDW006_88"), ColumnTextAlign.LEFT, "", true)); //B10_1
        columns.add(new MasterHeaderColumn("columnB", "", ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn("名称", TextResource.localize("KDW006_90"), ColumnTextAlign.LEFT, "", true)); //B10_2
        return columns;
    }

    @Override
    public List<MasterData> getMasterDatas(MasterListExportQuery query) {
    	String companyId = AppContexts.user().companyId();
    	
    	String use = TextResource.localize("KDW006_304");
    	String notUse = TextResource.localize("KDW006_303");
    	String canImplement = TextResource.localize("KDW006_299");
    	String cannotImplement = TextResource.localize("KDW006_300");
    	String maru = "〇";
    	String dash = "-";
    	
    	Optional<DaiPerformanceFun> daiPerformanceFunOpt = operationExcelRepo.getDaiPerformanceFunById(companyId);
        Optional<MonPerformanceFun> monPerformanceFunOpt = operationExcelRepo.getMonPerformanceFunById(companyId);
        Optional<IdentityProcess> identityProcessOpt = operationExcelRepo.getIdentityProcessById(companyId);
        Optional<ApprovalProcess> approvalProcessOpt = operationExcelRepo.getApprovalProcessById(companyId);
        Optional<RestrictConfirmEmployment> restrictConfirmEmploymentOpt = operationExcelRepo.getRestrictConfirmEmploymentByCompanyId(companyId);
        List<ApplicationCallExport> listApplicationCallExport = operationExcelRepo.findByCom(companyId);
        
        List<MasterData> datas = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();
        putDataEmptySetOperationRestriction(data);
        
        //daily
        data.put("項目", TextResource.localize("KDW006_35")); //B12_1
        
        //daily disp36Atr
        data.put("columnB", TextResource.localize("KDW006_96")); //B12_2
        if (daiPerformanceFunOpt.isPresent()) {
        	if (daiPerformanceFunOpt.get().getDisp36Atr() == 1) { //B11_1
        		data.put("名称", use);
        	} else {
        		data.put("名称", notUse);
        	}
        }
        datas.add(alignMasterDataSheetRestriction(data));
    	putDataEmptySetOperationRestriction(data);
    	
    	//daily flexDispAtr
        data.put("columnB", TextResource.localize("KDW006_97")); //B12_3
        if (daiPerformanceFunOpt.isPresent()) {
        	if (daiPerformanceFunOpt.get().getFlexDispAtr() == 1) { //B11_2
        		data.put("名称", use);
        	} else {
        		data.put("名称", notUse);
        	}
        }
        datas.add(alignMasterDataSheetRestriction(data));
    	putDataEmptySetOperationRestriction(data);
    	
    	//daily checkErrRefDisp
        data.put("columnB", TextResource.localize("KDW006_84")); //B12_4
        if (daiPerformanceFunOpt.isPresent()) {
        	if (daiPerformanceFunOpt.get().getCheckErrRefDisp() == 1) { //B11_3
        		data.put("名称", use);
        	} else {
        		data.put("名称", notUse);
        	}
        }
        datas.add(alignMasterDataSheetRestriction(data));
    	putDataEmptySetOperationRestriction(data);
    	
    	//daily appTypes
        data.put("columnB", TextResource.localize("KDW006_221")); //B12_5
        String appTypes = "";
        if (!CollectionUtil.isEmpty(listApplicationCallExport)) {
            List<String> listAppType = listApplicationCallExport.stream()
                    .map(applicationCall -> applicationCall.getAppType().nameId)
                    .collect(Collectors.toList());
            appTypes = String.join(",", listAppType);
        }
        data.put("名称", appTypes); //B11_4
        datas.add(alignMasterDataSheetRestriction(data));
    	putDataEmptySetOperationRestriction(data);

    	//daily comment
        data.put("columnB", TextResource.localize("KDW006_211")); //B12_6
        if (daiPerformanceFunOpt.isPresent()) {
        	data.put("名称", daiPerformanceFunOpt.get().getComment().v()); //B11_5
        }
        datas.add(alignMasterDataSheetRestriction(data));
    	putDataEmptySetOperationRestriction(data);
        
    	//monthly
        data.put("項目", TextResource.localize("KDW006_60")); //B12_7
        
        //monthly dailySelfChkDispAtr
        data.put("columnB", TextResource.localize("KDW006_223")); //B12_8
        if (monPerformanceFunOpt.isPresent()) {
        	if (monPerformanceFunOpt.get().getDailySelfChkDispAtr() == 1) { //B11_6
        		data.put("名称", use);
        	} else {
        		data.put("名称", notUse);
        	}
        }
        datas.add(alignMasterDataSheetRestriction(data));
    	putDataEmptySetOperationRestriction(data);
    	
    	//monthly comment
        data.put("columnB", TextResource.localize("KDW006_211")); //B12_9
        if (monPerformanceFunOpt.isPresent()) {
        	data.put("名称", monPerformanceFunOpt.get().getComment().v()); //B11_7
        }
        datas.add(alignMasterDataSheetRestriction(data));
    	putDataEmptySetOperationRestriction(data);
        
    	//restriction
        data.put("項目", TextResource.localize("KDW006_288")); //B12_10
        
        //restriction useDailySelfCk
        data.put("columnB", TextResource.localize("KDW006_294")); //B12_11
        if (identityProcessOpt.isPresent()) {
        	if (identityProcessOpt.get().getUseDailySelfCk() == 1) { //B11_8
        		data.put("名称", maru);
        	} else {
        		data.put("名称", dash);
        	}
        }
        datas.add(alignMasterDataSheetRestriction(data));
    	putDataEmptySetOperationRestriction(data);
    	
    	//restriction useDailyBossChk
        data.put("columnB", TextResource.localize("KDW006_295")); //B12_12
        if (approvalProcessOpt.isPresent()) {
        	if (approvalProcessOpt.get().getUseDailyBossChk() == 1) { //B11_9
        		data.put("名称", maru);
        	} else {
        		data.put("名称", dash);
        	}
        }
        datas.add(alignMasterDataSheetRestriction(data));
    	putDataEmptySetOperationRestriction(data);
    	
    	//restriction useMonthSelfCK
        data.put("columnB", TextResource.localize("KDW006_296")); //B12_13
        if (identityProcessOpt.isPresent()) {
        	if (identityProcessOpt.get().getUseMonthSelfCK() == 1) { //B11_10
        		data.put("名称", maru);
        	} else {
        		data.put("名称", dash);
        	}
        }
        datas.add(alignMasterDataSheetRestriction(data));
    	putDataEmptySetOperationRestriction(data);
    	
    	//restriction useMonthBossChk
        data.put("columnB", TextResource.localize("KDW006_297")); //B12_14
        if (approvalProcessOpt.isPresent()) {
        	if (approvalProcessOpt.get().getUseMonthBossChk() == 1) { //B11_11
        		data.put("名称", maru);
        	} else {
        		data.put("名称", dash);
        	}
        }
        datas.add(alignMasterDataSheetRestriction(data));
    	putDataEmptySetOperationRestriction(data);
    	
    	//restriction yourselfConfirmError
        data.put("columnB", TextResource.localize("KDW006_298")); //B12_15
        if (identityProcessOpt.isPresent()) {
        	if (identityProcessOpt.get().getYourselfConfirmError().value == YourselfConfirmError.CAN_CHECK_WHEN_ERROR.value) { //B11_12
        		data.put("名称", canImplement);
        	} else {
        		data.put("名称", cannotImplement);
        	}
        }
        datas.add(alignMasterDataSheetRestriction(data));
    	putDataEmptySetOperationRestriction(data);
    	
    	//restriction supervisorConfirmError
        data.put("columnB", TextResource.localize("KDW006_301")); //B12_16
        if (approvalProcessOpt.isPresent()) {
        	if (approvalProcessOpt.get().getSupervisorConfirmError().value == YourselfConfirmError.CAN_CHECK_WHEN_ERROR.value) { //B11_13
        		data.put("名称", canImplement);
        	} else {
        		data.put("名称", cannotImplement);
        	}
        }
        datas.add(alignMasterDataSheetRestriction(data));
    	putDataEmptySetOperationRestriction(data);
    	
    	//restriction confirmEmployment
        data.put("columnB", TextResource.localize("KDW006_302")); //B12_17
        if (restrictConfirmEmploymentOpt.isPresent()) {
        	if (restrictConfirmEmploymentOpt.get().isConfirmEmployment()) { //B11_14
        		data.put("名称", use);
        	} else {
        		data.put("名称", notUse);
        	}
        }
        datas.add(alignMasterDataSheetRestriction(data));
    	putDataEmptySetOperationRestriction(data);
        
        return datas;
    }

    private MasterData alignMasterDataSheetRestriction(Map<String, Object> data) {
        MasterData masterData = new MasterData(data, null, "");
        masterData.cellAt("項目").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("columnB").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        masterData.cellAt("名称").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
        return masterData;
    }

    private void putDataEmptySetOperationRestriction(Map<String, Object> data) {
        data.put("項目", "");
        data.put("columnB", "");
        data.put("名称", "");
    }


    /**
     * Sheet 2
     * @param header 
     */
    public List<MasterHeaderColumn> getHeaderColumnsSheetRole(MasterListExportQuery query, List<String> header) {
        List<MasterHeaderColumn> columns = new ArrayList<>();
        header.stream().forEach(x->{
        	columns.add(new MasterHeaderColumn(x, x, ColumnTextAlign.LEFT, "", true));
    	});
        return columns;
    }
    
    public List<MasterData> getMasterDatasSheetRoled(MasterListExportQuery query, List<String> header) {
    	String companyId = AppContexts.user().companyId();
        List<MasterData> datas = new ArrayList<>();
        List<RoleExport> listRoleExport = operationExcelRepo.findRole(companyId);
        if(CollectionUtil.isEmpty(listRoleExport)){
        	return null;
        }
        listRoleExport = listRoleExport.stream().sorted(
				Comparator.comparing(RoleExport::getCodeRole, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
        Map<String,Object> data = new HashMap<>();
        if(listRoleExport.size()==1){
            RoleExport roleExport = listRoleExport.get(0);
            data = new HashMap<>();
            if(roleExport.getAvailability()==1){
                data.put(roleExport.getDescription(),"○");
            }
            data.put("コード", roleExport.getCodeRole()==null?"":roleExport.getCodeRole());
            data.put("名称", roleExport.getNameRole()==null?TextResource.localize("KDW006_226"):roleExport.getNameRole());
            datas.add(alignMasterDataSheetRole(data,header));
            putDataEmptyRole(data, header);
        }
        boolean checkAvailable = false;
        for (int i = 0 ; i < listRoleExport.size(); i ++) {
            RoleExport roleExport = listRoleExport.get(i);
            if(i>0 &&!roleExport.getRoleId().equals(listRoleExport.get(i-1).getRoleId())){
            	datas.add(alignMasterDataSheetRole(data,header));
            	checkAvailable = false;
            }
            if(checkAvailable == false){
            	data = new HashMap<>();
            	putDataEmptyRole(data, header);
            	data.put("コード", roleExport.getCodeRole()==null?"":roleExport.getCodeRole());
                data.put("名称", roleExport.getNameRole()==null?TextResource.localize("KDW006_226"):roleExport.getNameRole());
                 checkAvailable=true;
            }
            if(roleExport.getAvailability()==1){
                data.put(roleExport.getDescription(),"○");
            }
            if(i==(listRoleExport.size()-1)){
            	datas.add(alignMasterDataSheetRole(data,header));
            	checkAvailable = false;
            }
        }
        return datas;
    }

    private void putDataEmptyRole(Map<String, Object> data, List<String> header) {
        for(int i = 0 ; i <header.size();i++ ){
        	if(i<2){
        		data.put(header.get(i), "");
        	}else {
        		data.put(header.get(i), "-");
			}
        }
    }
    private MasterData alignMasterDataSheetRole(Map<String, Object> data, List<String> header) {
        MasterData masterData = new MasterData(data, null, "");
        for (String string : header) {
        	masterData.cellAt(string).setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
		}
        return masterData;
    }
}
