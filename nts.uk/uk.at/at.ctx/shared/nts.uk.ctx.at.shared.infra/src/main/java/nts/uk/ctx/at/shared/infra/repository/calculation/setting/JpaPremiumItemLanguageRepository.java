package nts.uk.ctx.at.shared.infra.repository.calculation.setting;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PremiumItemLanguage;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PremiumItemLanguageRepository;
import nts.uk.ctx.at.shared.infra.entity.scherec.dailyattdcal.personcostcalc.premiumitem.KmnmtPremiumItemLanguage;
import nts.uk.ctx.at.shared.infra.entity.scherec.dailyattdcal.personcostcalc.premiumitem.KmnmtPremiumItemLanguagePK;

@Stateless
public class JpaPremiumItemLanguageRepository extends JpaRepository implements PremiumItemLanguageRepository {

	private static final String SEL_BY_CID_LANGID = "SELECT a FROM KmnmtPremiumItemLanguage a "
			+ "WHERE a.kmnmtPremiumItemLanguagePK.companyID = :companyId "
			+ "AND a.kmnmtPremiumItemLanguagePK.langID = :langId";

	@Override
	public List<PremiumItemLanguage> findByCIdAndLangId(String companyId, String langId) {
		List<PremiumItemLanguage> data = this.queryProxy().query(SEL_BY_CID_LANGID, KmnmtPremiumItemLanguage.class)
				.setParameter("companyId", companyId).setParameter("langId", langId).getList(x -> x.toDomain());
		return data;
	}

	@Override
	public Optional<PremiumItemLanguage> findById(String companyId, Integer displayNumber, String langId) {
		Optional<PremiumItemLanguage> data = this.queryProxy()
				.find(new KmnmtPremiumItemLanguagePK(companyId, displayNumber, langId), KmnmtPremiumItemLanguage.class)
				.map(x -> x.toDomain());
		return data;
	}

	@Override
	public void add(PremiumItemLanguage premiumItemLanguage) {
		this.commandProxy().insert(KmnmtPremiumItemLanguage.toEntity(premiumItemLanguage));

	}

	@Override
	public void update(PremiumItemLanguage premiumItemLanguage) {
		KmnmtPremiumItemLanguage entity = this.queryProxy()
				.find(new KmnmtPremiumItemLanguagePK(premiumItemLanguage.getCompanyID(),
						premiumItemLanguage.getDisplayNumber().value, premiumItemLanguage.getLangID().v()),
						KmnmtPremiumItemLanguage.class)
				.get();
		entity.name = !premiumItemLanguage.getName().isPresent() ? null : premiumItemLanguage.getName().get().v();
		this.commandProxy().update(entity);

	}

}
