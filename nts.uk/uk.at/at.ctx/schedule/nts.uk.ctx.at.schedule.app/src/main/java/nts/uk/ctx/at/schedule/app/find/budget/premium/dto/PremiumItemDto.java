package nts.uk.ctx.at.schedule.app.find.budget.premium.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PremiumItemLanguage;

/**
 * 
 * @author Doan Duy Hung
 *
 */
@AllArgsConstructor
@Getter
@Setter
public class PremiumItemDto {
	private String companyID;

	private Integer displayNumber;

	private String name;

	private int useAtr;

	public static PremiumItemDto fromDomainPremiumItemLanguage(PremiumItemLanguage premiumItem) {
		return new PremiumItemDto(premiumItem.getCompanyID(), premiumItem.getDisplayNumber().value,
				!premiumItem.getName().isPresent()?null:premiumItem.getName().get().v());
	}

	public PremiumItemDto(String companyID, Integer displayNumber, String name) {
		super();
		this.companyID = companyID;
		this.displayNumber = displayNumber;
		this.name = name;
	}
}
