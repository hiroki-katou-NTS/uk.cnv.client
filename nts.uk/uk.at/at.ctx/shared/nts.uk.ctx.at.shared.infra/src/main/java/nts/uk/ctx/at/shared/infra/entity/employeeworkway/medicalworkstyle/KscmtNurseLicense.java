package nts.uk.ctx.at.shared.infra.entity.employeeworkway.medicalworkstyle;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

// 看護区分
@Getter
@Setter
@Entity
@Table(name = "KSCMT_NURSE_LICENSE")
@AllArgsConstructor
@NoArgsConstructor
public class KscmtNurseLicense extends ContractUkJpaEntity implements Serializable { 
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	protected KscmtNurseLicensePK kscmtNurseLicensePK;

	/**
	 * 看護区分名称
	 */
	@Basic(optional = false)
	@NotNull
	@Column(name = "NAME")
	private String name;

	/**
	 * 免許区分
	 */
	@Basic(optional = false)
	@NotNull
	@Column(name = "LICENSE_ATR")
	private int licenseAtr;

	/**
	 * 事務的業務従事者か
	 */
	@Basic(optional = false)
	@NotNull
	@Column(name = "IS_OFFICE_WORK")
	private boolean officeWork;

	@Override
	protected Object getKey() {
		return kscmtNurseLicensePK;
	}

}
