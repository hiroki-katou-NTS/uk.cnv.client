package nts.uk.ctx.at.shared.app.find.scherec.totaltimes.dto;

import java.util.ArrayList;
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
import nts.uk.ctx.at.shared.app.find.worktype.WorkTypeDto;
import nts.uk.ctx.at.shared.app.find.worktype.WorkTypeSetDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.DailyAttendanceItemNameAdapter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.DailyAttendanceItemNameAdapterDto;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.ConditionThresholdLimit;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.CountAtr;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.SummaryAtr;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.TotalTimes;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.TotalTimesRepository;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.UseAtr;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.language.TotalTimesLang;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.language.TotalTimesLangRepository;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.masterlist.annotation.DomainID;
import nts.uk.shr.infra.file.report.masterlist.data.ColumnTextAlign;
import nts.uk.shr.infra.file.report.masterlist.data.MasterCellStyle;
import nts.uk.shr.infra.file.report.masterlist.data.MasterData;
import nts.uk.shr.infra.file.report.masterlist.data.MasterHeaderColumn;
import nts.uk.shr.infra.file.report.masterlist.data.MasterListData;
import nts.uk.shr.infra.file.report.masterlist.webservice.MasterListExportQuery;
import nts.uk.shr.infra.file.report.masterlist.webservice.MasterListMode;

/**
 * 
 * @author minhvv
 *
 */

@Stateless
@DomainID(value = "TotalTimes")
public class TotalTimesRepositoryImpl implements MasterListData{

	@Inject
	private TotalTimesRepository totalTimesRepository;
	
	@Inject
	private TotalTimesLangRepository langRepository;
	
	@Inject
	private WorkTypeRepository workTypeRepository;
	
	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;
	
