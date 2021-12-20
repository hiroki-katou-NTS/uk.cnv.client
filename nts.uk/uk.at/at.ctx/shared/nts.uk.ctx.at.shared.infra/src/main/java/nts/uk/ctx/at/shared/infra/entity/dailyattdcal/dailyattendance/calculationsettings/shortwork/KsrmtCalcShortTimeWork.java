package nts.uk.ctx.at.shared.infra.entity.dailyattdcal.dailyattendance.calculationsettings.shortwork;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * エンティティ：短時間勤務の計算
 * @author shuichi_ishida
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KSRMT_CALC_SHORTTIME_WORK")
public class KsrmtCalcShortTimeWork extends ContractUkJpaEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	/** 会社ID */
	@Id
	@Column(name = "CID")
	public String companyId;
	
	/** 計算方法 */
	@Column(name = "CALC_METHOD")
	public boolean calcMethod;
	
	@Override
	protected Object getKey() {
		return this.companyId;
	}
}
