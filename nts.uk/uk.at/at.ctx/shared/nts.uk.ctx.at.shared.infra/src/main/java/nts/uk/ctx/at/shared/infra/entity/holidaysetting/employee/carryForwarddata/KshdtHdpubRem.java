package nts.uk.ctx.at.shared.infra.entity.holidaysetting.employee.carryForwarddata;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardData;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 *  公休繰越データ
 * @author hayata_maekawa
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Table(name = "KSHDT_HDPUB_REM")
public class KshdtHdpubRem extends ContractUkJpaEntity {
	
	
	/* 主キー */
	@EmbeddedId
	protected KshdtHdpubRemPK pk;
	
	@Column(name = "ID")
	private int remainmngid;
	
	/* 期限日 */
	@Column(name = "DEADLINE")
	private GeneralDate deadline;
	
	/* 登録種別 */
	@Column(name = "REGISTER_TYPE")
	private int registerType;
	
	/* 繰越数 */
	@Column(name = "CARRIEDFORWARD")
	private double carriedforward;
	
	@Override
	protected Object getKey() {
		return pk;
	}
	
	/**
	 * ドメインから変換　(for Update)
	 * @param domain 公休繰越データ
	 */
	public void fromDomainForUpdate(PublicHolidayCarryForwardData domain){

		this.pk.employeeId = domain.getEmployeeId();
		this.pk.tagetmonth = domain.getYearMonth().v();
		this.deadline = domain.getYmd();
		this.registerType  = domain.getGrantRemainRegisterType().value;
		this.carriedforward = domain.getNumberCarriedForward().v();

	}
}
