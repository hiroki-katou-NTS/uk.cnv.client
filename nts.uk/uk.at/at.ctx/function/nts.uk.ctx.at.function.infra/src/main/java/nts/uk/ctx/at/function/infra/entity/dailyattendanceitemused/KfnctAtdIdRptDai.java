package nts.uk.ctx.at.function.infra.entity.dailyattendanceitemused;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nts.uk.ctx.at.function.dom.dailyattendanceitem.DailyAttendanceItemUsed;
import nts.uk.ctx.at.function.dom.dailyattendanceitem.FormCanUsedForTime;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

@Data
@Entity
@Table(name = "KFNCT_ATD_ID_RPT_DAI")
@EqualsAndHashCode(callSuper = true)
public class KfnctAtdIdRptDai extends UkJpaEntity
		implements DailyAttendanceItemUsed.MementoGetter, DailyAttendanceItemUsed.MementoSetter, Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private KfnctAtdIdRptDaiPK kfnctAtdIdRptDaiPK;
	
	@Column(name = "WORK_DAILY")
	private boolean workDaily;
	
	@Column(name = "WORK_ATTENDANCE")
	private boolean workAttendance;
	
	@Column(name = "ATD_WORK_DAILY")
	private boolean atdWorkDaily;
	
	@Column(name = "ATD_WORK_ATTENDANCE")
	private boolean atdWorkAttendance;

	@Override
	protected Object getKey() {
		return this.kfnctAtdIdRptDaiPK;
	}

	@Override
	public void setCompanyId(String companyId) {
		if (this.kfnctAtdIdRptDaiPK == null) {
			this.kfnctAtdIdRptDaiPK = new KfnctAtdIdRptDaiPK();
		}
		this.kfnctAtdIdRptDaiPK.setCompanyId(companyId);
		
	}

	@Override
	public void setItemDailyId(BigDecimal itemDailyId) {
		if (this.kfnctAtdIdRptDaiPK == null) {
			this.kfnctAtdIdRptDaiPK = new KfnctAtdIdRptDaiPK();
		}
		this.kfnctAtdIdRptDaiPK.setAttendanceItemId(itemDailyId);
	}

	@Override
	public void setFormCanUsedForTimes(List<FormCanUsedForTime> form) {
		for (FormCanUsedForTime formCanUsedForTime : form) {
			switch (formCanUsedForTime) {
				case DAILY_WORK_SCHEDULE:
					this.setWorkDaily(true);
					break;
				case ATTENDANCE_BOOK:
					this.setWorkAttendance(true);
					break;
				case WORK_SITUATION_TABLE:
					this.setAtdWorkDaily(true);
					break;
				case ANNUAL_WORK_LEDGER:
					this.setAtdWorkAttendance(true);
					break;
				default:
					break;
			}
		}
	}

	@Override
	public String getCompanyId() {
		return this.kfnctAtdIdRptDaiPK.getCompanyId();
	}

	@Override
	public BigDecimal getItemDailyId() {
		return this.kfnctAtdIdRptDaiPK.getAttendanceItemId();
	}

	@Override
	public List<FormCanUsedForTime> getFormCanUsedForTimes() {
		return Arrays.asList(
				FormCanUsedForTime.valueOf(BooleanUtils.toInteger(this.workDaily)),
				FormCanUsedForTime.valueOf(BooleanUtils.toInteger(this.workAttendance)),
				FormCanUsedForTime.valueOf(BooleanUtils.toInteger(this.atdWorkAttendance)),
				FormCanUsedForTime.valueOf(BooleanUtils.toInteger(this.atdWorkDaily)));
	}

}
