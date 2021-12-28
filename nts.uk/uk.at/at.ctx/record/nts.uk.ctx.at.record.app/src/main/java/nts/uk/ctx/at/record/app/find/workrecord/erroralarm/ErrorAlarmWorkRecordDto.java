/**
 * 4:55:17 PM Jul 21, 2017
 */
package nts.uk.ctx.at.record.app.find.workrecord.erroralarm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.record.app.find.workrecord.erroralarm.condition.AlarmCheckTargetConditionDto;
import nts.uk.ctx.at.record.app.find.workrecord.erroralarm.condition.ErAlAtdItemConditionDto;
import nts.uk.ctx.at.record.app.find.workrecord.erroralarm.condition.WorkTimeConditionDto;
import nts.uk.ctx.at.record.app.find.workrecord.erroralarm.condition.WorkTypeConditionDto;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecord;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.ErrorAlarmCondition;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.attendanceitem.ErAlAttendanceItemCondition;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.worktime.PlanActualWorkTime;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.worktime.SingleWorkTime;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.worktype.PlanActualWorkType;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.worktype.SingleWorkType;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.ConditionAtr;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.ConditionType;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.ErrorAlarmConditionType;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.FilterByCompare;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.AttendanceItemId;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.CheckedAmountValue;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.CheckedTimeDuration;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.CheckedTimesValue;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.CheckedTimesValueDay;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * @author hungnm
 *
 */
@Setter
@Getter
public class ErrorAlarmWorkRecordDto {

	/* 会社ID */
	private String companyId;
	/* コード */
	private String code;
	/* 名称 */
	private String name;
	/* システム固定とする */
	private int fixedAtr;
	/* 使用する */
	private int useAtr;
	
	private int remarkCancelErrorInput;
	
	private int remarkColumnNo;
	/* 区分 */
	private int typeAtr;
	/* 表示メッセージ */
	private String displayMessage;
	/* メッセージを太字にする */
	private int boldAtr;
	/* メッセージの色 */
	private String messageColor;
	/* エラーアラームを解除できる */
	private int cancelableAtr;
	/* エラー表示項目 */
	private Integer errorDisplayItem;
	/* チェック条件 */
	private AlarmCheckTargetConditionDto alCheckTargetCondition;
	/* 勤務種類の条件 */
	private WorkTypeConditionDto workTypeCondition;
	/* 就業時間帯の条件 */
	private WorkTimeConditionDto workTimeCondition;
	private int operatorBetweenPlanActual;
	private List<Integer> lstApplicationTypeCode;
	private int operatorBetweenGroups;
	private int operatorGroup1;
	private int operatorGroup2;
	private boolean group2UseAtr;
	private List<ErAlAtdItemConditionDto> erAlAtdItemConditionGroup1;
	private List<ErAlAtdItemConditionDto> erAlAtdItemConditionGroup2;

