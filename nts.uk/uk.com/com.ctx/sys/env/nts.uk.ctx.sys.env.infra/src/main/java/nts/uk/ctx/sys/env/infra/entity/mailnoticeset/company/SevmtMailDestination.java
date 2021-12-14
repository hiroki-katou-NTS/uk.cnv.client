package nts.uk.ctx.sys.env.infra.entity.mailnoticeset.company;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.EmailDestinationFunction;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.data.entity.UkJpaEntity;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity メール送信先機能
 */
@Data
@Entity
@Table(name = "SEVMT_MAIL_DESTINATION")
@EqualsAndHashCode(callSuper = true)
public class SevmtMailDestination extends UkJpaEntity {

	// column 契約コード
	@Basic(optional = false)
	@Column(name = "CONTRACT_CD")
	private String contractCd;

	// Embedded primary key 会社ID + メール分類 + 機能ID
	@EmbeddedId
	private SevmtMailDestinationPK pk;

	@ManyToOne
	@PrimaryKeyJoinColumn(name = "CID", referencedColumnName = "CID")
	private SevmtUserInfoUse sevmtUserInfoUse;

	public SevmtMailDestination(Integer emailClassification, Integer functionId, String cid) {
		this.pk = new SevmtMailDestinationPK();
		this.pk.cId = cid;
		this.pk.mailClassification = emailClassification;
		this.pk.funcId = functionId;
		this.contractCd = AppContexts.user().contractCode();
	}

	public SevmtMailDestination() {
	}

	public static List<SevmtMailDestination> toListEntity(EmailDestinationFunction domain, String cid) {
		List<SevmtMailDestination> sevmtMailDestinations = new ArrayList<>();
		if (domain.getFunctionIds() != null) {
			domain.getFunctionIds().forEach(item -> sevmtMailDestinations
					.add(new SevmtMailDestination(domain.getEmailClassification().value, item.v(), cid)));
		}
		return sevmtMailDestinations;
	}

	@Override
	protected Object getKey() {
		return this.pk;
	}
}
