package nts.uk.file.at.app.export.holidaysettingexport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.app.find.holidaysetting.configuration.PublicHolidayManagementUsageUnitFindDto;
import nts.uk.ctx.at.shared.app.find.holidaysetting.configuration.PublicHolidayManagementUsageUnitFinder;
import nts.uk.ctx.at.shared.app.find.holidaysetting.configuration.PublicHolidaySettingDto;
import nts.uk.ctx.at.shared.app.find.holidaysetting.configuration.PublicHolidaySettingFinder;
import nts.uk.ctx.at.shared.app.find.holidaysetting.employee.EmployeeMonthDaySettingFinder;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.Year;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.company.CompanyMonthDaySetting;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.company.CompanyMonthDaySettingRepository;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.DayOfWeek;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidayCarryOverDeadline;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidayPeriod;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.EmployeeMonthDaySetting;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.EmployeeMonthDaySettingRepository;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employment.EmploymentMonthDaySetting;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employment.EmploymentMonthDaySettingRepository;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.workplace.WorkplaceMonthDaySetting;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.workplace.WorkplaceMonthDaySettingRepository;
import nts.uk.ctx.bs.employee.app.find.employment.dto.EmploymentDto;
import nts.uk.ctx.bs.employee.app.find.workplace.config.dto.WkpConfigInfoFindObject;
import nts.uk.ctx.bs.employee.app.find.workplace.config.dto.WorkplaceHierarchyDto;
import nts.uk.ctx.bs.employee.app.find.workplace.config.info.WorkplaceConfigInfoFinder;
import nts.uk.ctx.bs.employee.dom.employment.Employment;
import nts.uk.ctx.bs.employee.dom.employment.EmploymentRepository;
import nts.uk.file.at.app.export.vacation.set.holiday.HolidayRepository;
import nts.uk.query.app.employee.RegulationInfoEmpQueryDto;
import nts.uk.query.app.employee.RegulationInfoEmployeeDto;
import nts.uk.query.app.employee.RegulationInfoEmployeeFinder;
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

@Stateless
@DomainID(value = "HolidaySetting")
public class HolidaySettingExportImpl implements MasterListData{
	
	@Inject
	private PublicHolidaySettingFinder pubHdSettingFinder;	
	
	@Inject
	private CompanyMonthDaySettingRepository companyMonthDaySettingFinder;
	
	@Inject
	private CompanyStartMonthAdapter companyStartMonthAdapter;
	
	@Inject
	private WorkplaceConfigInfoFinder workplaceConfigInfoFinder;
	
	@Inject
	private WorkplaceMonthDaySettingRepository workplaceMonthDaySettingRepository;
	
	@Inject
	private EmploymentRepository employmentRepository;
	
	@Inject
	private EmploymentMonthDaySettingRepository employmentMonthDaySettingRepository;
	
	@Inject
	private RegulationInfoEmployeeFinder regulationInfoEmployeeFinder;
	
	@Inject
	private PublicHolidayManagementUsageUnitFinder finderPublicHolidayManagement;
	
	@Inject
	private EmployeeMonthDaySettingFinder employeeMonthDaySettingFinder;
	
	@Inject
	private EmployeeMonthDaySettingRepository employeeMonthDaySettingRepository;
	
	@Inject
    private HolidayRepository holidayRepository;
	
	//設定 
	public static String value1= "項目";
	public static String value2= "column2";
	public static String value3= "column3";
	public static String value4= "column4";
	public static String value5= "値";
	//会社
	public static String valueTwo1= "年度";
	public static String valueTwo2= "月度";
	public static String valueTwo3= "公休日数";
	//職場
	public static String valueThree1= "年度";
	public static String valueThree2= "コード";
	public static String valueThree3= "名称";
	public static String valueThree4= "月度";
	public static String valueThree5= "公休日数";
	//雇用
	public static String valueFour1= "年度";
	public static String valueFour2= "コード";
	public static String valueFour3= "名称";
	public static String valueFour4= "月度";
	public static String valueFour5= "公休日数";
	//社員
	public static String valueFive1= "年度";
	public static String valueFive2= "コード";
	public static String valueFive3= "名称";
	public static String valueFive4= "月度";
	public static String valueFive5= "公休日数";
	
	public static final String KMF001_166 = "項目";
    public static final String KMF001_B01 = "Level_1";
    public static final String KMF001_B02 = "Level_2";
    public static final String KMF001_167 = "値";


	/** The Constant TIME_DAY_START. */
	public static final String TIME_DAY_START = " 00:00:00";

	/** The Constant DATE_TIME_FORMAT. */
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	
	
	@Override
	public List<MasterData> getMasterDatas(MasterListExportQuery query) {
		//休日
		String companyId = AppContexts.user().companyId();
		return holidayRepository.getAllHoliday(companyId);
	}
	@Override
	public List<MasterHeaderColumn> getHeaderColumns(MasterListExportQuery query) {
		return this.getHeaderColumnsHoliday();
	}
	
	private void putEmptyDataOne (Map<String, Object> data){
		data.put(value1, "");
		data.put(value2, "");
		data.put(value3, "");
		data.put(value4, "");
		data.put(value5, "");
	}
	