	@Inject
	private DailyAttendanceItemNameAdapter dailyAttendanceItemNameAdapter;
	
	
	@Override
	public List<MasterData> getMasterDatas(MasterListExportQuery query) {
		String companyId = AppContexts.user().companyId();
		
		List<MasterData> datas = new ArrayList<>();
		List<TotalTimesLang> totalTimeLang = langRepository.findAll(companyId, query.getLanguageId());
		List<TotalTimes> listTotalTimesItem = totalTimesRepository.getAllTotalTimes(companyId);
		
		if(CollectionUtil.isEmpty(listTotalTimesItem)){
			return null;
		}else{
			listTotalTimesItem.stream().forEach(c->{
				
				List<String> listWorkTypeCodes = new ArrayList<>();
				List<String> listCodes  = new ArrayList<>();
				
				Map<String, Object> data = new HashMap<>();
				
					
				
				Optional<TotalTimes> optTotalTimes = this.totalTimesRepository.getTotalTimesDetail(companyId,
						c.getTotalCountNo());

				if(c.getUseAtr() == UseAtr.NotUse){
					// neu =0 
					// khong in ra
				}else{
					Optional<TotalTimesLang> timeLang = totalTimeLang.stream().filter(x -> x.getTotalCountNo() == c.getTotalCountNo()).findFirst();
					putEmptyData(data);	
					data.put("No", c.getTotalCountNo());
					data.put("????????????", UseAtr.Use.nameId);
					if(timeLang.isPresent()){
						data.put("???????????????", timeLang.get().getTotalTimesNameEng());
					}

					List<String> lista= optTotalTimes.get().getSummaryList().getWorkTypeCodes();
					List<String> listb= optTotalTimes.get().getSummaryList().getWorkTimeCodes();
					
					for(int n= 0;n<lista.size();n++){
						// chua kt de add
						listWorkTypeCodes.add(lista.get(n));
					}
					for(int n= 0;n<listb.size();n++){
						// chua kt de add
						listCodes.add(listb.get(n));
					}
					
					listWorkTypeCodes = listWorkTypeCodes.stream()
							.sorted()
							.collect(Collectors.toList());
					
					listCodes = listCodes.stream()
							.sorted()
							.collect(Collectors.toList());

					
					
					//WorkType
					List<WorkTypeDto> listWorktypeDto = this.workTypeRepository.findByCompanyId(companyId).stream().map(m -> {
						List<WorkTypeSetDto> workTypeSetList = m.getWorkTypeSetList().stream()
								.map(x -> WorkTypeSetDto.fromDomain(x)).collect(Collectors.toList());
						WorkTypeDto workType = WorkTypeDto.fromDomain(m);
						workType.setWorkTypeSets(workTypeSetList);
						return workType;
					}).collect(Collectors.toList());
					
					Map<String ,WorkTypeDto> mapWorktypeDto = listWorktypeDto.stream()
							.collect(Collectors.toMap(WorkTypeDto::getWorkTypeCode, Function.identity()));

					//WorkTime
					List<WorkTimeSetting> listFindByCodes = workTimeSettingRepository.findByCompanyId(companyId)
							.stream().sorted(Comparator
									.comparing(WorkTimeSetting::getWorktimeCode))
									.collect(Collectors.toList());
					
					Map<WorkTimeCode ,WorkTimeSetting> mapWorkTimeDto = listFindByCodes.stream()
							.collect(Collectors.toMap(WorkTimeSetting::getWorktimeCode, Function.identity()));
					
					
					
					List<Integer> listAtdtemId = new ArrayList<>();
					if (c.getTotalCondition().getAtdItemId().isPresent()){
						listAtdtemId.add(c.getTotalCondition().getAtdItemId().get());
					}
					
					List<DailyAttendanceItemNameAdapterDto> dailyAttendanceItemDomainServiceDtos = this.dailyAttendanceItemNameAdapter
							.getDailyAttendanceItemName(listAtdtemId);
					
					if(CollectionUtil.isEmpty(listWorkTypeCodes)){
						data.put("????????????","");
					}else{
						if(c.getSummaryAtr() == SummaryAtr.WORKINGTIME){
							data.put("????????????","");
						}else {
							//????????????
							String typeOfDuty = "";
							for (int n = 0; n < listWorkTypeCodes.size(); n++) {
								String workTypeName = "";
								if(mapWorktypeDto.get(listWorkTypeCodes.get(n)) !=null){
									workTypeName = mapWorktypeDto.get(listWorkTypeCodes.get(n)).getName();
								}else{
									workTypeName = TextResource.localize("KSM006_13");
								}
								
								if (n == 0) {
									typeOfDuty = listWorkTypeCodes.get(n) +""+ workTypeName;
								} else {
									typeOfDuty += ","+listWorkTypeCodes.get(n) + workTypeName;
								}
							}
							data.put("????????????", typeOfDuty);
						}
						
					}
					if(CollectionUtil.isEmpty(listCodes)){
						data.put("???????????????", "");
					}else{
						if(c.getSummaryAtr() == SummaryAtr.DUTYTYPE){
							data.put("???????????????", "");
						}else{
							//???????????????

							String timeOfDuty = "";
							for (int n = 0; n < listCodes.size(); n++) {
								String workTimeName = "";
								WorkTimeCode workTimeCode = new WorkTimeCode(listCodes.get(n));
								
								if(mapWorkTimeDto.get(workTimeCode)!=null){
									workTimeName = mapWorkTimeDto.get(workTimeCode).getWorkTimeDisplayName().getWorkTimeName().v();
								}else{
									workTimeName = TextResource.localize("KSM006_13");
								}
								
								if (n == 0) {
									timeOfDuty = listCodes.get(n) +""+ workTimeName;
								} else {
									timeOfDuty += ","+listCodes.get(n) + workTimeName;
								}
							}
							data.put("???????????????", timeOfDuty);
						}
					}
					
					if(c.getTotalCondition().getLowerLimitSettingAtr() == UseAtr.Use){
						data.put("??????????????????", "???");

						ConditionThresholdLimit cond = new ConditionThresholdLimit(c.getTotalCondition().getThresoldLowerLimit().get().valueAsMinutes());
						String ThresholdLimit = "";
						if(cond.minute()<10){
							ThresholdLimit =  cond.hour() +":0"+ cond.minute();
						}else{
							ThresholdLimit =  cond.hour() +":"+ cond.minute();
						}
						data.put("??????", ThresholdLimit + "??????");
					}else{
						data.put("??????????????????", "-");
						data.put("??????", "");
					}
					
					if(c.getTotalCondition().getUpperLimitSettingAtr() == UseAtr.Use){
						data.put("??????????????????", "???");
						ConditionThresholdLimit cond2 = new ConditionThresholdLimit(c.getTotalCondition().getThresoldUpperLimit().get().valueAsMinutes());
						String thresoldUpperLimit = "";
						if(cond2.minute()<10){
							thresoldUpperLimit =  cond2.hour() +":0"+ cond2.minute();
						}else{
							thresoldUpperLimit =  cond2.hour() +":"+ cond2.minute();
						}
						data.put("??????",thresoldUpperLimit +"??????");
					}else{
						data.put("??????????????????", "-");
						data.put("??????", "");
					}
					
					if(c.getCountAtr() ==CountAtr.HALFDAY){
						data.put("??????????????????", "???");
					}else{
						data.put("??????????????????", "-");
					}
					
					if(c.getTotalCondition().getUpperLimitSettingAtr() == UseAtr.NotUse && c.getTotalCondition().getLowerLimitSettingAtr() == UseAtr.NotUse){
						data.put("????????????", "");
					}else{
						if(CollectionUtil.isEmpty(dailyAttendanceItemDomainServiceDtos)){
							data.put("????????????", "");
						}else{
							dailyAttendanceItemDomainServiceDtos.stream().forEach(m->{
								data.put("????????????", m.getAttendanceItemName());
							});
						}
					}
					data.put("??????", c.getTotalTimesName());
					data.put("??????", c.getTotalTimesABName());
					if(c.getSummaryAtr() == SummaryAtr.DUTYTYPE){
						data.put("????????????", SummaryAtr.DUTYTYPE.nameId);
					}else if(c.getSummaryAtr() == SummaryAtr.WORKINGTIME){
						data.put("????????????", SummaryAtr.WORKINGTIME.nameId);
					}else{
						data.put("????????????", SummaryAtr.COMBINATION.nameId);
					}
					
					MasterData masterData = new MasterData(data, null, "");
					masterData.cellAt("No").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
					masterData.cellAt("????????????").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
					masterData.cellAt("??????").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
					masterData.cellAt("??????").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
					masterData.cellAt("????????????").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
					masterData.cellAt("????????????").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
					masterData.cellAt("???????????????").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
					masterData.cellAt("??????????????????").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
					masterData.cellAt("??????").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT));
					masterData.cellAt("??????????????????").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
					masterData.cellAt("??????").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.RIGHT));
					masterData.cellAt("????????????").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
					masterData.cellAt("??????????????????").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
					masterData.cellAt("???????????????").setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
					datas.add(masterData);
				}
			});	
		}
		return datas;
	}


	@Override
	public List<MasterHeaderColumn> getHeaderColumns(MasterListExportQuery query) {
		
		
		List<MasterHeaderColumn> columns = new ArrayList<>();
		columns.add(new MasterHeaderColumn("No",TextResource.localize("KMK009_4"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn("????????????",TextResource.localize("KMK009_5"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn("??????",TextResource.localize("KMK009_6"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn("??????",TextResource.localize("KMK009_22"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn("????????????",TextResource.localize("KMK009_14"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn("????????????",TextResource.localize("KMK009_8"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn("???????????????",TextResource.localize("KMK009_9"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn("??????????????????",TextResource.localize("KMK009_10")+" "+TextResource.localize("KMK009_17"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn("??????",TextResource.localize("KMK009_17"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn("??????????????????", TextResource.localize("KMK009_10")+" "+TextResource.localize("KMK009_20"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn("??????", TextResource.localize("KMK009_20"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn("????????????", TextResource.localize("KMK009_23"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn("??????????????????", TextResource.localize("KMK009_11")+" "+TextResource.localize("KMK009_27"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn("???????????????", TextResource.localize("KMK009_99"),
				ColumnTextAlign.LEFT, "", true));
		return columns;
	}
	
	private void putEmptyData (Map<String, Object> data){
		data.put("No", "");
		data.put("????????????", "");
		data.put("??????", "");
		data.put("??????", "");
		data.put("????????????", "");
		data.put("????????????", "");
		data.put("???????????????", "");
		data.put("??????????????????", "");
		data.put("??????", "");
		data.put("??????????????????", "");
		data.put("??????","");
		data.put("????????????", "");
		data.put("??????????????????", "");
		data.put("???????????????", "");
		
	}


	@Override
	public String mainSheetName() {
		// TODO Auto-generated method stub
		return TextResource.localize("KMK009_26");
	}
	
	@Override
	public MasterListMode mainSheetMode(){
		return MasterListMode.NONE;
	}


}
