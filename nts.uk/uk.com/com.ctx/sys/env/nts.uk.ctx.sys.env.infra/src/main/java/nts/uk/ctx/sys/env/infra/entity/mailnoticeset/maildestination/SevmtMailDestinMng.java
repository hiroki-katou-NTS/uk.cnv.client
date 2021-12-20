package nts.uk.ctx.sys.env.infra.entity.mailnoticeset.maildestination;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Entity
@Table(name = "SEVMT_MAIL_DESTIN_MNG")
@AllArgsConstructor
@NoArgsConstructor
public class SevmtMailDestinMng extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	public SevmtMailDestinMngPK pk;
	
	/**
	 * 会社メールアドレスを利用する
	 */
	@NotNull
	@Column(name = "COM_MAIL_USE")
	public int companyMailUse;
	
	/**
	 * 会社携帯メールアドレスを利用する
	 */
	@NotNull
	@Column(name = "COM_MAIL_MOBILE_USE")
	public int companyMailMobileUse;
	
	/**
	 * 個人メールアドレスを利用する
	 */
	@NotNull
	@Column(name = "PS_MAIL_USE")
	public int personalMailUse;
	
	/**
	 * 個人メールアドレスを利用する
	 */
	@NotNull
	@Column(name = "PS_MAIL_MOBILE_USE")
	public int personalMailMobileUse;

	@Override
	protected Object getKey() {
		return this.pk;
	}
}
