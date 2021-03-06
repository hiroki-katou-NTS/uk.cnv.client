package nts.uk.ctx.at.function.infra.entity.processexecution;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
//import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.function.dom.processexecution.ExecutionCode;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.ExecutionTaskSetting;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.OneDayRepeatInterval;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.RepeatDetailSetting;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.TaskEndDate;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.TaskEndTime;
//import nts.uk.ctx.at.function.dom.processexecution.tasksetting.detail.DailyDaySetting;
//import nts.uk.ctx.at.function.dom.processexecution.tasksetting.detail.RepeatDetailSettingDaily;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.detail.RepeatDetailSettingMonthly;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.detail.RepeatDetailSettingWeekly;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.detail.RepeatMonthDaysSelect;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.detail.RepeatMonthSelect;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.detail.RepeatWeekDaysSelect;
//import nts.uk.ctx.at.function.dom.processexecution.tasksetting.detail.WeeklyWeekSetting;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.enums.EndDateClassification;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.enums.EndTimeClassification;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.enums.OneDayRepeatClassification;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.enums.RepeatContentItem;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.primitivevalue.EndTime;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.primitivevalue.OneDayRepeatIntervalDetail;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.primitivevalue.StartTime;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Entity
@Table(name="KFNMT_AUTOEXEC_TASK")
@AllArgsConstructor
@NoArgsConstructor
public class KfnmtExecutionTaskSetting extends ContractUkJpaEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	/* ????????? */
	@EmbeddedId
    public KfnmtExecutionTaskSettingPK kfnmtExecTaskSettingPK;

	/** The exclus ver. */
	@Version
	@Column(name = "EXCLUS_VER")
	private Long exclusVer;
	
	/* ????????? */
	@Column(name = "START_DATE")
	public GeneralDate startDate;
	
	/* ???????????? */
	@Column(name = "START_TIME")
	public Integer startTime;
	
	/* ????????????????????????????????? */
	@Column(name = "END_TIME_CLS")
	public Integer endTimeCls;
	
	/* ???????????? */
	@Column(name = "END_TIME")
	public Integer endTime;
	
	/* ????????????????????????????????? */
	@Column(name = "ONE_DAY_REP_CLS")
	public Integer oneDayRepCls;
	
	/* ?????????????????? */
	@Column(name = "ONE_DAY_REP_INR")
	public Integer oneDayRepInterval;
	
	/* ?????????????????? */
	@Column(name = "REP_CONTENT")
	public Integer repeatContent;
	
	/* ?????????????????????????????? */
	@Column(name = "END_DATE_CLS")
	public Integer endDateCls;
	
	/* ????????????????????? */
	@Column(name = "END_DATE")
	public GeneralDate endDate;
	
	/* ???????????????????????? */
	@Column(name = "ENABLED_SETTING")
	public Integer enabledSetting;
	
	/* ?????????????????? */
	@Column(name = "NEXT_EXEC_DATE_TIME")
	public GeneralDateTime nextExecDateTime;
	
	/* ??? */
	@Column(name = "MONDAY")
	public Integer monday;
	
	/* ??? */
	@Column(name = "TUESDAY")
	public Integer tuesday;
	
	/* ??? */
	@Column(name = "WEDNESDAY")
	public Integer wednesday;
	
	/* ??? */
	@Column(name = "THURSDAY")
	public Integer thursday;
	
	/* ??? */
	@Column(name = "FRIDAY")
	public Integer friday;
	
	/* ??? */
	@Column(name = "SATURDAY")
	public Integer saturday;
	
	/* ??? */
	@Column(name = "SUNDAY")
	public Integer sunday;
	
	/* 1??? */
	@Column(name = "REP_INR_JAN")
	public Integer january;
	
	/* 2??? */
	@Column(name = "REP_INR_FEB")
	public Integer february;
	
	/* 3??? */
	@Column(name = "REP_INR_MAR")
	public Integer march;
	
	/* 4??? */
	@Column(name = "REP_INR_APR")
	public Integer april;
	
	/* 5??? */
	@Column(name = "REP_INR_MAY")
	public Integer may;
	
	/* 6??? */
	@Column(name = "REP_INR_JUN")
	public Integer june;
	
	/* 7??? */
	@Column(name = "REP_INR_JUL")
	public Integer july;
	
	/* 8??? */
	@Column(name = "REP_INR_AUG")
	public Integer august;
	
	/* 9??? */
	@Column(name = "REP_INR_SEP")
	public Integer september;
	
	/* 10??? */
	@Column(name = "REP_INR_OCT")
	public Integer october;
	
	/* 11??? */
	@Column(name = "REP_INR_NOV")
	public Integer november;
	
	/* 12??? */
	@Column(name = "REP_INR_DEC")
	public Integer december;
	
	/* ??????????????????ID */
	@Column(name = "SCHEDULE_ID")
	public String scheduleId;
	
	/* ??????????????????????????????ID*/
	@Column(name = "END_SCHEDULE_ID")
	public String endScheduleId;
	
	@Column(name = "REPEAT_SCHEDULE_ID")
	public String repeatScheduleId;
	
	@OneToMany(mappedBy="execTaskSetting", cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "KFNMT_REP_MONTH_DATE")
	public List<KfnmtRepeatMonthDay> repeatMonthDateList;
	
	@Override
	protected Object getKey() {
		return this.kfnmtExecTaskSettingPK;
	}
	
	/**
	 * Convert entity to domain
	 * @return WorkplaceManager object
	 */
	public ExecutionTaskSetting toDomain() {
		// ???????????? 
		TaskEndTime endTime = new TaskEndTime(
				EnumAdaptor.valueOf(this.endTimeCls, EndTimeClassification.class),
				Optional.ofNullable(this.endTime).map(EndTime::new));
		
		// ??????????????????
		OneDayRepeatInterval oneDayRepInr = new OneDayRepeatInterval(
				Optional.ofNullable(this.oneDayRepInterval).map(data -> EnumAdaptor.valueOf(data, OneDayRepeatIntervalDetail.class)),
				EnumAdaptor.valueOf(this.oneDayRepCls, OneDayRepeatClassification.class));
				
		// ?????????????????????
		TaskEndDate endDate = new TaskEndDate(
				EnumAdaptor.valueOf(this.endDateCls, EndDateClassification.class),
				Optional.ofNullable(this.endDate));
		
		
		// ????????????????????????(??????)
		RepeatDetailSettingWeekly weekly =
				new RepeatDetailSettingWeekly(
						new RepeatWeekDaysSelect(this.monday == 1 ? true : false,
												this.tuesday == 1 ? true : false,
												this.wednesday == 1 ? true : false,
												this.thursday == 1 ? true : false,
												this.friday == 1 ? true : false,
												this.saturday == 1 ? true : false,
												this.sunday == 1 ? true : false));
		
		// ????????????????????????(??????)
		List<RepeatMonthDaysSelect> days =
				this.repeatMonthDateList
						.stream()
							.map(x -> EnumAdaptor.valueOf(x.kfnmtRepMonthDayPK.monthDay, RepeatMonthDaysSelect.class))
								.collect(Collectors.toList());
		RepeatMonthSelect months =
				new RepeatMonthSelect(this.january == 1 ? true : false,
									this.february == 1 ? true : false,
									this.march == 1 ? true : false,
									this.april == 1 ? true : false,
									this.may == 1 ? true : false,
									this.june == 1 ? true : false,
									this.july == 1 ? true : false,
									this.august == 1 ? true : false,
									this.september == 1 ? true : false,
									this.october == 1 ? true : false,
									this.november == 1 ? true : false,
									this.december == 1 ? true : false);
		RepeatDetailSettingMonthly monthly = new RepeatDetailSettingMonthly(days, months);
		
		// ????????????????????????
		RepeatDetailSetting detailSetting = new RepeatDetailSetting(Optional.of(weekly), Optional.of(monthly));
		
		return new ExecutionTaskSetting(oneDayRepInr,
										new ExecutionCode(this.kfnmtExecTaskSettingPK.execItemCd),
										this.kfnmtExecTaskSettingPK.companyId,
										this.enabledSetting == 1 ? true : false,
										this.nextExecDateTime,
										endDate,
										endTime,
										EnumAdaptor.valueOf(this.repeatContent, RepeatContentItem.class),
										detailSetting,
										startDate,
										new StartTime(this.startTime),this.scheduleId,this.endScheduleId,
										this.repeatScheduleId);
	}
	
	/**
	 * Convert domain to entity
	 * @param domain ExecutionTaskSetting object
	 * @return KfnmtExecutionTaskSetting object
	 */
	public static KfnmtExecutionTaskSetting toEntity(ExecutionTaskSetting domain) {
		return new KfnmtExecutionTaskSetting(
				new KfnmtExecutionTaskSettingPK(
						domain.getCompanyId(), 
						domain.getExecItemCd().v()),
				domain.getVersion(),
				domain.getStartDate(),
				domain.getStartTime().v(),
				domain.getEndTime().getEndTimeCls().value,
				domain.getEndTime().getEndTime().map(EndTime::v).orElse(null),
				domain.getOneDayRepInr().getOneDayRepCls().value,
				domain.getOneDayRepInr().getDetail().map(data -> data.value).orElse(null),
				domain.getContent().value,
				domain.getEndDate().getEndDateCls().value,
				domain.getEndDate().getEndDate().orElse(null),
				domain.isEnabledSetting() ? 1 : 0,
				domain.getNextExecDateTime().orElse(null),
				domain.getDetailSetting().getWeekly().map(data -> data.getWeekdaySetting().getMonday().value).orElse(null),
				domain.getDetailSetting().getWeekly().map(data -> data.getWeekdaySetting().getTuesday().value).orElse(null),
				domain.getDetailSetting().getWeekly().map(data -> data.getWeekdaySetting().getWednesday().value).orElse(null),
				domain.getDetailSetting().getWeekly().map(data -> data.getWeekdaySetting().getThursday().value).orElse(null),
				domain.getDetailSetting().getWeekly().map(data -> data.getWeekdaySetting().getFriday().value).orElse(null),
				domain.getDetailSetting().getWeekly().map(data -> data.getWeekdaySetting().getSaturday().value).orElse(null),
				domain.getDetailSetting().getWeekly().map(data -> data.getWeekdaySetting().getSunday().value).orElse(null),
				domain.getDetailSetting().getMonthly().map(data -> data.getMonth().getJanuary().value).orElse(null),
				domain.getDetailSetting().getMonthly().map(data -> data.getMonth().getFebruary().value).orElse(null),
				domain.getDetailSetting().getMonthly().map(data -> data.getMonth().getMarch().value).orElse(null),
				domain.getDetailSetting().getMonthly().map(data -> data.getMonth().getApril().value).orElse(null),
				domain.getDetailSetting().getMonthly().map(data -> data.getMonth().getMay().value).orElse(null),
				domain.getDetailSetting().getMonthly().map(data -> data.getMonth().getJune().value).orElse(null),
				domain.getDetailSetting().getMonthly().map(data -> data.getMonth().getJuly().value).orElse(null),
				domain.getDetailSetting().getMonthly().map(data -> data.getMonth().getAugust().value).orElse(null),
				domain.getDetailSetting().getMonthly().map(data -> data.getMonth().getSeptember().value).orElse(null),
				domain.getDetailSetting().getMonthly().map(data -> data.getMonth().getOctober().value).orElse(null),
				domain.getDetailSetting().getMonthly().map(data -> data.getMonth().getNovember().value).orElse(null),
				domain.getDetailSetting().getMonthly().map(data -> data.getMonth().getDecember().value).orElse(null),
				domain.getScheduleId(),		
				domain.getEndScheduleId().orElse(null),
				domain.getRepeatScheduleId().orElse(null),
				Collections.emptyList());
	}
}
