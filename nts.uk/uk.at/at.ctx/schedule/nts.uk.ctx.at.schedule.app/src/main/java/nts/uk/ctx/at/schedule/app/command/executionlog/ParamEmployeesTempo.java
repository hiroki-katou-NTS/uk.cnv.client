package nts.uk.ctx.at.schedule.app.command.executionlog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.Data;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.dom.adapter.executionlog.dto.ShortWorkTimeDto;
import nts.uk.ctx.at.schedule.dom.executionlog.CreationMethod;
import nts.uk.ctx.at.schedule.dom.executionlog.ImplementAtr;
import nts.uk.ctx.at.schedule.dom.schedule.algorithm.WorkRestTimeZoneDto;
import nts.uk.ctx.at.shared.dom.employeeworkway.EmployeeWorkingStatus;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.affiliationinfor.AffiliationInforOfDailyAttd;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;

/**
 * 勤務予定作成共通処理
 * 社員一日用のパラメータ（Temporary）
 * パラメータ（Temporary）
 * @author phongtq
 *
 */
@Data
public class ParamEmployeesTempo {
	/** 作成方法区分 */
	private CreationMethod createMethodAtr; // command.getContent().getSpecifyCreation().getCreationMethod()
	
	/** 労働条件情報 */
	private Optional<WorkCondItemDto> optWorkingConItem; // masterCache.getListWorkingConItem()
	
	/** 勤務種類 */
	private List<WorkType> lstWorkType; // masterCache.getListWorkType()
	
	/** 固定勤務設定一覧 */
	private Map<String, WorkRestTimeZoneDto> mapFixedWorkSetting = new HashMap<>(); // masterCache.getMapFixedWorkSetting()
	
	/** 実行区分 - 実施区分 */
	private ImplementAtr implementAtr; // command.getContent().getImplementAtr()
	
	/** 対象日 */
	private GeneralDate tartgetDate;
	
	/** 対象開始日 */
	private GeneralDate startDate;
	
	/** 対象終了日 */
	private GeneralDate endDate;
	 
	/** 所属情報 */
	private AffiliationInforOfDailyAttd affiliationInforOfDailyAttd;
	
	/** 時差勤務設定一覧 */
	private Map<String, WorkRestTimeZoneDto> mapDiffTimeWorkSetting = new HashMap<>(); // masterCache.getMapDiffTimeWorkSetting()
	
	/** 流動勤務設定一覧 */
	private Map<String, WorkRestTimeZoneDto> mapFlowWorkSetting = new HashMap<>(); // masterCache.getMapFlowWorkSetting()
	
	/** 社員ID */
	private String empId; // creator.getEmployeeId()
	
	/** 社員の在職状態 */
	private Optional<EmployeeWorkingStatus> optManaStatuTempo; // masterCache.getListManaStatuTempo()
	
	/** 社員の短時間勤務一覧 */
	private Optional<ShortWorkTimeDto> optShortWorkTimeDto; // masterCache.getListShortWorkTimeDto()

	public ParamEmployeesTempo(CreationMethod createMethodAtr, Optional<WorkCondItemDto> optWorkingConItem, List<WorkType> lstWorkType,
			Map<String, WorkRestTimeZoneDto> mapFixedWorkSetting, ImplementAtr implementAtr, GeneralDate startDate,
			GeneralDate endDate, AffiliationInforOfDailyAttd affiliationInforOfDailyAttd,
			Map<String, WorkRestTimeZoneDto> mapDiffTimeWorkSetting,
			Map<String, WorkRestTimeZoneDto> mapFlowWorkSetting, String empId, Optional<EmployeeWorkingStatus> optManaStatuTempo,
			Optional<ShortWorkTimeDto> optShortWorkTimeDto) {
		super();
		this.createMethodAtr = createMethodAtr;
		this.optWorkingConItem =  optWorkingConItem;
		this.lstWorkType = lstWorkType;
		this.mapFixedWorkSetting = mapFixedWorkSetting;
		this.implementAtr = implementAtr;
		this.startDate = startDate;
		this.endDate = endDate;
		this.affiliationInforOfDailyAttd = affiliationInforOfDailyAttd;
		this.mapDiffTimeWorkSetting = mapDiffTimeWorkSetting;
		this.mapFlowWorkSetting = mapFlowWorkSetting;
		this.empId = empId;
		this.optManaStatuTempo = optManaStatuTempo;
		this.optShortWorkTimeDto = optShortWorkTimeDto;
	}

	public ParamEmployeesTempo(CreationMethod createMethodAtr, Optional<WorkCondItemDto> optWorkingConItem,
			List<WorkType> lstWorkType, Map<String, WorkRestTimeZoneDto> mapFixedWorkSetting, ImplementAtr implementAtr,
			GeneralDate tartgetDate, GeneralDate startDate, GeneralDate endDate,
			AffiliationInforOfDailyAttd affiliationInforOfDailyAttd,
			Map<String, WorkRestTimeZoneDto> mapDiffTimeWorkSetting,
			Map<String, WorkRestTimeZoneDto> mapFlowWorkSetting, String empId,
			Optional<EmployeeWorkingStatus> optManaStatuTempo, Optional<ShortWorkTimeDto> optShortWorkTimeDto) {
		super();
		this.createMethodAtr = createMethodAtr;
		this.optWorkingConItem = optWorkingConItem;
		this.lstWorkType = lstWorkType;
		this.mapFixedWorkSetting = mapFixedWorkSetting;
		this.implementAtr = implementAtr;
		this.tartgetDate = tartgetDate;
		this.startDate = startDate;
		this.endDate = endDate;
		this.affiliationInforOfDailyAttd = affiliationInforOfDailyAttd;
		this.mapDiffTimeWorkSetting = mapDiffTimeWorkSetting;
		this.mapFlowWorkSetting = mapFlowWorkSetting;
		this.empId = empId;
		this.optManaStatuTempo = optManaStatuTempo;
		this.optShortWorkTimeDto = optShortWorkTimeDto;
	}
}