	private static ErAlAtdItemConditionDto convertItemDomainToDto(ErAlAttendanceItemCondition<?> itemDomain) {
		ErAlAtdItemConditionDto erAlAtdItemConditionDto = new ErAlAtdItemConditionDto();
		erAlAtdItemConditionDto.setTargetNO(itemDomain.getTargetNO());
		erAlAtdItemConditionDto.setConditionAtr(itemDomain.getConditionAtr().value);
		erAlAtdItemConditionDto.setUseAtr(itemDomain.isUse());
//		erAlAtdItemConditionDto.setConditionType(itemDomain.getType().);
		// Check Target
		// チェック対象
		if (itemDomain.getConditionAtr() == ConditionAtr.TIME_WITH_DAY || itemDomain.getType() == ErrorAlarmConditionType.INPUT_CHECK) {
			erAlAtdItemConditionDto.setUncountableAtdItem(itemDomain.getUncountableTarget().getAttendanceItem());
		} else {
			erAlAtdItemConditionDto.setCountableAddAtdItems(
					itemDomain.getCountableTarget().getAddSubAttendanceItems().getAdditionAttendanceItems());
			erAlAtdItemConditionDto.setCountableSubAtdItems(
					itemDomain.getCountableTarget().getAddSubAttendanceItems().getSubstractionAttendanceItems());
		}
		// Check Condition
		// チェック条件
		if (itemDomain.getCompareRange() != null) {
			switch (itemDomain.getConditionAtr()) {
			case AMOUNT_VALUE:
				erAlAtdItemConditionDto.setCompareStartValue(
						new BigDecimal(((CheckedAmountValue) itemDomain.getCompareRange().getStartValue()).v()));
				erAlAtdItemConditionDto.setCompareEndValue(
						new BigDecimal(((CheckedAmountValue) itemDomain.getCompareRange().getEndValue()).v()));
				break;
			case TIME_DURATION:
				erAlAtdItemConditionDto.setCompareStartValue(
						new BigDecimal(((CheckedTimeDuration) itemDomain.getCompareRange().getStartValue()).v()));
				erAlAtdItemConditionDto.setCompareEndValue(
						new BigDecimal(((CheckedTimeDuration) itemDomain.getCompareRange().getEndValue()).v()));
				break;
			case TIME_WITH_DAY:
				erAlAtdItemConditionDto.setCompareStartValue(
						new BigDecimal(((TimeWithDayAttr) itemDomain.getCompareRange().getStartValue()).v()));
				erAlAtdItemConditionDto.setCompareEndValue(
						new BigDecimal(((TimeWithDayAttr) itemDomain.getCompareRange().getEndValue()).v()));
				break;
			case TIMES:
				erAlAtdItemConditionDto.setCompareStartValue(
						new BigDecimal(((CheckedTimesValue) itemDomain.getCompareRange().getStartValue()).v()));
				erAlAtdItemConditionDto.setCompareEndValue(
						new BigDecimal(((CheckedTimesValue) itemDomain.getCompareRange().getEndValue()).v()));
				break;
			case DAYS:
				erAlAtdItemConditionDto.setCompareStartValue(
						new BigDecimal(((CheckedTimesValueDay) itemDomain.getCompareRange().getStartValue()).v()));
				erAlAtdItemConditionDto.setCompareEndValue(
						new BigDecimal(((CheckedTimesValueDay) itemDomain.getCompareRange().getEndValue()).v()));
				break;
			}
			erAlAtdItemConditionDto.setCompareOperator(itemDomain.getCompareRange().getCompareOperator().value);
		} else if (itemDomain.getCompareSingleValue() != null) {
			if (itemDomain.getCompareSingleValue().getConditionType() == ConditionType.FIXED_VALUE) {
				switch (itemDomain.getConditionAtr()) {
				case AMOUNT_VALUE:
					erAlAtdItemConditionDto.setCompareStartValue(
							new BigDecimal(((CheckedAmountValue) itemDomain.getCompareSingleValue().getValue()).v()));
					break;
				case TIME_DURATION:
					erAlAtdItemConditionDto.setCompareStartValue(
							new BigDecimal(((CheckedTimeDuration) itemDomain.getCompareSingleValue().getValue()).v()));
					break;
				case TIME_WITH_DAY:
					erAlAtdItemConditionDto.setCompareStartValue(
							new BigDecimal(((TimeWithDayAttr) itemDomain.getCompareSingleValue().getValue()).v()));
					break;
				case TIMES:
					erAlAtdItemConditionDto.setCompareStartValue(
							new BigDecimal(((CheckedTimesValue) itemDomain.getCompareSingleValue().getValue()).v()));
					break;
				case DAYS:
					erAlAtdItemConditionDto.setCompareStartValue(
							new BigDecimal(((CheckedTimesValueDay) itemDomain.getCompareSingleValue().getValue()).v()));
					break;
				}
			} else {
				erAlAtdItemConditionDto.setSingleAtdItem(((AttendanceItemId) itemDomain.getCompareSingleValue().getValue()).v());
			}
			erAlAtdItemConditionDto.setConditionType(itemDomain.getCompareSingleValue().getConditionType().value);
			erAlAtdItemConditionDto.setCompareOperator(itemDomain.getCompareSingleValue().getCompareOpertor().value);
		} else if (itemDomain.getInputCheck() != null) {
			erAlAtdItemConditionDto.setInputCheckCondition(itemDomain.getInputCheck().getInputCheckCondition().value);
			erAlAtdItemConditionDto.setConditionType(itemDomain.getType().value);
		}
		return erAlAtdItemConditionDto;
	}

