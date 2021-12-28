/**
 * 5:09:42 PM Jul 24, 2017
 */
package nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.KrcmtErAlCondition;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.KrcmtEralApplication;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.KrcstErAlApplicationPK;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.KrcmtEralBusinessType;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.KrcstErAlBusinessTypePK;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.KrcmtEralClass;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.KrcstErAlClassPK;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.KrcmtEralEmployment;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.KrcstErAlEmploymentPK;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.KrcmtEralJobTitle;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.KrcstErAlJobTitlePK;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcmtEralstCndgrp;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcmtErAlAtdItemConPK;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcmtEralstCndexprange;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlAtdTargetPK;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlCompareRange;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlCompareRangePK;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlCompareSingle;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlCompareSinglePK;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcmtEralstCndexpiptchk;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlInputCheck;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlInputCheckPK;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlSingleAtd;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlSingleAtdPK;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlSingleFixed;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlSingleFixedPK;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.worktime.KrcmtEralWktmActual;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.worktime.KrcmtEralWktmPlan;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.worktime.KrcstErAlWhPlanActualPK;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.worktype.KrcmtEralWktpActual;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.worktype.KrcmtEralWktpPlan;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.worktype.KrcstErAlWtPlanActualPK;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.TimeWithDayAttr;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * @author hungnm
 *日別実績のエラーアラーム
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KRCMT_ERAL_DAY_SET")
public class KwrmtErAlWorkRecord extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KwrmtErAlWorkRecordPK kwrmtErAlWorkRecordPK;

	@Column(name = "ERROR_ALARM_NAME")
	public String errorAlarmName;

	@Column(name = "FIXED_ATR")
	public boolean fixedAtr;

	@Column(name = "USE_ATR")
	public boolean useAtr;
	
	@Column(name = "REMARK_CANCEL_ERR_INP")
	public boolean remarkCancelErrorInput;
	
	@Column(name = "REMARK_COLUMN_NO")
	public int remarkColumnNo;

	@Column(name = "ERAL_ATR")
	public int typeAtr;

	@Column(name = "BOLD_ATR")
	public boolean boldAtr;

	@Column(name = "MESSAGE_COLOR")
	public String messageColor;

	@Column(name = "CANCELABLE_ATR")
	public boolean cancelableAtr;

	@Column(name = "ERROR_DISPLAY_ITEM")
	public Integer errorDisplayItem;

	@Basic(optional = true)
	@Column(name = "ERAL_CHECK_ID")
	public String eralCheckId;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true, fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "ERAL_CHECK_ID", referencedColumnName = "ERAL_CHECK_ID", insertable = false, updatable = false),
		@JoinColumn(name = "CID", referencedColumnName = "CID", insertable = false, updatable = false)
	})
	public KrcmtErAlCondition krcmtErAlCondition;

	@OneToMany(mappedBy = "kwrmtErAlWorkRecord", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	public List<KrcmtEralApplication> krcstErAlApplication;

	@Column(name = "CANCEL_ROLE_ID")
	public String cancelRoleId;
	
	@Transient
	public KrcmtEralApplication oneKrcstErAlApplication;

	@Override
	protected Object getKey() {
		return this.kwrmtErAlWorkRecordPK;
	}

	public KwrmtErAlWorkRecord(KwrmtErAlWorkRecordPK kwrmtErAlWorkRecordPK, String errorAlarmName, boolean fixedAtr,
			boolean useAtr, boolean remarkCancelErrorInput, int remarkColumnNo, int typeAtr, boolean boldAtr, String messageColor,
			boolean cancelableAtr, Integer errorDisplayItem, String eralCheckId, KrcmtErAlCondition krcmtErAlCondition,
			List<KrcmtEralApplication> krcstErAlApplication, String cancelRoleId) {
		super();
		this.kwrmtErAlWorkRecordPK = kwrmtErAlWorkRecordPK;
		this.errorAlarmName = errorAlarmName;
		this.fixedAtr = fixedAtr;
		this.useAtr = useAtr;
		this.remarkCancelErrorInput = remarkCancelErrorInput;
		this.remarkColumnNo = remarkColumnNo;
		this.typeAtr = typeAtr;
		this.boldAtr = boldAtr;
		this.messageColor = messageColor;
		this.cancelableAtr = cancelableAtr;
		this.errorDisplayItem = errorDisplayItem;
		this.eralCheckId = eralCheckId;
		this.krcmtErAlCondition = krcmtErAlCondition;
		this.krcstErAlApplication = krcstErAlApplication;
		this.cancelRoleId = cancelRoleId;
	}

	@SuppressWarnings("unchecked")
	private static <V> ErAlAttendanceItemCondition<V> convertKrcmtErAlAtdItemConToDomain(String comId, String eralCode,
			KrcmtEralstCndgrp atdItemCon) {
		ErAlAttendanceItemCondition<V> atdItemConDomain = new ErAlAttendanceItemCondition<V>(comId, eralCode,
				atdItemCon.krcmtErAlAtdItemConPK.atdItemConNo, atdItemCon.conditionAtr, atdItemCon.useAtr == 1,
				atdItemCon.type);
		// Set Target
		if (atdItemCon.conditionAtr == ConditionAtr.TIME_WITH_DAY.value || atdItemCon.type == ErrorAlarmConditionType.INPUT_CHECK.value) {
			atdItemConDomain.setUncountableTarget(Optional.ofNullable(atdItemCon.lstAtdItemTarget)
					.orElse(Collections.emptyList()).stream().findFirst().get().krcstErAlAtdTargetPK.attendanceItemId);
		} else {
			atdItemConDomain.setCountableTarget(
					Optional.ofNullable(atdItemCon.lstAtdItemTarget).orElse(Collections.emptyList()).stream()
							.filter(atdItemTarget -> atdItemTarget.targetAtr == 0)
							.map(addItem -> addItem.krcstErAlAtdTargetPK.attendanceItemId).collect(Collectors.toList()),
					Optional.ofNullable(atdItemCon.lstAtdItemTarget).orElse(Collections.emptyList()).stream()
							.filter(atdItemTarget -> atdItemTarget.targetAtr == 1)
							.map(addItem -> addItem.krcstErAlAtdTargetPK.attendanceItemId)
							.collect(Collectors.toList()));
		}
		// Set Compare
		if (atdItemCon.erAlCompareRange != null) {
			if (atdItemCon.conditionAtr == ConditionAtr.AMOUNT_VALUE.value) {
				atdItemConDomain.setCompareRange(atdItemCon.erAlCompareRange.compareAtr,
						(V) new CheckedAmountValue((int)atdItemCon.erAlCompareRange.startValue),
						(V) new CheckedAmountValue((int)atdItemCon.erAlCompareRange.endValue));
			} else if (atdItemCon.conditionAtr == ConditionAtr.TIME_DURATION.value) {
				atdItemConDomain.setCompareRange(atdItemCon.erAlCompareRange.compareAtr,
						(V) new CheckedTimeDuration((int)atdItemCon.erAlCompareRange.startValue),
						(V) new CheckedTimeDuration((int)atdItemCon.erAlCompareRange.endValue));
			} else if (atdItemCon.conditionAtr == ConditionAtr.TIME_WITH_DAY.value) {
				atdItemConDomain.setCompareRange(atdItemCon.erAlCompareRange.compareAtr,
						(V) new TimeWithDayAttr((int)atdItemCon.erAlCompareRange.startValue),
						(V) new TimeWithDayAttr((int)atdItemCon.erAlCompareRange.endValue));
			} else if (atdItemCon.conditionAtr == ConditionAtr.TIMES.value) {
				atdItemConDomain.setCompareRange(atdItemCon.erAlCompareRange.compareAtr,
						(V) new CheckedTimesValue((int)atdItemCon.erAlCompareRange.startValue),
						(V) new CheckedTimesValue((int)atdItemCon.erAlCompareRange.endValue));
			} else if (atdItemCon.conditionAtr == ConditionAtr.DAYS.value) {
				atdItemConDomain.setCompareRange(atdItemCon.erAlCompareRange.compareAtr,
						(V) new CheckedTimesValueDay(atdItemCon.erAlCompareRange.startValue),
						(V) new CheckedTimesValueDay(atdItemCon.erAlCompareRange.endValue));
			}
		} else if (atdItemCon.erAlCompareSingle != null) {
			if (atdItemCon.erAlCompareSingle.conditionType == ConditionType.FIXED_VALUE.value) {
				if (atdItemCon.conditionAtr == ConditionAtr.AMOUNT_VALUE.value) {
					atdItemConDomain.setCompareSingleValue(atdItemCon.erAlCompareSingle.compareAtr,
							atdItemCon.erAlCompareSingle.conditionType,
							(V) new CheckedAmountValue((int)atdItemCon.erAlSingleFixed.fixedValue));
				} else if (atdItemCon.conditionAtr == ConditionAtr.TIME_DURATION.value) {
					atdItemConDomain.setCompareSingleValue(atdItemCon.erAlCompareSingle.compareAtr,
							atdItemCon.erAlCompareSingle.conditionType,
							(V) new CheckedTimeDuration((int)atdItemCon.erAlSingleFixed.fixedValue));
				} else if (atdItemCon.conditionAtr == ConditionAtr.TIME_WITH_DAY.value) {
					atdItemConDomain.setCompareSingleValue(atdItemCon.erAlCompareSingle.compareAtr,
							atdItemCon.erAlCompareSingle.conditionType,
							(V) new TimeWithDayAttr((int)atdItemCon.erAlSingleFixed.fixedValue));
				} else if (atdItemCon.conditionAtr == ConditionAtr.TIMES.value) {
					atdItemConDomain.setCompareSingleValue(atdItemCon.erAlCompareSingle.compareAtr,
							atdItemCon.erAlCompareSingle.conditionType,
							(V) new CheckedTimesValue((int)atdItemCon.erAlSingleFixed.fixedValue));
				} else if (atdItemCon.conditionAtr == ConditionAtr.DAYS.value) {
					atdItemConDomain.setCompareSingleValue(atdItemCon.erAlCompareSingle.compareAtr,
							atdItemCon.erAlCompareSingle.conditionType,
							(V) new CheckedTimesValueDay(atdItemCon.erAlSingleFixed.fixedValue));
				}
			} else {
				atdItemConDomain.setCompareSingleValue(atdItemCon.erAlCompareSingle.compareAtr,
						atdItemCon.erAlCompareSingle.conditionType, (V) new AttendanceItemId(
								atdItemCon.erAlSingleAtd.get(0).krcstEralSingleAtdPK.attendanceItemId));
			}
		} else if (atdItemCon.erAlInputCheck != null) {
			atdItemConDomain.setInputCheck(atdItemCon.erAlInputCheck.inputCheckCondition);
		}
		return atdItemConDomain;
	}

	private static KrcmtEralstCndgrp getKrcmtErAlAtdItemConFromDomain(String atdItemConditionGroup1,
			ErAlAttendanceItemCondition<?> erAlAtdItemCon) {
		KrcmtErAlAtdItemConPK krcmtErAlAtdItemConPK = new KrcmtErAlAtdItemConPK(atdItemConditionGroup1,
				(erAlAtdItemCon.getTargetNO()));
		List<KrcmtEralstCndexprange> lstAtdItemTarget = new ArrayList<>();
		if (erAlAtdItemCon.getConditionAtr() == ConditionAtr.TIME_WITH_DAY || erAlAtdItemCon.getType() == ErrorAlarmConditionType.INPUT_CHECK) {
			lstAtdItemTarget.add(new KrcmtEralstCndexprange(new KrcstErAlAtdTargetPK(atdItemConditionGroup1,
					(erAlAtdItemCon.getTargetNO()), (erAlAtdItemCon.getUncountableTarget().getAttendanceItem())), erAlAtdItemCon.getConditionAtr().value));
		} else {
			List<KrcmtEralstCndexprange> lstAtdItemTargetAdd = erAlAtdItemCon.getCountableTarget()
					.getAddSubAttendanceItems().getAdditionAttendanceItems().stream()
					.map(atdItemId -> new KrcmtEralstCndexprange(new KrcstErAlAtdTargetPK(atdItemConditionGroup1,
							(erAlAtdItemCon.getTargetNO()), (atdItemId)), 0))
					.collect(Collectors.toList());
			List<KrcmtEralstCndexprange> lstAtdItemTargetSub = erAlAtdItemCon.getCountableTarget()
					.getAddSubAttendanceItems().getSubstractionAttendanceItems().stream()
					.map(atdItemId -> new KrcmtEralstCndexprange(new KrcstErAlAtdTargetPK(atdItemConditionGroup1,
							(erAlAtdItemCon.getTargetNO()), (atdItemId)), 1))
					.collect(Collectors.toList());
			lstAtdItemTarget.addAll(lstAtdItemTargetAdd);
			lstAtdItemTarget.addAll(lstAtdItemTargetSub);
		}
		int compareAtr = 0;
		int conditionType = 0;
		double startValue = 0;
		double endValue = 0;
		KrcstErAlCompareSingle erAlCompareSingle = null;
		KrcstErAlCompareRange erAlCompareRange = null;
		KrcstErAlInputCheck erAlInputCheck = null;
		KrcstErAlSingleFixed erAlSingleFixed = null;
		List<KrcstErAlSingleAtd> erAlSingleAtd = new ArrayList<>();
		if (erAlAtdItemCon.getCompareRange() != null) {
			compareAtr = erAlAtdItemCon.getCompareRange().getCompareOperator().value;
			if (erAlAtdItemCon.getConditionAtr() == ConditionAtr.AMOUNT_VALUE) {
				startValue = ((CheckedAmountValue) erAlAtdItemCon.getCompareRange().getStartValue()).v();
				endValue = ((CheckedAmountValue) erAlAtdItemCon.getCompareRange().getEndValue()).v();
			} else if (erAlAtdItemCon.getConditionAtr() == ConditionAtr.TIME_DURATION) {
				startValue = ((CheckedTimeDuration) erAlAtdItemCon.getCompareRange().getStartValue()).v();
				endValue = ((CheckedTimeDuration) erAlAtdItemCon.getCompareRange().getEndValue()).v();
			} else if (erAlAtdItemCon.getConditionAtr() == ConditionAtr.TIME_WITH_DAY) {
				startValue = ((TimeWithDayAttr) erAlAtdItemCon.getCompareRange().getStartValue()).v();
				endValue = ((TimeWithDayAttr) erAlAtdItemCon.getCompareRange().getEndValue()).v();
			} else if (erAlAtdItemCon.getConditionAtr() == ConditionAtr.TIMES) {
				startValue = ((CheckedTimesValue) erAlAtdItemCon.getCompareRange().getStartValue()).v();
				endValue = ((CheckedTimesValue) erAlAtdItemCon.getCompareRange().getEndValue()).v();
			} else if (erAlAtdItemCon.getConditionAtr() == ConditionAtr.DAYS) {
				startValue = ((CheckedTimesValueDay) erAlAtdItemCon.getCompareRange().getStartValue()).v();
				endValue = ((CheckedTimesValueDay) erAlAtdItemCon.getCompareRange().getEndValue()).v();
			}
			erAlCompareRange = new KrcstErAlCompareRange(
					new KrcstErAlCompareRangePK(atdItemConditionGroup1, (erAlAtdItemCon.getTargetNO())), compareAtr,
					startValue, endValue);
		} else if (erAlAtdItemCon.getCompareSingleValue() != null) {
			compareAtr = erAlAtdItemCon.getCompareSingleValue().getCompareOpertor().value;
			conditionType = erAlAtdItemCon.getCompareSingleValue().getConditionType().value;
			erAlCompareSingle = new KrcstErAlCompareSingle(
					new KrcstErAlCompareSinglePK(atdItemConditionGroup1, (erAlAtdItemCon.getTargetNO())), compareAtr,
					conditionType);
			if (erAlAtdItemCon.getCompareSingleValue().getConditionType() == ConditionType.FIXED_VALUE) {
				double fixedValue = 0;
				if (erAlAtdItemCon.getConditionAtr() == ConditionAtr.AMOUNT_VALUE) {
					fixedValue = ((CheckedAmountValue) erAlAtdItemCon.getCompareSingleValue().getValue()).v();
				} else if (erAlAtdItemCon.getConditionAtr() == ConditionAtr.TIME_DURATION) {
					fixedValue = ((CheckedTimeDuration) erAlAtdItemCon.getCompareSingleValue().getValue()).v();
				} else if (erAlAtdItemCon.getConditionAtr() == ConditionAtr.TIME_WITH_DAY) {
					fixedValue = ((TimeWithDayAttr) erAlAtdItemCon.getCompareSingleValue().getValue()).v();
				} else if (erAlAtdItemCon.getConditionAtr() == ConditionAtr.TIMES) {
					fixedValue = ((CheckedTimesValue) erAlAtdItemCon.getCompareSingleValue().getValue()).v();
				} else if (erAlAtdItemCon.getConditionAtr() == ConditionAtr.DAYS) {
					fixedValue = ((CheckedTimesValueDay) erAlAtdItemCon.getCompareSingleValue().getValue()).v();
				}
				erAlSingleFixed = new KrcstErAlSingleFixed(
						new KrcstErAlSingleFixedPK(atdItemConditionGroup1, erAlAtdItemCon.getTargetNO()), fixedValue);
			} else {
				erAlSingleAtd
						.add(new KrcstErAlSingleAtd(
								new KrcstErAlSingleAtdPK(atdItemConditionGroup1, erAlAtdItemCon.getTargetNO(),
										((AttendanceItemId) erAlAtdItemCon.getCompareSingleValue().getValue()).v()),
								2));
			}
		} else if (erAlAtdItemCon.getInputCheck() != null) {
			erAlInputCheck = new KrcstErAlInputCheck(
					new KrcstErAlInputCheckPK(atdItemConditionGroup1, erAlAtdItemCon.getTargetNO()),
					erAlAtdItemCon.getInputCheck().getInputCheckCondition().value);
		}
		return new KrcmtEralstCndgrp(krcmtErAlAtdItemConPK, erAlAtdItemCon.getConditionAtr().value,
				erAlAtdItemCon.isUse() ? 1 : 0, erAlAtdItemCon.getType().value, lstAtdItemTarget, erAlCompareSingle,
				erAlCompareRange, erAlInputCheck, erAlSingleFixed, erAlSingleAtd);
	}

	public static KwrmtErAlWorkRecord fromDomain(ErrorAlarmWorkRecord domain, ErrorAlarmCondition conditionDomain) {
		// Set PK
		KwrmtErAlWorkRecordPK kwrmtErAlWorkRecordPK = new KwrmtErAlWorkRecordPK(AppContexts.user().companyId(),
				domain.getCode().v());
		// Set main data KwrmtErAlWorkRecord
		String errorAlarmName = domain.getName().v();
		boolean fixedAtr = domain.getFixedAtr();
		boolean useAtr = domain.getUseAtr();
		int typeAtr = domain.getTypeAtr().value;
		boolean boldAtr = domain.getMessage().getBoldAtr();
		String messageColor = domain.getMessage().getMessageColor().v();
		boolean cancelableAtr = domain.getCancelableAtr();
		Integer errorDisplayItem = domain.getErrorDisplayItem();
		String eralCheckId = domain.getErrorAlarmCheckID();
		List<KrcmtEralApplication> krcstErAlApplication = domain.getLstApplication().stream()
				.map(appTypeCd -> new KrcmtEralApplication(
						new KrcstErAlApplicationPK(AppContexts.user().companyId(), domain.getCode().v(), (appTypeCd))))
				.collect(Collectors.toList());
		String cancelRoleId = domain.getCancelRoleId();
		String messageDisplay = conditionDomain.getDisplayMessage().v();
		KrcmtErAlCondition krcmtErAlCondition = new KrcmtErAlCondition(eralCheckId,domain.getCompanyId(), messageDisplay, (0),
				Collections.emptyList(), (0), Collections.emptyList(), (0), Collections.emptyList(), (0),
				Collections.emptyList(), (0), (0), null, null, null, Collections.emptyList(), Collections.emptyList(),
				(0), (0), null, null, null, Collections.emptyList(), Collections.emptyList(), (0), (0), "0", null, null,
				null, 0);
			// Set Check target condition
			int filterByBusinessType = conditionDomain.getCheckTargetCondtion().getFilterByBusinessType() ? (1) : (0);
			int filterByJobTitle = (conditionDomain.getCheckTargetCondtion().getFilterByJobTitle() ? 1 : 0);
			int filterByEmployment = (conditionDomain.getCheckTargetCondtion().getFilterByEmployment() ? 1 : 0);
			int filterByClassification = (conditionDomain.getCheckTargetCondtion().getFilterByClassification() ? 1 : 0);
			List<KrcmtEralBusinessType> lstBusinessType = conditionDomain.getCheckTargetCondtion()
					.getLstBusinessTypeCode().stream().map(businessTypeCd -> new KrcmtEralBusinessType(
							new KrcstErAlBusinessTypePK(eralCheckId, businessTypeCd.v(),domain.getCompanyId())))
					.collect(Collectors.toList());
			List<KrcmtEralJobTitle> lstJobTitle = conditionDomain.getCheckTargetCondtion().getLstJobTitleId().stream()
					.map(jobTitleId -> new KrcmtEralJobTitle(new KrcstErAlJobTitlePK(eralCheckId, jobTitleId,domain.getCompanyId())))
					.collect(Collectors.toList());
			List<KrcmtEralEmployment> lstEmployment = conditionDomain.getCheckTargetCondtion().getLstEmploymentCode()
					.stream().map(emptCd -> new KrcmtEralEmployment(new KrcstErAlEmploymentPK(eralCheckId, emptCd.v(),domain.getCompanyId())))
					.collect(Collectors.toList());
			List<KrcmtEralClass> lstClassification = conditionDomain.getCheckTargetCondtion().getLstClassificationCode()
					.stream().map(clssCd -> new KrcmtEralClass(new KrcstErAlClassPK(eralCheckId, clssCd.v(),domain.getCompanyId())))
					.collect(Collectors.toList());
		if (!domain.getFixedAtr()) {
			// Set worktype condition
			int workTypeUseAtr = conditionDomain.getWorkTypeCondition().isUse() ? 1 : 0;
			int wtCompareAtr = conditionDomain.getWorkTypeCondition().getComparePlanAndActual().value;
			int wtPlanActualOperator = 0;
			int wtPlanFilterAtr = 0;
			int wtActualFilterAtr = 0;
			List<KrcmtEralWktpPlan> lstWtPlan = new ArrayList<>();
			List<KrcmtEralWktpActual> lstWtActual = new ArrayList<>();
			if (wtCompareAtr != FilterByCompare.SELECTED.value) {
				PlanActualWorkType wtypeCondition = (PlanActualWorkType) conditionDomain.getWorkTypeCondition();
				wtPlanActualOperator = wtypeCondition.getOperatorBetweenPlanActual().value;
				wtPlanFilterAtr = wtypeCondition.getWorkTypePlan().isUse() ? 1 : 0;
				wtActualFilterAtr = wtypeCondition.getWorkTypeActual().isUse() ? 1 : 0;
				lstWtPlan = wtypeCondition.getWorkTypePlan().getLstWorkType().stream()
						.map(wtCode -> new KrcmtEralWktpPlan(new KrcstErAlWtPlanActualPK(eralCheckId, wtCode.v(),domain.getCompanyId())))
						.collect(Collectors.toList());
				lstWtActual = wtypeCondition.getWorkTypeActual().getLstWorkType().stream()
						.map(wtCode -> new KrcmtEralWktpActual(new KrcstErAlWtPlanActualPK(eralCheckId, wtCode.v(),domain.getCompanyId())))
						.collect(Collectors.toList());
			} else {
				SingleWorkType wtypeCondition = (SingleWorkType) conditionDomain.getWorkTypeCondition();
				wtPlanFilterAtr = wtypeCondition.getTargetWorkType().isUse() ? 1 : 0;
				lstWtPlan = wtypeCondition.getTargetWorkType().getLstWorkType().stream()
						.map(wtCode -> new KrcmtEralWktpPlan(new KrcstErAlWtPlanActualPK(eralCheckId, wtCode.v(),domain.getCompanyId())))
						.collect(Collectors.toList());
			}
			// Set worktime condition
			int workingHoursUseAtr = conditionDomain.getWorkTimeCondition().isUse() ? 1 : 0;
			int whCompareAtr = conditionDomain.getWorkTimeCondition().getComparePlanAndActual().value;
			int whPlanActualOperator = 0;
			int whPlanFilterAtr = 0;
			int whActualFilterAtr = 0;
			List<KrcmtEralWktmPlan> lstWhPlan = new ArrayList<>();
			List<KrcmtEralWktmActual> lstWhActual = new ArrayList<>();
			if (whCompareAtr != FilterByCompare.SELECTED.value) {
				PlanActualWorkTime wtimeCondition = (PlanActualWorkTime) conditionDomain.getWorkTimeCondition();
				whPlanActualOperator = wtimeCondition.getOperatorBetweenPlanActual().value;
				whPlanFilterAtr = wtimeCondition.getWorkTimePlan().isUse() ? 1 : 0;
				whActualFilterAtr = wtimeCondition.getWorkTimeActual().isUse() ? 1 : 0;
				lstWhPlan = wtimeCondition.getWorkTimePlan().getLstWorkTime().stream()
						.map(wtCode -> new KrcmtEralWktmPlan(new KrcstErAlWhPlanActualPK(eralCheckId, wtCode.v(),domain.getCompanyId())))
						.collect(Collectors.toList());
				lstWhActual = wtimeCondition.getWorkTimeActual().getLstWorkTime().stream()
						.map(wtCode -> new KrcmtEralWktmActual(new KrcstErAlWhPlanActualPK(eralCheckId, wtCode.v(),domain.getCompanyId())))
						.collect(Collectors.toList());
			} else {
				SingleWorkTime wtimeCondition = (SingleWorkTime) conditionDomain.getWorkTimeCondition();
				whPlanFilterAtr = wtimeCondition.getTargetWorkTime().isUse() ? 1 : 0;
				lstWhPlan = wtimeCondition.getTargetWorkTime().getLstWorkTime().stream()
						.map(wtCode -> new KrcmtEralWktmPlan(new KrcstErAlWhPlanActualPK(eralCheckId, wtCode.v(),domain.getCompanyId())))
						.collect(Collectors.toList());
			}
			// Set attendance item condition
			int operatorBetweenGroups = conditionDomain.getAtdItemCondition().getOperatorBetweenGroups().value;
			int group2UseAtr = conditionDomain.getAtdItemCondition().isUseGroup2() ? 1 : 0;
			String atdItemConditionGroup1 = conditionDomain.getAtdItemCondition().getGroup1().getAtdItemConGroupId();
			String atdItemConditionGroup2 = conditionDomain.getAtdItemCondition().getGroup2().getAtdItemConGroupId();
			int conditionOperator1 = conditionDomain.getAtdItemCondition().getGroup1().getConditionOperator().value;
			List<KrcmtEralstCndgrp> lstAtdItemCon1 = conditionDomain.getAtdItemCondition().getGroup1()
					.getLstErAlAtdItemCon().stream()
					.map(erAlAtdItemCon -> getKrcmtErAlAtdItemConFromDomain(atdItemConditionGroup1, erAlAtdItemCon))
					.collect(Collectors.toList());
			KrcmtEralstCndexpiptchk krcstErAlConGroup1 = new KrcmtEralstCndexpiptchk(atdItemConditionGroup1, conditionOperator1,
					lstAtdItemCon1);
			int conditionOperator2 = conditionDomain.getAtdItemCondition().getGroup2().getConditionOperator().value;
			List<KrcmtEralstCndgrp> lstAtdItemCon2 = conditionDomain.getAtdItemCondition().getGroup2()
					.getLstErAlAtdItemCon().stream()
					.map(erAlAtdItemCon -> getKrcmtErAlAtdItemConFromDomain(atdItemConditionGroup2, erAlAtdItemCon))
					.collect(Collectors.toList());
			KrcmtEralstCndexpiptchk krcstErAlConGroup2 = new KrcmtEralstCndexpiptchk(atdItemConditionGroup2, conditionOperator2,
					lstAtdItemCon2);
			krcmtErAlCondition = new KrcmtErAlCondition(eralCheckId,domain.getCompanyId(), messageDisplay, filterByBusinessType,
					lstBusinessType, filterByJobTitle, lstJobTitle, filterByEmployment, lstEmployment,
					filterByClassification, lstClassification, workTypeUseAtr, wtPlanActualOperator, wtPlanFilterAtr,
					wtActualFilterAtr, wtCompareAtr, lstWtActual, lstWtPlan, workingHoursUseAtr, whPlanActualOperator,
					whPlanFilterAtr, whActualFilterAtr, whCompareAtr, lstWhActual, lstWhPlan, operatorBetweenGroups,
					group2UseAtr, atdItemConditionGroup1, krcstErAlConGroup1, atdItemConditionGroup2,
					krcstErAlConGroup2,
					conditionDomain.getContinuousPeriod() != null ? conditionDomain.getContinuousPeriod().v() : 0);
		} 

		KwrmtErAlWorkRecord entity = new KwrmtErAlWorkRecord(kwrmtErAlWorkRecordPK, errorAlarmName, fixedAtr, useAtr,
				domain.getRemarkCancelErrorInput().isUse(), domain.getRemarkColumnNo(), typeAtr, boldAtr,
				messageColor.equals("") ? null : messageColor, cancelableAtr, errorDisplayItem, eralCheckId,
				krcmtErAlCondition, krcstErAlApplication, cancelRoleId);
		return entity;
	}
	public static KwrmtErAlWorkRecord fromDomainFixed(ErrorAlarmWorkRecord domain, ErrorAlarmCondition conditionDomain) {
		// Set PK
		KwrmtErAlWorkRecordPK kwrmtErAlWorkRecordPK = new KwrmtErAlWorkRecordPK(AppContexts.user().companyId(),
				domain.getCode().v());
		// Set main data KwrmtErAlWorkRecord
		String errorAlarmName = domain.getName().v();
		boolean fixedAtr = domain.getFixedAtr();
		boolean useAtr = domain.getUseAtr();
		int typeAtr = domain.getTypeAtr().value;
		boolean boldAtr = domain.getMessage().getBoldAtr();
		String messageColor = domain.getMessage().getMessageColor().v();
		boolean cancelableAtr = domain.getCancelableAtr();
		Integer errorDisplayItem = domain.getErrorDisplayItem();
		String eralCheckId = domain.getErrorAlarmCheckID();
		List<KrcmtEralApplication> krcstErAlApplication = domain.getLstApplication().stream()
				.map(appTypeCd -> new KrcmtEralApplication(
						new KrcstErAlApplicationPK(AppContexts.user().companyId(), domain.getCode().v(), (appTypeCd))))
				.collect(Collectors.toList());
		String cancelRoleId = domain.getCancelRoleId();
		String messageDisplay = conditionDomain.getDisplayMessage().v();
		KrcmtErAlCondition krcmtErAlCondition = new KrcmtErAlCondition(eralCheckId,domain.getCompanyId(), messageDisplay, (0),
				Collections.emptyList(), (0), Collections.emptyList(), (0), Collections.emptyList(), (0),
				Collections.emptyList(), (0), (0), null, null, null, Collections.emptyList(), Collections.emptyList(),
				(0), (0), null, null, null, Collections.emptyList(), Collections.emptyList(), (0), (0), "0", null, null,
				null, 0);
		if (domain.getFixedAtr()) { // kdw007 ver22
			// Set Check target condition
			int filterByBusinessType = conditionDomain.getCheckTargetCondtion().getFilterByBusinessType() ? (1) : (0);
			int filterByJobTitle = (conditionDomain.getCheckTargetCondtion().getFilterByJobTitle() ? 1 : 0);
			int filterByEmployment = (conditionDomain.getCheckTargetCondtion().getFilterByEmployment() ? 1 : 0);
			int filterByClassification = (conditionDomain.getCheckTargetCondtion().getFilterByClassification() ? 1 : 0);
			List<KrcmtEralBusinessType> lstBusinessType = conditionDomain.getCheckTargetCondtion()
					.getLstBusinessTypeCode().stream().map(businessTypeCd -> new KrcmtEralBusinessType(
							new KrcstErAlBusinessTypePK(eralCheckId, businessTypeCd.v(),domain.getCompanyId())))
					.collect(Collectors.toList());
			List<KrcmtEralJobTitle> lstJobTitle = conditionDomain.getCheckTargetCondtion().getLstJobTitleId().stream()
					.map(jobTitleId -> new KrcmtEralJobTitle(new KrcstErAlJobTitlePK(eralCheckId, jobTitleId,domain.getCompanyId())))
					.collect(Collectors.toList());
			List<KrcmtEralEmployment> lstEmployment = conditionDomain.getCheckTargetCondtion().getLstEmploymentCode()
					.stream().map(emptCd -> new KrcmtEralEmployment(new KrcstErAlEmploymentPK(eralCheckId, emptCd.v(),domain.getCompanyId())))
					.collect(Collectors.toList());
			List<KrcmtEralClass> lstClassification = conditionDomain.getCheckTargetCondtion().getLstClassificationCode()
					.stream().map(clssCd -> new KrcmtEralClass(new KrcstErAlClassPK(eralCheckId, clssCd.v(),domain.getCompanyId())))
					.collect(Collectors.toList());
			krcmtErAlCondition = new KrcmtErAlCondition(eralCheckId,domain.getCompanyId(), messageDisplay, filterByBusinessType,
					lstBusinessType, filterByJobTitle, lstJobTitle, filterByEmployment, lstEmployment,
					filterByClassification, lstClassification,(0), (0), null, null, null, Collections.emptyList(), Collections.emptyList(),
					(0), (0), null, null, null, Collections.emptyList(), Collections.emptyList(), (0), (0), "0", null, null,
					null, 0);		
		}
		
		KwrmtErAlWorkRecord entity = new KwrmtErAlWorkRecord(kwrmtErAlWorkRecordPK, errorAlarmName, fixedAtr, useAtr,
				domain.getRemarkCancelErrorInput().isUse(), domain.getRemarkColumnNo(), typeAtr, boldAtr,
				messageColor.equals("") ? null : messageColor, cancelableAtr, errorDisplayItem, eralCheckId,
				krcmtErAlCondition, krcstErAlApplication, cancelRoleId);
		return entity;
	}

	public static ErrorAlarmWorkRecord toDomain(KwrmtErAlWorkRecord entity) {
		return toDomain(entity, entity.krcstErAlApplication);
	}
	
	public static ErrorAlarmWorkRecord toDomain(KwrmtErAlWorkRecord entity, List<KrcmtEralApplication> erAlApp) {
		return ErrorAlarmWorkRecord.createFromJavaType(entity.kwrmtErAlWorkRecordPK.companyId,
				entity.kwrmtErAlWorkRecordPK.errorAlarmCode, entity.errorAlarmName, entity.fixedAtr,
				entity.useAtr, entity.remarkCancelErrorInput, entity.remarkColumnNo, entity.typeAtr,
				entity.boldAtr, entity.messageColor, entity.cancelableAtr, entity.errorDisplayItem,
				Optional.ofNullable(erAlApp).orElse(Collections.emptyList()).stream()
						.map(eralAppEntity -> eralAppEntity.krcstErAlApplicationPK.appTypeCd)
						.collect(Collectors.toList()),
				entity.eralCheckId);
	}

	//fix for response
	public static ErrorAlarmWorkRecord toDomainForRes(List<KwrmtErAlWorkRecord> entity) {
		return convertToDomainForRes(entity.get(0), entity);
	}
	
	//fix for response
	public static ErrorAlarmWorkRecord convertToDomainForRes(KwrmtErAlWorkRecord entity, List<KwrmtErAlWorkRecord> erAlApp) {
		return ErrorAlarmWorkRecord.createFromJavaType(entity.kwrmtErAlWorkRecordPK.companyId,
				entity.kwrmtErAlWorkRecordPK.errorAlarmCode, entity.errorAlarmName, entity.fixedAtr,
				entity.useAtr, entity.remarkCancelErrorInput, entity.remarkColumnNo, entity.typeAtr,
				entity.boldAtr, entity.messageColor, entity.cancelableAtr, entity.errorDisplayItem,
						erAlApp.isEmpty() || erAlApp.get(0).oneKrcstErAlApplication == null  
						? null : erAlApp.stream().map(item -> 
						item.oneKrcstErAlApplication.krcstErAlApplicationPK.appTypeCd).collect(Collectors.toList()),
				entity.eralCheckId);
	}

	//fix for response
	public static ErrorAlarmCondition toConditionDomainForRes(KwrmtErAlWorkRecord entity, 
			List<KrcmtErAlCondition> alCons, 
			Map<List<KrcmtEralstCndexpiptchk>, List<KrcmtEralstCndgrp>> conditionGroup1, 
			Map<List<KrcmtEralstCndexpiptchk>, List<KrcmtEralstCndgrp>> conditionGroup2) {
		return convertToConditionDomainForRes(entity, alCons.get(0), alCons, conditionGroup1, conditionGroup2);
	}
	
	//fix for response
	public static ErrorAlarmCondition convertToConditionDomainForRes(KwrmtErAlWorkRecord entity, KrcmtErAlCondition alCon, 
			List<KrcmtErAlCondition> alCons, 
			Map<List<KrcmtEralstCndexpiptchk>, List<KrcmtEralstCndgrp>> conditionGroup1, 
			Map<List<KrcmtEralstCndexpiptchk>, List<KrcmtEralstCndgrp>> conditionGroup2) {
		ErrorAlarmCondition condition = ErrorAlarmCondition.init();
		condition.setDisplayMessage(alCon.messageDisplay);
		condition.setContinuousPeriod(alCon.continuousPeriod);
		if (!entity.fixedAtr) {
			// Set AlCheckTargetCondition
			condition.createAlCheckTargetCondition(alCon.filterByBusinessType == 1,
					alCon.filterByJobTitle == 1, alCon.filterByEmployment == 1,
							alCon.filterByClassification == 1,
					Optional.ofNullable(alCons.get(0).businessType != null ? alCons : null).orElse(Collections.emptyList()).stream()
							.filter(businessType -> businessType.businessType.krcstErAlBusinessTypePK.businessTypeCd != null)
							.map(businessType -> businessType.businessType.krcstErAlBusinessTypePK.businessTypeCd)
							.collect(Collectors.toList()),	
					Optional.ofNullable(alCons.get(0).jobTitle != null ? alCons : null).orElse(Collections.emptyList()).stream()
							.filter(jobTitle -> jobTitle.jobTitle.krcstErAlJobTitlePK.jobId != null)
							.map(jobTitle -> jobTitle.jobTitle.krcstErAlJobTitlePK.jobId).collect(Collectors.toList()),	
					Optional.ofNullable(alCons.get(0).employment != null ? alCons : null).orElse(Collections.emptyList()).stream()
							.filter(empt -> empt.employment.krcstErAlEmploymentPK.emptcd != null)
							.map(empt -> empt.employment.krcstErAlEmploymentPK.emptcd).collect(Collectors.toList()),			
					Optional.ofNullable(alCons.get(0).classification != null ? alCons : null).orElse(Collections.emptyList()).stream()
							.filter(clss -> clss.classification.krcstErAlClassPK.clscd != null)
							.map(clss -> clss.classification.krcstErAlClassPK.clscd).collect(Collectors.toList()));
			// Set WorkTypeCondition
			condition.createWorkTypeCondition(alCon.workTypeUseAtr == 1,
					alCon.wtCompareAtr);
			if (alCon.wtCompareAtr != FilterByCompare.SELECTED.value) {
				condition.setWorkTypePlan(alCon.wtPlanFilterAtr == 1,
						Optional.ofNullable(alCons.get(0).wtPlan != null ? alCons : null).orElse(Collections.emptyList()).stream()
								.filter(wtype -> wtype.wtPlan.krcstErAlWtPlanPK.workTypeCode != null)
								.map(wtype -> wtype.wtPlan.krcstErAlWtPlanPK.workTypeCode)
								.collect(Collectors.toList()));
				condition.setWorkTypeActual(alCon.wtActualFilterAtr == 1,
						Optional.ofNullable(alCons.get(0).wtActual != null ? alCons : null).orElse(Collections.emptyList()).stream()
								.filter(wtype -> wtype.wtActual.krcstErAlWtPlanActualPK.workTypeCode != null)
								.map(wtype -> wtype.wtActual.krcstErAlWtPlanActualPK.workTypeCode)
								.collect(Collectors.toList()));
				condition.chooseWorkTypeOperator(alCon.wtPlanActualOperator);
			} else {
				condition.setWorkTypeSingle(alCon.wtPlanFilterAtr == 1,
						Optional.ofNullable(alCons.get(0).wtPlan != null ? alCons : null).orElse(Collections.emptyList()).stream()
								.filter(wtype -> wtype.wtPlan.krcstErAlWtPlanPK.workTypeCode != null)
								.map(wtype -> wtype.wtPlan.krcstErAlWtPlanPK.workTypeCode)
								.collect(Collectors.toList()));
			}
			// Set WorkTimeCondtion
			condition.createWorkTimeCondition(alCon.workingHoursUseAtr == 1,
					alCon.whCompareAtr);
			if (alCon.whCompareAtr != FilterByCompare.SELECTED.value) {
				condition.setWorkTimePlan(alCon.whPlanFilterAtr == 1,
						Optional.ofNullable(alCons.get(0).whPlan != null ? alCons : null).orElse(Collections.emptyList()).stream()
								.filter(wtime -> wtime.whPlan.krcstErAlWhPlanActualPK.workTimeCode != null)
								.map(wtime -> wtime.whPlan.krcstErAlWhPlanActualPK.workTimeCode)
								.collect(Collectors.toList()));
				condition.setWorkTimeActual(alCon.whActualFilterAtr == 1,
						Optional.ofNullable(alCons.get(0).whActual != null ? alCons : null).orElse(Collections.emptyList()).stream()
								.filter(wtime -> wtime.whActual.krcstErAlWhPlanActualPK.workTimeCode != null)
								.map(wtime -> wtime.whActual.krcstErAlWhPlanActualPK.workTimeCode)
								.collect(Collectors.toList()));
				condition.chooseWorkTimeOperator(alCon.whPlanActualOperator);
			} else {
				condition.setWorkTimeSingle(alCon.whPlanFilterAtr == 1,
						Optional.ofNullable(alCons.get(0).whPlan != null ? alCons : null).orElse(Collections.emptyList()).stream()
								.filter(wtime -> wtime.whPlan.krcstErAlWhPlanActualPK.workTimeCode != null)
								.map(wtime -> wtime.whPlan.krcstErAlWhPlanActualPK.workTimeCode)
								.collect(Collectors.toList()));
			}
			
			String comId = entity.kwrmtErAlWorkRecordPK.companyId;
			String eralCode = entity.kwrmtErAlWorkRecordPK.errorAlarmCode;
			List<ErAlAttendanceItemCondition<?>> conditionsGroup1 = new ArrayList<>();
			// Set AttendanceItemCondition
			if(alCon.atdItemConditionGroup1 != null) {
				List<ErAlAttendanceItemCondition<?>> conditionsGroup1s = new ArrayList<>();
				Map<Integer, List<KrcmtEralstCndgrp>> itemCon1 = conditionGroup1.values().stream().collect(Collectors.toList()).get(0)
						.stream().filter(item -> item.krcmtErAlAtdItemConPK.conditionGroupId.equals(alCon.atdItemConditionGroup1))
						.collect(Collectors.groupingBy(x->x.krcmtErAlAtdItemConPK.atdItemConNo));
				itemCon1.forEach((key,value) -> {
					conditionsGroup1s.addAll(value.stream().map(item -> convertKrcmtErAlAtdItemConToDomainForRes(
							comId, eralCode, value, key)).collect(Collectors.toList()));
				});
				conditionsGroup1.addAll(conditionsGroup1s);
			}

			List<ErAlAttendanceItemCondition<?>> conditionsGroup2 = new ArrayList<>();
			if(alCon.atdItemConditionGroup2 != null) {
				List<ErAlAttendanceItemCondition<?>> conditionsGroup2s = new ArrayList<>();

				Map<Integer, List<KrcmtEralstCndgrp>> itemCon1 = conditionGroup2.values().stream().collect(Collectors.toList()).get(0)
						.stream().filter(item -> item.krcmtErAlAtdItemConPK.conditionGroupId.equals(alCon.atdItemConditionGroup2))
						.collect(Collectors.groupingBy(x->x.krcmtErAlAtdItemConPK.atdItemConNo));
				itemCon1.forEach((key,value) -> {
					conditionsGroup2s.addAll(value.stream().map(item -> convertKrcmtErAlAtdItemConToDomainForRes(
							comId, eralCode, value, key)).collect(Collectors.toList()));
				});
				conditionsGroup2.addAll(conditionsGroup2s);
			}

			List<KrcmtEralstCndexpiptchk> conGroup1 = conditionGroup1.keySet().stream().collect(Collectors.toList()).get(0)
					.stream().filter(item -> item.conditionGroupId.equals(alCon.atdItemConditionGroup1))
					.collect(Collectors.toList());
			List<KrcmtEralstCndexpiptchk> conGroup2 = conditionGroup1.keySet().stream().collect(Collectors.toList()).get(0)
			.stream().filter(item -> item.conditionGroupId.equals(alCon.atdItemConditionGroup2))
			.collect(Collectors.toList());
			
			condition
					.createAttendanceItemCondition(alCon.operatorBetweenGroups,
							alCon.group2UseAtr == 1)
					.setAttendanceItemConditionGroup1(conGroup1.isEmpty() ? 0 : conGroup1.get(0).conditionOperator,
							conditionsGroup1)
					.setAttendanceItemConditionGroup2(conGroup2.isEmpty() ? 0 : conGroup2.get(0).conditionOperator,
							conditionsGroup2);
		}
		condition.setCheckId(entity.eralCheckId);
		return condition;
	}

	//fix for response
	@SuppressWarnings("unchecked")
	private static <V> ErAlAttendanceItemCondition<V> convertKrcmtErAlAtdItemConToDomainForRes(String comId, String eralCode,
			 List<KrcmtEralstCndgrp> atdItemCon, Integer key) {
		ErAlAttendanceItemCondition<V> atdItemConDomain = new ErAlAttendanceItemCondition<V>(comId, eralCode,
				atdItemCon.get(0).krcmtErAlAtdItemConPK.atdItemConNo, atdItemCon.get(0).conditionAtr, atdItemCon.get(0).useAtr == 1,
				atdItemCon.get(0).type);
		// Set Target
		if (atdItemCon.get(0).conditionAtr == ConditionAtr.TIME_WITH_DAY.value 
				|| atdItemCon.get(0).type == ErrorAlarmConditionType.INPUT_CHECK.value) {
			atdItemConDomain.setUncountableTarget(Optional.ofNullable(atdItemCon.get(0).atdItemTarget != null ? atdItemCon : null)
					.orElse(Collections.emptyList()).stream().findFirst().get().atdItemTarget.krcstErAlAtdTargetPK.attendanceItemId);
		} else {
			atdItemConDomain.setCountableTarget(
					Optional.ofNullable(atdItemCon.get(0).atdItemTarget != null ? atdItemCon : null)
							.orElse(Collections.emptyList()).stream()
							.filter(atdItemTarget -> atdItemTarget.atdItemTarget.targetAtr == 0)
							.map(addItem -> addItem.atdItemTarget.krcstErAlAtdTargetPK.attendanceItemId).collect(Collectors.toList()),
					Optional.ofNullable(atdItemCon.get(0).atdItemTarget != null ? atdItemCon : null).orElse(Collections.emptyList()).stream()
							.filter(atdItemTarget -> atdItemTarget.atdItemTarget.targetAtr == 1)
							.map(addItem -> addItem.atdItemTarget.krcstErAlAtdTargetPK.attendanceItemId)
							.collect(Collectors.toList()));
		}
		// Set Compare
		if (atdItemCon.get(0).erAlCompareRange != null) {
			if (atdItemCon.get(0).conditionAtr == ConditionAtr.AMOUNT_VALUE.value) {
				atdItemConDomain.setCompareRange(atdItemCon.get(0).erAlCompareRange.compareAtr,
						(V) new CheckedAmountValue((int)atdItemCon.get(0).erAlCompareRange.startValue),
						(V) new CheckedAmountValue((int)atdItemCon.get(0).erAlCompareRange.endValue));
			} else if (atdItemCon.get(0).conditionAtr == ConditionAtr.TIME_DURATION.value) {
				atdItemConDomain.setCompareRange(atdItemCon.get(0).erAlCompareRange.compareAtr,
						(V) new CheckedTimeDuration((int)atdItemCon.get(0).erAlCompareRange.startValue),
						(V) new CheckedTimeDuration((int)atdItemCon.get(0).erAlCompareRange.endValue));
			} else if (atdItemCon.get(0).conditionAtr == ConditionAtr.TIME_WITH_DAY.value) {
				atdItemConDomain.setCompareRange(atdItemCon.get(0).erAlCompareRange.compareAtr,
						(V) new TimeWithDayAttr((int)atdItemCon.get(0).erAlCompareRange.startValue),
						(V) new TimeWithDayAttr((int)atdItemCon.get(0).erAlCompareRange.endValue));
			} else if (atdItemCon.get(0).conditionAtr == ConditionAtr.TIMES.value) {
				atdItemConDomain.setCompareRange(atdItemCon.get(0).erAlCompareRange.compareAtr,
						(V) new CheckedTimesValue((int)atdItemCon.get(0).erAlCompareRange.startValue),
						(V) new CheckedTimesValue((int)atdItemCon.get(0).erAlCompareRange.endValue));
			} else if (atdItemCon.get(0).conditionAtr == ConditionAtr.DAYS.value) {
				atdItemConDomain.setCompareRange(atdItemCon.get(0).erAlCompareRange.compareAtr,
						(V) new CheckedTimesValueDay(atdItemCon.get(0).erAlCompareRange.startValue),
						(V) new CheckedTimesValueDay(atdItemCon.get(0).erAlCompareRange.endValue));
			}
		} else if (atdItemCon.get(0).erAlCompareSingle != null) {
			if (atdItemCon.get(0).erAlCompareSingle.conditionType == ConditionType.FIXED_VALUE.value) {
				if (atdItemCon.get(0).conditionAtr == ConditionAtr.AMOUNT_VALUE.value) {
					atdItemConDomain.setCompareSingleValue(atdItemCon.get(0).erAlCompareSingle.compareAtr,
							atdItemCon.get(0).erAlCompareSingle.conditionType,
							(V) new CheckedAmountValue((int)atdItemCon.get(0).erAlSingleFixed.fixedValue));
				} else if (atdItemCon.get(0).conditionAtr == ConditionAtr.TIME_DURATION.value) {
					atdItemConDomain.setCompareSingleValue(atdItemCon.get(0).erAlCompareSingle.compareAtr,
							atdItemCon.get(0).erAlCompareSingle.conditionType,
							(V) new CheckedTimeDuration((int)atdItemCon.get(0).erAlSingleFixed.fixedValue));
				} else if (atdItemCon.get(0).conditionAtr == ConditionAtr.TIME_WITH_DAY.value) {
					atdItemConDomain.setCompareSingleValue(atdItemCon.get(0).erAlCompareSingle.compareAtr,
							atdItemCon.get(0).erAlCompareSingle.conditionType,
							(V) new TimeWithDayAttr((int)atdItemCon.get(0).erAlSingleFixed.fixedValue));
				} else if (atdItemCon.get(0).conditionAtr == ConditionAtr.TIMES.value) {
					atdItemConDomain.setCompareSingleValue(atdItemCon.get(0).erAlCompareSingle.compareAtr,
							atdItemCon.get(0).erAlCompareSingle.conditionType,
							(V) new CheckedTimesValue((int)atdItemCon.get(0).erAlSingleFixed.fixedValue));
				} else if (atdItemCon.get(0).conditionAtr == ConditionAtr.DAYS.value) {
					atdItemConDomain.setCompareSingleValue(atdItemCon.get(0).erAlCompareSingle.compareAtr,
							atdItemCon.get(0).erAlCompareSingle.conditionType,
							(V) new CheckedTimesValueDay(atdItemCon.get(0).erAlSingleFixed.fixedValue));
				}
			} else {
				atdItemConDomain.setCompareSingleValue(atdItemCon.get(0).erAlCompareSingle.compareAtr,
						atdItemCon.get(0).erAlCompareSingle.conditionType, (V) new AttendanceItemId(
								atdItemCon.get(0).erAlSingleAtd.get(0).krcstEralSingleAtdPK.attendanceItemId));
			}
		} else if (atdItemCon.get(0).erAlInputCheck != null) {
			atdItemConDomain.setInputCheck(atdItemCon.get(0).erAlInputCheck.inputCheckCondition);
		}
		return atdItemConDomain;
	}

	public static ErrorAlarmCondition toConditionDomain(KwrmtErAlWorkRecord entity) {
		return toConditionDomain(entity, entity.krcmtErAlCondition);
	}
	
	public static ErrorAlarmCondition toConditionDomain(KwrmtErAlWorkRecord entity, KrcmtErAlCondition alCon) {
		ErrorAlarmCondition condition = ErrorAlarmCondition.init();
		if(alCon == null)
			return new ErrorAlarmCondition(entity.eralCheckId, "");
		condition.setDisplayMessage(alCon.messageDisplay);
		condition.setContinuousPeriod(alCon.continuousPeriod);
		if (!entity.fixedAtr) {
			// Set AlCheckTargetCondition
			condition.createAlCheckTargetCondition(alCon.filterByBusinessType == 1,
					alCon.filterByJobTitle == 1, alCon.filterByEmployment == 1,
							alCon.filterByClassification == 1,
					Optional.ofNullable(alCon.lstBusinessType).orElse(Collections.emptyList())
							.stream().map(businessType -> businessType.krcstErAlBusinessTypePK.businessTypeCd)
							.collect(Collectors.toList()),
					Optional.ofNullable(alCon.lstJobTitle).orElse(Collections.emptyList()).stream()
							.map(jobTitle -> jobTitle.krcstErAlJobTitlePK.jobId).collect(Collectors.toList()),
					Optional.ofNullable(alCon.lstEmployment).orElse(Collections.emptyList())
							.stream().map(empt -> empt.krcstErAlEmploymentPK.emptcd).collect(Collectors.toList()),
					Optional.ofNullable(alCon.lstClassification).orElse(Collections.emptyList())
							.stream().map(clss -> clss.krcstErAlClassPK.clscd).collect(Collectors.toList()));
			// Set WorkTypeCondition
			condition.createWorkTypeCondition(alCon.workTypeUseAtr == 1,
					alCon.wtCompareAtr);
			if (alCon.wtCompareAtr != FilterByCompare.SELECTED.value) {
				condition.setWorkTypePlan(alCon.wtPlanFilterAtr == 1,
						Optional.ofNullable(alCon.lstWtPlan).orElse(Collections.emptyList())
								.stream().map(wtype -> wtype.krcstErAlWtPlanPK.workTypeCode)
								.collect(Collectors.toList()));
				condition.setWorkTypeActual(alCon.wtActualFilterAtr == 1,
						Optional.ofNullable(alCon.lstWtActual).orElse(Collections.emptyList())
								.stream().map(wtype -> wtype.krcstErAlWtPlanActualPK.workTypeCode)
								.collect(Collectors.toList()));
				condition.chooseWorkTypeOperator(alCon.wtPlanActualOperator);
			} else {
				condition.setWorkTypeSingle(alCon.wtPlanFilterAtr == 1,
						Optional.ofNullable(alCon.lstWtPlan).orElse(Collections.emptyList())
								.stream().map(wtype -> wtype.krcstErAlWtPlanPK.workTypeCode)
								.collect(Collectors.toList()));
			}
			// Set WorkTimeCondtion
			condition.createWorkTimeCondition(alCon.workingHoursUseAtr == 1,
					alCon.whCompareAtr);
			if (alCon.whCompareAtr != FilterByCompare.SELECTED.value) {
				condition.setWorkTimePlan(alCon.whPlanFilterAtr == 1,
						Optional.ofNullable(alCon.lstWhPlan).orElse(Collections.emptyList())
								.stream().map(wtime -> wtime.krcstErAlWhPlanActualPK.workTimeCode)
								.collect(Collectors.toList()));
				condition.setWorkTimeActual(alCon.whActualFilterAtr == 1,
						Optional.ofNullable(alCon.lstWhActual).orElse(Collections.emptyList())
								.stream().map(wtime -> wtime.krcstErAlWhPlanActualPK.workTimeCode)
								.collect(Collectors.toList()));
				condition.chooseWorkTimeOperator(alCon.whPlanActualOperator);
			} else {
				condition.setWorkTimeSingle(alCon.whPlanFilterAtr == 1,
						Optional.ofNullable(alCon.lstWhPlan).orElse(Collections.emptyList())
								.stream().map(wtime -> wtime.krcstErAlWhPlanActualPK.workTimeCode)
								.collect(Collectors.toList()));
			}
			String comId = entity.kwrmtErAlWorkRecordPK.companyId;
			String eralCode = entity.kwrmtErAlWorkRecordPK.errorAlarmCode;
			// Set AttendanceItemCondition
			List<ErAlAttendanceItemCondition<?>> conditionsGroup1 = Optional
					.ofNullable(alCon.krcstErAlConGroup1)
					.orElse(new KrcmtEralstCndexpiptchk("", 0, new ArrayList<>())).lstAtdItemCon.stream()
							.map(atdItemCon -> convertKrcmtErAlAtdItemConToDomain(comId, eralCode, atdItemCon))
							.collect(Collectors.toList());
			List<ErAlAttendanceItemCondition<?>> conditionsGroup2 = Optional
					.ofNullable(alCon.krcstErAlConGroup2)
					.orElse(new KrcmtEralstCndexpiptchk("", 0, new ArrayList<>())).lstAtdItemCon.stream()
							.map(atdItemCon -> convertKrcmtErAlAtdItemConToDomain(comId, eralCode, atdItemCon))
							.collect(Collectors.toList());
			condition
					.createAttendanceItemCondition(alCon.operatorBetweenGroups,
							alCon.group2UseAtr == 1)
					.setAttendanceItemConditionGroup1(
							Optional.ofNullable(alCon.krcstErAlConGroup1)
									.orElse(new KrcmtEralstCndexpiptchk("", 0, new ArrayList<>())).conditionOperator,
							conditionsGroup1)
					.setAttendanceItemConditionGroup2(
							Optional.ofNullable(alCon.krcstErAlConGroup2)
									.orElse(new KrcmtEralstCndexpiptchk("", 0, new ArrayList<>())).conditionOperator,
							conditionsGroup2);
		}
		condition.setCheckId(entity.eralCheckId);
		return condition;
	}
	
	public String getGroup1Id() {
		return this.krcmtErAlCondition.atdItemConditionGroup1;
	}
	
	public String getGroup2Id() {
		return this.krcmtErAlCondition.atdItemConditionGroup2;
	}

}
