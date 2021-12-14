package nts.uk.ctx.exio.app.input.errors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportErrors;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportErrors.RequireToText;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportErrorsRepository;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItem;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItemsRepository;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSettingRepository;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.TransactionAttribute;

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
	
	/**
	 * 指定したページのエラーテキストを返す
	 * @param pageNo 1スタート
	 * @return
	 */
	public ErrorsTextDto getTextPage(ExternalImportCode settingCode, int pageNo) {
		
		String companyId = AppContexts.user().companyId();
		val setting = settingRepo.get(companyId, settingCode).get();
		val context = ExecutionContext.create(setting);
		
		int startErrorNo = MAX_PAGE_SIZE * (pageNo - 1);
		val errors = errorsRepo.find(context, startErrorNo, MAX_PAGE_SIZE);
		
		return new ErrorsTextDto(errors.isExecution(), pageNo, errors.count(), errorsToText(context, errors));
	}
	
	private String errorsToText(ExecutionContext context, ExternalImportErrors errors) {
		
		val require = new RequireToText() {
			@Override
			public ImportableItem getImportableItem(ImportingDomainId domainId, int itemNo) {
				return itemRepo.get(domainId, itemNo).get();
			}
		};
		
		return errors.toText(require, context);
	}
}