	public ErrorAlarmWorkRecordDto() {
		super();
	}

	public ErrorAlarmWorkRecordDto(String companyId, String code, String name, int fixedAtr, int useAtr,
			int remarkCancelErrorInput, int remarkColumnNo, int typeAtr, String displayMessage, int boldAtr,
			String messageColor, int cancelableAtr, Integer errorDisplayItem, int operatorBetweenPlanActual,
			List<Integer> lstApplicationTypeCode, int operatorBetweenGroups, int operatorGroup1, int operatorGroup2,
			boolean group2UseAtr) {
		super();
		this.companyId = companyId;
		this.code = code;
		this.name = name;
		this.fixedAtr = fixedAtr;
		this.useAtr = useAtr;
		this.remarkCancelErrorInput = remarkCancelErrorInput;
		this.remarkColumnNo = remarkColumnNo;
		this.typeAtr = typeAtr;
		this.displayMessage = displayMessage;
		this.boldAtr = boldAtr;
		this.messageColor = messageColor;
		this.cancelableAtr = cancelableAtr;
		this.errorDisplayItem = errorDisplayItem;
		this.operatorBetweenPlanActual = operatorBetweenPlanActual;
		this.lstApplicationTypeCode = lstApplicationTypeCode;
		this.operatorBetweenGroups = operatorBetweenGroups;
		this.operatorGroup1 = operatorGroup1;
		this.operatorGroup2 = operatorGroup2;
		this.group2UseAtr = group2UseAtr;
	}

