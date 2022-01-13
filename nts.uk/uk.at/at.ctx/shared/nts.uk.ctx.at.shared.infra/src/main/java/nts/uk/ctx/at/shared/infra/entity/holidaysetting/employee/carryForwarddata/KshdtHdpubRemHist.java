package nts.uk.ctx.at.shared.infra.entity.holidaysetting.employee.carryForwarddata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardHistory;
import nts.uk.shr.infra.data.entity.ContractCompanyUkJpaEntity;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KSHDT_HDPUB_REM_HIST")
public class KshdtHdpubRemHist extends ContractCompanyUkJpaEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/* 主キー */
	@EmbeddedId
	protected KshdtHdpubRemHistPK pk;
	
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
		this.registerType  = domain.getGrantRemainRegisterType().value;
		this.carriedforward = domain.getNumberCarriedForward().v();
	}
	
	public void fromDomainForInsert(PublicHolidayCarryForwardHistory domain){

		KshdtHdpubRemHistPK pk = new KshdtHdpubRemHistPK();
		this.pk = pk;
		this.pk.sid = domain.getEmployeeId();
		this.pk.yearMonth = domain.getHistYearMonth().v();
		this.pk.closureId = domain.getClosureId().value;
		this.pk.closeDay =domain.getClosureDate().getClosureDay().v();
		this.pk.isLastDay = domain.getClosureDate().getLastDayOfMonth();
		this.registerType  = domain.getGrantRemainRegisterType().value;
		this.carriedforward = domain.getNumberCarriedForward().v();
	}
}
