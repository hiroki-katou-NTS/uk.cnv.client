package nts.uk.ctx.cloud.operate.dom.service;

import java.util.Optional;

import nts.arc.task.tran.AtomTask;
import nts.uk.ctx.bs.company.dom.company.AbolitionAtr;
import nts.uk.ctx.bs.company.dom.company.Company;
import nts.uk.ctx.bs.company.dom.company.CompanyCode;
import nts.uk.ctx.bs.company.dom.company.MonthStr;
import nts.uk.ctx.bs.company.dom.company.Name;
import nts.uk.ctx.bs.company.dom.company.primitive.ContractCd;

/**
 * デフォルト会社を作る
 * @author keisuke_hoshina
 *
 */
public class CreateDefaultCompanyService {

	public static AtomTask create(Require require,String contractCode, String companyName) {
		Company company = new Company(new CompanyCode("0001"),
																	new Name(companyName),
																	MonthStr.FOUR,
																	AbolitionAtr.NOT_ABOLITION,
																	Optional.empty(),
																	Optional.empty(),
																	Optional.empty(),
																	Optional.empty(),
																	new ContractCd(contractCode),
																	Optional.empty(),
																	Optional.empty());
		return AtomTask.of(() ->{
			require.saveDefaultCompany(company);
		});
	}

	public static interface Require{
		void saveDefaultCompany (Company company);
	}
}
