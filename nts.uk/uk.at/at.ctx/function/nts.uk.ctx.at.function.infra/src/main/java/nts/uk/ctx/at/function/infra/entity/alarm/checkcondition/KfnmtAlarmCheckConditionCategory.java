package nts.uk.ctx.at.function.infra.entity.alarm.checkcondition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.annual.ScheduleAnnualAlarmCheckCond;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.schedaily.AlarmType;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.schedaily.ScheduleDailyAlarmCheckCond;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.schemonthly.ScheduleMonthlyAlarmCheckCond;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.weekly.WeeklyAlarmCheckCond;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.AlarmCheckConditionByCategory;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.AlarmCheckConditionCode;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.AlarmCheckTargetCondition;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.ExtractionCondition;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.agree36.AgreeCondOt;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.agree36.AgreeConditionError;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.agree36.AlarmChkCondAgree36;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.agree36.ErrorAlarm;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.agree36.MessageDisp;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.agree36.Number;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.agree36.OverTime;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.agree36.Period;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.agree36.UseClassification;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.annualholiday.AnnualHolidayAlarmCondition;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.appapproval.AppApprovalAlarmCheckCondition;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.daily.DailyAlarmCondition;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.fourweekfourdayoff.AlarmCheckCondition4W4D;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.master.MasterCheckAlarmCheckCondition;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.monthly.MonAlarmCheckCon;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.multimonth.MulMonAlarmCond;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.agree36.Kfnmt36AgreeCondErr;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.agree36.Kfnmt36AgreeCondOt;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.annualholiday.KfnmtAlCheckConAg;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.annualholiday.KfnmtAlCheckSubConAg;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.appapproval.KrqmtAppApprovalCondition;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.daily.KrcmtDailyAlarmCondition;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.fourweekfourdayoff.KfnmtAlarmCheck4W4D;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.mastercheck.KrcmtMasterCheckAlarmCheckCondition;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.monthly.KfnmtMonAlarmCheckCon;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.multimonth.KfnmtMulMonAlarmCond;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.schedule.KfndtScheCondDayLink;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.schedule.KfndtScheCondMonthLink;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.schedule.KfndtScheCondYearLink;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.weekly.KfndtWeekCondAlarmLink;
import nts.uk.ctx.at.shared.dom.alarmList.AlarmCategory;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 
 * @author HungTT
 *
 */

