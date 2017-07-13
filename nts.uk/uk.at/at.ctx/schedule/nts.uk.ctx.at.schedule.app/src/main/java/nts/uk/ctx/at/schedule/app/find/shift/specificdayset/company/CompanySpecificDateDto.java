package nts.uk.ctx.at.schedule.app.find.shift.specificdayset.company;

import java.math.BigDecimal;

import lombok.Value;
import nts.uk.ctx.at.schedule.dom.shift.specificdayset.company.CompanySpecificDateItem;

@Value
public class CompanySpecificDateDto {
	
	public BigDecimal specificDateItemNo;
	
	public BigDecimal specificDate;
	
	public static CompanySpecificDateDto fromDomain(CompanySpecificDateItem domain) {
		return new CompanySpecificDateDto(domain.getSpecificDate().v(), domain.getSpecificDateItemNo().v());
	}
}
