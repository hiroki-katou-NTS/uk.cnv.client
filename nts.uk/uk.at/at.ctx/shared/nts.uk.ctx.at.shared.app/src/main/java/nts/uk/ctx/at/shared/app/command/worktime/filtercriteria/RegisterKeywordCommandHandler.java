package nts.uk.ctx.at.shared.app.command.worktime.filtercriteria;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.shared.dom.worktime.filtercriteria.WorkHoursFilterCondition;
import nts.uk.ctx.at.shared.dom.worktime.filtercriteria.WorkHoursFilterConditionRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.就業.KDL_ダイアログ.KDL001_就業時間帯選択.A：就業時間帯選択.アルゴリズム.絞り込みキーワードを登録する.絞り込みキーワードを登録する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RegisterKeywordCommandHandler extends CommandHandler<RegisterKeywordCommand> {

	@Inject
	private WorkHoursFilterConditionRepository repository;

	@Override
	protected void handle(CommandHandlerContext<RegisterKeywordCommand> context) {
		RegisterKeywordCommand command = context.getCommand();
		String cid = AppContexts.user().companyId();
		command.getConditions().stream().map(data -> data.toDomain(cid)).forEach(domain -> {
			// 登録前チェック
			domain.validate();
			// ドメインモデル「就業時間帯の絞り込み条件」を取得
			List<WorkHoursFilterCondition> conditions = this.repository.findByCid(cid);
			// 処理中のNOの絞り込み条件があるか
			Optional<WorkHoursFilterCondition> optCondition = conditions.stream()
					.filter(data -> data.getNo().equals(domain.getNo())).findFirst();
			// 存在しない
			if (!optCondition.isPresent()) {
				// ドメインモデル「就業時間帯の絞り込み条件」を新規登録する
				this.repository.insert(domain);
			} else {
				// ドメインモデル「就業時間帯の絞り込み条件」を更新する
				this.repository.update(domain);
			}
		});
	}

}
