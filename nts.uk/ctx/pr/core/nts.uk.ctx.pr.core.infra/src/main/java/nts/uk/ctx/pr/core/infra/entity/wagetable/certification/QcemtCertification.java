/******************************************************************
 * Copyright (c) 2016 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.pr.core.infra.entity.wagetable.certification;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class QcemtCertification.
 */
@Getter
@Setter
@Entity
@Table(name = "QCEMT_CERTIFICATION")
public class QcemtCertification implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The qcemt certification PK. */
	@EmbeddedId
	protected QcemtCertificationPK qcemtCertificationPK;

	/** The ins date. */
	@Column(name = "INS_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date insDate;

	/** The ins ccd. */
	@Column(name = "INS_CCD")
	private String insCcd;

	/** The ins scd. */
	@Column(name = "INS_SCD")
	private String insScd;

	/** The ins pg. */
	@Column(name = "INS_PG")
	private String insPg;

	/** The upd date. */
	@Column(name = "UPD_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updDate;

	/** The upd ccd. */
	@Column(name = "UPD_CCD")
	private String updCcd;

	/** The upd scd. */
	@Column(name = "UPD_SCD")
	private String updScd;

	/** The upd pg. */
	@Column(name = "UPD_PG")
	private String updPg;

	/** The exclus ver. */
	@Basic(optional = false)
	@Column(name = "EXCLUS_VER")
	private long exclusVer;

	/** The name. */
	@Basic(optional = false)
	@Column(name = "NAME")
	private String name;

	/** The qwtmt wagetable certify list. */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "qcemtCertification")
	private List<QwtmtWagetableCertify> qwtmtWagetableCertifyList;

	/**
	 * Instantiates a new qcemt certification.
	 */
	public QcemtCertification() {
		super();
	}

	/**
	 * Instantiates a new qcemt certification.
	 *
	 * @param qcemtCertificationPK
	 *            the qcemt certification PK
	 */
	public QcemtCertification(QcemtCertificationPK qcemtCertificationPK) {
		this.qcemtCertificationPK = qcemtCertificationPK;
	}

	/**
	 * Instantiates a new qcemt certification.
	 *
	 * @param ccd
	 *            the ccd
	 * @param certCd
	 *            the cert cd
	 */
	public QcemtCertification(String ccd, String certCd) {
		this.qcemtCertificationPK = new QcemtCertificationPK(ccd, certCd);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		hash += (this.qcemtCertificationPK == null ? 0 : this.qcemtCertificationPK.hashCode());
		return hash;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof QcemtCertification)) {
			return false;
		}
		QcemtCertification other = (QcemtCertification) object;
		if ((this.qcemtCertificationPK == null && other.qcemtCertificationPK != null)
			|| (this.qcemtCertificationPK != null
				&& !this.qcemtCertificationPK.equals(other.qcemtCertificationPK))) {
			return false;
		}
		return true;
	}

}
