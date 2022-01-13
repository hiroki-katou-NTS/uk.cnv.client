package nts.uk.ctx.at.shared.infra.entity.scherec.dailyattdcal.personcostcalc.premiumitem;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
//import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PremiumItemLanguage;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PremiumName;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.outsideot.overtime.language.LanguageId;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 
 * @author tutk
 *
 */
@NoArgsConstructor
@Setter
@Entity
@Table(name = "KMNMT_PREMIUM_ITEM_LANG")
public class KmnmtPremiumItemLanguage extends ContractUkJpaEntity {
	@EmbeddedId
	public KmnmtPremiumItemLanguagePK kmnmtPremiumItemLanguagePK;

	@Column(name = "PREMIUM_NAME")
	public String name;

	@OneToOne
	@PrimaryKeyJoinColumns(value = { @PrimaryKeyJoinColumn(name = "CID", referencedColumnName = "CID"),
			@PrimaryKeyJoinColumn(name = "PREMIUM_NO", referencedColumnName = "PREMIUM_NO") })
	public KscmtPremiumItem kmnmtPremiumItem;

	@Override
	protected Object getKey() {
		return kmnmtPremiumItemLanguagePK;
	}

	public KmnmtPremiumItemLanguage(KmnmtPremiumItemLanguagePK kmnmtPremiumItemLanguagePK, String name) {
		super();
		this.kmnmtPremiumItemLanguagePK = kmnmtPremiumItemLanguagePK;
		this.name = name;
	}

	public PremiumItemLanguage toDomain() {
		return new PremiumItemLanguage(this.kmnmtPremiumItemLanguagePK.companyID,
				ExtraTimeItemNo.valueOf(this.kmnmtPremiumItemLanguagePK.displayNumber), new LanguageId(this.kmnmtPremiumItemLanguagePK.langID),
				this.name == null ? null : new PremiumName(this.name));
	}

	public static KmnmtPremiumItemLanguage toEntity(PremiumItemLanguage domain) {
		String name = null;
		if(domain.getName().isPresent()) {
			name = domain.getName().get().v();
		}
		return new KmnmtPremiumItemLanguage(
				new KmnmtPremiumItemLanguagePK(domain.getCompanyID(), domain.getDisplayNumber().value, domain.getLangID().v()),
				name);
	}

}
