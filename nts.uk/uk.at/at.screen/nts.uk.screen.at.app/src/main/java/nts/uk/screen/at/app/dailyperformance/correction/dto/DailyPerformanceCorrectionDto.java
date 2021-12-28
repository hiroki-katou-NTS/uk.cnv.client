/**
 * 1:59:07 PM Aug 21, 2017
 */
package nts.uk.screen.at.app.dailyperformance.correction.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.xml.security.utils.I18n;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.function.app.find.dailyperformanceformat.dto.DailyPerformanceCodeDto;
import nts.uk.ctx.at.record.app.find.dailyperform.DailyRecordDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.enums.DailyAttendanceAtr;
import nts.uk.screen.at.app.dailyperformance.correction.dto.cache.DPCorrectionStateParam;
import nts.uk.screen.at.app.dailyperformance.correction.dto.checkshowbutton.DailyPerformanceAuthorityDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.style.TextStyle;
import nts.uk.screen.at.app.dailyperformance.correction.error.DCErrorInfomation;
import nts.uk.screen.at.app.dailyperformance.correction.identitymonth.IndentityMonthResult;
import nts.uk.screen.at.app.dailyperformance.correction.monthflex.DPMonthResult;
import nts.uk.screen.at.app.dailyperformance.correction.text.DPText;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.license.option.OptionLicense;

