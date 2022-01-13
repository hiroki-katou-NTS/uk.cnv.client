package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.service;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PersonCostCalculation;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PersonCostCalAndDateDto {
    private PersonCostCalculation personCostCalculation;
    private GeneralDate startDate;
    private GeneralDate endDate;
}
