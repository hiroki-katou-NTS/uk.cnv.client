package nts.uk.ctx.at.record.app.find.dailyperformanceprocessing.creationprocess;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.CreatingDailyResultsCondition;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.CreatingDailyResultsConditionRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CreatingDailyResultsConditionFinder {

	@Inject
	private CreatingDailyResultsConditionRepository repository;
	
	/**
	 * UKDesign.UniversalK.就業.KDW_日別実績.KDW001_就業計算と集計.D：実施内容確認.アルゴリズム.D：未来日を作成するかのチェック.D：未来日を作成するかのチェック
	 */
	public boolean isCreatingFutureDay(CreateFutureDayCheckParam param) {
		String cid = AppContexts.user().companyId();
		// ドメインモデル「日別実績を作成する条件」を取得する
		Optional<CreatingDailyResultsCondition> optDomain = this.repository.findByCid(cid);
		// 「日別実績を作成するか判断する」を呼び出す
		return optDomain.map(data -> data.isCreatingDailyResults(param.getEndDate())).orElse(false);
	}
	
	/**
	 * ドメインモデル「日別実績を作成する条件」を取得する
	 */
	public boolean getCreatingDailyResultsCondition() {
		String cid = AppContexts.user().companyId();
		return this.repository.findByCid(cid).map(data -> data.getIsCreatingFutureDay().isUse())
				.orElse(false);
	}
}
