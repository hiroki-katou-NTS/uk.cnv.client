package command.person.setting.selection;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import command.person.info.category.GetListCompanyOfContract;
import nts.arc.error.BusinessException;
import nts.arc.error.RawErrorMessage;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.arc.time.GeneralDate;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.bs.person.dom.person.info.category.PersonInfoCategory;
import nts.uk.ctx.bs.person.dom.person.setting.selection.IPerInfoSelectionItemRepository;
import nts.uk.ctx.bs.person.dom.person.setting.selection.PerInfoHistorySelection;
import nts.uk.ctx.bs.person.dom.person.setting.selection.PerInfoHistorySelectionRepository;
import nts.uk.ctx.bs.person.dom.person.setting.selection.PerInfoSelectionItem;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class AddSelectionItemCommandHandler extends CommandHandlerWithResult<AddSelectionItemCommand, String> {

	@Inject
	private IPerInfoSelectionItemRepository perInfoSelectionItemRepo;

	@Inject
	private PerInfoHistorySelectionRepository historySelectionRepository;

	@Override
	protected String handle(CommandHandlerContext<AddSelectionItemCommand> context) {
		AddSelectionItemCommand command = context.getCommand();
		String newId = IdentifierUtil.randomUniqueId();
		String rootCID = PersonInfoCategory.ROOT_COMPANY_ID;
		String newHistId = IdentifierUtil.randomUniqueId();

		// ドメインモデル「個人情報の選択項目」のエラーチェック
		Optional<PerInfoSelectionItem> opt = this.perInfoSelectionItemRepo
				.checkItemName(command.getSelectionItemName());

		// 「選択項目名称」は重複してはならない
		if (opt.isPresent()) {
			throw new BusinessException(new RawErrorMessage("Msg_513"));
		}

		// ドメインモデル「個人情報の選択項目」を追加登録する
		PerInfoSelectionItem domain = PerInfoSelectionItem.createFromJavaType(newId, command.getSelectionItemName(),
				command.getMemo(), command.isSelectionItemClassification() == true ? 1 : 0,
				AppContexts.user().contractCode(), command.getIntegrationCode(),
				command.getFormatSelection().getSelectionCode(),
				command.getFormatSelection().isSelectionCodeCharacter() == true ? 1 : 0,
				command.getFormatSelection().getSelectionName(),
				command.getFormatSelection().getSelectionExternalCode());

		// 「個人情報の選択項目」を追加登録する
		this.perInfoSelectionItemRepo.add(domain);

		// ドメインモデル「選択肢履歴」を登録する
		boolean itemClassification = command.isSelectionItemClassification();
		GeneralDate startDate = GeneralDate.ymd(1900, 1, 1);
		GeneralDate endDate = GeneralDate.ymd(9999, 12, 31);

		// 画面項目「グループ会社で共有する：選択項目区分をチェックする」
		if (itemClassification == true) {// TRUE → 0会社の場合
			PerInfoHistorySelection domainHist = PerInfoHistorySelection.historySelection(newHistId, newId, rootCID,
					endDate, startDate);

			// 0会社の場合:「選択肢履歴」を登録する
			this.historySelectionRepository.add(domainHist);
		} else {// FALSE → 全会社 の場合
			List<String> companyIdList = GetListCompanyOfContract.LIST_COMPANY_OF_CONTRACT;
			for (String cid : companyIdList) {
				newHistId = IdentifierUtil.randomUniqueId();
				PerInfoHistorySelection domainHist = PerInfoHistorySelection.historySelection(newHistId, newId, cid,
						endDate, startDate);

				// 全会社 の場合:「選択肢履歴」を登録する
				this.historySelectionRepository.add(domainHist);
			}

			newHistId = IdentifierUtil.randomUniqueId();
			PerInfoHistorySelection domainHist = PerInfoHistorySelection.historySelection(newHistId, newId, rootCID,
					endDate, startDate);

			// 0会社の場合: 「選択肢履歴」を登録する
			this.historySelectionRepository.add(domainHist);
		}

		return newId;
	}
}
