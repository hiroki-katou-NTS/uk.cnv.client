package nts.uk.ctx.at.shared.infra.entity.remainingnumber.annlea;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;

import lombok.NoArgsConstructor;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveRemainingHistory;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;
import nts.uk.shr.infra.data.entity.ContractCompanyUkJpaEntity;
/**
 * 
 * @author phongtq
 * 年休付与残数履歴データ
 */

@NoArgsConstructor
@Entity
@Table(name = "KRCDT_HDPAID_REM_HIST")
public class KrcdtAnnLeaRemainHist extends ContractCompanyUkJpaEntity implements Serializable{
	
	@EmbeddedId
	public KrcdtAnnLeaRemainHistPK  krcdtAnnLeaRemainHistPK;  

//	/** 会社ID */
//	@Column(name = "CID")
//	public String cid;

	/** 期限日 */
	@Column(name = "DEADLINE")
	public GeneralDate deadline;

	/** 期限切れ状態 */
	@Column(name = "EXP_STATUS")
	public int expStatus;

	/** 登録種別 */
	@Column(name = "REGISTER_TYPE")
	public int registerType;

	/** 付与日数 */
	@Column(name = "GRANT_DAYS")
	public double grantDays;

	/** 付与時間 */
	@Column(name = "GRANT_MINUTES")
	@Basic(optional = true)
	public Integer grantMinutes;

	/** 使用日数 */
	@Column(name = "USED_DAYS")
	public double usedDays;

	/** 使用時間 */
	@Column(name = "USED_MINUTES")
	@Basic(optional = true)
	public Integer usedMinutes;

	/** 積崩し日数 */
	@Column(name = "STOWAGE_DAYS")
	@Basic(optional = true)
	public Double stowageDays;

	/** 残日数 */
	@Column(name = "REMAINING_DAYS")
	public double remainingDays;

	/** 残時間*/
	@Column(name = "REMAINING_MINUTES")
	@Basic(optional = true)
	public Integer remaningMinutes;

	/** 使用率 */
	@Column(name = "USED_PERCENT")
	public double usedPercent;

	/** 所定日数 */
	@Column(name = "PRESCRIBED_DAYS")
	@Basic(optional = true)
	public Double prescribedDays;

	/** 控除日数 */
	@Column(name = "DEDUCTED_DAYS")
	@Basic(optional = true)
	public Double deductedDays;

	/** 労働日数 */
	@Column(name = "WORKING_DAYS")
	@Basic(optional = true)
	public Double workingDays;

	private static final long serialVersionUID = 1L;

	@Override
	protected Object getKey() {
		return krcdtAnnLeaRemainHistPK;
	}

	public KrcdtAnnLeaRemainHist(String sid, int yearMonth, int closureId,
			int closeDay, int isLastDay, GeneralDate grantDate, GeneralDate deadline,
			int expStatus, int registerType, double grantDays, Integer grantMinutes, double usedDays,
			Integer usedMinutes, Double stowageDays, double remainingDays, Integer remaningMinutes, double usedPercent,
			Double prescribedDays, Double deductedDays, Double workingDays) {
		super();
		this.krcdtAnnLeaRemainHistPK = new KrcdtAnnLeaRemainHistPK(sid, yearMonth, closureId, closeDay, BooleanUtils.toBoolean(isLastDay), grantDate);
		this.deadline = deadline;
		this.expStatus = expStatus;
		this.registerType = registerType;
		this.grantDays = grantDays;
		this.grantMinutes = grantMinutes;
		this.usedDays = usedDays;
		this.usedMinutes = usedMinutes;
		this.stowageDays = stowageDays;
		this.remainingDays = remainingDays;
		this.remaningMinutes = remaningMinutes;
		this.usedPercent = usedPercent;
		this.prescribedDays = prescribedDays;
		this.deductedDays = deductedDays;
		this.workingDays = workingDays;
		
	}

	public static KrcdtAnnLeaRemainHist fromDomain(AnnualLeaveRemainingHistory domain) {
		return new KrcdtAnnLeaRemainHist(
				domain.getEmployeeId(),
				domain.getYearMonth().v(), domain.getClosureId().value, domain.getClosureDate().getClosureDay().v(),
				domain.getClosureDate().getLastDayOfMonth() ? 1 : 0,
				domain.getGrantDate(), 
				domain.getDeadline(), 
				domain.getExpirationStatus().value,
				domain.getRegisterType().value, 
				domain.getDetails().getGrantNumber().getDays().v(),
				domain.getDetails().getGrantNumber().getMinutes().isPresent()
						? domain.getDetails().getGrantNumber().getMinutes().get().v() : null,
				domain.getDetails().getUsedNumber().getDays().v(),
				domain.getDetails().getUsedNumber().getMinutes().isPresent()
						? domain.getDetails().getUsedNumber().getMinutes().get().v() : null,
				domain.getDetails().getUsedNumber().getStowageDays().isPresent()
						? domain.getDetails().getUsedNumber().getStowageDays().get().v() : null,
				domain.getDetails().getRemainingNumber().getDays().v(),
				domain.getDetails().getRemainingNumber().getMinutes().isPresent()
						? domain.getDetails().getRemainingNumber().getMinutes().get().v() : null,
				domain.getDetails().getUsedPercent().v().doubleValue(),
				domain.getAnnualLeaveConditionInfo().isPresent()
						? domain.getAnnualLeaveConditionInfo().get().getPrescribedDays().v() : null,
				domain.getAnnualLeaveConditionInfo().isPresent()
						? domain.getAnnualLeaveConditionInfo().get().getDeductedDays().v() : null,
				domain.getAnnualLeaveConditionInfo().isPresent()
						? domain.getAnnualLeaveConditionInfo().get().getWorkingDays().v() : null);
	}

	public AnnualLeaveRemainingHistory toDomain() {
		
		val annualLeaveGrantRemainingData = AnnualLeaveGrantRemainingData.createFromJavaType("", krcdtAnnLeaRemainHistPK.sid,
				krcdtAnnLeaRemainHistPK.grantDate, this.deadline, this.expStatus,
				this.registerType, this.grantDays, this.grantMinutes, this.usedDays, this.usedMinutes, this.stowageDays, this.remainingDays,
				this.remaningMinutes, this.usedPercent, this.prescribedDays, this.deductedDays, this.workingDays);
		
		return new AnnualLeaveRemainingHistory(annualLeaveGrantRemainingData, 
				new YearMonth(krcdtAnnLeaRemainHistPK.yearMonth),
				EnumAdaptor.valueOf(krcdtAnnLeaRemainHistPK.closureId, ClosureId.class),
				new ClosureDate(krcdtAnnLeaRemainHistPK.closeDay, krcdtAnnLeaRemainHistPK.isLastDay));
	}

}
