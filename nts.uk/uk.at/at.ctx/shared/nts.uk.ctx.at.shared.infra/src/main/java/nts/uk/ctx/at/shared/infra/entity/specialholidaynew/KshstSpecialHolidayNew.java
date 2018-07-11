package nts.uk.ctx.at.shared.infra.entity.specialholidaynew;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.infra.entity.specialholidaynew.grantcondition.KshstSpecialLeaveRestriction;
import nts.uk.ctx.at.shared.infra.entity.specialholidaynew.grantinformation.KshstGrantRegularNew;
import nts.uk.ctx.at.shared.infra.entity.specialholidaynew.periodinformation.KshstGrantPeriodicNew;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * 特別休暇
 * 
 * @author tanlv
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KSHST_SPECIAL_HOLIDAY")
public class KshstSpecialHolidayNew extends UkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/* 主キー */
	@EmbeddedId
	public KshstSpecialHolidayPKNew pk;

	/* 特別休暇名称 */
	@Column(name = "SPHD_NAME")
	public String specialHolidayName;
	
	/* メモ */
	@Column(name = "MEMO")
	public String memo;
	
	@OneToOne(cascade = CascadeType.ALL, mappedBy="specialHoliday", orphanRemoval = true)
	public KshstGrantRegularNew grantRegular;
	
	@OneToOne(cascade = CascadeType.ALL, mappedBy="specialHoliday", orphanRemoval = true)
	public KshstGrantPeriodicNew grantPeriodic;
	
	@OneToOne(cascade = CascadeType.ALL, mappedBy="specialHoliday", orphanRemoval = true)
	public KshstSpecialLeaveRestriction specialLeaveRestriction;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy="specialHoliday", orphanRemoval = true)
	public List<KshstSphdAbsence> sphdAbsence;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy="specialHoliday", orphanRemoval = true)
	public List<KshstSphdSpecLeave> sphdSpecLeave;
	
	@Override
	protected Object getKey() {
		return pk;
	}
}
