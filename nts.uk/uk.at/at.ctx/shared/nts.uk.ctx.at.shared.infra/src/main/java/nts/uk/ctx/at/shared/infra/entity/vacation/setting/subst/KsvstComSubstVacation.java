/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.entity.vacation.setting.subst;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * The Class KsvstComSubstVacation.
 */
@Getter
@Setter
@Entity
@Table(name = "KSHMT_HDSUB_CMP")
public class KsvstComSubstVacation extends ContractUkJpaEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** 会社ID */
	@Id
	@Column(name = "CID")
	private String cid;
    
	/** 管理区分 **/ 
	@Column(name = "MANAGE_ATR")
	private boolean manageAtr;
	/** 紐付け管理区分 **/
	@Column(name = "LINK_MNG_ATR")
	private boolean linkMngAtr;
	/** 休暇使用期限**/
	@Column(name = "EXPIRATION_DATE_SET")
	private Integer expitationDateSet;
	/** 先取り許可**/
	@Column(name = "ALLOW_PREPAID_LEAVE")
	private Integer allowPrepaidLeave;
	/** 期限日の管理方法**/
	@Column(name = "EXP_DATE_MNG_METHOD")
	private Integer expDateMngMethod;
	/**
	 * Instantiates a new ksvst com subst vacation.
	 */
	public KsvstComSubstVacation() {
		super();
	}

	/**
	 * Instantiates a new ksvst com subst vacation.
	 *
	 * @param cid
	 *            the cid
	 */
	public KsvstComSubstVacation(String cid) {
		this.cid = cid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		hash += (cid != null ? cid.hashCode() : 0);
		return hash;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof KsvstComSubstVacation)) {
			return false;
		}
		KsvstComSubstVacation other = (KsvstComSubstVacation) object;
		if ((this.cid == null && other.cid != null)
				|| (this.cid != null && !this.cid.equals(other.cid))) {
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
		return this.cid;
	}

}
