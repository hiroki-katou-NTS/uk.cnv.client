package nts.uk.ctx.at.shared.infra.entity.holidaysetting.employee.carryForwarddata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardHistory;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KSHDT_HDPUB_REM_HIST")
public class KshdtHdpubRemHist extends ContractUkJpaEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/* 主キー */
	@EmbeddedId
	protected KshdtHdpubRemHistPK pk;
	
	@Column(name = "ID")
	private int remainmngid;
	
	/** 期限日 */
	@Column(name = "DEADLINE")
	private GeneralDate deadline;
	
	/** 登録種別 */
	@Column(name = "REGISTER_TYPE")
	private int registerType;
	
	/** 繰越数 */
	@Column(name = "CARRIEDFORWARD")
	private double carriedforward;
	
	@Override
	protected Object getKey() {
		return pk;
	}
	
	
	public void fromDomainForUpdate(PublicHolidayCarryForwardHistory domain){

		this.pk.sid = domain.getEmployeeId();
		this.pk.yearMonth = domain.getHistYearMonth().v();
		this.pk.closureId = domain.getClosureId().value;
		this.pk.tagetmonth = domain.getYearMonth().v();
		this.deadline = domain.getYmd();
		this.registerType  = domain.getGrantRemainRegisterType().value;
		this.carriedforward = domain.getNumberCarriedForward().v();
	}
}
