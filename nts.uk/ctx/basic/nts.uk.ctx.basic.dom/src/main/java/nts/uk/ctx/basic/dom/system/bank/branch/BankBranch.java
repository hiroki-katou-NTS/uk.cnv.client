package nts.uk.ctx.basic.dom.system.bank.branch;

import java.util.UUID;

import lombok.Getter;
import nts.arc.error.BusinessException;
import nts.arc.layer.dom.AggregateRoot;
import nts.gul.text.StringUtil;
import nts.uk.ctx.basic.dom.company.CompanyCode;
import nts.uk.shr.com.primitive.Memo;

public class BankBranch extends AggregateRoot{
	/**
	 * Company code
	 */
	@Getter
	private CompanyCode companyCode;
	
	/**
	 * branch id
	 */
	@Getter
	private UUID branchId;
	
	/**
	 * Bank code
	 */
	@Getter
	private String bankCode;
	/**
	 *  Bank branch code
	 */
	@Getter
	private BankBranchCode bankBranchCode;
	/**
	 *   Bank branch name
	 */
	@Getter
	private BankBranchName bankBranchName;
	/**
	 *   Bank branch name katakana
	 */
	@Getter
	private BankBranchNameKana bankBranchNameKana;
	/**
	 *   Memo
	 */
	@Getter
    private Memo memo;
	
	@Override
	public void validate() {
		super.validate();
		if (this.bankBranchCode == null || StringUtil.isNullOrEmpty(this.bankBranchCode.v(), true)) {
			throw new BusinessException("ER001");
		}
		
		if (this.bankBranchName == null || StringUtil.isNullOrEmpty(this.bankBranchName.v(), true)) {
			throw new BusinessException("ER001");
		}
	}
	
	public BankBranch(CompanyCode companyCode, UUID branchId, String bankCode, BankBranchCode bankBranchCode,
			BankBranchName bankBranchName, BankBranchNameKana bankBranchNameKana, Memo memo) {
		super();
		this.companyCode = companyCode;
		this.branchId = branchId;
		this.bankCode = bankCode;
		this.bankBranchCode = bankBranchCode;
		this.bankBranchName = bankBranchName;
		this.bankBranchNameKana = bankBranchNameKana;
		this.memo = memo;
	}

	public static BankBranch createFromJavaType (String companyCode, String branchId, String bankCode,String bankBranchCode, String bankBranchName,
			String bankBranchNameKana, String memo){
		if (StringUtil.isNullOrEmpty(bankBranchCode, true)) {
			throw new BusinessException("ER001");
		}
		
		if (StringUtil.isNullOrEmpty(bankBranchName, true)) {
			throw new BusinessException("ER001");
		}
		
		return new BankBranch(new CompanyCode(companyCode), UUID.fromString(branchId), bankCode,new BankBranchCode(bankBranchCode), new BankBranchName(bankBranchName), new BankBranchNameKana(bankBranchNameKana), new Memo(memo));
	}
	
	/**
	 * New mode: branch
	 * @param companyCode
	 * @param bankCode
	 * @param bankBranchCode
	 * @param bankBranchName
	 * @param bankBranchNameKana
	 * @param memo
	 * @return
	 */
	public static BankBranch newBranch(String companyCode, String bankCode,String bankBranchCode, String bankBranchName,
			String bankBranchNameKana, String memo) {
		UUID branchId = UUID.randomUUID();
		return createFromJavaType(companyCode, branchId.toString(), bankCode, bankBranchCode, bankBranchName, bankBranchNameKana, memo);
	}
	
}


