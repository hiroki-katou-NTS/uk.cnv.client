package nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
//import lombok.Setter;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.ErrorAlarmCondition;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.attendanceitem.ErAlAttendanceItemCondition;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.worktime.PlanActualWorkTime;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.worktime.SingleWorkTime;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.worktype.PlanActualWorkType;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.worktype.SingleWorkType;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.ConditionAtr;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.ConditionType;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.FilterByCompare;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.AttendanceItemId;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.CheckedAmountValue;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.CheckedTimeDuration;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.CheckedTimesValue;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.CheckedTimesValueDay;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.KwrmtErAlWorkRecord;
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
 * @author hungnm 勤務実績のエラーアラームチェック
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KRCMT_ERALST_CND")
public class KrcmtErAlCondition extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KrcmtErAlConditionPK krcmtErAlConditionPK;

	@Basic(optional = true)
	@Column(name = "MESSAGE_DISPLAY")
	public String messageDisplay;

	@Column(name = "CONTINUOUS_PERIOD")
	public int continuousPeriod;

	@Column(name = "FILTER_BY_BUSINESS_TYPE")
	public int filterByBusinessType;

	@OneToMany(mappedBy ="krcmtErAlCondition", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	//@JoinColumns({ @JoinColumn(name = "ERAL_CHECK_ID", referencedColumnName = "ERAL_CHECK_ID", nullable = true) })
	public List<KrcmtEralBusinessType> lstBusinessType;
	
	@Transient
	public KrcmtEralBusinessType businessType;

	@Column(name = "FILTER_BY_JOB_TITLE")
	public int filterByJobTitle;

	@OneToMany(mappedBy = "krcmtErAlCondition" , cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	//@JoinColumns({ @JoinColumn(name = "ERAL_CHECK_ID", referencedColumnName = "ERAL_CHECK_ID", nullable = true) })
	public List<KrcmtEralJobTitle> lstJobTitle;
	
	@Transient
	public KrcmtEralJobTitle jobTitle;

	@Column(name = "FILTER_BY_EMPLOYMENT")
	public int filterByEmployment;

	@OneToMany(mappedBy="krcmtErAlCondition", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	//@JoinColumns({ @JoinColumn(name = "ERAL_CHECK_ID", referencedColumnName = "ERAL_CHECK_ID", nullable = true) })
	public List<KrcmtEralEmployment> lstEmployment;
	
	@Transient
	public KrcmtEralEmployment employment;

	@Column(name = "FILTER_BY_CLASSIFICATION")
	public int filterByClassification;

	@OneToMany(mappedBy = "krcmtErAlCondition" ,cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	//@JoinColumns({ @JoinColumn(name = "ERAL_CHECK_ID", referencedColumnName = "ERAL_CHECK_ID", nullable = true) })
	public List<KrcmtEralClass> lstClassification;
	
	@Transient
	public KrcmtEralClass classification;

	@Column(name = "WORKTYPE_USE_ATR")
	public int workTypeUseAtr;

	@Column(name = "WT_PLAN_ACTUAL_OPERATOR")
	public int wtPlanActualOperator;

	@Column(name = "WT_PLAN_FILTER_ATR")
	public Integer wtPlanFilterAtr;

	@Column(name = "WT_ACTUAL_FILTER_ATR")
	public Integer wtActualFilterAtr;

	@Column(name = "WT_COMPARE_ATR")
	public Integer wtCompareAtr;

	@OneToMany(mappedBy ="krcmtErAlCondition" , cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	//@JoinColumns({ @JoinColumn(name = "ERAL_CHECK_ID", referencedColumnName = "ERAL_CHECK_ID", nullable = true) })
	public List<KrcmtEralWktpActual> lstWtActual;
	
	@Transient
	public KrcmtEralWktpActual wtActual;

	@OneToMany(mappedBy = "krcmtErAlCondition",cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	//@JoinColumns({ @JoinColumn(name = "ERAL_CHECK_ID", referencedColumnName = "ERAL_CHECK_ID", nullable = true) })
	public List<KrcmtEralWktpPlan> lstWtPlan;
	
	@Transient
	public KrcmtEralWktpPlan wtPlan;

	@Column(name = "WORKING_HOURS_USE_ATR")
	public int workingHoursUseAtr;

	@Column(name = "WH_PLAN_ACTUAL_OPERATOR")
	public int whPlanActualOperator;

	@Column(name = "WH_PLAN_FILTER_ATR")
	public Integer whPlanFilterAtr;

	@Column(name = "WH_ACTUAL_FILTER_ATR")
	public Integer whActualFilterAtr;

	@Column(name = "WH_COMPARE_ATR")
	public Integer whCompareAtr;

	@OneToMany(mappedBy = "krcmtErAlCondition",cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	//@JoinColumns({ @JoinColumn(name = "ERAL_CHECK_ID", referencedColumnName = "ERAL_CHECK_ID", nullable = true) })
	public List<KrcmtEralWktmActual> lstWhActual;
	
	@Transient
	public KrcmtEralWktmActual whActual;

	@OneToMany(mappedBy = "krcmtErAlCondition",cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	//@JoinColumns({ @JoinColumn(name = "ERAL_CHECK_ID", referencedColumnName = "ERAL_CHECK_ID", nullable = true) })
	public List<KrcmtEralWktmPlan> lstWhPlan;
	
	@Transient
	public KrcmtEralWktmPlan whPlan;

	@Column(name = "OPERATOR_BETWEEN_GROUPS")
	public int operatorBetweenGroups;

	@Column(name = "GROUP2_USE_ATR")
	public int group2UseAtr;

	@Basic(optional = true)
	@Column(name = "ATD_ITEM_CONDITION_GROUP1")
	public String atdItemConditionGroup1;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	@JoinColumn(name = "ATD_ITEM_CONDITION_GROUP1", referencedColumnName = "CONDITION_GROUP_ID", insertable = false, updatable = false)
	public KrcmtEralstCndexpiptchk krcstErAlConGroup1;

	@Basic(optional = true)
	@Column(name = "ATD_ITEM_CONDITION_GROUP2")
	public String atdItemConditionGroup2;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	@JoinColumn(name = "ATD_ITEM_CONDITION_GROUP2", referencedColumnName = "CONDITION_GROUP_ID", insertable = false, updatable = false)
	public KrcmtEralstCndexpiptchk krcstErAlConGroup2;

	@OneToOne(mappedBy = "krcmtErAlCondition")
	public KwrmtErAlWorkRecord kwrmtErAlWorkRecord;

	
	@Override
	protected Object getKey() {
		return this.krcmtErAlConditionPK;
	}

	public KrcmtErAlCondition(String eralCheckId,String cid, String messageDisplay, int filterByBusinessType,
			List<KrcmtEralBusinessType> lstBusinessType, int filterByJobTitle, List<KrcmtEralJobTitle> lstJobTitle,
			int filterByEmployment, List<KrcmtEralEmployment> lstEmployment, int filterByClassification,
			List<KrcmtEralClass> lstClassification, int workTypeUseAtr, int wtPlanActualOperator,
			Integer wtPlanFilterAtr, Integer wtActualFilterAtr, Integer wtCompareAtr,
			List<KrcmtEralWktpActual> lstWtActual, List<KrcmtEralWktpPlan> lstWtPlan, int workingHoursUseAtr,
			int whPlanActualOperator, Integer whPlanFilterAtr, Integer whActualFilterAtr, Integer whCompareAtr,
			List<KrcmtEralWktmActual> lstWhActual, List<KrcmtEralWktmPlan> lstWhPlan, int operatorBetweenGroups,
			int group2UseAtr, String atdItemConditionGroup1, KrcmtEralstCndexpiptchk krcstErAlConGroup1,
			String atdItemConditionGroup2, KrcmtEralstCndexpiptchk krcstErAlConGroup2, int continuousPeriod) {
		super();
		this.krcmtErAlConditionPK = new KrcmtErAlConditionPK(eralCheckId,cid);
		this.messageDisplay = messageDisplay;
		this.filterByBusinessType = filterByBusinessType;
		this.lstBusinessType = lstBusinessType;
		this.filterByJobTitle = filterByJobTitle;
		this.lstJobTitle = lstJobTitle;
		this.filterByEmployment = filterByEmployment;
		this.lstEmployment = lstEmployment;
		this.filterByClassification = filterByClassification;
		this.lstClassification = lstClassification;
		this.workTypeUseAtr = workTypeUseAtr;
		this.wtPlanActualOperator = wtPlanActualOperator;
		this.wtPlanFilterAtr = wtPlanFilterAtr;
		this.wtActualFilterAtr = wtActualFilterAtr;
		this.wtCompareAtr = wtCompareAtr;
		this.lstWtActual = lstWtActual;
		this.lstWtPlan = lstWtPlan;
		this.workingHoursUseAtr = workingHoursUseAtr;
		this.whPlanActualOperator = whPlanActualOperator;
		this.whPlanFilterAtr = whPlanFilterAtr;
		this.whActualFilterAtr = whActualFilterAtr;
		this.whCompareAtr = whCompareAtr;
		this.lstWhActual = lstWhActual;
		this.lstWhPlan = lstWhPlan;
		this.operatorBetweenGroups = operatorBetweenGroups;
		this.group2UseAtr = group2UseAtr;
		this.atdItemConditionGroup1 = atdItemConditionGroup1;
		this.krcstErAlConGroup1 = krcstErAlConGroup1;
		this.atdItemConditionGroup2 = atdItemConditionGroup2;
		this.krcstErAlConGroup2 = krcstErAlConGroup2;
		this.continuousPeriod = continuousPeriod;
	}

	public static KrcmtErAlCondition fromDomain(ErrorAlarmCondition conditionDomain) {
		String companyID= AppContexts.user().companyId();
		String eralCheckId = conditionDomain.getErrorAlarmCheckID();
		String displayMessage = conditionDomain.getDisplayMessage().v();
		KrcmtErAlCondition krcmtErAlCondition = new KrcmtErAlCondition(eralCheckId,companyID, displayMessage, 0,
				Collections.emptyList(), 0, Collections.emptyList(), 0, Collections.emptyList(), 0,
				Collections.emptyList(), 0, 0, null, null, null, Collections.emptyList(), Collections.emptyList(), 0, 0,
				null, null, null, Collections.emptyList(), Collections.emptyList(), 0, 0, "0", null, null, null, 0);
		// Set Check target condition
		// if (!domain.getFixedAtr()) {
		int filterByBusinessType = conditionDomain.getCheckTargetCondtion().getFilterByBusinessType() ? 1 : 0;
		int filterByJobTitle = conditionDomain.getCheckTargetCondtion().getFilterByJobTitle() ? 1 : 0;
		int filterByEmployment = conditionDomain.getCheckTargetCondtion().getFilterByEmployment() ? 1 : 0;
		int filterByClassification = conditionDomain.getCheckTargetCondtion().getFilterByClassification() ? 1 : 0;
		List<KrcmtEralBusinessType> lstBusinessType = conditionDomain.getCheckTargetCondtion().getLstBusinessTypeCode()
				.stream().map(businessTypeCd -> new KrcmtEralBusinessType(
						new KrcstErAlBusinessTypePK(eralCheckId, businessTypeCd.v(),companyID)))
				.collect(Collectors.toList());
		List<KrcmtEralJobTitle> lstJobTitle = conditionDomain.getCheckTargetCondtion().getLstJobTitleId().stream()
				.map(jobTitleId -> new KrcmtEralJobTitle(new KrcstErAlJobTitlePK(eralCheckId, jobTitleId,companyID)))
				.collect(Collectors.toList());
		List<KrcmtEralEmployment> lstEmployment = conditionDomain.getCheckTargetCondtion().getLstEmploymentCode()
				.stream().map(emptCd -> new KrcmtEralEmployment(new KrcstErAlEmploymentPK(eralCheckId, emptCd.v(),companyID)))
				.collect(Collectors.toList());
		List<KrcmtEralClass> lstClassification = conditionDomain.getCheckTargetCondtion().getLstClassificationCode()
				.stream().map(clssCd -> new KrcmtEralClass(new KrcstErAlClassPK(eralCheckId, clssCd.v(),companyID)))
				.collect(Collectors.toList());
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
					.map(wtCode -> new KrcmtEralWktpPlan(new KrcstErAlWtPlanActualPK(eralCheckId, wtCode.v(),companyID)))
					.collect(Collectors.toList());
			lstWtActual = wtypeCondition.getWorkTypeActual().getLstWorkType().stream()
					.map(wtCode -> new KrcmtEralWktpActual(new KrcstErAlWtPlanActualPK(eralCheckId, wtCode.v(),companyID)))
					.collect(Collectors.toList());
		} else {
			SingleWorkType wtypeCondition = (SingleWorkType) conditionDomain.getWorkTypeCondition();
			wtPlanFilterAtr = wtypeCondition.getTargetWorkType().isUse() ? 1 : 0;
			lstWtPlan = wtypeCondition.getTargetWorkType().getLstWorkType().stream()
					.map(wtCode -> new KrcmtEralWktpPlan(new KrcstErAlWtPlanActualPK(eralCheckId, wtCode.v(),companyID)))
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
					.map(wtCode -> new KrcmtEralWktmPlan(new KrcstErAlWhPlanActualPK(eralCheckId, wtCode.v(),companyID)))
					.collect(Collectors.toList());
			lstWhActual = wtimeCondition.getWorkTimeActual().getLstWorkTime().stream()
					.map(wtCode -> new KrcmtEralWktmActual(new KrcstErAlWhPlanActualPK(eralCheckId, wtCode.v(),companyID)))
					.collect(Collectors.toList());
		} else {
			SingleWorkTime wtimeCondition = (SingleWorkTime) conditionDomain.getWorkTimeCondition();
			whPlanFilterAtr = (wtimeCondition.getTargetWorkTime().isUse() ? 1 : 0);
			lstWhPlan = wtimeCondition.getTargetWorkTime().getLstWorkTime().stream()
					.map(wtCode -> new KrcmtEralWktmPlan(new KrcstErAlWhPlanActualPK(eralCheckId, wtCode.v(),companyID)))
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
		krcmtErAlCondition = new KrcmtErAlCondition(eralCheckId,companyID, displayMessage, filterByBusinessType, lstBusinessType,
				filterByJobTitle, lstJobTitle, filterByEmployment, lstEmployment, filterByClassification,
				lstClassification, workTypeUseAtr, wtPlanActualOperator, wtPlanFilterAtr, wtActualFilterAtr,
				wtCompareAtr, lstWtActual, lstWtPlan, workingHoursUseAtr, whPlanActualOperator, whPlanFilterAtr,
				whActualFilterAtr, whCompareAtr, lstWhActual, lstWhPlan, operatorBetweenGroups, group2UseAtr,
				atdItemConditionGroup1, krcstErAlConGroup1, atdItemConditionGroup2, krcstErAlConGroup2,
				conditionDomain.getContinuousPeriod() != null ? (conditionDomain.getContinuousPeriod().v()) : 0);
		// }
		return krcmtErAlCondition;
	}

	private static KrcmtEralstCndgrp getKrcmtErAlAtdItemConFromDomain(String atdItemConditionGroup1,
			ErAlAttendanceItemCondition<?> erAlAtdItemCon) {
		KrcmtErAlAtdItemConPK krcmtErAlAtdItemConPK = new KrcmtErAlAtdItemConPK(atdItemConditionGroup1,
				(erAlAtdItemCon.getTargetNO()));
		List<KrcmtEralstCndexprange> lstAtdItemTarget = new ArrayList<>();
		if (erAlAtdItemCon.getConditionAtr() == ConditionAtr.TIME_WITH_DAY) {
			lstAtdItemTarget.add(new KrcmtEralstCndexprange(new KrcstErAlAtdTargetPK(atdItemConditionGroup1,
					(erAlAtdItemCon.getTargetNO()), (erAlAtdItemCon.getUncountableTarget().getAttendanceItem())), 2));
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
					new KrcstErAlCompareSinglePK(atdItemConditionGroup1, erAlAtdItemCon.getTargetNO()), compareAtr,
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
						new KrcstErAlSingleFixedPK(atdItemConditionGroup1, (erAlAtdItemCon.getTargetNO())), fixedValue);
			} else {
				erAlSingleAtd
						.add(new KrcstErAlSingleAtd(
								new KrcstErAlSingleAtdPK(atdItemConditionGroup1, (erAlAtdItemCon.getTargetNO()),
										(((AttendanceItemId) erAlAtdItemCon.getCompareSingleValue().getValue()).v())),
								2));
			}
		} else if (erAlAtdItemCon.getCompareSingleValue() != null) {
			erAlInputCheck = new KrcstErAlInputCheck(
					new KrcstErAlInputCheckPK(atdItemConditionGroup1, erAlAtdItemCon.getTargetNO()),
					erAlAtdItemCon.getInputCheck().getInputCheckCondition().value);
		}
		return new KrcmtEralstCndgrp(krcmtErAlAtdItemConPK, erAlAtdItemCon.getConditionAtr().value,
				erAlAtdItemCon.isUse() ? 1 : 0, erAlAtdItemCon.getType().value, lstAtdItemTarget, erAlCompareSingle,
				erAlCompareRange, erAlInputCheck, erAlSingleFixed, erAlSingleAtd);
	}

	public static ErrorAlarmCondition toDomain(KrcmtErAlCondition entity, String companyId, String errorAlarmCode) {
		ErrorAlarmCondition condition = ErrorAlarmCondition.init();
		condition.setDisplayMessage(entity.messageDisplay);
		condition.setContinuousPeriod(entity.continuousPeriod);
		// if (entity.fixedAtr != 1) {
		// Set AlCheckTargetCondition
		condition.createAlCheckTargetCondition(entity.filterByBusinessType == 1, entity.filterByJobTitle == 1,
				entity.filterByEmployment == 1, entity.filterByClassification == 1,
				Optional.ofNullable(entity.lstBusinessType).orElse(Collections.emptyList()).stream()
						.map(businessType -> businessType.krcstErAlBusinessTypePK.businessTypeCd)
						.collect(Collectors.toList()),
				Optional.ofNullable(entity.lstJobTitle).orElse(Collections.emptyList()).stream()
						.map(jobTitle -> jobTitle.krcstErAlJobTitlePK.jobId).collect(Collectors.toList()),
				Optional.ofNullable(entity.lstEmployment).orElse(Collections.emptyList()).stream()
						.map(empt -> empt.krcstErAlEmploymentPK.emptcd).collect(Collectors.toList()),
				Optional.ofNullable(entity.lstClassification).orElse(Collections.emptyList()).stream()
						.map(clss -> clss.krcstErAlClassPK.clscd).collect(Collectors.toList()));
		// Set WorkTypeCondition

		condition.createWorkTypeCondition(entity.workTypeUseAtr == 1, entity.wtCompareAtr == null ? 0 : entity.wtCompareAtr);
		if (entity.wtCompareAtr != null && entity.wtCompareAtr != FilterByCompare.SELECTED.value) {
			condition.setWorkTypePlan((entity.wtPlanFilterAtr != null && entity.wtPlanFilterAtr == 1),
					Optional.ofNullable(entity.lstWtPlan).orElse(Collections.emptyList()).stream()
							.map(wtype -> wtype.krcstErAlWtPlanPK.workTypeCode).collect(Collectors.toList()));
			condition.setWorkTypeActual((entity.wtActualFilterAtr != null && entity.wtActualFilterAtr == 1),
					Optional.ofNullable(entity.lstWtActual).orElse(Collections.emptyList()).stream()
							.map(wtype -> wtype.krcstErAlWtPlanActualPK.workTypeCode).collect(Collectors.toList()));
			condition.chooseWorkTypeOperator(entity.wtPlanActualOperator);
		} else {
			if (entity.workTypeUseAtr == 1) {
				condition.setWorkTypeSingle((entity.wtPlanFilterAtr != null && entity.wtPlanFilterAtr == 1),
						entity.lstWtPlan.isEmpty() ? Collections.emptyList() : Optional.ofNullable(entity.lstWtPlan).orElse(Collections.emptyList()).stream()
								.map(wtype -> wtype.krcstErAlWtPlanPK.workTypeCode).collect(Collectors.toList()));	
			}
		}
		// Set WorkTimeCondtion
		condition.createWorkTimeCondition(entity.workingHoursUseAtr == 1,
				entity.whCompareAtr == null ? 0 : entity.whCompareAtr);
		if (entity.whCompareAtr != null && entity.whCompareAtr != FilterByCompare.SELECTED.value) {
			condition.setWorkTimePlan((entity.whPlanFilterAtr != null && entity.whPlanFilterAtr == 1),
					Optional.ofNullable(entity.lstWhPlan).orElse(Collections.emptyList()).stream()
							.map(wtime -> wtime.krcstErAlWhPlanActualPK.workTimeCode).collect(Collectors.toList()));
			condition.setWorkTimeActual(entity.whActualFilterAtr == 1,
					Optional.ofNullable(entity.lstWhActual).orElse(Collections.emptyList()).stream()
							.map(wtime -> wtime.krcstErAlWhPlanActualPK.workTimeCode).collect(Collectors.toList()));
			condition.chooseWorkTimeOperator(entity.whPlanActualOperator);
		} else {
			if (entity.workTypeUseAtr == 1) {
				condition.setWorkTimeSingle((entity.whPlanFilterAtr != null && entity.whPlanFilterAtr == 1),
						Optional.ofNullable(entity.lstWhPlan).orElse(Collections.emptyList()).stream()
								.map(wtime -> wtime.krcstErAlWhPlanActualPK.workTimeCode).collect(Collectors.toList()));	
			}
		}
		// Set AttendanceItemCondition
		List<ErAlAttendanceItemCondition<?>> conditionsGroup1 = Optional.ofNullable(entity.krcstErAlConGroup1)
				.orElse(new KrcmtEralstCndexpiptchk("", 0, new ArrayList<>())).lstAtdItemCon.stream().map(
						atdItemCon -> convertKrcmtErAlAtdItemConToDomain(entity, atdItemCon, companyId, errorAlarmCode))
						.collect(Collectors.toList());
		List<ErAlAttendanceItemCondition<?>> conditionsGroup2 = Optional.ofNullable(entity.krcstErAlConGroup2)
				.orElse(new KrcmtEralstCndexpiptchk("", 0, new ArrayList<>())).lstAtdItemCon.stream().map(
						atdItemCon -> convertKrcmtErAlAtdItemConToDomain(entity, atdItemCon, companyId, errorAlarmCode))
						.collect(Collectors.toList());
		condition.createAttendanceItemCondition(entity.operatorBetweenGroups, entity.group2UseAtr == 1)
				.setAttendanceItemConditionGroup1(
						Optional.ofNullable(entity.krcstErAlConGroup1)
								.orElse(new KrcmtEralstCndexpiptchk("", 0, new ArrayList<>())).conditionOperator,
						conditionsGroup1)
				.setAttendanceItemConditionGroup2(
						Optional.ofNullable(entity.krcstErAlConGroup2)
								.orElse(new KrcmtEralstCndexpiptchk("", 0, new ArrayList<>())).conditionOperator,
						conditionsGroup2);
		// }
		condition.setCheckId(entity.krcmtErAlConditionPK.eralCheckId);
		return condition;
	}

	@SuppressWarnings("unchecked")
	private static <V> ErAlAttendanceItemCondition<V> convertKrcmtErAlAtdItemConToDomain(KrcmtErAlCondition entity,
			KrcmtEralstCndgrp atdItemCon, String companyId, String errorAlarmCode) {
		ErAlAttendanceItemCondition<V> atdItemConDomain = new ErAlAttendanceItemCondition<V>(companyId, errorAlarmCode,
				atdItemCon.krcmtErAlAtdItemConPK.atdItemConNo, atdItemCon.conditionAtr, atdItemCon.useAtr == 1,
				atdItemCon.type);
		// Set Target
		if (atdItemCon.conditionAtr == ConditionAtr.TIME_WITH_DAY.value) {
			atdItemConDomain.setUncountableTarget(Optional.ofNullable(atdItemCon.lstAtdItemTarget)
					.orElse(Collections.emptyList()).stream().filter(atdItemTarget -> (atdItemTarget.targetAtr == 2))
					.findFirst().get().krcstErAlAtdTargetPK.attendanceItemId);
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
		}
		return atdItemConDomain;
	}

	public void setCompanyId(String companyId) {
		this.krcmtErAlConditionPK.cid = companyId;
	}
}
