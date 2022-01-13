package nts.uk.screen.at.app.kdw006.k;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesDetail;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesHistoryRepository;

/**
 * ScreenQuery: 作業補足情報の選択肢詳細を取得する
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW006_前準備.K：任意作業コード設定.メニュー別OCD.作業補足情報の選択肢詳細を取得する
 * 
 * @author chungnt
 *
 */

@Stateless
public class GetWorkInforDetails {

	@Inject
	private TaskSupInfoChoicesHistoryRepository taskSupInfoChoicesHistoryRepo;

	public List<GetWorkInforDetailsDto> getWorkInforDetails(GetWorkInforDetailsInput input) {
		List<GetWorkInforDetailsDto> result = new ArrayList<>();

		List<TaskSupInfoChoicesDetail> domains = taskSupInfoChoicesHistoryRepo.get(input.getHistoryId());

		result = domains.stream().map(m -> {
			return new GetWorkInforDetailsDto(m.getHistoryId(), m.getItemId(), m.getCode().v(), m.getName().v(),
					m.getExternalCode().map(f -> f.v()).orElse(""));
		}).collect(Collectors.toList());

		return result;
	}

	public List<GetWorkInforDetailsDto> getWorkInforDetailsbyList(GetWorkInforDetailsbyListInput input) {
		List<GetWorkInforDetailsDto> result = new ArrayList<>();
		
		List<TaskSupInfoChoicesDetail> domains = taskSupInfoChoicesHistoryRepo.get(input.getHistoryId());
		
		result = domains.stream().map(m -> {
			return new GetWorkInforDetailsDto(
					m.getHistoryId(),
					m.getItemId(),
					m.getCode().v(),
					m.getName().v(),
					m.getExternalCode().map(f -> f.v()).orElse("")
					);
		}).collect(Collectors.toList());
		
		return result;
	}

}
