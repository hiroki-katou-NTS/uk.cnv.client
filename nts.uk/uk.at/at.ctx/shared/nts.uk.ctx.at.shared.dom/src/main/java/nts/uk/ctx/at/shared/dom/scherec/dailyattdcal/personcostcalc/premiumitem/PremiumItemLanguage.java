package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem;

import java.util.Optional;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.outsideot.overtime.language.LanguageId;
/**
 * 割増時間項目の他言語表示名
 * @author tutk
 *
 */
@Getter
@NoArgsConstructor
public class PremiumItemLanguage extends AggregateRoot {
	/** 会社ID */
	private String companyID;
	/** 割増項目NO */
	private ExtraTimeItemNo displayNumber;
	/** 言語ID */
	private LanguageId langID;
	/** 名称 */
	private Optional<PremiumName> name;

	public PremiumItemLanguage(String companyID, ExtraTimeItemNo displayNumber, LanguageId langID, PremiumName name) {
		super();
		this.companyID = companyID;
		this.displayNumber = displayNumber;
		this.langID = langID;
		this.name = Optional.ofNullable(name);
	}

}
