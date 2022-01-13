package nts.uk.ctx.exio.app.input.setting.assembly.revise;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.ReviseItemRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * 項目の編集を取得する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ReviseItemFinder {
	
	@Inject
	private ReviseItemRepository repo;
	
	public Optional<ReviseItemDto> find(ExternalImportCode settingCode, ImportingDomainId domainId, int itemNo) {
		
		String companyId = AppContexts.user().companyId();
		
		return repo.get(companyId, settingCode, domainId, itemNo)
				.map(ReviseItemDto::of);
	}
	
}
