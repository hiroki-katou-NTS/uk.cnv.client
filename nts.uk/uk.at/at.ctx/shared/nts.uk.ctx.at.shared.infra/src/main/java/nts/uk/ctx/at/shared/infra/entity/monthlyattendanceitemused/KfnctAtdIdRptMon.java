package nts.uk.ctx.at.shared.infra.entity.monthlyattendanceitemused;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nts.uk.shr.infra.data.entity.UkJpaEntity;
import nts.uk.ctx.at.shared.dom.monthlyattditem.FormCanUsedForTime;
import nts.uk.ctx.at.shared.dom.monthlyattditem.MonthlyAttendanceItemUsed;

@Data
@Entity
@Table(name = "KFNCT_ATD_ID_RPT_MON")
@EqualsAndHashCode(callSuper = true)
public class KfnctAtdIdRptMon extends UkJpaEntity implements MonthlyAttendanceItemUsed.MementoGetter, MonthlyAttendanceItemUsed.MementoSetter, Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private KfnctAtdIdRptMonPK kfnctAtdIdRptMonPK;
	
	@Column(name = "EXCLUS_VER")
	private String exclusVer;
	
	@Column(name = "CONTRACT_CD")
	private String contractCd;
	
	@Column(name = "WORK_ATTENDANCE")
	private BigDecimal workAttendance;
	
	@Column(name = "WORK_MONTHLY")
	private BigDecimal workMonthly;
	
	@Column(name = "WORK_YEARLY_ATD")
	private BigDecimal workYearlyAtd;
	
	@Column(name = "WORK_PERIOD")
	private BigDecimal workPeriod;
	
	@Column(name = "ATD_WORK_ATTENDANCE")
	private BigDecimal atdWorkAttendance;
	
	@Column(name = "ATD_WORK_YEARLY")
	private BigDecimal atdWorkYearly;

	@Column(name = "WORK_YEARLY_36")
	private BigDecimal workYearly36;
	
	
	@Override
	protected Object getKey() {
		return this.kfnctAtdIdRptMonPK;
	}
	
	@Override
	public void setCompanyId(String companyId) {
		if (this.kfnctAtdIdRptMonPK == null) {
			this.kfnctAtdIdRptMonPK = new KfnctAtdIdRptMonPK();
		}
		this.kfnctAtdIdRptMonPK.setCompanyId(companyId);
	}

	@Override
	public void setAttendanceItemId(int attendanceItemId) {
		if (this.kfnctAtdIdRptMonPK == null) {
			this.kfnctAtdIdRptMonPK = new KfnctAtdIdRptMonPK();
		}
		this.kfnctAtdIdRptMonPK.setAttendanceItemId(attendanceItemId);
	}

	@Override
	public void setFormCanUsedForTimes(List<FormCanUsedForTime> forms) {
		this.setWorkAttendance(BigDecimal.valueOf(0));
		this.setWorkMonthly(BigDecimal.valueOf(0));
		this.setWorkYearlyAtd(BigDecimal.valueOf(0));
		this.setWorkPeriod(BigDecimal.valueOf(0));
		this.setAtdWorkAttendance(BigDecimal.valueOf(0));
		this.setAtdWorkYearly(BigDecimal.valueOf(0));
		this.setWorkYearly36(BigDecimal.valueOf(0));
		for (FormCanUsedForTime form : forms) {
			switch (form) {
				case ATTENDANCE_BOOK:
					this.setWorkAttendance(BigDecimal.valueOf(1));
					break;
				case MONTHLY_WORK_SCHEDULE:
					this.setWorkMonthly(BigDecimal.valueOf(1));
					break;
				case ANNUAL_WORK_SCHEDULE:
					this.setWorkYearlyAtd(BigDecimal.valueOf(1));
					break;
				case OPTIONAL_PERIOD_SCHEDULE:
					this.setWorkPeriod(BigDecimal.valueOf(1));
					break;
				case ANNUAL_WORK_LEDGER:
					this.setAtdWorkAttendance(BigDecimal.valueOf(1));
					break;
				case WORKBOOK:
					this.setAtdWorkYearly(BigDecimal.valueOf(1));
					break;
				case ANNUAL_ROSTER_36_AGREEMENT:
					this.setWorkYearly36(BigDecimal.valueOf(1));
					break;
				default:
					break;
			}
		}
	}

	@Override
	public String getCompanyId() {
		return this.kfnctAtdIdRptMonPK.getCompanyId();
	}

	@Override
	public int getAttendanceItemId() {
		return this.kfnctAtdIdRptMonPK.getAttendanceItemId();
	}

	@Override
	public List<FormCanUsedForTime> getFormCanUsedForTimes() {
		List<FormCanUsedForTime> canUsedForTimes = new ArrayList<FormCanUsedForTime>();
		if (this.getWorkAttendance().intValue() == 1) {
			canUsedForTimes.add(FormCanUsedForTime.ATTENDANCE_BOOK);
		}

		if (this.workMonthly.intValue() == 1) {
			canUsedForTimes.add(FormCanUsedForTime.MONTHLY_WORK_SCHEDULE);
		}

		if (this.workYearlyAtd.intValue() == 1) {
			canUsedForTimes.add(FormCanUsedForTime.ANNUAL_WORK_SCHEDULE);
		}

		if (this.workPeriod.intValue() == 1) {
			canUsedForTimes.add(FormCanUsedForTime.OPTIONAL_PERIOD_SCHEDULE);
		}

		if (this.atdWorkAttendance.intValue() == 1) {
			canUsedForTimes.add(FormCanUsedForTime.ANNUAL_WORK_LEDGER);
		}

		if (this.atdWorkYearly.intValue() == 1) {
			canUsedForTimes.add(FormCanUsedForTime.WORKBOOK);
		}

		if (this.workYearly36.intValue() == 1) {
			canUsedForTimes.add(FormCanUsedForTime.ANNUAL_ROSTER_36_AGREEMENT);
		}

		return canUsedForTimes;
	}
	
	

}