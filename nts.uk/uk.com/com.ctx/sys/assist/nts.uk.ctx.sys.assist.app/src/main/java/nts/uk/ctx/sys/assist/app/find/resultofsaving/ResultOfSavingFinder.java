package nts.uk.ctx.sys.assist.app.find.resultofsaving;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.sys.assist.app.find.params.LogDataParams;
import nts.uk.ctx.sys.assist.dom.storage.ResultOfSavingRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
/**
 * データ保存の保存結果
 */
public class ResultOfSavingFinder {

	@Inject
	private ResultOfSavingRepository finder;

	public List<ResultOfSavingDto> getAllResultOfSaving() {
		return finder.getAllResultOfSaving().stream().map(item -> ResultOfSavingDto.fromDomain(item))
				.collect(Collectors.toList());
	}

	public ResultOfSavingDto getResultOfSavingById(String storeProcessingId) {
		return ResultOfSavingDto.fromDomain(finder.getResultOfSavingById(storeProcessingId).get());
	}
	
	//step データ保存の保存結果を取得
	public List<ResultOfSavingDto> getResultOfSaving (LogDataParams logDataParams) {
		String companyId = AppContexts.user().companyId();
		return finder.getResultOfSaving(
				companyId,
				logDataParams.getStartDateOperator(),
				logDataParams.getEndDateOperator(),
				logDataParams.getListOperatorEmployeeId()
				).stream()
				.map(item -> ResultOfSavingDto.fromDomain(item))
				.collect(Collectors.toList());
	}
}