	public static ErrorAlarmWorkRecordDto fromDomain(ErrorAlarmWorkRecord domain, ErrorAlarmCondition conditionDomain) {
		// Create to DTO root
		ErrorAlarmWorkRecordDto errorAlarmWorkRecordDto = new ErrorAlarmWorkRecordDto(domain.getCompanyId(),
				domain.getCode().v().substring(1), domain.getName().v(), domain.getFixedAtr() ? 1 : 0, domain.getUseAtr() ? 1 : 0,
				domain.getRemarkCancelErrorInput().value, domain.getRemarkColumnNo(), domain.getTypeAtr().value,
				conditionDomain != null ? conditionDomain.getDisplayMessage().v() : "",
				domain.getMessage().getBoldAtr() ? 1 : 0, domain.getMessage().getMessageColor().v(),
				domain.getCancelableAtr() ? 1 : 0,
				domain.getErrorDisplayItem() != null ? domain.getErrorDisplayItem().intValue() : null, 0,
				domain.getLstApplication(),
				domain.getFixedAtr() ? 0 : conditionDomain.getAtdItemCondition().getOperatorBetweenGroups().value,
				domain.getFixedAtr() ? 0
						: conditionDomain.getAtdItemCondition().getGroup1().getConditionOperator().value,
				domain.getFixedAtr() ? 0
						: conditionDomain.getAtdItemCondition().getGroup2().getConditionOperator().value,
				domain.getFixedAtr() ? false : conditionDomain.getAtdItemCondition().isUseGroup2());
			if (!domain.getFixedAtr()) {
			// Set AlarmCheckTargetConditionDto
			errorAlarmWorkRecordDto.setAlCheckTargetCondition(new AlarmCheckTargetConditionDto(
					conditionDomain.getCheckTargetCondtion().getFilterByBusinessType(),
					conditionDomain.getCheckTargetCondtion().getFilterByJobTitle(),
					conditionDomain.getCheckTargetCondtion().getFilterByEmployment(),
					conditionDomain.getCheckTargetCondtion().getFilterByClassification(),
					conditionDomain.getCheckTargetCondtion().getLstBusinessTypeCode().stream()
							.map(lstCode -> lstCode.v()).collect(Collectors.toList()),
					conditionDomain.getCheckTargetCondtion().getLstJobTitleId(),
					conditionDomain.getCheckTargetCondtion().getLstEmploymentCode().stream()
							.map(lstCode -> lstCode.v()).collect(Collectors.toList()),
					conditionDomain.getCheckTargetCondtion().getLstClassificationCode().stream()
							.map(lstCode -> lstCode.v()).collect(Collectors.toList())));
			
			WorkTypeConditionDto wtypeConditionDto = new WorkTypeConditionDto();
			WorkTimeConditionDto wtimeConditionDto = new WorkTimeConditionDto();
			// Set WorkTypeConditionDto
			if (conditionDomain.getWorkTypeCondition()
					.getComparePlanAndActual() != FilterByCompare.SELECTED) {
				PlanActualWorkType wtypeConditionDomain = (PlanActualWorkType) conditionDomain
						.getWorkTypeCondition();
				wtypeConditionDto.setActualFilterAtr(wtypeConditionDomain.getWorkTypeActual().isUse());
				wtypeConditionDto.setActualLstWorkType(wtypeConditionDomain.getWorkTypeActual().getLstWorkType()
						.stream().map(wtypeCode -> wtypeCode.v()).collect(Collectors.toList()));
				wtypeConditionDto.setUseAtr(wtypeConditionDomain.isUse());
				wtypeConditionDto.setPlanFilterAtr(wtypeConditionDomain.getWorkTypePlan().isUse());
				wtypeConditionDto.setPlanLstWorkType(wtypeConditionDomain.getWorkTypePlan().getLstWorkType().stream()
						.map(wtypeCode -> wtypeCode.v()).collect(Collectors.toList()));
				errorAlarmWorkRecordDto
						.setOperatorBetweenPlanActual(wtypeConditionDomain.getOperatorBetweenPlanActual().value);
			} else {
				SingleWorkType wtypeConditionDomain = (SingleWorkType) conditionDomain
						.getWorkTypeCondition();
				wtypeConditionDto.setUseAtr(wtypeConditionDomain.isUse());
				wtypeConditionDto.setPlanFilterAtr(wtypeConditionDomain.getTargetWorkType().isUse());
				wtypeConditionDto.setPlanLstWorkType(wtypeConditionDomain.getTargetWorkType().getLstWorkType().stream()
						.map(wtypeCode -> wtypeCode.v()).collect(Collectors.toList()));
			}
			wtypeConditionDto.setComparePlanAndActual(
					conditionDomain.getWorkTypeCondition().getComparePlanAndActual().value);
			errorAlarmWorkRecordDto.setWorkTypeCondition(wtypeConditionDto);
			// Set WorkTimeConditionDto
			if (conditionDomain.getWorkTimeCondition()
					.getComparePlanAndActual() != FilterByCompare.SELECTED) {
				PlanActualWorkTime wtimeConditionDomain = (PlanActualWorkTime) conditionDomain
						.getWorkTimeCondition();
				wtimeConditionDto.setActualFilterAtr(wtimeConditionDomain.getWorkTimeActual().isUse());
				wtimeConditionDto.setActualLstWorkTime(wtimeConditionDomain.getWorkTimeActual().getLstWorkTime()
						.stream().map(wtypeCode -> wtypeCode.v()).collect(Collectors.toList()));
				wtimeConditionDto.setUseAtr(wtimeConditionDomain.isUse());
				wtimeConditionDto.setPlanFilterAtr(wtimeConditionDomain.getWorkTimePlan().isUse());
				wtimeConditionDto.setPlanLstWorkTime(wtimeConditionDomain.getWorkTimePlan().getLstWorkTime().stream()
						.map(wtypeCode -> wtypeCode.v()).collect(Collectors.toList()));
			} else {
				SingleWorkTime wtimeConditionDomain = (SingleWorkTime) conditionDomain
						.getWorkTimeCondition();
				wtimeConditionDto.setUseAtr(wtimeConditionDomain.isUse());
				wtimeConditionDto.setPlanFilterAtr(wtimeConditionDomain.getTargetWorkTime().isUse());
				wtimeConditionDto.setPlanLstWorkTime(wtimeConditionDomain.getTargetWorkTime().getLstWorkTime().stream()
						.map(wtypeCode -> wtypeCode.v()).collect(Collectors.toList()));
			}
			wtimeConditionDto.setComparePlanAndActual(
					conditionDomain.getWorkTimeCondition().getComparePlanAndActual().value);
			errorAlarmWorkRecordDto.setWorkTimeCondition(wtimeConditionDto);
			// Set ErAlAtdItemConditionDto
			List<ErAlAtdItemConditionDto> erAlAtdItemConditionGroup1 = new ArrayList<>();
			List<ErAlAtdItemConditionDto> erAlAtdItemConditionGroup2 = new ArrayList<>();
			List<ErAlAttendanceItemCondition<?>> atdItemConditionDomain1 = conditionDomain
					.getAtdItemCondition().getGroup1().getLstErAlAtdItemCon();
			for (ErAlAttendanceItemCondition<?> itemDomain : atdItemConditionDomain1) {
				ErAlAtdItemConditionDto erAlAtdItemConditionDto = convertItemDomainToDto(itemDomain);
				erAlAtdItemConditionGroup1.add(erAlAtdItemConditionDto);
			}
			errorAlarmWorkRecordDto.setErAlAtdItemConditionGroup1(erAlAtdItemConditionGroup1);
			List<ErAlAttendanceItemCondition<?>> atdItemConditionDomain2 = conditionDomain
					.getAtdItemCondition().getGroup2().getLstErAlAtdItemCon();
			for (ErAlAttendanceItemCondition<?> itemDomain : atdItemConditionDomain2) {
				ErAlAtdItemConditionDto erAlAtdItemConditionDto = convertItemDomainToDto(itemDomain);
				erAlAtdItemConditionGroup2.add(erAlAtdItemConditionDto);
			}
			errorAlarmWorkRecordDto.setErAlAtdItemConditionGroup2(erAlAtdItemConditionGroup2);
		} else if(conditionDomain.getCheckTargetCondtion() != null){ // kdw007_ver22
			errorAlarmWorkRecordDto.setAlCheckTargetCondition(new AlarmCheckTargetConditionDto(
					conditionDomain.getCheckTargetCondtion().getFilterByBusinessType(),
					conditionDomain.getCheckTargetCondtion().getFilterByJobTitle(),
					conditionDomain.getCheckTargetCondtion().getFilterByEmployment(),
					conditionDomain.getCheckTargetCondtion().getFilterByClassification(),
					conditionDomain.getCheckTargetCondtion().getLstBusinessTypeCode().stream()
							.map(lstCode -> lstCode.v()).collect(Collectors.toList()),
					conditionDomain.getCheckTargetCondtion().getLstJobTitleId(),
					conditionDomain.getCheckTargetCondtion().getLstEmploymentCode().stream()
							.map(lstCode -> lstCode.v()).collect(Collectors.toList()),
					conditionDomain.getCheckTargetCondtion().getLstClassificationCode().stream()
							.map(lstCode -> lstCode.v()).collect(Collectors.toList())));
		}
		return errorAlarmWorkRecordDto;
	}

}
