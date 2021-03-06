package nts.uk.ctx.sys.assist.app.find.resultofdeletion;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.sys.assist.app.find.params.LogDataParams;
import nts.uk.ctx.sys.assist.dom.deletedata.CategoryDeletion;
import nts.uk.ctx.sys.assist.dom.deletedata.ManualSetDeletionRepository;
import nts.uk.ctx.sys.assist.dom.deletedata.ResultDeletionRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ResultOfDeletionFinder {
	
	@Inject
	private ResultDeletionRepository finder;

	@Inject
	private ManualSetDeletionRepository manualSetDeletionRepository;

	// step データ削除の保存結果を取得
	public List<ResultOfDeletionDto> getResultOfDeletion(LogDataParams logDataParams) {

		// step ドメインモデル「データ削除の保存結果」を取得
		List<ResultOfDeletionDto> resultOfDeletions = finder.getResultOfDeletion(AppContexts.user().companyId(), logDataParams.getStartDateOperator(),
						logDataParams.getEndDateOperator(), logDataParams.getListOperatorEmployeeId()).stream()
				.map(item -> ResultOfDeletionDto.fromDomain(item))
				.collect(Collectors.toList());
		
		// step 処理IDでドメインモデル「データ削除の手動設定」を取得する
		List<String> listDelId = resultOfDeletions.stream()
				.map(item -> item.getDelId())
				.collect(Collectors.toList());
		List<String> manualSetDeletionsFilterIds = this.manualSetDeletionRepository.getManualSetDeletionsSystemTypeAndId(listDelId).stream()
				.filter(manualSetDeletion -> {
					Optional<CategoryDeletion> op = manualSetDeletion.getCategories().stream()
							.filter(categoryDeletion -> categoryDeletion.getSystemType() == logDataParams.getSystemType())
							.findAny();
					return op.isPresent();
				})
				.map(manualSetDeletion -> manualSetDeletion.getDelId())
				.collect(Collectors.toList());
		
		// step List<データ削除の結果>を返す。
		return resultOfDeletions.stream()
				.filter(resultOfDeletion -> manualSetDeletionsFilterIds.contains(resultOfDeletion.getDelId()))
				.collect(Collectors.toList());
	}
}
