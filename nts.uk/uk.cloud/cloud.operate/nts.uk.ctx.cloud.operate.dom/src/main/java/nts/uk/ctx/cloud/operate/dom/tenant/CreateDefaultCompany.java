package nts.uk.ctx.cloud.operate.dom.tenant;

import lombok.val;
import nts.arc.task.tran.AtomTask;
import nts.uk.ctx.bs.company.dom.company.*;
import nts.uk.ctx.bs.company.dom.company.primitive.ContractCd;
import nts.uk.shr.com.company.CompanyId;

import java.util.Optional;

public class CreateDefaultCompany {

    public static AtomTask create(Require require, String tenantCode, String companyName) {

        val defaultCompany = createDefaultCompany(tenantCode, companyName);

        return AtomTask.of(() -> {
            require.save(defaultCompany);
        });
    }

    private static Company createDefaultCompany(String tenantCode, String companyName) {
        String companyCode = "0001";
        return new Company(
                new CompanyCode(companyCode),
                new Name(companyName),
                CompanyId.create(tenantCode, companyCode),
                MonthStr.FOUR,
                AbolitionAtr.NOT_ABOLITION,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                new ContractCd(tenantCode),
                Optional.empty(),
                Optional.empty());
    }

    public static interface Require {

        void save(Company company);
    }
}