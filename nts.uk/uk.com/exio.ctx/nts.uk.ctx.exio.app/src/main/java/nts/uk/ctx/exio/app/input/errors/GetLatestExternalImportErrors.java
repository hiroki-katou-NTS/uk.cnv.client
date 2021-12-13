package nts.uk.ctx.exio.app.input.errors;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomain;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainRepository;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportErrors;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportErrors.RequireToText;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportErrorsRepository;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItem;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItemsRepository;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSettingRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * 前回実行した外部受入の出力エラーを取得する。
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class GetLatestExternalImportErrors {

	/**
	 * 1ページあたりのエラー数の上限。
	 * エラー数は予測できないため、一度に全て読むとOOMEのリスクがある。
	 * それを避けるために、一度に読める量を制限する。
	 * 適正値は不明。暫定で1000とする。
	 */
	private final int MAX_PAGE_SIZE = 1000;
	
	@Inject
	private ExternalImportSettingRepository settingRepo;
	
	@Inject
	private ExternalImportErrorsRepository errorsRepo;
	
	@Inject
	private ImportableItemsRepository itemRepo;
	
	@Inject
	private ImportingDomainRepository domainRepo;
	
	/**
	 * 指定したページのエラーテキストを返す
	 * @param pageNo 1スタート
	 * @return
	 */
	public ErrorsTextDto getTextPage(ExternalImportCode settingCode, int pageNo) {
		
		String companyId = AppContexts.user().companyId();
		val context = ExecutionContext.createForErrorTableName(companyId);
		
		int startErrorNo = MAX_PAGE_SIZE * (pageNo - 1);
		val errors = errorsRepo.find(companyId, startErrorNo, MAX_PAGE_SIZE);
		
		return new ErrorsTextDto(errors.isExecution(), pageNo, errors.count(), errorsToText(errors));
	}
	
	private String errorsToText(ExternalImportErrors errors) {
		
		val require = new RequireToText() {
			
			private Map<String, String> domainNameChache;
			{
				domainNameChache = new HashMap<>();
			}
			
			@Override
			public String getDomainName(ImportingDomainId domainId) {
				if(domainId == null) return "";
				
				if (!domainNameChache.containsKey(domainId.name())) {
					ImportingDomain domain = domainRepo.find(domainId);
					domainNameChache.put(domainId.name(), domain.getName());
				}
				return domainNameChache.get(domainId.name());
			}
			
			@Override
			public ImportableItem getImportableItem(ImportingDomainId domainId, int itemNo) {
				return itemRepo.get(domainId, itemNo).get();
			}
		};
		
		return errors.toText(require);
	}
}