	@Override
	public String mainSheetName() {
		return TextResource.localize("KMF001_335");
	}

	@Override
	public MasterListMode mainSheetMode() {
		return MasterListMode.NONE;
	}
	private void putDataCustom(List<MasterData> datas,String column1, String column2, String column5, int possition) {
		Map<String, Object> data = new HashMap<>();
		putEmptyDataOne(data);
		data.put(value1,column1);
		data.put(value2,column2);
		data.put(value5,column5);
		datas.add(alignMasterDataSheetRole(data,possition));
	}
	private MasterData alignMasterDataSheetRole(Map<String, Object> data,int possiton) {
		MasterData masterData = new MasterData(data, null, "");
		masterData.cellAt(value1).setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
		masterData.cellAt(value2).setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
		/** 0 is align left*/
		if(possiton==0){
			masterData.cellAt(value5).setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
		/** 1 is align right*/	
		}else {
			masterData.cellAt(value5).setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT));
		}
		return masterData;
	}

	//会社
	private List<MasterData> getMasterDataTwo(MasterListExportQuery query) {
		int startDate = query.getStartDate().year();
		int endDate = query.getEndDate().year();
		List<MasterData> datas = new ArrayList<>();
		String companyId = AppContexts.user().companyId();
		int year = endDate-startDate;
		List<Integer> listYear = new ArrayList<>();

		for(int i=0;i<year+1;i++){
			listYear.add(startDate+i);
		}

		Optional<CompanyStartMonthData> companyStartMonth=companyStartMonthAdapter.getComanyInfoByCid(companyId);
		
//		HolidaySettingConfigDto optPubHDSet = finder.findHolidaySettingConfigData();
		PublicHolidaySettingDto optPubHdSetting = pubHdSettingFinder.findPubHdSetting();
		
		if(optPubHdSetting != null && optPubHdSetting.getManagePublicHoliday() == 1){// 1
			if(!CollectionUtil.isEmpty(listYear)){
				for(int y=0;y<listYear.size();y++){
					Optional<CompanyMonthDaySetting> optional = companyMonthDaySettingFinder.findByYear(new CompanyId(companyId), new Year(listYear.get(y)));
					if(optional.isPresent() && companyStartMonth.isPresent()){
						int startMonth = companyStartMonth.get().getStartMonth();
						for(int i=0; i<12;i++){
							Map<String, Object> data = new HashMap<>();
							putEmptyDataTwo(data);
							if(i==0){
								data.put(valueTwo1, listYear.get(y));
							}
							data.put(valueTwo2, startMonth+TextResource.localize("KMF002_12"));
							data.put(valueTwo3, optional.get().getPublicHolidayMonthSettings().get(startMonth-1).getInLegalHoliday()+TextResource.localize("KMF002_8"));
							MasterData masterData = new MasterData(data, null, "");	
							masterData.cellAt(valueTwo1).setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT));
							masterData.cellAt(valueTwo2).setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT));
							masterData.cellAt(valueTwo3).setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT));
							datas.add(masterData);
							
							if(startMonth<12){
								startMonth++;
							}else if(startMonth ==12){
								startMonth = 1;
							}
						}
					}
				}
			}
		}

		return datas;
	}
	
	public List<MasterHeaderColumn> getHeaderColumnTwos(MasterListExportQuery query) {
		List<MasterHeaderColumn> columns = new ArrayList<>();
		columns.add(new MasterHeaderColumn(valueTwo1, TextResource.localize("KMF002_20"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(valueTwo2, TextResource.localize("KMF002_12"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(valueTwo3, TextResource.localize("KMF002_13"),
				ColumnTextAlign.LEFT, "", true));
		return columns;
	}
	//雇用
	@SuppressWarnings("unchecked")
	private List<MasterData> getMasterDataThree(MasterListExportQuery query) {
		String companyId = AppContexts.user().companyId();
		
		List<MasterData> datas = new ArrayList<>();
		int endDate = query.getEndDate().year();
		int startDate = query.getStartDate().year();
		
		WkpConfigInfoFindObject wkpConfigInfoFindObject = new WkpConfigInfoFindObject();
		wkpConfigInfoFindObject.setSystemType(2);
		wkpConfigInfoFindObject.setBaseDate(GeneralDate.ymd(9999, 12, 31));
		wkpConfigInfoFindObject.setRestrictionOfReferenceRange(true);
		int year = endDate-startDate;
		List<Integer> listYear = new ArrayList<>();
		
		PublicHolidaySettingDto optPubHdSetting = pubHdSettingFinder.findPubHdSetting();
//		HolidaySettingConfigDto optPubHDSet = finder.findHolidaySettingConfigData();
		
		for(int i=0;i<year+1;i++){
			listYear.add(startDate+i);
		}

		List<WorkplaceHierarchyDto> workplaceHierarchyDtos = spreadOutWorkplaceInfos(workplaceConfigInfoFinder.findAllByBaseDate(wkpConfigInfoFindObject));
		
		if (optPubHdSetting.getManagePublicHoliday() == 1){// 1
			List<String> listAllWorkPlaceIds = new ArrayList<>();
			Map<String, Object> mapWorkPlaceByYears = new HashMap<>();
			for(int y=0; y<listYear.size(); y++){
				List<String> lstWpk = workplaceMonthDaySettingRepository.findWkpRegisterByYear(new CompanyId(companyId), new Year(listYear.get(y)));
				if (!CollectionUtil.isEmpty(lstWpk)) {
					for (String _id : lstWpk) {
						if (!listAllWorkPlaceIds.contains(_id)) listAllWorkPlaceIds.add(_id);
					}
				}
				mapWorkPlaceByYears.put(listYear.get(y).toString(), lstWpk);
			}
			
			List<WorkplaceHierarchyDto> listAllWorkPlaces = getListWorkplaceHierarchyDto(workplaceHierarchyDtos, listAllWorkPlaceIds);
			listAllWorkPlaces = listAllWorkPlaces.stream().sorted(
					Comparator.comparing(WorkplaceHierarchyDto::getHierarchyCode, Comparator.nullsLast(String::compareTo)))
					.collect(Collectors.toList());
			
			if (!CollectionUtil.isEmpty(listAllWorkPlaces)){  
				for(int i=0; i<listAllWorkPlaces.size(); i++){
					WorkplaceHierarchyDto workplace = listAllWorkPlaces.get(i);
					String workPlaceId = workplace.getWorkplaceId();
					int count = 0;
					for(int y=0; y<listYear.size(); y++){
						List<String> lstWpk = new ArrayList<>();
						if (mapWorkPlaceByYears.containsKey(listYear.get(y).toString())){
							lstWpk = (List<String>) mapWorkPlaceByYears.get(listYear.get(y).toString());
						}
						if (!CollectionUtil.isEmpty(lstWpk)){
							if (lstWpk.contains(workPlaceId)){
								count = count + 1;
								Map<String, Object> data = new HashMap<>();
								Optional<CompanyStartMonthData> companyStartMonth=companyStartMonthAdapter.getComanyInfoByCid(companyId);
								int startMonthThree = companyStartMonth.get().getStartMonth();
								for(int j=0; j<12 ; j++){
									putEmptyDataThree(data);
									if(count == 1 && j == 0) {
										data.put(valueThree2, workplace.getCode());
						                data.put(valueThree3, workplace.getName());
									}
									if(j == 0){
										data.put(valueThree1, listYear.get(y));
									}
									
									data.put(valueThree4, startMonthThree +TextResource.localize("KMF002_12"));
									Optional<WorkplaceMonthDaySetting> optionalThree = workplaceMonthDaySettingRepository
											.findByYear(new CompanyId(companyId), workPlaceId ,new Year(listYear.get(y)));

									data.put(valueThree5, optionalThree.get().getPublicHolidayMonthSettings().get(startMonthThree-1)
											.getInLegalHoliday()+TextResource.localize("KMF002_8"));
									
									if (startMonthThree < 12) {
										startMonthThree++;
									} else if (startMonthThree == 12) {
										startMonthThree = 1;
									}
									
									MasterData masterData = new MasterData(data, null, "");	
									masterData.cellAt(valueThree1).setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT));
									masterData.cellAt(valueThree2).setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
									masterData.cellAt(valueThree3).setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
									masterData.cellAt(valueThree4).setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT));
									masterData.cellAt(valueThree5).setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT));
									datas.add(masterData);
								}
							}
						}
					}
				}
			}
			
		}
		return datas;
	}
	
	public List<MasterHeaderColumn> getHeaderColumnThree(MasterListExportQuery query) {
		List<MasterHeaderColumn> columns = new ArrayList<>();
		columns.add(new MasterHeaderColumn(valueThree2, TextResource.localize("KMF002_24"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(valueThree3, TextResource.localize("KMF002_57"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(valueThree1, TextResource.localize("KMF002_20"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(valueThree4, TextResource.localize("KMF002_12"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(valueThree5, TextResource.localize("KMF002_13"),
				ColumnTextAlign.LEFT, "", true));
		
		return columns;
	}

	
	@SuppressWarnings("unchecked")
	private List<MasterData> getMasterDataFour(MasterListExportQuery query) {
		int endDate = query.getEndDate().year();
		int startDate = query.getStartDate().year();
		
		String companyId = AppContexts.user().companyId();
		List<MasterData> datas = new ArrayList<>();
		List<Employment> empList = employmentRepository.findAll(companyId);

		List<EmploymentDto> listEmployee = empList.stream().map(employment -> {
			EmploymentDto dto = new EmploymentDto();
			dto.setCode(employment.getEmploymentCode().v());
			dto.setName(employment.getEmploymentName().v());
			return dto;
		}).collect(Collectors.toList());
		
		int year = endDate-startDate;
		List<Integer> listYear = new ArrayList<>();
//		HolidaySettingConfigDto optPubHDSet = finder.findHolidaySettingConfigData();
		PublicHolidaySettingDto optPubHdSetting = pubHdSettingFinder.findPubHdSetting();
		for(int i=0;i<year+1;i++){
			listYear.add(startDate+i);
		}
		Optional<CompanyStartMonthData> companyStartMonth=companyStartMonthAdapter.getComanyInfoByCid(companyId);
		
		List<String> listAllEmplIds = new ArrayList<>();
		Map<String, Object> mapEmployeeByYears = new HashMap<>();
		for(int y=0; y<listYear.size(); y++){
			List<String> lstEmp = employmentMonthDaySettingRepository.findAllEmpRegister(new CompanyId(companyId), new Year(listYear.get(y)));
			if (!CollectionUtil.isEmpty(lstEmp)) {
				for (String _id : lstEmp) {
					if (!listAllEmplIds.contains(_id)) listAllEmplIds.add(_id);
				}
			}
			mapEmployeeByYears.put(listYear.get(y).toString(), lstEmp);
		}
		
		List<EmploymentDto> listEmployeeExport = getListEmploymentDto(listEmployee, listAllEmplIds);
		
		listEmployeeExport = listEmployeeExport.stream().sorted(
				Comparator.comparing(EmploymentDto::getCode, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
			
		if(optPubHdSetting.getManagePublicHoliday() == 1){// 1
			if (!CollectionUtil.isEmpty(listEmployeeExport)) {
				Map<String, Object> data = new HashMap<>();
				int startMonthFour = companyStartMonth.get().getStartMonth();
				for (int i = 0; i < listEmployeeExport.size(); i++) {
					EmploymentDto employee = listEmployeeExport.get(i);
					String wordCode = employee.getCode();
					int count = 0;
					for(int y=0;y<listYear.size();y++){
						List<String> lstEmpl = new ArrayList<>();
						if (mapEmployeeByYears.containsKey(listYear.get(y).toString())){
							lstEmpl = (List<String>) mapEmployeeByYears.get(listYear.get(y).toString());
						}
						if (!CollectionUtil.isEmpty(lstEmpl)){
							if (lstEmpl.contains(wordCode)){
								count = count + 1;
								for (int j = 0; j < 12; j++) {
									putEmptyDataFour(data);
									
									if(count == 1 && j == 0) {
										data.put(valueFour2, employee.getCode());
										data.put(valueFour3, employee.getName());
									}
									if(j == 0){
										data.put(valueFour1, listYear.get(y));
									}
									
									data.put(valueFour4, startMonthFour + TextResource.localize("KMF002_12"));

									Optional<EmploymentMonthDaySetting> optionalFour = employmentMonthDaySettingRepository
											.findByYear(new CompanyId(companyId), wordCode, new Year(listYear.get(y)));

									data.put(valueFour5, optionalFour.get().getPublicHolidayMonthSettings().get(startMonthFour - 1)
											.getInLegalHoliday() + TextResource.localize("KMF002_8"));

									if (startMonthFour < 12) {
										startMonthFour++;
									} else if (startMonthFour == 12) {
										startMonthFour = 1;
									}

									MasterData masterData = new MasterData(data, null, "");
									masterData.cellAt(valueFour1)
											.setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT));
									masterData.cellAt(valueFour2)
											.setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
									masterData.cellAt(valueFour3)
											.setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
									masterData.cellAt(valueFour4)
											.setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT));
									masterData.cellAt(valueFour5)
											.setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT));
									datas.add(masterData);
								}
							}
						}
					}

				}
			}
		}
		return datas;
	}

	public List<MasterHeaderColumn> getHeaderColumnFour(MasterListExportQuery query) {
		List<MasterHeaderColumn> columns = new ArrayList<>();
		columns.add(
				new MasterHeaderColumn(valueFour2, TextResource.localize("KMF002_24"), ColumnTextAlign.LEFT, "", true));
		columns.add(
				new MasterHeaderColumn(valueFour3, TextResource.localize("KMF002_57"), ColumnTextAlign.LEFT, "", true));
		columns.add(
				new MasterHeaderColumn(valueFour1, TextResource.localize("KMF002_20"), ColumnTextAlign.LEFT, "", true));
		columns.add(
				new MasterHeaderColumn(valueFour4, TextResource.localize("KMF002_12"), ColumnTextAlign.LEFT, "", true));
		columns.add(
				new MasterHeaderColumn(valueFour5, TextResource.localize("KMF002_13"), ColumnTextAlign.LEFT, "", true));

		return columns;
	}
	
	@SuppressWarnings("unchecked")
	private List<MasterData> getMasterDataFive(MasterListExportQuery query) {
		List<MasterData> datas = new ArrayList<>();
		String companyId = AppContexts.user().companyId();		
		RegulationInfoEmpQueryDto ccg = new RegulationInfoEmpQueryDto();
		ccg.setBaseDate("9999-12-31");
		ccg.setClassificationCodes(null);
		ccg.setClosureIds(null);
		ccg.setDepartmentCodes(null);
		ccg.setEmploymentCodes(null);
		ccg.setFilterByClassification(false);
		ccg.setFilterByClosure(false);
		ccg.setFilterByDepartment(false);
		ccg.setFilterByEmployment(false);
		ccg.setFilterByJobTitle(false);
		ccg.setFilterByWorkplace(false);
		ccg.setFilterByWorktype(false);
		ccg.setIncludeIncumbents(true);
		ccg.setIncludeOccupancy(true);
		ccg.setIncludeRetirees(false);
		ccg.setIncludeWorkersOnLeave(true);
		ccg.setNameType("1");
		ccg.setPeriodEnd("9999-12-31");
		ccg.setPeriodStart("1900-01-01");
		ccg.setReferenceRange(0);
		ccg.setSortOrderNo(1);
		ccg.setSystemType(2);

		List<RegulationInfoEmployeeDto> listFind = regulationInfoEmployeeFinder.find(ccg);
		int endDate = query.getEndDate().year();
		int startDate = query.getStartDate().year();
		
		Optional<CompanyStartMonthData> companyStartMonth=companyStartMonthAdapter.getComanyInfoByCid(companyId);
		
		int year = endDate-startDate;
		List<Integer> listYear = new ArrayList<>();
//		HolidaySettingConfigDto optPubHDSet = finder.findHolidaySettingConfigData();
		PublicHolidaySettingDto optPubHdSetting = pubHdSettingFinder.findPubHdSetting();
		
		for(int i=0;i<year+1;i++){
			listYear.add(startDate+i);
		}
		
		List<String> listAllIds = new ArrayList<>();
		Map<String, Object> mapByYears = new HashMap<>();
		for(int y=0; y<listYear.size(); y++){
			List<String> lstTemp = employeeMonthDaySettingFinder.findAllEmployeeRegister(listYear.get(y));
			if (!CollectionUtil.isEmpty(lstTemp)) {
				for (String _id : lstTemp) {
					if (!listAllIds.contains(_id)) listAllIds.add(_id);
				}
			}
			mapByYears.put(listYear.get(y).toString(), lstTemp);
		}
		
		List<RegulationInfoEmployeeDto> listmployee = getListRegulationInfoEmployeeDto(listFind, listAllIds);
		
		
		if(optPubHdSetting.getManagePublicHoliday() == 1){// 1
			if(!CollectionUtil.isEmpty(listmployee)){
				int startMonthFour = companyStartMonth.get().getStartMonth();
				Map<String, Object> data = new HashMap<>();
				for (int i = 0; i < listmployee.size(); i++) {
						
					RegulationInfoEmployeeDto employee = listmployee.get(i);
					String employeeId = employee.getEmployeeId();
					int count = 0;
					for(int y=0; y<listYear.size(); y++){
						List<String> lstTemp = new ArrayList<>();
						if (mapByYears.containsKey(listYear.get(y).toString())){
							lstTemp = (List<String>) mapByYears.get(listYear.get(y).toString());
						}
						if (!CollectionUtil.isEmpty(lstTemp)){
							if (lstTemp.contains(employeeId)){
								count = count + 1;
							
								for (int j = 0; j < 12; j++) {
									putEmptyDataFive(data);
									
									if (count == 1 && j == 0) {
										data.put(valueFive2, employee.getEmployeeCode());
										data.put(valueFive3, employee.getEmployeeName());
									}
									if (j == 0){
										data.put(valueFive1, listYear.get(y));
									}
									
									data.put(valueFive4, startMonthFour + TextResource.localize("KMF002_12"));
									
									Optional<EmployeeMonthDaySetting> optional = employeeMonthDaySettingRepository
											.findByYear(new CompanyId(companyId), employeeId ,new Year(listYear.get(y)));
									
									data.put(valueFive5, optional.get().getPublicHolidayMonthSettings().get(startMonthFour - 1)
											.getInLegalHoliday() + TextResource.localize("KMF002_8"));
														
									MasterData masterData = new MasterData(data, null, "");
									masterData.cellAt(valueFive1)
											.setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT));
									masterData.cellAt(valueFive2)
											.setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
									masterData.cellAt(valueFive3)
											.setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
									masterData.cellAt(valueFive4)
											.setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT));
									masterData.cellAt(valueFive5)
											.setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT));
									datas.add(masterData);
									
									if (startMonthFour < 12) {
										startMonthFour++;
									} else if (startMonthFour == 12) {
										startMonthFour = 1;
									}
								}
							}
						}
					}
				}
			}
		}

		return datas;
	}

	public List<MasterHeaderColumn> getHeaderColumnFive(MasterListExportQuery query) {
		List<MasterHeaderColumn> columns = new ArrayList<>();
		columns.add(
				new MasterHeaderColumn(valueFive2, TextResource.localize("KMF002_24"), ColumnTextAlign.LEFT, "", true));
		columns.add(
				new MasterHeaderColumn(valueFive3, TextResource.localize("KMF002_57"), ColumnTextAlign.LEFT, "", true));
		columns.add(
				new MasterHeaderColumn(valueFive1, TextResource.localize("KMF002_20"), ColumnTextAlign.LEFT, "", true));
		columns.add(
				new MasterHeaderColumn(valueFive4, TextResource.localize("KMF002_12"), ColumnTextAlign.LEFT, "", true));
		columns.add(
				new MasterHeaderColumn(valueFive5, TextResource.localize("KMF002_13"), ColumnTextAlign.LEFT, "", true));

		return columns;
	}

	public List<MasterHeaderColumn> getHeaderColumnsHoliday() {
        List<MasterHeaderColumn> columns = new ArrayList<>();
        columns.add(new MasterHeaderColumn(KMF001_166, TextResource.localize("KMF001_166"),
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn(KMF001_B01, "",
                ColumnTextAlign.LEFT, "", true));
        columns.add(new MasterHeaderColumn(KMF001_167, TextResource.localize("KMF001_167"),
                ColumnTextAlign.LEFT, "", true));
        
        return columns;
    }
	
	public List<MasterHeaderColumn> getHeaderManageSetting() {
		List<MasterHeaderColumn> columns = new ArrayList<>();
		columns.add(new MasterHeaderColumn(value1, TextResource.localize("KMF002_59"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(value2, "",
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(value5, TextResource.localize("KMF002_58"),
				ColumnTextAlign.LEFT, "", true));
		return columns;
	}
	
	private void putEmptyDataTwo(Map<String, Object> data){
		data.put(valueTwo1, "");
		data.put(valueTwo2, "");
		data.put(valueTwo3, "");
	}
	
	private void putEmptyDataThree(Map<String, Object> data){
		data.put(valueThree1, "");
		data.put(valueThree2, "");
		data.put(valueThree3, "");
		data.put(valueThree4, "");
		data.put(valueThree5, "");
	}

	private void putEmptyDataFour(Map<String, Object> data) {
		data.put(valueFour1, "");
		data.put(valueFour2, "");
		data.put(valueFour3, "");
		data.put(valueFour4, "");
		data.put(valueFour5, "");
	}
	
	private void putEmptyDataFive(Map<String, Object> data) {
		data.put(valueFive1, "");
		data.put(valueFive2, "");
		data.put(valueFive3, "");
		data.put(valueFive4, "");
		data.put(valueFive5, "");
	}
	
	

	@Override
	public List<SheetData> extraSheets(MasterListExportQuery query){
		List<SheetData> listSheetData = new ArrayList<>();
		
//		HolidaySettingConfigDto optPubHDSet = finder.findHolidaySettingConfigData();	
		PublicHolidaySettingDto optPubHdSetting = pubHdSettingFinder.findPubHdSetting();
		
		
		PublicHolidayManagementUsageUnitFindDto findData = finderPublicHolidayManagement.findData();

		List<MasterData> datas = new ArrayList<>();
		
		// 公休管理設定
		if(optPubHdSetting != null && optPubHdSetting.getManagePublicHoliday() == 1) {
			int carryOverDeadline = optPubHdSetting.getPublicHdCarryOverDeadline();
			int pubHdPeriodSetting = optPubHdSetting.getPublicHolidayPeriod();
			//1
			putDataCustom(datas,TextResource.localize("KMF002_61"),"","管理する",0);
			
			//2
			putDataCustom(datas,TextResource.localize("KMF002_4"),"",getCarryOverDeadline(carryOverDeadline),1);
			//3
			if(optPubHdSetting.getCarryOverNumberOfPublicHdIsNegative() ==1){
				putDataCustom(datas,"",TextResource.localize("KMF002_7"),"○",0);
			}else{
				putDataCustom(datas,"",TextResource.localize("KMF002_7"),"-",0);
			}
			putDataCustom(datas,TextResource.localize("KMF002_72"),"",getPubHdPeriod(pubHdPeriodSetting), 0);
			
		}else if(optPubHdSetting !=null){
			putDataCustom(datas,TextResource.localize("KMF002_61"),"","管理しない",0);
			putDataCustom(datas,TextResource.localize("KMF002_4"),"","",1);
			putDataCustom(datas,"",TextResource.localize("KMF002_7"),"",0);
			putDataCustom(datas,TextResource.localize("KMF002_72"),"","", 0);
		}else{
			putDataCustom(datas,TextResource.localize("KMF002_61"),"","",0);
			putDataCustom(datas,TextResource.localize("KMF002_4"),"","",1);
			putDataCustom(datas,"",TextResource.localize("KMF002_7"),"",0);
			putDataCustom(datas,TextResource.localize("KMF002_72"),"","", 0);
		}
		SheetData sheet = SheetData.builder()
				.mainData(datas)
				.mainDataColumns(this.getHeaderManageSetting())
				.mode(MasterListMode.NONE)
				.sheetName(TextResource.localize("KMF002_1"))
				.build();
		listSheetData.add(sheet);
		
		if(optPubHdSetting !=null){//1ヵ月	
			if(findData.getIsManageEmpPublicHd()==1 && findData.getIsManageEmployeePublicHd()==0 && findData.getIsManageWkpPublicHd()==0){
				SheetData sheetDataTwo = new SheetData(getMasterDataTwo(query), getHeaderColumnTwos(query), null, null, TextResource.localize("KMF002_75"), MasterListMode.YEAR_RANGE);
				SheetData sheetDataFour = new SheetData(getMasterDataFour(query), getHeaderColumnFour(query), null, null, TextResource.localize("KMF002_77"), MasterListMode.YEAR_RANGE);
				listSheetData.add(sheetDataTwo);
				listSheetData.add(sheetDataFour);
				
			}else if(findData.getIsManageEmpPublicHd()==0 && findData.getIsManageEmployeePublicHd()==0 && findData.getIsManageWkpPublicHd()==1){
				SheetData sheetDataTwo = new SheetData(getMasterDataTwo(query), getHeaderColumnTwos(query), null, null, TextResource.localize("KMF002_75"), MasterListMode.YEAR_RANGE);
				SheetData sheetDataThree = new SheetData(getMasterDataThree(query), getHeaderColumnThree(query), null, null, TextResource.localize("KMF002_76"), MasterListMode.YEAR_RANGE);
				listSheetData.add(sheetDataTwo);
				listSheetData.add(sheetDataThree);
			}else if(findData.getIsManageEmpPublicHd()==0 && findData.getIsManageEmployeePublicHd()==1 && findData.getIsManageWkpPublicHd()==0){
				SheetData sheetDataTwo = new SheetData(getMasterDataTwo(query), getHeaderColumnTwos(query), null, null, TextResource.localize("KMF002_75"), MasterListMode.YEAR_RANGE);
				SheetData sheetDataFive = new SheetData(getMasterDataFive(query), getHeaderColumnFive(query), null, null, TextResource.localize("KMF002_78"), MasterListMode.YEAR_RANGE);
				listSheetData.add(sheetDataTwo);
				listSheetData.add(sheetDataFive);
			}else if(findData.getIsManageEmpPublicHd()==1 && findData.getIsManageEmployeePublicHd()==1 && findData.getIsManageWkpPublicHd()==0){
				SheetData sheetDataTwo = new SheetData(getMasterDataTwo(query), getHeaderColumnTwos(query), null, null, TextResource.localize("KMF002_75"), MasterListMode.YEAR_RANGE);
				SheetData sheetDataFour = new SheetData(getMasterDataFour(query), getHeaderColumnFour(query), null, null, TextResource.localize("KMF002_77"), MasterListMode.YEAR_RANGE);
				SheetData sheetDataFive = new SheetData(getMasterDataFive(query), getHeaderColumnFive(query), null, null, TextResource.localize("KMF002_78"), MasterListMode.YEAR_RANGE);
				listSheetData.add(sheetDataTwo);
				listSheetData.add(sheetDataFour);
				listSheetData.add(sheetDataFive);
			}else if(findData.getIsManageEmpPublicHd()==0 && findData.getIsManageEmployeePublicHd()==1 && findData.getIsManageWkpPublicHd()==1){
				SheetData sheetDataTwo = new SheetData(getMasterDataTwo(query), getHeaderColumnTwos(query), null, null, TextResource.localize("KMF002_75"), MasterListMode.YEAR_RANGE);
				SheetData sheetDataThree = new SheetData(getMasterDataThree(query), getHeaderColumnThree(query), null, null, TextResource.localize("KMF002_76"), MasterListMode.YEAR_RANGE);
				SheetData sheetDataFive = new SheetData(getMasterDataFive(query), getHeaderColumnFive(query), null, null, TextResource.localize("KMF002_78"), MasterListMode.YEAR_RANGE);
				listSheetData.add(sheetDataTwo);
				listSheetData.add(sheetDataThree);
				listSheetData.add(sheetDataFive);
			}
			if(findData.getIsManageEmpPublicHd()==0 && findData.getIsManageWkpPublicHd()==0 && findData.getIsManageEmployeePublicHd()==0){
				SheetData sheetDataTwo = new SheetData(getMasterDataTwo(query), getHeaderColumnTwos(query), null, null, TextResource.localize("KMF002_75"), MasterListMode.YEAR_RANGE);
				listSheetData.add(sheetDataTwo);
			}
		} else {
			SheetData sheetDataTwo = new SheetData(getMasterDataTwo(query), getHeaderColumnTwos(query), null, null, TextResource.localize("KMF002_75"), MasterListMode.YEAR_RANGE);
			listSheetData.add(sheetDataTwo);
		}
		
		return listSheetData;
	}
	
	private String getStartDay(int startDay) {
		String dayOfWeek = null;
		switch(startDay){
			case 0:
				dayOfWeek = TextResource.localize(DayOfWeek.MONDAY.nameId);
				break;
			case 1:
				dayOfWeek = TextResource.localize(DayOfWeek.TUESDAY.nameId);
				break;
			case 2:
				dayOfWeek = TextResource.localize(DayOfWeek.WEDNESDAY.nameId);
				break;
			case 3:
				dayOfWeek = TextResource.localize(DayOfWeek.THURSDAY.nameId);
				break;
			case 4:
				dayOfWeek = TextResource.localize(DayOfWeek.FRIDAY.nameId);
				break;
			case 5:
				dayOfWeek = TextResource.localize(DayOfWeek.SATURDAY.nameId);
				break;
			case 6:
				dayOfWeek = TextResource.localize(DayOfWeek.SUNDAY.nameId);
				break;
			default:
				break;
		}
		return dayOfWeek;
	}
	private String getCarryOverDeadline(int carryOverDeadline) {
		String CarryOver = null;
		switch(carryOverDeadline){			
			case 2:
				CarryOver = TextResource.localize(PublicHolidayCarryOverDeadline.YEAR_END.nameId);
				break;
			case 1:
				CarryOver = TextResource.localize(PublicHolidayCarryOverDeadline.INDEFINITE.nameId);
				break;
			case 0:
				CarryOver = TextResource.localize(PublicHolidayCarryOverDeadline.CURRENT_MONTH.nameId);
				break;
			default:
				break;
		}
		return CarryOver;
	}
	
	private String getPubHdPeriod(int pubHdPeriodSetting) {
		String pubHdPeriod = null;
		switch(pubHdPeriodSetting){			
			case 0:
				pubHdPeriod = TextResource.localize(PublicHolidayPeriod.CLOSURE_PERIOD.nameId);
				break;
			case 1:
				pubHdPeriod = TextResource.localize(PublicHolidayPeriod.FIRST_DAY_TO_LAST_DAY.nameId);
				break;		
			default:
				break;
		}
		return pubHdPeriod;
	}
	private List<WorkplaceHierarchyDto> spreadOutWorkplaceInfos(List<WorkplaceHierarchyDto> workplaceHierarchyDtos) {
		List<WorkplaceHierarchyDto> listWorkplaceHierarchyDtos = new ArrayList<>();
		workplaceHierarchyDtos.stream().forEach(x -> {
			listWorkplaceHierarchyDtos.add(x);
			if (!CollectionUtil.isEmpty(x.getChilds())) {
				listWorkplaceHierarchyDtos.addAll(spreadOutWorkplaceInfos(x.getChilds()));
			}
			
		});
		return listWorkplaceHierarchyDtos;
	}
	
	private List<WorkplaceHierarchyDto> getListWorkplaceHierarchyDto(List<WorkplaceHierarchyDto> workplaceHierarchyDtos, List<String> lstWpk){
		List<WorkplaceHierarchyDto> listWorkplace = new ArrayList<>();
		
		if (!CollectionUtil.isEmpty(workplaceHierarchyDtos)){
			for(int i =0;i<workplaceHierarchyDtos.size();i++){
				for(int j =0; j<lstWpk.size();j++){
					if(lstWpk.get(j).equals(workplaceHierarchyDtos.get(i).getWorkplaceId())){
						listWorkplace.add(workplaceHierarchyDtos.get(i));
						lstWpk.remove(j);
					}
				}
			}
		}
		if(!CollectionUtil.isEmpty(lstWpk)){
			for(int i=0;i<lstWpk.size();i++){
				WorkplaceHierarchyDto temp = new WorkplaceHierarchyDto();
				temp.setWorkplaceId(lstWpk.get(i));
				temp.setName("マスタ未登録");
				listWorkplace.add(temp);
			}
		}
		return listWorkplace;
	}
	
	private List<EmploymentDto> getListEmploymentDto(List<EmploymentDto> listEmployee,List<String> lstEmp){
		List<EmploymentDto>  listEmployeeExport =  new ArrayList<>();
		if(listEmployee!=null && listEmployee.size() !=0){
			for(int i=0;i<listEmployee.size();i++){
				if (lstEmp != null && !lstEmp.isEmpty()) {
					for(int j=0;j<lstEmp.size();j++){
						if(lstEmp.get(j).equals(listEmployee.get(i).getCode())){
							listEmployeeExport.add(listEmployee.get(i));
							lstEmp.remove(j);
						}
					}
				}
			}
			if(lstEmp.size()!=0){
				for(int i=0;i<lstEmp.size();i++){
					EmploymentDto temp = new EmploymentDto();
					temp.setCode(lstEmp.get(i));
					temp.setName("マスタ未登録");
					listEmployeeExport.add(temp);
				}
			}
		}
		return listEmployeeExport;
	}
	
	private List<RegulationInfoEmployeeDto> getListRegulationInfoEmployeeDto(List<RegulationInfoEmployeeDto> listFind, List<String> listEmployeeRegister){
		List<RegulationInfoEmployeeDto> lististRegulationInfoEmployee = new ArrayList<>();
		
		if(listFind.size()!=0){
			for(int i =0;i<listFind.size();i++){
				for(int j =0; j<listEmployeeRegister.size();j++){
					if(listEmployeeRegister.get(j).equals(listFind.get(i).getEmployeeId())){
						lististRegulationInfoEmployee.add(listFind.get(i));
						listEmployeeRegister.remove(j);
					}
				}
			}
			if(listEmployeeRegister.size()!=0){
				for(int i=0;i<listEmployeeRegister.size();i++){
					RegulationInfoEmployeeDto temp = RegulationInfoEmployeeDto.builder()
							.employeeId(listEmployeeRegister.get(i)).employeeName("マスタ未登録").build();
					lististRegulationInfoEmployee.add(temp);
				}
			}
		}
		return lististRegulationInfoEmployee;
	}

}
