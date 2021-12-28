package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem;

import lombok.EqualsAndHashCode;
import lombok.Value;
/**
 * 割増時間項目
 * @author Doan Duy Hung
 *
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class PremiumItem {
	
	private String companyID;
	
	private ExtraTimeItemNo displayNumber;
	
	private PremiumName name;

	private UseAttribute useAtr;

	public PremiumItem(String companyID, ExtraTimeItemNo displayNumber, PremiumName name, UseAttribute useAtr) {
		super();
		this.companyID = companyID;
		this.displayNumber = displayNumber;
		this.name = name;
		this.useAtr = useAtr;
	}
}
