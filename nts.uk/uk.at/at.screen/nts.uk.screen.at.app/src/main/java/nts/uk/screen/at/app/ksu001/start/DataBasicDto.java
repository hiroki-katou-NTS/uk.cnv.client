package nts.uk.screen.at.app.ksu001.start;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.enums.EnumConstant;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.DateInMonth;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.PersonalCounterCategory;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.WorkplaceCounterCategory;
import nts.uk.screen.at.app.ksu001.getinfoofInitstartup.DataScreenQueryGetInforDto;
import nts.uk.screen.at.app.ksu001.getinfoofInitstartup.DataScreenQueryGetInforDto;
import nts.uk.screen.at.app.ksu001.getinfoofInitstartup.FuncCtrlDisplayFormatDto;
import nts.uk.screen.at.app.ksu001.getinfoofInitstartup.ScheFunctionControlDto;
import nts.uk.screen.at.app.ksu001.getinfoofInitstartup.ScheFunctionCtrlByWorkplaceDto;
import nts.uk.screen.at.app.ksu001.getinfoofInitstartup.ScheModifyAuthCtrlByWorkplaceDto;
import nts.uk.screen.at.app.ksu001.getinfoofInitstartup.ScheModifyAuthCtrlCommonDto;

/**
 * @author laitv
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DataBasicDto {
	public GeneralDate startDate; // ・期間 A3_1_2
	public GeneralDate endDate;   // ・期間 A3_1_4
	public  int unit; //WORKPLACE(0), //WORKPLACE_GROUP(1);
	public  String workplaceId;
	public  String workplaceGroupId;
	public String designation ; //  Aa1_2_2
	public String targetOrganizationName ;   // A2_2	 表示名 
	public String code;
	public GeneralDate scheduleModifyStartDate;  // 修正可能開始日
	public Boolean usePublicAtr; // 公開を利用するか
	public Boolean useWorkAvailabilityAtr; // 勤務希望を利用するか
	// スケジュール修正の機能制御
	public ScheFunctionControlDto scheFunctionControl;
	// スケジュール修正職場別の機能制御
	public ScheFunctionCtrlByWorkplaceDto scheFunctionCtrlByWorkplace;
	// スケジュール修正共通の権限制御
	public List<ScheModifyAuthCtrlCommonDto> scheModifyAuthCtrlCommon;
	// スケジュール修正職場別の権限制御
	public List<ScheModifyAuthCtrlByWorkplaceDto> scheModifyAuthCtrlByWorkplace;
	
	// 職場計.利用カテゴリ一覧
	public List<EnumConstant> useCategoriesWorkplace = Collections.emptyList();
	
	// 個人計.利用カテゴリ一覧
	public List<EnumConstant> useCategoriesPersonal = Collections.emptyList();
	
	// ・締め日 = スケジュール修正職場別の表示設定.初期表示期間の終了日.締め日
	public DateInMonth closeDate;
	
	public boolean medicalOP; // 医療OP
	
	public boolean nursingCareOP; // 介護OP 
	
	public String viewModeSelected;
	
	public DataBasicDto(DataScreenQueryGetInforDto resultStep1) {
		this.startDate = resultStep1.startDate;
		this.endDate = resultStep1.endDate;
		this.unit = resultStep1.targetOrgIdenInfor.unit;
		this.workplaceId = resultStep1.targetOrgIdenInfor.workplaceId;
		this.workplaceGroupId = resultStep1.targetOrgIdenInfor.workplaceGroupId;
		this.designation = resultStep1.displayInforOrganization.getDesignation();
		this.targetOrganizationName = resultStep1.displayInforOrganization.getDisplayName();
		this.code = resultStep1.displayInforOrganization.getCode();
		this.scheduleModifyStartDate = resultStep1.scheduleModifyStartDate;
		this.usePublicAtr = resultStep1.usePublicAtr;
		this.useWorkAvailabilityAtr = resultStep1.useWorkAvailabilityAtr;
		
		this.scheFunctionControl = resultStep1.scheFunctionControl;
		this.scheFunctionCtrlByWorkplace = resultStep1.scheFunctionCtrlByWorkplace;
		this.scheModifyAuthCtrlCommon = resultStep1.scheModifyAuthCtrlCommon;
		this.scheModifyAuthCtrlByWorkplace = resultStep1.scheModifyAuthCtrlByWorkplace;
		this.medicalOP = resultStep1.medicalOP;
		this.nursingCareOP = resultStep1.nursingCareOP;
		
		this.closeDate = resultStep1.closeDate;
		this.useCategoriesPersonal = resultStep1.useCategoriesPersonal;
		this.useCategoriesWorkplace = resultStep1.useCategoriesWorkplace;
	}
	
	public void setViewMode(Integer vMode) {
		if (vMode == FuncCtrlDisplayFormatDto.WorkInfo.value) {
			this.viewModeSelected = "time";
		} else if (vMode == FuncCtrlDisplayFormatDto.AbbreviatedName.value) {
			this.viewModeSelected = "shortName";
		} else if (vMode == FuncCtrlDisplayFormatDto.Shift.value) {
			this.viewModeSelected = "shift";
		}
	}
}
