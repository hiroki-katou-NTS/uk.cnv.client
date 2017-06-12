package nts.uk.ctx.at.record.infra.entity.stamp.stampcard;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class KwkdtStampCardPK implements Serializable {
	private static final long serialVersionUID = 1L;
	/* 会社ID */
	@Column(name = "CID")
	public String companyId;

	/* 個人ID */
	@Column(name = "PID")
	public String personId;
	
	/* カード番号 */
	@Column(name = "CARD_NUMBER")
	public String cardNumber;

}
