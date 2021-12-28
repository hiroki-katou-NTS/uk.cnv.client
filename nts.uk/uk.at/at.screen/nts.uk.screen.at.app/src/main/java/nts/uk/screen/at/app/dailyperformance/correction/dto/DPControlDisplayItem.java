/**
 * 5:42:15 PM Aug 23, 2017
 */
package nts.uk.screen.at.app.dailyperformance.correction.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Data;
import nts.arc.error.BusinessException;
import nts.uk.ctx.at.record.dom.daily.dailyperformance.classification.EnumCodeName;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.enums.DailyAttendanceAtr;

/**
 * @author hungnm
 *
 */
@Data
public class DPControlDisplayItem {
	// sheet dtos
	private List<DPSheetDto> lstSheet;
	// header dtos
	private List<DPHeaderDto> lstHeader;
	
	private List<DPAttendanceItem> lstAttendanceItem;
	
	private Set<String> formatCode;
	
	private List<ColumnSetting> columnSettings;
	
	private boolean settingUnit;
	
	private List<EnumCodeName> comboItemCalc;
	
	private List<EnumCodeName> comboItemDoWork;
	
	private List<EnumCodeName> comboItemReason;
	
	private List<EnumCodeName> comboItemCalcCompact;
	
	private List<EnumCodeName> comboTimeLimit;
	
	private List<Integer> itemIds;
	
	Map<Integer, DPAttendanceItem> mapDPAttendance;
	
	private Map<Integer, String> itemInputName;

	public DPControlDisplayItem() {
		super();
		this.lstSheet = new ArrayList<>();
		this.lstHeader = new ArrayList<>();
		this.columnSettings = new ArrayList<>();
		this.itemIds = new ArrayList<>();
		this.mapDPAttendance = new HashMap<>();
		this.itemInputName = new HashMap<>();
	}
	
	private boolean isExistHeader(DPHeaderDto header) {
		for(DPHeaderDto exstHeader : this.lstHeader){
			if(exstHeader.getKey().equals(header.getKey())){
				return true;
			}
		}
		return false;
	}
	
	private boolean isExistSheet(DPSheetDto sheet){
		for(DPSheetDto exstSheet : this.lstSheet){
			if(exstSheet.getName().equals(sheet.getName())){
				return true;
			}
		}
		return false;
	}
	
	public void setLstHeader(List<DPHeaderDto> lstHeader) {
		for(DPHeaderDto header : lstHeader){
			if(!isExistHeader(header)){
				this.lstHeader.add(header);
			}
		}
	}

	public void createSheets(List<DPSheetDto> lstSheet) {
		for (DPSheetDto sheet : lstSheet) {
			if(!isExistSheet(sheet)){
				this.lstSheet.add(sheet);
			}
		}
		if(!this.lstSheet.isEmpty()) {
			this.lstSheet.sort((e1,e2) -> new BigDecimal(e1.getName()).compareTo(new BigDecimal(e2.getName())));
		}
	}

	public void addColumnsToSheet(List<FormatDPCorrectionDto> lstFormat, Map<Integer,DPAttendanceItem>  mapDP, boolean showButton) {
		lstFormat.forEach(f -> {
			this.lstSheet.forEach(s -> {
				if (f.getSheetNo().equals(s.getName()) && !s.isExistColumn(String.valueOf(f.getAttendanceItemId()))) {
					if (mapDP.containsKey(f.getAttendanceItemId())) {
						int attendanceAtr = mapDP.get(f.getAttendanceItemId()).getAttendanceAtr();
						if (attendanceAtr == DailyAttendanceAtr.Code.value
								|| attendanceAtr == DailyAttendanceAtr.Classification.value) {
							if (attendanceAtr == DailyAttendanceAtr.Code.value) {
								s.addColumn("Code" + f.getAttendanceItemId());
							} else {
								s.addColumn("NO" + f.getAttendanceItemId());
							}
							s.addColumn("Name" + f.getAttendanceItemId());
						} else if(attendanceAtr == DailyAttendanceAtr.Application.value) {
							s.addColumn("Submitted");
							if(showButton){
								s.addColumn("Application");
							}
							s.addColumn("ApplicationList");
						} else {
							s.addColumn("A" + String.valueOf(f.getAttendanceItemId()));
						}
					}
				}
			});
		});
		
		if(this.lstSheet.size() == 0){
			throw new BusinessException("Msg_1261");
		}
	}
	
	public void setHeaderText(List<DPAttendanceItem> lstAttendanceItem) {
		lstAttendanceItem.stream().forEach(i -> {
			Optional<DPHeaderDto> header = this.getLstHeader().stream()
					.filter(h -> h.getKey().equals("A"+String.valueOf(i.getId()))).findFirst();
			if (header.isPresent()) {
				header.get().setHeaderText(i);
			}
		});
	}

	public void setHeaderColor(List<DPAttendanceItemControl> lstAttendanceItemControl) {
		lstAttendanceItemControl.stream().forEach(i -> {
			Optional<DPHeaderDto> header = this.getLstHeader().stream()
					.filter(h -> h.getKey().equals(String.valueOf("A"+i.getAttendanceItemId()))).findFirst();
			if (header.isPresent()) {
				header.get().setHeaderColor(i);
				if(!header.get().getGroup().isEmpty()){
					header.get().getGroup().get(0).setHeaderColor(i);	
					header.get().getGroup().get(1).setHeaderColor(i);	
				}
			}
		});
	}

	public void setColumnsAccessModifier(List<DPBusinessTypeControl> lstDPBusinessTypeControl) {
		lstDPBusinessTypeControl.stream().forEach(i -> {
			Optional<DPHeaderDto> header = this.getLstHeader().stream()
					.filter(h -> h.getKey().substring(1, h.getKey().length()).equals(String.valueOf(i.getAttendanceItemId()))).findFirst();
			if (header.isPresent()) {
				header.get().setChangedByOther(i.isChangedByOther());
				header.get().setChangedByYou(i.isChangedByYou());
			}
		});
	}

}
