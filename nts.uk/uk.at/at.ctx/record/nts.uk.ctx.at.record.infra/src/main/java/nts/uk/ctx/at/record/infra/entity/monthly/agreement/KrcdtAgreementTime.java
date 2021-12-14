package nts.uk.ctx.at.record.infra.entity.monthly.agreement;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeBreakdown;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfManagePeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeStatusOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.AgreementOneMonthTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.OneMonthErrorAlarmTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.OneMonthTime;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * エンティティ：管理期間の36協定時間
 * @author shuichi_ishida
 */
@Entity
@Table(name = "KRCDT_36AGR_TIME")
@NoArgsConstructor
public class KrcdtAgreementTime extends ContractUkJpaEntity implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** プライマリキー */
	@EmbeddedId
	public KrcdtAgreementTimePK id;
	
	@Column(name = "AGREEMENT_TIME")
	public int agreementTime;/**36協定時間*/
	@Column(name = "AGR_ERROR_TIME")
	public int agreementErrorTime;/**限度エラー時間*/
	@Column(name = "AGR_ALARM_TIME")
	public int agreementAlarmTime;/**限度アラーム時間*/
	@Column(name = "AGR_LIMIT_TIME")
	public int agreementLimitTime;/**上限時間*/
	@Column(name = "EXCEPT_AGREEMENT_TIME")
	public int exceptAgreementTime;/**法定上限対象時間*/
	@Column(name = "EXC_ERROR_TIME")
	public int exceptAgreementErrorTime;/**法定上限対象エラー時間*/
	@Column(name = "EXC_ALARM_TIME")
	public int exceptAgreementAlarmTime;/**法定上限対象アラーム時間*/
	@Column(name = "EXC_LIMIT_TIME")
	public int exceptAgreementLimitTime;/**法定上限対象上限時間*/
	@Column(name = "STATUS")
	public int status;/**"状態 */
	@Column(name = "OVER_TIME")
	public int overTime;/**残業時間*/
	@Column(name = "TRANS_OVER_TIME")
	public int transferOverTime;/**振替残業時間*/
	@Column(name = "HDWK_TIME")
	public int holidayWorkTime;/**法定外休出時間*/
	@Column(name = "TRANS_HDWK_TIME")
	public int transferHolidayWorkTime;/**法定外振替時間*/
	@Column(name = "LEGAL_HDWK_TIME")
	public int legalHolidayWorkTime;/**法定内休出時間*/
	@Column(name = "LEGAL_TRANS_HDWK_TIME")
	public int legalTransferHolidayWorkTime;/**法定内振替時間*/
	@Column(name = "FLEX_LEGAL_TIME")
	public int flexLegalTime;/**フレックス法定内時間*/
	@Column(name = "FLEX_ILLEGAL_TIME")
	public int flexIllegalTime;/**フレックス法定外時間*/
	@Column(name = "WEEK_PREMIUM_TIME")
	public int weekPremiumTime;/**週割増合計時間*/
	@Column(name = "MONTH_PREMIUM_TIME")
	public int monthPremiumTime;/**月割増合計時間*/
	@Column(name = "PRESCR_PREMIUM_TIME")
	public int withinPremiumTime;/**所定内割増時間*/
	@Column(name = "EXTRA_WORK_TIME")
	public int temporaryTime;/**臨時時間*/
	
	public KrcdtAgreementTime(KrcdtAgreementTimePK id) {
		this.id = id;
	}
	
	/**
	 * キー取得
	 */
	@Override
	protected Object getKey() {
		return this.id;
	}
	
	/**
	 * ドメインに変換
	 * @return 管理期間の36協定時間
	 */
	public AgreementTimeOfManagePeriod toDomain(){
		
		/** 36協定時間 */
		val agreementTime = AgreementTimeOfMonthly.of(new AttendanceTimeMonth(this.agreementTime), 
														OneMonthTime.createWithCheck(
																OneMonthErrorAlarmTime.of(
																	new AgreementOneMonthTime(this.agreementErrorTime), 
																	new AgreementOneMonthTime(this.agreementAlarmTime)), 
																new AgreementOneMonthTime(this.agreementLimitTime)));
		/** 36協定上限時間 */
		val exceptTime = AgreementTimeOfMonthly.of(new AttendanceTimeMonth(this.exceptAgreementTime), 
													OneMonthTime.createWithCheck(
															OneMonthErrorAlarmTime.of(
																new AgreementOneMonthTime(this.exceptAgreementErrorTime), 
																new AgreementOneMonthTime(this.exceptAgreementAlarmTime)), 
															new AgreementOneMonthTime(this.exceptAgreementLimitTime)));
		/** 内訳 */
		val breakdown = AgreementTimeBreakdown.of(new AttendanceTimeMonth(this.overTime), 
													new AttendanceTimeMonth(this.transferOverTime), 
													new AttendanceTimeMonth(this.holidayWorkTime), 
													new AttendanceTimeMonth(this.transferHolidayWorkTime),
													new AttendanceTimeMonth(this.flexLegalTime),
													new AttendanceTimeMonth(this.flexIllegalTime), 
													new AttendanceTimeMonth(this.withinPremiumTime),
													new AttendanceTimeMonth(this.weekPremiumTime), 
													new AttendanceTimeMonth(this.monthPremiumTime), 
													new AttendanceTimeMonth(this.temporaryTime), 
													new AttendanceTimeMonth(this.legalHolidayWorkTime),
													new AttendanceTimeMonth(this.legalTransferHolidayWorkTime));
		
		/** 状態 */
		val status = EnumAdaptor.valueOf(this.status, AgreementTimeStatusOfMonthly.class);
		
		return AgreementTimeOfManagePeriod.of(this.id.employeeId, new YearMonth(this.id.yearMonth),
												agreementTime, exceptTime, breakdown, status);
	}
	
	/**
	 * ドメインから変換　(for Update)
	 * @param domain 管理期間の36協定時間
	 */
	public void fromDomainForUpdate(AgreementTimeOfManagePeriod domain){
		
		val agreementTime = domain.getAgreementTime();
		val breakdown = domain.getBreakdown();
		val exceptTime = domain.getLegalMaxTime();
		
		/** 36協定時間 */
		this.agreementTime = agreementTime.getAgreementTime().valueAsMinutes(); 
		this.agreementErrorTime = agreementTime.getThreshold().getErAlTime().getError().valueAsMinutes(); 
		this.agreementAlarmTime = agreementTime.getThreshold().getErAlTime().getAlarm().valueAsMinutes();
		this.agreementLimitTime = agreementTime.getThreshold().getUpperLimit().valueAsMinutes();
		
		/** 36協定上限時間 */
		this.exceptAgreementTime = exceptTime.getAgreementTime().valueAsMinutes(); 
		this.exceptAgreementErrorTime = exceptTime.getThreshold().getErAlTime().getError().valueAsMinutes(); 
		this.exceptAgreementAlarmTime = exceptTime.getThreshold().getErAlTime().getAlarm().valueAsMinutes(); 
		this.exceptAgreementLimitTime = exceptTime.getThreshold().getUpperLimit().valueAsMinutes();
		
		/** 内訳 */
		this.overTime = breakdown.getOverTime().valueAsMinutes(); 
		this.transferOverTime = breakdown.getTransferOverTime().valueAsMinutes(); 
		this.holidayWorkTime = breakdown.getIllegalHolidayWorkTime().valueAsMinutes();
		this.transferHolidayWorkTime = breakdown.getIllegaltransferTime().valueAsMinutes();
		this.flexLegalTime = breakdown.getFlexLegalTime().valueAsMinutes();
		this.flexIllegalTime = breakdown.getFlexIllegalTime().valueAsMinutes();
		this.withinPremiumTime = breakdown.getWithinPrescribedPremiumTime().valueAsMinutes();
		this.weekPremiumTime = breakdown.getWeeklyPremiumTime().valueAsMinutes();
		this.monthPremiumTime = breakdown.getMonthlyPremiumTime().valueAsMinutes();
		this.temporaryTime = breakdown.getTemporaryTime().valueAsMinutes();
		this.legalHolidayWorkTime = breakdown.getLegalHolidayWorkTime().valueAsMinutes();
		this.legalTransferHolidayWorkTime = breakdown.getLegalTransferTime().valueAsMinutes();

		/** 状態 */
		this.status = domain.getStatus().value;
	}
}
