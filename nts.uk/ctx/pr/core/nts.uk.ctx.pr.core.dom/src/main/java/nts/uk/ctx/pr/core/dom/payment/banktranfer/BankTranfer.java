package nts.uk.ctx.pr.core.dom.payment.banktranfer;

import java.math.BigDecimal;

import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.core.dom.company.CompanyCode;
import nts.uk.shr.com.primitive.PersonId;

@Getter
public class BankTranfer extends AggregateRoot {
	private String companyCode;
	private String companyNameKana;
	private PersonId personId;
	private BankInfo fromBank;
	private BankInfo toBank;
	private String departmentCode;
	private PayMoney paymentMoney;
	private PayBonusAtr paymentBonusAtr;
	private int processingNo;
	private YearMonth processingYM;
	private GeneralDate paymentDate;
	private SparePayAtr sparePaymentAtr;

	/**
	 * Constructor with full parameter
	 * @param companyCode company code
	 * @param companyNameKana company name katakana
	 * @param personId person identify
	 * @param fromBank form bank information
	 * @param toBank to bank information
	 * @param departmentCode department code
	 * @param paymentMoney payment money
	 * @param paymentBonusAtr payment bonus attribute
	 * @param processingNo processing number
	 * @param processingYM processing year month
	 * @param paymentDate payment date
	 * @param sparePaymentAtr spare payment attribute
	 */
	public BankTranfer(String companyCode, String companyNameKana, PersonId personId, BankInfo fromBank,
			BankInfo toBank, String departmentCode, PayMoney paymentMoney, PayBonusAtr paymentBonusAtr,
			int processingNo, YearMonth processingYM, GeneralDate paymentDate, SparePayAtr sparePaymentAtr) {
		super();
		this.companyCode = companyCode;
		this.companyNameKana = companyNameKana;
		this.personId = personId;
		this.fromBank = fromBank;
		this.toBank = toBank;
		this.departmentCode = departmentCode;
		this.paymentMoney = paymentMoney;
		this.paymentBonusAtr = paymentBonusAtr;
		this.processingNo = processingNo;
		this.processingYM = processingYM;
		this.paymentDate = paymentDate;
		this.sparePaymentAtr = sparePaymentAtr;
	}

	/**
	 * Create from bank information
	 * @param branchId branch identify
	 * @param bankNameKana bank name katakana
	 * @param branchNameKana branch name katakana
	 * @param accountAtr account attribute
	 * @param accountNo account number
	 * @param accountNameKana account name katakana
	 * @return bank information
	 */
	public void fromBank(String branchId, String bankNameKana, String branchNameKana, int accountAtr, String accountNo, String accountNameKana) {
		this.fromBank = createBank(branchId, bankNameKana, branchNameKana, accountAtr, accountNo, accountNameKana);
	}
	
	/**
	 * Create to bank information
	 * @param branchId branch identify
	 * @param bankNameKana bank name katakana
	 * @param branchNameKana branch name katakana
	 * @param accountAtr account attribute
	 * @param accountNo account number
	 * @param accountNameKana account name katakana
	 * @return bank information
	 */
	public void toBank(String branchId, String bankNameKana, String branchNameKana, int accountAtr, String accountNo, String accountNameKana) {
		this.toBank = createBank(branchId, bankNameKana, branchNameKana, accountAtr, accountNo, accountNameKana);
	}

	/**
	 * Create bank information
	 * @param branchId branch identify
	 * @param bankNameKana bank name katakana
	 * @param branchNameKana branch name katakana
	 * @param accountAtr account attribute
	 * @param accountNo account number
	 * @param accountNameKana account name katakana
	 * @return bank information
	 */
	private BankInfo createBank(String branchId, String bankNameKana, String branchNameKana, int accountAtr, String accountNo, String accountNameKana) {
		return new BankInfo(branchId, bankNameKana, branchNameKana, accountAtr, accountNo, accountNameKana);
	}

}