@NoArgsConstructor
@Entity
@Table(name = "KFNMT_ALST_CHK")
public class KfnmtAlarmCheckConditionCategory extends ContractUkJpaEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KfnmtAlarmCheckConditionCategoryPk pk;

	@Basic
	@Column(name = "NAME")
	public String name;

	@Basic
	@Column(name = "EXTRACT_TARGET_COND_ID")
	public String targetConditionId;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "EXTRACT_TARGET_COND_ID", insertable = false, updatable = false)
	public KfnmtAlarmCheckTargetCondition targetCondition;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "condition", orphanRemoval = true)
	public List<KfnmtAlarmCheckConditionCategoryRole> listAvailableRole;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "condition", orphanRemoval = true)
	public KrcmtDailyAlarmCondition dailyAlarmCondition;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "condition", orphanRemoval = true)
	public KfnmtAlarmCheck4W4D schedule4W4DAlarmCondition;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "condition", orphanRemoval = true)
	public KfnmtMonAlarmCheckCon kfnmtMonAlarmCheckCon;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "condition", orphanRemoval = true)
	public List<Kfnmt36AgreeCondErr> listCondErr;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "condition", orphanRemoval = true)
	public List<Kfnmt36AgreeCondOt> listCondOt;
	
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "condition", orphanRemoval = true)
	public KfnmtMulMonAlarmCond mulMonAlarmCond;
	
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "condition", orphanRemoval = true)
	public KfnmtAlCheckConAg alCheckConAg;
	
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "condition", orphanRemoval = true)
	public KfnmtAlCheckSubConAg alCheckSubConAg;
	

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "condition", orphanRemoval = true)
	public List<KfndtScheCondDayLink> scheduleCondDayLinks;


	@OneToOne(cascade = CascadeType.ALL, mappedBy = "condition", orphanRemoval = true)
	public KrcmtMasterCheckAlarmCheckCondition masterCheckAlarmCheckCon;
	
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "condition", orphanRemoval = true)
	public KrqmtAppApprovalCondition appApprovalCondition;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "condition", orphanRemoval = true)
	public List<KfndtScheCondMonthLink> scheduleCondMonLinks;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "condition", orphanRemoval = true)
	public List<KfndtScheCondYearLink> scheduleCondYearLinks;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "condition", orphanRemoval = true)
	public List<KfndtWeekCondAlarmLink> scheduleCondWeeklyLinks;
	
	@Override
	protected Object getKey() {
		return this.pk;
	}

	public KfnmtAlarmCheckConditionCategory(String companyId, int category, String code, String name,
			KfnmtAlarmCheckTargetCondition targetCondition,
			List<KfnmtAlarmCheckConditionCategoryRole> listAvailableRole, KrcmtDailyAlarmCondition dailyAlarmCondition,
			KfnmtAlarmCheck4W4D schedule4W4DAlarmCondition, KfnmtMonAlarmCheckCon kfnmtMonAlarmCheckCon,
			List<Kfnmt36AgreeCondErr> listCondErr, List<Kfnmt36AgreeCondOt> listCondOt,
			KfnmtMulMonAlarmCond mulMonAlarmCond, KfnmtAlCheckConAg alCheckConAg,
			KfnmtAlCheckSubConAg alCheckSubConAg,KrqmtAppApprovalCondition appApprovalCondition, KrcmtMasterCheckAlarmCheckCondition masterCheckAlarmCheckCon,List<KfndtScheCondDayLink> scheduleCondDayLinks, List<KfndtScheCondMonthLink> scheduleCondMonLinks, List<KfndtScheCondYearLink> scheduleCondYearLinks,
			List<KfndtWeekCondAlarmLink> scheduleCondWeeklyLinks) {

		super();
		this.pk = new KfnmtAlarmCheckConditionCategoryPk(companyId, category, code);
		this.name = name;
		this.targetConditionId = targetCondition.id;
		this.targetCondition = targetCondition;
		this.listAvailableRole = listAvailableRole;
		this.dailyAlarmCondition = dailyAlarmCondition;
		this.schedule4W4DAlarmCondition = schedule4W4DAlarmCondition;
		this.kfnmtMonAlarmCheckCon = kfnmtMonAlarmCheckCon;
		this.listCondErr = listCondErr;
		this.listCondOt = listCondOt;
		this.mulMonAlarmCond = mulMonAlarmCond;
		this.alCheckConAg = alCheckConAg;
		this.alCheckSubConAg = alCheckSubConAg;
		this.scheduleCondDayLinks = scheduleCondDayLinks;
		this.appApprovalCondition = appApprovalCondition;
		this.masterCheckAlarmCheckCon = masterCheckAlarmCheckCon;
		this.scheduleCondMonLinks = scheduleCondMonLinks;
		this.scheduleCondYearLinks = scheduleCondYearLinks;
		this.scheduleCondWeeklyLinks = scheduleCondWeeklyLinks;
	}
	/**
	 * convert from entity to domain 
	 * @param entity
	 * @return
	 */
	public static AgreeConditionError toDomainEr(Kfnmt36AgreeCondErr entity) {
		return new AgreeConditionError(entity.kfnmt36AgreeCondErrPK.id, 
											entity.kfnmt36AgreeCondErrPK.companyId, 
											EnumAdaptor.valueOf(entity.kfnmt36AgreeCondErrPK.category, AlarmCategory.class), 
											new AlarmCheckConditionCode(entity.kfnmt36AgreeCondErrPK.code), 
											EnumAdaptor.valueOf(entity.useAtr, UseClassification.class), 
											EnumAdaptor.valueOf(entity.period, Period.class), 
											EnumAdaptor.valueOf(entity.errorAlarm, ErrorAlarm.class), 
											entity.messageDisp == null ? null : new MessageDisp(entity.messageDisp));
	}
	
	public static AgreeCondOt toDomainOt(Kfnmt36AgreeCondOt ent){
		return new AgreeCondOt(ent.kfnmt36AgreeCondOtPK.id, 
									ent.kfnmt36AgreeCondOtPK.companyId, 
									EnumAdaptor.valueOf(ent.kfnmt36AgreeCondOtPK.category, AlarmCategory.class), 
									new AlarmCheckConditionCode(ent.kfnmt36AgreeCondOtPK.code), 
									ent.kfnmt36AgreeCondOtPK.no, 
									new OverTime(ent.ot36), 
									new Number(ent.excessNum), 
									ent.messageDisp == null ? null : new MessageDisp(ent.messageDisp));
	}

	public static AlarmCheckConditionByCategory toDomain(KfnmtAlarmCheckConditionCategory entity) {
		ExtractionCondition extractionCondition = null;
		AlarmCategory category = AlarmCategory.values()[entity.pk.category];
		switch (category) {
		case DAILY:
			extractionCondition = entity.dailyAlarmCondition == null ? null : entity.dailyAlarmCondition.toDomain();
			break;
		case SCHEDULE_4WEEK:
			extractionCondition = entity.schedule4W4DAlarmCondition == null ? null
					: entity.schedule4W4DAlarmCondition.toDomain();
			break;
		case MONTHLY:
			extractionCondition = entity.kfnmtMonAlarmCheckCon == null ? null : entity.kfnmtMonAlarmCheckCon.toDomain();
			break;
		case MULTIPLE_MONTH:
			extractionCondition = entity.mulMonAlarmCond == null ? null : entity.mulMonAlarmCond.toDomain();
			break;
		case ATTENDANCE_RATE_FOR_HOLIDAY:
			extractionCondition = entity.alCheckConAg == null && entity.alCheckSubConAg == null ? null
					: new AnnualHolidayAlarmCondition(
							entity.alCheckConAg == null ? null : entity.alCheckConAg.toDomain(),
							entity.alCheckSubConAg == null ? null : entity.alCheckSubConAg.toDomain());
			break;
		case SCHEDULE_DAILY:
			String listFixedItem = "";
			String listOptionalItem = "";
			if (entity.scheduleCondDayLinks.size() > 0) {
				listFixedItem = entity.scheduleCondDayLinks.stream().filter(x -> x.pk.alarmType == AlarmType.UNIQUE_EXTRACTION.value).findFirst().get().eralCheckId;
				listOptionalItem = entity.scheduleCondDayLinks.stream().filter(x -> x.pk.alarmType == AlarmType.ARBITRARY_EXTRACTION_CONDITIONS.value).findFirst().get().eralCheckId;
			}
			
			extractionCondition = new ScheduleDailyAlarmCheckCond(listOptionalItem, listFixedItem);
			break;
		case APPLICATION_APPROVAL:
			extractionCondition = entity.appApprovalCondition == null ? null : entity.appApprovalCondition.toDomain();
			break;
		case MASTER_CHECK:
			extractionCondition = entity.masterCheckAlarmCheckCon == null ? null : entity.masterCheckAlarmCheckCon.toDomain();
			break;
		case SCHEDULE_MONTHLY:
			String listFixedMonItem = "";
			String listOptionalMonItem = "";
			if (entity.scheduleCondMonLinks.size() > 0) {
				listFixedMonItem = entity.scheduleCondMonLinks.stream().filter(x -> x.pk.alarmType == AlarmType.UNIQUE_EXTRACTION.value).findFirst().get().eralCheckId;
				listOptionalMonItem = entity.scheduleCondMonLinks.stream().filter(x -> x.pk.alarmType == AlarmType.ARBITRARY_EXTRACTION_CONDITIONS.value).findFirst().get().eralCheckId;
			}
			
			extractionCondition = new ScheduleMonthlyAlarmCheckCond(listOptionalMonItem, listFixedMonItem);
			break;
		case SCHEDULE_YEAR:
			String listOptionalYearItem = "";
			if (entity.scheduleCondYearLinks.size() > 0) {
				listOptionalYearItem = entity.scheduleCondYearLinks.stream().findFirst().get().eralCheckId;
			}
			
			extractionCondition = new ScheduleAnnualAlarmCheckCond(listOptionalYearItem);
			break;
		case WEEKLY:
			String listOptionalWeeklyItem = "";
			if (entity.scheduleCondWeeklyLinks.size() > 0) {
				listOptionalWeeklyItem = entity.scheduleCondWeeklyLinks.stream().findFirst().get().eralCheckId;
			}
			
			extractionCondition = new WeeklyAlarmCheckCond(listOptionalWeeklyItem);
			break;
		default:
			break;
		}
		return new AlarmCheckConditionByCategory(entity.pk.companyId, entity.pk.category, entity.pk.code, entity.name,
				new AlarmCheckTargetCondition(entity.targetConditionId,
						entity.targetCondition.filterByBusinessType,
						entity.targetCondition.filterByJobTitle,
						entity.targetCondition.filterByEmployment,
						entity.targetCondition.filterByClassification,
						entity.targetCondition.listBusinessType.stream().map(item -> item.pk.businessTypeCode)
								.collect(Collectors.toList()),
						entity.targetCondition.listJobTitle.stream().map(item -> item.pk.jobTitleId)
								.collect(Collectors.toList()),
						entity.targetCondition.listEmployment.stream().map(item -> item.pk.employmentCode)
								.collect(Collectors.toList()),
						entity.targetCondition.listClassification.stream().map(item -> item.pk.classificationCode)
								.collect(Collectors.toList())),
				entity.listAvailableRole.stream().map(item -> item.pk.roleId).collect(Collectors.toList()),
				extractionCondition, new AlarmChkCondAgree36(entity.listCondErr.stream().map(c -> toDomainEr(c)).collect(Collectors.toList()),
																entity.listCondOt.stream().map(x -> toDomainOt(x)).collect(Collectors.toList())));
	}

	public static KfnmtAlarmCheckConditionCategory fromDomain(AlarmCheckConditionByCategory domain) {
		List<KfndtScheCondDayLink> scheduleCondDayLinks = new ArrayList<>();
		List<KfndtScheCondMonthLink> scheduleCondMonthLinks = new ArrayList<>();
		List<KfndtScheCondYearLink> scheduleCondYearLinks = new ArrayList<>();
		List<KfndtWeekCondAlarmLink> scheduleCondWeeklyLinks = new ArrayList<>();
		if (domain.getCategory() == AlarmCategory.SCHEDULE_DAILY && (ScheduleDailyAlarmCheckCond) domain.getExtractionCondition() != null) {
			ScheduleDailyAlarmCheckCond scheDailyAlarmCheckCondDomain = (ScheduleDailyAlarmCheckCond) domain.getExtractionCondition();
			if (StringUtils.isNotEmpty(scheDailyAlarmCheckCondDomain.getListFixedItem())) {
				scheduleCondDayLinks.add(KfndtScheCondDayLink.toEntity(
						domain.getCompanyId(), scheDailyAlarmCheckCondDomain.getListFixedItem(),
						domain.getCode().v(), domain.getCategory().value, 
						AlarmType.UNIQUE_EXTRACTION.value));
			}
			if (StringUtils.isNotEmpty(scheDailyAlarmCheckCondDomain.getListOptionalItem())) {
				scheduleCondDayLinks.add(KfndtScheCondDayLink.toEntity(
						domain.getCompanyId(), scheDailyAlarmCheckCondDomain.getListOptionalItem(),
						domain.getCode().v(), domain.getCategory().value, 
						AlarmType.ARBITRARY_EXTRACTION_CONDITIONS.value));
			}
		} else if (domain.getCategory() == AlarmCategory.SCHEDULE_MONTHLY) {
			scheduleCondMonthLinks = fromDomainScheCondMonthLink(domain);
		} else if (domain.getCategory() == AlarmCategory.SCHEDULE_YEAR) {
			scheduleCondYearLinks = fromDomainScheCondYearLink(domain);
		} else if (domain.getCategory() == AlarmCategory.WEEKLY) {
			scheduleCondWeeklyLinks = fromDomainScheCondWeeklyLink(domain);
		}
		
		return new KfnmtAlarmCheckConditionCategory(domain.getCompanyId(), domain.getCategory().value,
				domain.getCode().v(), domain.getName().v(),
				new KfnmtAlarmCheckTargetCondition(domain.getExtractTargetCondition().getId(),
						domain.getExtractTargetCondition().isFilterByEmployment() ? 1 : 0,
						domain.getExtractTargetCondition().isFilterByClassification() ? 1 : 0,
						domain.getExtractTargetCondition().isFilterByJobTitle() ? 1 : 0,
						domain.getExtractTargetCondition().isFilterByBusinessType() ? 1 : 0,
						domain.getExtractTargetCondition().getLstEmploymentCode().stream()
								.map(item -> new KfnmtAlarmCheckTargetEmployment(
										domain.getExtractTargetCondition().getId(), item))
								.collect(Collectors.toList()),
						domain.getExtractTargetCondition().getLstClassificationCode().stream()
								.map(item -> new KfnmtAlarmCheckTargetClassification(
										domain.getExtractTargetCondition().getId(), item))
								.collect(Collectors.toList()),
						domain.getExtractTargetCondition().getLstJobTitleId().stream()
								.map(item -> new KfnmtAlarmCheckTargetJobTitle(
										domain.getExtractTargetCondition().getId(), item))
								.collect(Collectors.toList()),
						domain.getExtractTargetCondition().getLstBusinessTypeCode().stream()
								.map(item -> new KfnmtAlarmCheckTargetBusinessType(
										domain.getExtractTargetCondition().getId(), item))
								.collect(Collectors.toList())),
				domain.getListRoleId().stream()
						.map(item -> new KfnmtAlarmCheckConditionCategoryRole(domain.getCompanyId(),
								domain.getCategory().value, domain.getCode().v(), item))
						.collect(
								Collectors.toList()),
				domain.getCategory() == AlarmCategory.DAILY
						? KrcmtDailyAlarmCondition.toEntity(domain.getCompanyId(), domain.getCode(),
								domain.getCategory(), (DailyAlarmCondition) domain.getExtractionCondition())
						: null,
				domain.getCategory() == AlarmCategory.SCHEDULE_4WEEK
						? KfnmtAlarmCheck4W4D.toEntity((AlarmCheckCondition4W4D) domain.getExtractionCondition(),
								domain.getCompanyId(), domain.getCategory(), domain.getCode())
						: null,
				domain.getCategory() == AlarmCategory.MONTHLY
						? KfnmtMonAlarmCheckCon.toEntity(domain.getCompanyId(), domain.getCode().v(),
								domain.getCategory().value, (MonAlarmCheckCon) domain.getExtractionCondition())
						: null,
				domain.getCategory() == AlarmCategory.AGREEMENT
						? (domain.getAlarmChkCondAgree36().getListCondError() == null ? null : domain.getAlarmChkCondAgree36().getListCondError().stream()
								.map(c -> Kfnmt36AgreeCondErr.toEntity(c)).collect(Collectors.toList())): null,
								
				domain.getCategory() == AlarmCategory.AGREEMENT
						? (domain.getAlarmChkCondAgree36().getListCondOt() == null ? null : domain.getAlarmChkCondAgree36().getListCondOt().stream()
								.map(a -> Kfnmt36AgreeCondOt.toEnity(a)).collect(Collectors.toList())) : null,
				domain.getCategory() == AlarmCategory.MULTIPLE_MONTH
								? KfnmtMulMonAlarmCond.toEntity(domain.getCompanyId(), domain.getCode().v(),
										domain.getCategory().value, (MulMonAlarmCond) domain.getExtractionCondition()) : null,
				domain.getCategory() == AlarmCategory.ATTENDANCE_RATE_FOR_HOLIDAY  
				&& (AnnualHolidayAlarmCondition) domain.getExtractionCondition() != null
				&& ((AnnualHolidayAlarmCondition) domain.getExtractionCondition()).getAlarmCheckConAgr() != null
				? KfnmtAlCheckConAg.toEntity(domain.getCompanyId(), domain.getCode().v(),
						domain.getCategory().value, ((AnnualHolidayAlarmCondition) domain.getExtractionCondition()).getAlarmCheckConAgr()) : null,
				domain.getCategory() == AlarmCategory.ATTENDANCE_RATE_FOR_HOLIDAY 
			    && (AnnualHolidayAlarmCondition) domain.getExtractionCondition() != null
			    && ((AnnualHolidayAlarmCondition) domain.getExtractionCondition()).getAlarmCheckSubConAgr() != null
				? KfnmtAlCheckSubConAg.toEntity(domain.getCompanyId(), domain.getCode().v(),
						domain.getCategory().value, ((AnnualHolidayAlarmCondition) domain.getExtractionCondition()).getAlarmCheckSubConAgr()): null,
				domain.getCategory() == AlarmCategory.APPLICATION_APPROVAL
						? KrqmtAppApprovalCondition.toEntity(domain.getCompanyId(), domain.getCode(),
								domain.getCategory(), (AppApprovalAlarmCheckCondition) domain.getExtractionCondition())
						: null,
				domain.getCategory() == AlarmCategory.MASTER_CHECK ? KrcmtMasterCheckAlarmCheckCondition.toEntity(domain.getCompanyId(), domain.getCode(),
								domain.getCategory(), (MasterCheckAlarmCheckCondition) domain.getExtractionCondition()) : null, scheduleCondDayLinks,
								scheduleCondMonthLinks, scheduleCondYearLinks, scheduleCondWeeklyLinks
		);
	}
	
	private static List<KfndtScheCondMonthLink> fromDomainScheCondMonthLink(AlarmCheckConditionByCategory domain) {
		List<KfndtScheCondMonthLink> scheduleCondMonLinks = new ArrayList<>();
		if ((ScheduleMonthlyAlarmCheckCond) domain.getExtractionCondition() == null) {
			return scheduleCondMonLinks;
		}
		
		ScheduleMonthlyAlarmCheckCond scheDailyAlarmCheckCondDomain = (ScheduleMonthlyAlarmCheckCond) domain.getExtractionCondition();
		if (StringUtils.isNotEmpty(scheDailyAlarmCheckCondDomain.getListFixedItem())) {
			scheduleCondMonLinks.add(KfndtScheCondMonthLink.toEntity(
					domain.getCompanyId(), scheDailyAlarmCheckCondDomain.getListFixedItem(),
					domain.getCode().v(), domain.getCategory().value, 
					AlarmType.UNIQUE_EXTRACTION.value));
		}
		if (StringUtils.isNotEmpty(scheDailyAlarmCheckCondDomain.getListOptionalItem())) {
			scheduleCondMonLinks.add(KfndtScheCondMonthLink.toEntity(
					domain.getCompanyId(), scheDailyAlarmCheckCondDomain.getListOptionalItem(),
					domain.getCode().v(), domain.getCategory().value, 
					AlarmType.ARBITRARY_EXTRACTION_CONDITIONS.value));
		}
		
		return scheduleCondMonLinks;
	}
	
	private static List<KfndtScheCondYearLink> fromDomainScheCondYearLink(AlarmCheckConditionByCategory domain) {
		List<KfndtScheCondYearLink> scheduleCondYearLinks = new ArrayList<>();
		if ((ScheduleAnnualAlarmCheckCond) domain.getExtractionCondition() == null) {
			return scheduleCondYearLinks;
		}
		
		ScheduleAnnualAlarmCheckCond scheDailyAlarmCheckCondDomain = (ScheduleAnnualAlarmCheckCond) domain.getExtractionCondition();
		if (StringUtils.isNotEmpty(scheDailyAlarmCheckCondDomain.getListOptionalItem())) {
			scheduleCondYearLinks.add(KfndtScheCondYearLink.toEntity(
					domain.getCompanyId(), scheDailyAlarmCheckCondDomain.getListOptionalItem(),
					domain.getCode().v(), domain.getCategory().value, 
					AlarmType.ARBITRARY_EXTRACTION_CONDITIONS.value));
		}
		
		return scheduleCondYearLinks;
	}
	
	private static List<KfndtWeekCondAlarmLink> fromDomainScheCondWeeklyLink(AlarmCheckConditionByCategory domain) {
		List<KfndtWeekCondAlarmLink> scheduleCondWeeklyLinks = new ArrayList<>();
		if ((WeeklyAlarmCheckCond) domain.getExtractionCondition() == null) {
			return scheduleCondWeeklyLinks;
		}
		
		WeeklyAlarmCheckCond scheDailyAlarmCheckCondDomain = (WeeklyAlarmCheckCond) domain.getExtractionCondition();
		if (StringUtils.isNotEmpty(scheDailyAlarmCheckCondDomain.getListOptionalItem())) {
			scheduleCondWeeklyLinks.add(KfndtWeekCondAlarmLink.toEntity(
					domain.getCompanyId(), scheDailyAlarmCheckCondDomain.getListOptionalItem(),
					domain.getCode().v(), domain.getCategory().value, 
					AlarmType.ARBITRARY_EXTRACTION_CONDITIONS.value));
		}
		
		return scheduleCondWeeklyLinks;
	}

}