/**
 * @author hungnm
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class DailyPerformanceCorrectionDto implements Serializable{
	private static final long serialVersionUID = 1L;

	private YearHolidaySettingDto yearHolidaySettingDto;

	private SubstVacationDto substVacationDto;

	private CompensLeaveComDto compensLeaveComDto;

	private Com60HVacationDto com60HVacationDto;

	private DateRange dateRange;
	
	private DatePeriodInfo periodInfo;

	private List<DailyPerformanceEmployeeDto> lstEmployee;

	private List<DPHeaderDto> lstFixedHeader;

	private DPControlDisplayItem lstControlDisplayItem;

	private Map<String, DPCellStateDto> mapCellState;
	
	private List<DPCellStateDto> lstCellState;
	
	private List<DPCellStateDto> lstCellStateCalc;

	private List<DPDataDto> lstData;

	private List<DailyPerformanceAuthorityDto> authorityDto;
	
	private DailyPerformanceCodeDto dailyPerformanceCodeDto;

	private String employmentCode;
	
	private Integer closureId;

	// A13_1 コメント
	private String comment;
	
	private Integer typeBussiness;

	private Set<ItemValue> itemValues;

	private Boolean showPrincipal;
	
	private Boolean showSupervisor;
	
	private Map<String, String > data;
	
	private List<DPErrorDto> dPErrorDto;
	
	private IdentityProcessUseSetDto identityProcessDto;
	
	private DPMonthResult monthResult;
	
	private Integer showQuestionSPR;
	
	private ChangeSPR changeSPR;
	
	private List<TextStyle> textStyles;
	
	private Set<String> autBussCode;
	
	private List<DailyRecordDto> domainOld;
	
	private List<DailyRecordDto> domainOldForLog;
	
	private boolean showTighProcess;
	
	private IndentityMonthResult indentityMonthResult;
	
	private boolean showErrorDialog;
	
	private List<DCMessageError> errors;
	
	private int errorInfomation;
	
	private List<DPHideControlCell> lstHideControl = new ArrayList<>();
	
	private List<DPHideControlCell> lstCellDisByLock = new ArrayList<>();
	
	private boolean lockDisableFlex;
	
	private DateRange rangeLock;
	
	private List<String> employeeIds;
	
	private List<String> changeEmployeeIds;
	
	private Optional<IdentityProcessUseSetDto> identityProcessDtoOpt;
	
	private Optional<ApprovalUseSettingDto> approvalUseSettingDtoOpt;
	
	private DateRange datePeriodResult;
	
//	private List<DPErrorDto> lstError;
	
	private DisplayItem disItem;
	
	private ApprovalConfirmCache approvalConfirmCache;

	private DPCorrectionStateParam stateParam;
	
	private DPCorrectionMenuDto dPCorrectionMenuDto;
	
	// 就業確定の機能制限.就業確定を行う	RestrictConfirmEmployment.confirmEmployment
	private Boolean confirmEmployment;
	// 作業運用設定.作業運用方法 && アプリケーションコンテキスト．オプションライセンス．就業．作業・応援管理
	private boolean showWorkLoad;
	
	public DailyPerformanceCorrectionDto() {
		super();
		this.lstFixedHeader = DPHeaderDto.GenerateFixedHeader();
		this.mapCellState = new HashMap<>();
		this.lstCellState = new ArrayList<>();
		this.lstControlDisplayItem = new DPControlDisplayItem();
		this.itemValues = new HashSet<>();
		this.data = new HashMap<>();
		this.dPErrorDto = new ArrayList<>();
		this.changeSPR = new ChangeSPR(false, false);
		this.textStyles = new ArrayList<>();
		this.autBussCode = new HashSet<>();
		this.showTighProcess = false;
		this.lstCellStateCalc = new ArrayList<>();
		this.indentityMonthResult = new IndentityMonthResult(false, false, true);
		this.errors = new ArrayList<>();
		this.errorInfomation = DCErrorInfomation.NORMAL.value;
		this.lockDisableFlex = false;
		this.employeeIds = new ArrayList<>();
		this.changeEmployeeIds = new ArrayList<>();
		this.identityProcessDtoOpt = Optional.empty();
		this.approvalUseSettingDtoOpt = Optional.empty();
//		this.lstError = new ArrayList<>();
		this.disItem = new DisplayItem();
	}

	/** Check if employeeId is login user */
	private boolean isLoginUser(String employeeId) {
		for (DailyPerformanceEmployeeDto employee : this.lstEmployee) {
			if (employee.getId().equals(employeeId) && employee.isLoginUser()) {
				return true;
			}
		}
		return false;
	}

	/** Find cell by dataID and columnKey */
	private Optional<DPCellStateDto> findExistCellState(String dataId, String columnKey) {
		DPCellStateDto dto = mapCellState.get("_" + String.valueOf(dataId)+ "|"+columnKey);
		return Optional.ofNullable(dto);
//		 return this.mapCellState.stream().filter(x -> {
//			 String rowId = x.getRowId();
//			 String column = x.getColumnKey();
//		     if (rowId != null && column != null & rowId.equals("_" + String.valueOf(dataId))
//						&& column.equals(String.valueOf(columnKey))) {
//		    	 return true;
//		     }else {
//		    	 return false;
//		     }
//		 }).findFirst();
	}

	/** Set disable cell & Create not existed cell */
	private void setDisableCell(DPHeaderDto header, DPDataDto data, Map<Integer, DPAttendanceItem> mapDP) {
//		Optional<DPCellStateDto> existedCellState = findExistCellState(data.getId(), header.getKey());
//		if (existedCellState.isPresent()) {
//			existedCellState.get().addState("mgrid-disable");
//		} else {
		   if(!header.getKey().equals("Application") && !header.getKey().equals("Submitted") && !header.getKey().equals("ApplicationList")){
			int attendanceAtr = mapDP.get(Integer.parseInt(getID(header.getKey()))).getAttendanceAtr();
			if (attendanceAtr == DailyAttendanceAtr.Code.value || attendanceAtr == DailyAttendanceAtr.Classification.value) {
				if (attendanceAtr == DailyAttendanceAtr.Classification.value) {
					this.mapCellState.put("_" + data.getId() + "|"+ "NO" + getID(header.getKey()), new DPCellStateDto("_" + data.getId(), "NO" + getID(header.getKey()), toList("mgrid-disable")));
				    lstCellDisByLock.add(new DPHideControlCell("_" + data.getId(), "NO" + getID(header.getKey())));
				} else {
					this.mapCellState.put("_" + data.getId()+ "|" + "Code" + getID(header.getKey()), new DPCellStateDto("_" + data.getId(), "Code" + getID(header.getKey()), toList("mgrid-disable")));
					 lstCellDisByLock.add(new DPHideControlCell("_" + data.getId(), "Code" + getID(header.getKey())));
				}
				this.mapCellState.put("_" + data.getId() + "|" + "Name" + getID(header.getKey()), new DPCellStateDto("_" + data.getId(), "Name" + getID(header.getKey()), toList("mgrid-disable")));
				 lstCellDisByLock.add(new DPHideControlCell("_" + data.getId(), "Name" + getID(header.getKey())));
			} else {
				this.mapCellState.put("_" + data.getId()+ "|" + header.getKey(), new DPCellStateDto("_" + data.getId(), header.getKey(), toList("mgrid-disable")));
				 lstCellDisByLock.add(new DPHideControlCell("_" + data.getId(), header.getKey()));
			}
		   }
//		}
	}

	private String getID(String key) {
		key = key.trim();
		return key.substring(1, key.length());
	}

	/** Mark current login employee */
	public void markLoginUser(String sid) {
		for (DailyPerformanceEmployeeDto employee : lstEmployee) {
			if (employee.getId().equals(sid)) {
				employee.setLoginUser(true);
			}
		}
	}

	/** Create Access Modifier Cellstate */
	public void createAccessModifierCellState(Map<Integer, DPAttendanceItem> mapDP) {
		this.getLstData().forEach(data -> {
			boolean isLoginUser = isLoginUser(data.getEmployeeId());
			this.getLstControlDisplayItem().getLstHeader().forEach(header -> {
				if (header.getChangedByOther() && !header.getChangedByYou()) {
					if (isLoginUser) {
						setDisableCell(header, data, mapDP);
					}
				} else if (!header.getChangedByOther() && header.getChangedByYou()) {
					if (!isLoginUser) {
						setDisableCell(header, data, mapDP);
					}
				} else if (!header.getChangedByOther() && !header.getChangedByYou()) {
					setDisableCell(header, data, mapDP);
				}
			});
		});
	}

	public void createModifierCellStateCaseRow(Map<Integer, DPAttendanceItem> mapDP, List<DPHeaderDto> lstHeader) {
		this.getLstData().forEach(data -> {
			boolean isLoginUser = isLoginUser(data.getEmployeeId());
			lstHeader.forEach(header -> {
				if (header.getChangedByOther() && !header.getChangedByYou()) {
					if (isLoginUser) {
						setDisableCell(header, data, mapDP);
					}
				} else if (!header.getChangedByOther() && header.getChangedByYou()) {
					if (!isLoginUser) {
						setDisableCell(header, data, mapDP);
					}
				} else if (!header.getChangedByOther() && !header.getChangedByYou()) {
					setDisableCell(header, data, mapDP);
				}
			});
		});
	}
	
	/** Set Error/Alarm text and state for cell */
	public void addErrorToResponseData(List<DPErrorDto> lstError, List<DPErrorSettingDto> lstErrorSetting, Map<Integer, DPAttendanceItem> mapDP, boolean showTextError) {
		lstError.forEach(error -> {
			this.lstData.forEach(data -> {
				if (data.getEmployeeId().equals(error.getEmployeeId())
						&& data.getDate().equals(error.getProcessingDate())) {
					String errorTypeTemp = getErrorType(lstErrorSetting, error, showTextError);
					String errorType = errorTypeTemp.equals("T") ? "" : errorTypeTemp;
					if(!errorTypeTemp.equals("")) data.setErrorOther(true);
					// add error alarm to response data
					if (!data.getError().isEmpty()) {
						if (!errorType.equals(data.getError()) && !errorType.isEmpty()) {
							data.setError(TextResource.localize("KDW003_129"));
						}
					} else {
						if(errorType.equals("ER")) {
							data.setError(TextResource.localize("KDW003_130"));
						} else if(errorType.equals("AL")) {
							data.setError(TextResource.localize("KDW003_131"));
						}
					}
					// add error alarm cell state
					error.getAttendanceItemId().stream().forEach(x ->{
						if(errorType.contains("ER") || errorType.contains("AL")) setCellStateCheck(data.getId(), x.toString(),
								errorType.contains("ER") ? "mgrid-error" : "mgrid-alarm", mapDP);
					});
				}
			});
		});
	}

	private String getErrorType(List<DPErrorSettingDto> lstErrorSetting, DPErrorDto error, boolean showTextError) {
		DPErrorSettingDto setting = lstErrorSetting.stream()
				.filter(c -> c.getErrorAlarmCode().equals(error.getErrorCode())).findFirst().orElse(null);
		if (setting == null) {
			return "";
		}
		return setting.getTypeAtr() == 0 ? "ER" : setting.getTypeAtr() == 2 ? (showTextError ? "T" : "") : "AL";
	}

	/** Set AlarmCell state for Fixed cell */
	public void setAlarmCellForFixedColumn(String dataId, Integer mode) {
		if (mode == 0) {
			setCellStateFixed(dataId, "date", "mgrid-alarm");
		} else {
			Stream.of("date", "employeeCode", "employeeName").forEach(columnKey -> {
				setCellStateFixed(dataId, columnKey, "mgrid-alarm");
			});
		}
	}

	private void setCellStateFixed(String dataId, String columnKey, String state) {
		Optional<DPCellStateDto> existedCellState = findExistCellState(dataId, columnKey);
		if (existedCellState.isPresent()) {
			existedCellState.get().addState(state);
		} else {
			DPCellStateDto dto = new DPCellStateDto("_" + dataId, columnKey, toList(state));
			this.mapCellState.put("_" + dataId + "|" + columnKey, dto);
		}
	}
	
	private void setCellStateCheck(String dataId, String columnKey, String state, Map<Integer, DPAttendanceItem> mapDP) {
//		Optional<DPCellStateDto> existedCellState = findExistCellState(dataId, columnKey);
//		if (existedCellState.isPresent()) {
//			existedCellState.get().addState(state);
//		} else {
		String colKey = null, nameKey = null;
		if(mapDP.containsKey(Integer.parseInt(columnKey))){
			int attendanceAtr = mapDP.get(Integer.parseInt(columnKey)).getAttendanceAtr();
			if (attendanceAtr == DailyAttendanceAtr.Code.value || attendanceAtr == DailyAttendanceAtr.Classification.value) {
				if (attendanceAtr == DailyAttendanceAtr.Classification.value) {
					colKey = "NO" + columnKey;
				} else {
					colKey = "Code" + columnKey;
				}
				nameKey = "Name" + columnKey;
			} else {
				colKey = "A" + columnKey;
			}
			
			Optional<DPCellStateDto> existedCellState = findExistCellState(dataId, colKey);
			if (existedCellState.isPresent()) {
				existedCellState.get().addState(state);
			} else {
				this.mapCellState.put("_" + dataId + "|" + colKey, new DPCellStateDto("_" + dataId, colKey, toList(state)));
			}
			
			if (nameKey != null) {
				Optional<DPCellStateDto> existedNameCellState = findExistCellState(dataId, nameKey);
				if (existedNameCellState.isPresent()) {
					existedNameCellState.get().addState(state);
				} else {
					this.mapCellState.put("_" + dataId + "|" + nameKey, new DPCellStateDto("_" + dataId, nameKey, toList(state)));
				}
			}
		}
//		}
	}

	private List<String> toList(String... item) {
		return Stream.of(item).collect(Collectors.toCollection(ArrayList::new));
	}

	/** Set AlarmCell state for Fixed cell */
	public void setCellSate(String rowId, String columnKey, String state) {
		Optional<DPCellStateDto> existedCellState = findExistCellState(rowId, columnKey);
		if (existedCellState.isPresent()) {
			List<String> stateCell = existedCellState.get().getState();
			if(stateCell.contains(DPText.STATE_DISABLE)) {
				lstCellDisByLock.add(new DPHideControlCell(rowId, columnKey));
			}
			if (existedCellState.get().getState().contains(state))
				return;
			existedCellState.get().addState(state);
		} else {
			List<String> states = new ArrayList<>();
			states.add(state);
			DPCellStateDto dto = new DPCellStateDto("_" + rowId, columnKey, states);
			this.mapCellState.put("_" + rowId + "|" + columnKey, dto);
			if(states.contains(DPText.STATE_DISABLE)) {
				lstCellDisByLock.add(new DPHideControlCell("_" + rowId, columnKey));
			}
		}
	}
	
	/** Set AlarmCell state for Fixed cell */
	public void setCellSate(String rowId, String columnKey, String state, boolean lock) {
		Optional<DPCellStateDto> existedCellState = findExistCellState(rowId, columnKey);
		if (existedCellState.isPresent()) {
			if (existedCellState.get().getState().contains(state))
				return;
			existedCellState.get().addState(state);
		} else {
			List<String> states = new ArrayList<>();
			states.add(state);
			DPCellStateDto dto = new DPCellStateDto("_" + rowId, columnKey, states);
			this.mapCellState.put("_" + rowId + "|" + columnKey, dto);
		}

	}

	public void checkShowTighProcess(int displayMode, boolean userLogin){
		this.showTighProcess = identityProcessDto.isUseIdentityOfMonth() && displayMode == 0 && userLogin && indentityMonthResult.getEnableButton();
		indentityMonthResult.setShow26(indentityMonthResult.getShow26() && identityProcessDto.isUseIdentityOfMonth() && displayMode == 0 && userLogin);
		indentityMonthResult.setHideAll(displayMode != 0 || !identityProcessDto.isUseIdentityOfMonth());
	}
	
	public void resetDailyInit() {
		this.employeeIds = null;
		this.changeEmployeeIds = null;
		this.identityProcessDtoOpt = null;
		this.approvalUseSettingDtoOpt = null;
		this.datePeriodResult = null;
//		this.lstError = null;
		this.disItem = null;
	}
}
