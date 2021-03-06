package nts.uk.file.at.app.export.schedulevertical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.schedule.app.find.budget.schedulevertical.fixedverticalsetting.FixedVerticalSettingDto;
import nts.uk.ctx.at.schedule.app.find.budget.schedulevertical.fixedverticalsetting.FixedVerticalSettingFinder;
import nts.uk.ctx.at.schedule.app.find.budget.schedulevertical.fixedverticalsetting.VerticalCntSettingDto;
import nts.uk.ctx.at.schedule.app.find.budget.schedulevertical.fixedverticalsetting.VerticalCntSettingFinder;
import nts.uk.ctx.at.schedule.dom.budget.schedulevertical.fixedverticalsetting.FixedVerticalSettingRepository;
import nts.uk.ctx.at.schedule.dom.budget.schedulevertical.fixedverticalsetting.VerticalTime;
import nts.uk.ctx.at.shared.app.find.scherec.totaltimes.TotalTimesFinder;
import nts.uk.ctx.at.shared.app.find.scherec.totaltimes.dto.TotalTimesDetailDto;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.time.TimeWithDayAttr;
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
 * @author thangnv
 *
 */

@Stateless
@DomainID(value = "SettingScheVerticalScale")
public class SettingScheVerticalScale implements MasterListData {
	@Inject
	private FixedVerticalSettingFinder verticalSettingFinder;
	
	@Inject
	private FixedVerticalSettingRepository fixedVerticalSettingRepository;
	
	@Inject
	private VerticalCntSettingFinder verticalCntSettingFinder;
	
	@Inject
	private UniversalVerticalSettingSheet universalVerticalSettingSheet;
	
	@Inject
	private TotalTimesFinder totalTimesFinder;
	
	private static final String column_1 = "????????????";
	private static final String column_2 = "????????????";
	private static final String column_3 = "????????????";
	
	@Override
	public List<MasterHeaderColumn> getHeaderColumns(MasterListExportQuery query) {
		
		List<MasterHeaderColumn> columns = new ArrayList<>();
		columns.add(new MasterHeaderColumn(column_1, TextResource.localize("KML002_82"), ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(column_2, TextResource.localize("KML002_83"), ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(column_3, TextResource.localize("KML002_84"), ColumnTextAlign.LEFT, "", true));
		
		return columns;
	}

	@Override
	public MasterListMode mainSheetMode(){
		return MasterListMode.NONE;
	}
	
	/**
	 * multi sheet
	 */
	@Override
	public List<SheetData> extraSheets(MasterListExportQuery query) {
		List<SheetData> sheetDatas = new ArrayList<>();
		SheetData displayControl = new SheetData(universalVerticalSettingSheet.getMasterDatas(query), universalVerticalSettingSheet.getHeaderColumns(query), null, null,TextResource.localize("KML002_80"), MasterListMode.NONE);	 
		sheetDatas.add(displayControl);
		
		return sheetDatas;
	}
	
	/** 
	 * @param MasterListExportQuery query
	 * @return List of MasterData
	 */
	@Override
	public List<MasterData> getMasterDatas(MasterListExportQuery query) {
		List<MasterData> datas = new ArrayList<>();
		List<FixedVerticalSettingDto> listFixedVerticals = verticalSettingFinder.findByCid();
		
		listFixedVerticals.stream().forEach(item -> {
			datas.add(createMasterDateForRow(item));
		});
		return datas;
	}
	
	/**
	 * 
	 * @param String item
	 * @param String companyId
	 * @return instance of MasterData
	 */
	private MasterData createMasterDateForRow (FixedVerticalSettingDto rowData){
		String companyId = AppContexts.user().companyId();
		Map<String, Object> data = new HashMap<>();
		putEmptyData(data); 
		// column 1
		data.put(column_1, rowData.getFixedVerticalName());
		// column 2
		switch (rowData.getUseAtr()) {
		case 0:
			data.put(column_2, TextResource.localize("KML002_100"));
			break;
		case 1:
			data.put(column_2, TextResource.localize("KML002_99"));
			break;
		default:
			break;
		}
		// column 3
		if (rowData.getUseAtr() == 1){
			StringBuffer column3Content = new StringBuffer();
			switch (rowData.getFixedItemAtr()) {
			case 0:
				// 0- ????????? - TIME_ZONE
				
				List<VerticalTime> listVerTimes = fixedVerticalSettingRepository.findAllVerticalTime(companyId, rowData.getFixedItemAtr());
				for (VerticalTime time : listVerTimes) {
					if (time.getDisplayAtr().value == 1){
						TimeWithDayAttr _time = new TimeWithDayAttr(time.getStartClock().v());
						if (column3Content.length() <= 0) {
							column3Content.append(_time.getFullText());
						} else {
							column3Content.append(",");
							column3Content.append(_time.getFullText());
						}
					}
				}
				break;
			case 1:
				// 1- ???????????? - TOTAL_COUNT
				List<VerticalCntSettingDto> listCnts = verticalCntSettingFinder.findAll(rowData.getFixedItemAtr());
				listCnts = listCnts.stream().sorted((object1, object2) -> object1.getVerticalCountNo() - object2.getVerticalCountNo()).collect(Collectors.toList());
				for (VerticalCntSettingDto cnt : listCnts) {
					int no = cnt.getVerticalCountNo();
					TotalTimesDetailDto detail = totalTimesFinder.getTotalTimesDetails(no);
					if (detail.getUseAtr() == 1){
						if (column3Content.length() <= 0) {
							column3Content.append(detail.getTotalCountNo());
							column3Content.append(detail.getTotalTimesName());
						} else {
							column3Content.append(",");
							column3Content.append(detail.getTotalCountNo());
							column3Content.append(detail.getTotalTimesName());
						}
					}
				}
				break;
			case 6:
				// 6- ?????? - ROLE
				break;
			default:
				break;
			}
			data.put(column_3, column3Content.toString());
		} else {
			data.put(column_3, "");
		}
		
		MasterData masterData = new MasterData(data, null, "");
		
		// set cell align
		masterData.cellAt(column_1).setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
		masterData.cellAt(column_2).setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
		masterData.cellAt(column_3).setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
			
		return masterData;
	}
	
	/* Sheet name */
	@Override
	public String mainSheetName() {
		return TextResource.localize("KML002_79"); 
	} 
	
	/* put empty map */
	private void putEmptyData(Map<String, Object> data){
		data.put(column_1,"");
		data.put(column_2,"");
		data.put(column_3,"");
	}
}
