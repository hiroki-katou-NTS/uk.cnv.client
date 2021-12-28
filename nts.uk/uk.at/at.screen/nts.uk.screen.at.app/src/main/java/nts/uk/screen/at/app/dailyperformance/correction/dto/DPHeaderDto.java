/**
 * 2:20:39 PM Sep 5, 2017
 */
package nts.uk.screen.at.app.dailyperformance.correction.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.enums.DailyAttendanceAtr;
import nts.uk.screen.at.app.dailyperformance.correction.dto.primitive.PrimitiveValueDaily;
import nts.uk.screen.at.app.dailyperformance.correction.dto.type.TypeLink;
import nts.uk.screen.at.app.dailyperformance.correction.text.DPText;
import nts.uk.shr.com.i18n.TextResource;

/**
 * @author hungnm
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DPHeaderDto {

	private String headerText;
	
	private String attendanceName;

	private String key;

	private String dataType;

	private String width;

	private String color;

	private boolean hidden;

	private String ntsControl;

	private String ntsType;

	private String onChange;

	private Boolean changedByOther;

	private Boolean changedByYou;

	private List<DPHeaderDto> group;

	private Constraint constraint;
	
	private String headerCssClass;
	
	private String inputProcess;
	
	private Boolean grant;
	
	private String columnCssClass;
	
	private Integer displayNumber;

	private DPHeaderDto(String headerText, String attendanceName, String key, String dataType, String width, String color, boolean hidden,
			String ntsControl, Boolean changedByOther, Boolean changedByYou, String headerCss, String inputProcess, String columnCssClass) {
		super();
		this.headerText = headerText;
		this.attendanceName = attendanceName;
		this.key = key;
		this.dataType = dataType;
		this.width = width;
		this.color = color;
		this.hidden = hidden;
		this.ntsControl = ntsControl;
		this.changedByOther = changedByOther;
		this.changedByYou = changedByYou;
		this.group = new ArrayList<>();
		this.headerCssClass = headerCss;
		this.inputProcess = inputProcess;
		this.grant = false;
		this.columnCssClass = columnCssClass;
	}

	private DPHeaderDto(String headerText, String attendanceName, String key, String dataType, String width, String color, boolean hidden,
			String ntsControl, String ntsType, String onChange, Boolean changedByOther, Boolean changedByYou, String inputProcess) {
		super();
		this.headerText = headerText;
		this.attendanceName = attendanceName;
		this.key = key;
		this.dataType = dataType;
		this.width = width;
		this.color = color;
		this.hidden = hidden;
		this.ntsControl = ntsControl;
		this.ntsType = ntsType;
		this.onChange = onChange;
		this.changedByOther = changedByOther;
		this.changedByYou = changedByYou;
		this.inputProcess = inputProcess;
		this.group = new ArrayList<>();
	}

	public static DPHeaderDto createSimpleHeader(String companyId, String key, String width,
			Map<Integer, DPAttendanceItem> mapDP, Map<Integer, DPAttendanceItemControl> mapColor) {
		val keyId = getCode(key);
		val colorHeader = mapColor.get(Integer.parseInt(keyId));
		DPHeaderDto dto = new DPHeaderDto("", "", key, "String", width,
				colorHeader == null ? "#CFF1A5"
						: colorHeader.getHeaderBackgroundColor().isEmpty() ? "#CFF1A5"
								: colorHeader.getHeaderBackgroundColor(),
				false, "", false, false, "center-align", inputProcess(Integer.parseInt(keyId)), "");
		// optionalRepo.findByListNos(companyId, optionalitemNos)
		DPAttendanceItem item = mapDP.get(Integer.parseInt(keyId));
		int attendanceAtr = item.getAttendanceAtr();
		if (attendanceAtr == DailyAttendanceAtr.Code.value) {
			List<DPHeaderDto> groups = new ArrayList<>();
			int withChild = Integer.parseInt(width.substring(0, width.length() - 2)) / 2;
			DPHeaderDto dtoG = new DPHeaderDto("<div style=\"max-height: 20px;\">コード</div>", "<div style=\"max-height: 20px;\">コード</div>", "Code" + keyId, "String", String.valueOf(withChild) + "px",
					"", false, "", "code_"+"Name"+ keyId, "search", false, false, inputProcess(Integer.parseInt(keyId)));
			dtoG.setConstraint(new Constraint("Primitive", isRequired(item), getPrimitiveAllName(item)));
			dtoG.setColor(dto.getColor());
			groups.add(dtoG);
			groups.add(new DPHeaderDto("<div style=\"max-height: 20px;\">名称</div>", "<div style=\"max-height: 20px;\">名称</div>", "Name" + keyId, "String", String.valueOf(withChild) + "px", dto.getColor(),
					false, "Link2", false, false, "center-align", null, ""));
			dto.setGroup(groups);
			dto.setConstraint(new Constraint("Primitive", false, ""));
		} else if (attendanceAtr == DailyAttendanceAtr.Classification.value && item.getTypeGroup() != null) {
			List<DPHeaderDto> groups = new ArrayList<>();
			int withChild = Integer.parseInt(width.substring(0, width.length() - 2)) / 2;
			groups.add(new DPHeaderDto("<div style=\"max-height: 20px;\">NO</div>", "<div style=\"max-height: 20px;\">NO</div>", "NO" + keyId, "number", String.valueOf(withChild) + "px", dto.getColor(), false,
					"", "comboCode_"+"Name"+ keyId, "", false, false, inputProcess(Integer.parseInt(keyId))));
			if (item.getTypeGroup() == TypeLink.CALC.value) {
				if(!DPText.ITEM_COMBOBOX_CALC.contains(Integer.parseInt(keyId))){
					DPHeaderDto dtoG = new DPHeaderDto("<div style=\"max-height: 20px;\">名称</div>", "<div style=\"max-height: 20px;\">名称</div>", "Name" + keyId, "number",
							String.valueOf(withChild) + "px", "", false, "ComboboxCalc", false, false, "center-align", null, "");
					groups.get(0).setConstraint(new Constraint("Integer", true, "2"));
					groups.add(dtoG);
				}else{
					DPHeaderDto dtoG = new DPHeaderDto("<div style=\"max-height: 20px;\">名称</div>", "<div style=\"max-height: 20px;\">名称</div>", "Name" + keyId, "number",
							String.valueOf(withChild) + "px", "", false, "ComboItemsCompact", false, false, "center-align", null, "");
					groups.get(0).setConstraint(new Constraint("Integer", true, Arrays.asList(0, 2)));
					groups.add(dtoG);
				}
			}else if (item.getTypeGroup() == TypeLink.REASON_GO_OUT.value) {
				DPHeaderDto dtoG = new DPHeaderDto("<div style=\"max-height: 20px;\">名称</div>", "<div style=\"max-height: 20px;\">名称</div>", "Name" + keyId, "number",
						String.valueOf(withChild) + "px", "", false, "ComboboxReason", false, false, "center-align", null, "");
				groups.add(dtoG);
				groups.get(0).setConstraint(new Constraint("Integer", true, "3"));
			}else if (item.getTypeGroup() == TypeLink.DOWORK.value) {
				DPHeaderDto dtoG = new DPHeaderDto("<div style=\"max-height: 20px;\">名称</div>", "<div style=\"max-height: 20px;\">名称</div>", "Name" + keyId, "number",
						String.valueOf(withChild) + "px", "", false, "ComboboxDoWork" + "Name" + keyId, false, false, "center-align", null, "");
				groups.add(dtoG);
				groups.get(0).setConstraint(new Constraint("Integer", true, "1"));
			}else if (item.getTypeGroup() == TypeLink.TIME_LIMIT.value) {
				DPHeaderDto dtoG = new DPHeaderDto("<div style=\"max-height: 20px;\">名称</div>", "<div style=\"max-height: 20px;\">名称</div>", "Name" + keyId, "number",
						String.valueOf(withChild) + "px", "", false, "ComboboxTimeLimit", false, false, "center-align", null, "");
				groups.add(dtoG);
				groups.get(0).setConstraint(new Constraint("Integer", true, "2"));
			}
			groups.get(0).setColor(dto.getColor());
			groups.get(1).setColor(dto.getColor());
			dto.setGroup(groups);
			dto.setConstraint(new Constraint("Combo", true, ""));
		} else if (attendanceAtr == DailyAttendanceAtr.AmountOfMoney.value) {
			dto.setGrant(true);
			if (item.getPrimitive() != null && item.getPrimitive() == 54) {
				dto.setConstraint(new Constraint("Currency", false, "").createMinMax("-999999", "999999"));
			} else if (item.getPrimitive() != null && item.getPrimitive() == 55) {
				dto.setConstraint(new Constraint("Currency", false, "").createMinMax("-999999999", "999999999"));
			} else {
				dto.setConstraint(new Constraint("Currency", false, "").createMinMax("-999999999", "999999999"));
			}
		} else if (attendanceAtr == DailyAttendanceAtr.Time.value) {
			dto.setGrant(true);
			if(item.getPrimitive() != null && item.getPrimitive() == 1){
				dto.setConstraint(new Constraint("Clock", false, "").createMinMax("00:00", "48:00"));
			}else if(item.getPrimitive() != null && (item.getPrimitive() == 56 || item.getPrimitive() == 57)){
				if(item.getPrimitive() == 56){
					dto.setConstraint(new Constraint("Clock", false, "").createMinMax("-99:59", "99:59"));
				}else{
					dto.setConstraint(new Constraint("Clock", false, "").createMinMax("-999999:59", "999999:59"));
				}
			}else{
				dto.setConstraint(new Constraint("Clock", false, "").createMinMax("-48:00", "48:00"));
			}
			//dto.setConstraint(new Constraint("Primitive", false, getPrimitiveAllName(item)));
		} else if (attendanceAtr == DailyAttendanceAtr.NumberOfTime.value) {
			if (item.getTypeGroup() != null &&  item.getTypeGroup() == TypeLink.DOWORK.value) {
				dto.setDataType("boolean");
				dto.setNtsControl("Checkbox");
				dto.setInputProcess(null);
				dto.setColumnCssClass("halign-center");
			} else {
				dto.setGrant(true);
				dto.setConstraint(new Constraint("Primitive", false, getPrimitiveAllName(item)));
			}
		} else if (attendanceAtr == DailyAttendanceAtr.TimeOfDay.value) {
			dto.setConstraint(new Constraint("TimeWithDay", false, ""));
		} else if(attendanceAtr == DailyAttendanceAtr.Charater.value){
			dto.setConstraint(new Constraint("Primitive", false, getPrimitiveAllName(item)));
		} else if (attendanceAtr == DailyAttendanceAtr.NumbericValue.value) {
			dto.setGrant(true);
			dto.setColumnCssClass("halign-right");
			dto.setConstraint(new Constraint("Primitive", false, getPrimitiveAllName(item)));
		}
		return dto;
	}

	public static DPHeaderDto addHeaderApplication(String key, Map<Integer, DPAttendanceItemControl> mapColor) {
		val colorHeader = setColorHeadeAppSubAppList(key, mapColor);
		return new DPHeaderDto(TextResource.localize("KDW003_63"), TextResource.localize("KDW003_63"), "Application", "String", "90px", colorHeader, false, "Button",
				false, false, "center-align", null, "");
	}

	public static DPHeaderDto addHeaderSubmitted(String key, Map<Integer, DPAttendanceItemControl> mapColor) {
		val colorHeader = setColorHeadeAppSubAppList(key, mapColor);
		return new DPHeaderDto(TextResource.localize("KDW003_62"), TextResource.localize("KDW003_62"), "Submitted", "String", "90px", colorHeader, false, "Label",
				false, false, "center-align", null, "");
	}

	public static DPHeaderDto addHeaderApplicationList(String key, Map<Integer, DPAttendanceItemControl> mapColor) {
		val colorHeader = setColorHeadeAppSubAppList(key, mapColor);
		return new DPHeaderDto(TextResource.localize("KDW003_110"), TextResource.localize("KDW003_110"), "ApplicationList", "String", "90px", colorHeader, false,
				"ButtonList", false, false, "center-align", null, "");
	}
	
	private static String setColorHeadeAppSubAppList(String key, Map<Integer, DPAttendanceItemControl> mapColor) {
		val keyId = getCode(key);
		val colorHeader = mapColor.get(Integer.parseInt(keyId));
		val color = colorHeader == null ? "#CFF1A5" : colorHeader.getHeaderBackgroundColor().isEmpty() ? "#CFF1A5" : colorHeader.getHeaderBackgroundColor();
		return color;
	}

	private static String getCode(String key) {
		return key.trim().substring(1, key.trim().length());
	}

	public void setHeaderText(DPAttendanceItem param) {
		this.displayNumber = param.getDisplayNumber();
		
		String displayText = Strings.isNotBlank(param.getName()) ? param.getName() : param.getDisplayName();
		if (param.getLineBreakPosition() != null && param.getLineBreakPosition() > 0 && param.getName() != null) {
			if(displayText.length() > param.getLineBreakPosition()) {
				this.headerText = displayText.substring(0, param.getLineBreakPosition()) + "<br/>" + displayText.substring(param.getLineBreakPosition(), displayText.length());
			} else {
				this.headerText = displayText;
			}
		} else {
			this.headerText = displayText;
		}
		this.attendanceName = param.getDisplayName();
	}

	public void setHeaderColor(DPAttendanceItemControl param) {
		this.color = param.getHeaderBackgroundColor();
	}

	public static List<DPHeaderDto> GenerateFixedHeader() {
		List<DPHeaderDto> lstHeader = new ArrayList<>();
		lstHeader.add(new DPHeaderDto("ID", "ID", "id", "String", "30px", "#CFF1A5", true, "Label", true, true, "center-align", null, ""));
		lstHeader.add(new DPHeaderDto("状<br/>態", "状<br/>態", "state", "String", "30px", "#CFF1A5", false, "FlexImage", true, true, "center-align", null, ""));
		lstHeader.add(new DPHeaderDto(TextResource.localize("KDW003_129"), TextResource.localize("KDW003_129"), "error", "String", "60px", "#CFF1A5", false, "Label", 
				true, true, "center-align", null, ""));
		lstHeader.add(new DPHeaderDto(TextResource.localize("KDW003_41"), TextResource.localize("KDW003_41"), "date", "String", "70px", "#CFF1A5", false, "Label",
				true, true, "center-align", null, ""));
		lstHeader.add(new DPHeaderDto(TextResource.localize("KDW003_42"), TextResource.localize("KDW003_42"), "sign", "boolean", "35px", "#CFF1A5", false,
				"Checkbox", true, true, "center-align", null, ""));
		lstHeader.add(new DPHeaderDto(TextResource.localize("KDW003_32"), TextResource.localize("KDW003_32"), "employeeCode", "String", "87px", "#CFF1A5", false,
				"Label", true, true, "center-align", null, ""));
		lstHeader.add(new DPHeaderDto(TextResource.localize("KDW003_33"), TextResource.localize("KDW003_33"), "employeeName", "String", "162px", "#CFF1A5", false,
				"Label", true, true, "center-align", null, ""));
		lstHeader.add(new DPHeaderDto("", "", "picture-person", "String", "35px", "", false, "Image", true, true, "center-align", null, "#CFF1A5"));
		lstHeader.add(new DPHeaderDto(TextResource.localize("承認"), TextResource.localize("承認"), "approval", "boolean", "35px", "#CFF1A5", false, "Checkbox",
				true, true, "center-align", null, ""));
		return lstHeader;
	}

//	private static String getPrimitiveName(DPAttendanceItem item) {
//		if (item.getTypeGroup() != null) {
//			switch (item.getTypeGroup()) {
//			case 1:
//				return "WorkTypeCode";
//			case 2:
//				return "WorkTimeCode";
//			case 3:
//				return "WorkLocationCD";
//			case 4:
//				return "DiverdenceReasonCode";
//			case 5:
//				return "WorkplaceCode";
//			case 6:
//				return "ClassificationCode";
//			case 7:
//				return "JobTitleCode";
//			case 8:
//				return "EmploymentCode";
//			default:
//				return "";
//			}
//		} else {
//			return "WorkTypeCode";
//		}
//	}

	private static String getPrimitiveAllName(DPAttendanceItem item) {
		if(item.getPrimitive() == null) return "";
		return PrimitiveValueDaily.mapValuePrimitive.get(item.getPrimitive());
	}
	
	private static String inputProcess(int itemId) {
		//if (itemId == 28 || itemId == 29 || itemId == 31 || itemId == 34 || itemId == 41 || itemId == 44)
		return "inputProcess";
		//return null;
	}
	
	private static boolean isRequired(DPAttendanceItem item){
		if(DPText.ITEM_REQUIRED.contains(item.getId())) return true;
		return false;
	}
	
	public static Map<Integer, String> getPrimitiveAll() {
		return PrimitiveValueDaily.mapValuePrimitive;
	}
}
