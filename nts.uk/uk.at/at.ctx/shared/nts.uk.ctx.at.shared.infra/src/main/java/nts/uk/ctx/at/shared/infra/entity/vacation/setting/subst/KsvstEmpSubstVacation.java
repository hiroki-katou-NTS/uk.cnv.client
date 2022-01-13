/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.entity.vacation.setting.subst;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.infra.data.jdbc.map.JpaEntityMapper;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * The Class KclstEmpSubstVacation.
 */
@Getter
@Setter
@Entity
@Table(name = "KSHMT_HDSUB_EMP")
public class KsvstEmpSubstVacation extends ContractUkJpaEntity  implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The kclst emp compens leave PK. */
	@EmbeddedId
	protected KsvstEmpSubstVacationPK kclstEmpSubstVacationPK;
	//管理区分
	@Column(name = "MANAGE_ATR")
	private boolean manageAtr;

	public static final JpaEntityMapper<KsvstEmpSubstVacation> MAPPER =
			new JpaEntityMapper<>(KsvstEmpSubstVacation.class);
	
	/**
	 * Instantiates a new kclst emp compens leave.
	 */
	public KsvstEmpSubstVacation() {
		super();
	}

	/**
	 * Instantiates a new kclst emp compens leave.
	 *
	 * @param kclstEmpSubstVacationPK
	 *            the kclst emp compens leave PK
	 */
	public KsvstEmpSubstVacation(KsvstEmpSubstVacationPK kclstEmpSubstVacationPK) {
		this.kclstEmpSubstVacationPK = kclstEmpSubstVacationPK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		hash += (kclstEmpSubstVacationPK != null ? kclstEmpSubstVacationPK.hashCode() : 0);
		return hash;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof KsvstEmpSubstVacation)) {
			return false;
		}
		KsvstEmpSubstVacation other = (KsvstEmpSubstVacation) object;
		if ((this.kclstEmpSubstVacationPK == null && other.kclstEmpSubstVacationPK != null)
				|| (this.kclstEmpSubstVacationPK != null
						&& !this.kclstEmpSubstVacationPK.equals(other.kclstEmpSubstVacationPK))) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#getKey()
	 */
	@Override
	protected Object getKey() {
		return this.kclstEmpSubstVacationPK;
	}

}
