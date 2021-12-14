package nts.uk.ctx.sys.tenant.dom;

import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.error.RawErrorMessage;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.gateway.dom.tenantlogin.TenantAuthentication;

/**
 * テナントを作る
 */
public class CreateTenant {

	public static AtomTask create(
			Require require,
			String tenantCode,
			String tenantPassword,
			GeneralDate tenantStartDate,
			String administratorLoginId,
			String administratorPassword) {

		if (require.existsTenant(tenantCode)) {
			throw new BusinessException(
					new RawErrorMessage("テナントコード " + tenantCode + " は既に存在します。"));
		}

		val tenantAuthentication = TenantAuthentication.create(tenantCode, tenantPassword, tenantStartDate);
		
		return AtomTask.of(() -> {

			require.add(tenantAuthentication);

		});
	}

	public static interface Require {

		boolean existsTenant(String tenantCode);

		void add(TenantAuthentication tenantAuthentication);
	}
}
