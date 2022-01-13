package nts.uk.screen.at.app.monthlyperformance.correction.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Data;
import nts.uk.ctx.at.record.app.find.monthly.root.common.ClosureDateDto;
import nts.uk.ctx.at.record.app.find.workrecord.operationsetting.FormatPerformanceDto;
import nts.uk.ctx.at.record.app.find.workrecord.operationsetting.IdentityProcessDto;
import nts.uk.ctx.at.record.dom.adapter.company.AffCompanyHistImport;
import nts.uk.ctx.at.record.dom.application.realitystatus.output.UseSetingOutput;
import nts.uk.ctx.at.shared.app.find.scherec.monthlyattditem.MonthlyItemControlByAuthDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.screen.at.app.monthlyperformance.correction.param.MonthlyPerformanceParam;

/**
 * TODO
 */

@Data
public class MonthlyPerformanceCorrectionDto {

	private Set<ItemValue> itemValues;
	private MPControlDisplayItem lstControlDisplayItem;
	private String employmentCode;
	private List<MonthlyPerformanceEmployeeDto> lstEmployee;
	private List<MPDataDto> lstData;
	private Map<String, String > data;
	private List<MPCellStateDto> lstCellState;
	/**
	 * 本人確認処理の利用設定
	 */
	private IdentityProcessDto identityProcess;
	/**
	 * 実績修正画面で利用するフォーマット
	 */
	private FormatPerformanceDto formatPerformance;
	/**
	 * ログイン社員の月別実績の権限を取得する
	 */
	private List<MonthlyPerformanceAuthorityDto> authorityDto;
	/**
	 * list fixed header
	 */
	private List<MPHeaderDto> lstFixedHeader;
	
	/**
	 * コメント
	 */
	private String comment;
	
	/**
	 * 日別の本人確認を表示する
	 */
	private int dailySelfChkDispAtr = 0;

	/**
	 * 処理年月
	 * YYYYMM
	 */
	private int processDate;
	
	/**
	 * 締め名称
	 * 画面項目「A4_2：対象締め日」
	 */
	private String closureName;
	/** Hidden closureId*/
	private Integer closureId;
	/**
	 * ・実績期間：List＜実績期間＞
	 * 画面項目「A4_5：実績期間選択肢」
	 */
	private List<ActualTime> lstActualTimes;
	/**
	 * 期間：取得した期間に一致する
	 * ※一致する期間がない場合は、先頭を選択状態にする
	 */
	private ActualTime selectedActualTime;
	/**
	 * 画面項目の非活制御をする
	 */
	private int actualTimeState;

	/**
	 * パラメータ
	 */
	private MonthlyPerformanceParam param;
	/**
	 * 締め日: 日付
	 */
	private ClosureDateDto closureDate;
	
	private Boolean showRegisterButton;
	
	private MonthlyItemControlByAuthDto authDto;
	/**
	 * A4_8
	 */
	private List<ClosureInfoOuput> lstclosureInfoOuput;
	/**
	 * A4_7
	 */
	private Integer selectedClosure;
	
	private String mess;
	
	private List<MPSateCellHideControl> mPSateCellHideControl = new ArrayList<>();
	
	private UseSetingOutput useSetingOutput;
	
	//history into company
	private List<AffCompanyHistImport> lstAffComHist;
	public MonthlyPerformanceCorrectionDto(){
		super();
		this.lstFixedHeader = MPHeaderDto.GenerateFixedHeader();
		this.lstData = new ArrayList<>();
		this.lstCellState = new ArrayList<>();
		this.lstControlDisplayItem = new MPControlDisplayItem();
		this.itemValues = new HashSet<>();
		this.data = new HashMap<>();
		this.lstAffComHist = new ArrayList<>();
		
	}
	
	public void setLoginUser(String employeeId){
		this.lstEmployee = lstEmployee.stream().map(x -> {
			x.setLoginUser(x.getId().equals(employeeId));
			return x;
		}).collect(Collectors.toList());
	}
	
	private Optional<MPCellStateDto> findExistCellState(String dataId, String columnKey) {
		String rowId, column;
		for (int i = 0; i < this.lstCellState.size(); i++) {
			rowId = this.lstCellState.get(i).getRowId();
			column = this.lstCellState.get(i).getColumnKey();
			if (rowId != null && column != null & rowId.equals( String.valueOf(dataId))
					&& column.equals(String.valueOf(columnKey))) {
				return Optional.of(this.lstCellState.get(i));
			}
		}
		return Optional.empty();
	}
	
	/** Set disable cell & Create not existed cell */
	public void createAccessModifierCellState() {
		//Map<Integer, PAttendanceItem>  pAItemMap = this.param.getLstAtdItemUnique();
		if(this.getAuthDto() == null) return;
		for(MonthlyPerformanceEmployeeDto emp : this.lstEmployee){
			this.getAuthDto().getListDisplayAndInputMonthly().forEach(header -> {
				if (!header.isYouCanChangeIt() && header.isCanBeChangedByOthers()) {
					if (emp.isLoginUser()) {
						setStateCell("A"+header.getItemMonthlyId(), emp.getId(), "mgrid-disable", header.getItemMonthlyId());
					}
				} else if (!header.isCanBeChangedByOthers() && header.isYouCanChangeIt()) {
					if (!emp.isLoginUser()) {
						setStateCell("A"+header.getItemMonthlyId(), emp.getId(), "mgrid-disable", header.getItemMonthlyId());
					}
				} else if (!header.isCanBeChangedByOthers() && !header.isYouCanChangeIt()) {
					setStateCell("A"+header.getItemMonthlyId(), emp.getId(), "mgrid-disable", header.getItemMonthlyId());
				}
			});
		};
	}
	
	public void setStateCell(String columnKey, String rowId, String state){
		Optional<MPCellStateDto> mp = findExistCellState(rowId, columnKey);
		if(mp.isPresent()){
			mp.get().addState(state);
		}else{
			this.lstCellState.add(new MPCellStateDto(rowId, columnKey, Arrays.asList(state)));
		}
	}
	
	public void setStateCell(String columnKey, String rowId, String state, int itemMonthlyId){
		if(MPText.ITEM_CODE_LINK.contains(itemMonthlyId)){
			columnKey = MPText.CODE + itemMonthlyId;
			this.setStateCell(columnKey, rowId, state);
			columnKey = MPText.NAME + itemMonthlyId;
		}
		this.setStateCell(columnKey, rowId, state);
	}
	
	public void setListStateCell(String columnKey, String rowId, List<String> listState){
		Optional<MPCellStateDto> mp = findExistCellState(rowId, columnKey);
		if(mp.isPresent()){
			listState.stream().forEach(state -> mp.get().addState(state));
		}else{
			this.lstCellState.add(new MPCellStateDto(rowId, columnKey, listState));
		}
	}
}
