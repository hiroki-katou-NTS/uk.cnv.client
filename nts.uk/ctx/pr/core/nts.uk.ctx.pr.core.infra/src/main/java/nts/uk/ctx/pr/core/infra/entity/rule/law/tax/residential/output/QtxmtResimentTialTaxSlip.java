package nts.uk.ctx.pr.core.infra.entity.rule.law.tax.residential.output;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.layer.infra.data.entity.type.LocalDateToDBConverter;

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "QTXMT_RESIDENTIAL_TAXSLIP")
@Entity
public class QtxmtResimentTialTaxSlip {
	@EmbeddedId
	public QtxmtResimentTialTaxSlipPk qtxmtResimentTialTaxSlipPk;
	
	@Column(name = "TAX_PAYROLL_MNY")
	public BigDecimal taxPayRollMoney;
	
	@Column(name = "TAX_BONUS_MNY")
	public BigDecimal taxBonusMoney;
	
	@Column(name = "TAX_OVERDUE_MNY")
	public BigDecimal taxOverDueMoney;
	
	@Column(name = "TAX_DEMAND_CHARGE_MNY")
	public BigDecimal taxDemandChargeMoyney;
	
	@Column(name = "ADDRESS")
	public String address;
	
	@Column(name = "DUE_DATE")
	@Temporal(TemporalType.DATE)
	@Convert(converter = LocalDateToDBConverter.class)
	public LocalDate dueDate;
	
	@Column(name = "HEADCOUNT")
	public int headcount;
	
	@Column(name = "RETIREMENT_BONUS_AMOUNT")
	public BigDecimal retirementBonusAmout;
	
	@Column(name = "CITY_TAX_MNY")
	public BigDecimal cityTaxMoney;
	
	@Column(name = "PREFECTURE_TAX_MNY")
	public BigDecimal prefectureTaxMoney;
}
