package nts.uk.ctx.sys.env.infra.repository.mailnoticeset.maildestination;

import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.FunctionType;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.MailDestinationFunctionManage;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.MailDestinationFunctionManageRepository;
import nts.uk.ctx.sys.env.infra.entity.mailnoticeset.maildestination.SevmtMailDestinMng;
import nts.uk.ctx.sys.env.infra.entity.mailnoticeset.maildestination.SevmtMailDestinMngPK;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@Stateless
public class JpaMailDestinationFunctionManageRepositoryImpl extends JpaRepository
		implements MailDestinationFunctionManageRepository {

	private SevmtMailDestinMng toEntity(MailDestinationFunctionManage domain) {
		String cid = AppContexts.user().companyId();
		SevmtMailDestinMngPK pk = new SevmtMailDestinMngPK(cid, domain.getFunctionId().value);
		return new SevmtMailDestinMng(pk, domain.getUseCompanyMailAddress().value,
				domain.getUseCompanyMobileMailAddress().value, domain.getUsePersonalMailAddress().value,
				domain.getUsePersonalMobileMailAddress().value);
	}

	private MailDestinationFunctionManage toDomain(SevmtMailDestinMng entity) {
		return new MailDestinationFunctionManage(EnumAdaptor.valueOf(entity.pk.functionId, FunctionType.class),
				NotUseAtr.valueOf(entity.companyMailUse), NotUseAtr.valueOf(entity.companyMailMobileUse),
				NotUseAtr.valueOf(entity.personalMailUse), NotUseAtr.valueOf(entity.personalMailMobileUse));
	}

	@Override
	public void insert(MailDestinationFunctionManage domain) {
		this.commandProxy().insert(this.toEntity(domain));
	}

	@Override
	public void update(MailDestinationFunctionManage domain) {
		this.commandProxy().update(this.toEntity(domain));
	}

	@Override
	public void delete(String cid, int functionId) {
		this.commandProxy().remove(SevmtMailDestinMng.class, new SevmtMailDestinMngPK(cid, functionId));
	}

	@Override
	public Optional<MailDestinationFunctionManage> findByFunctionId(String cid, int functionId) {
		return this.queryProxy().find(new SevmtMailDestinMngPK(cid, functionId), SevmtMailDestinMng.class)
				.map(this::toDomain);
	}

}
