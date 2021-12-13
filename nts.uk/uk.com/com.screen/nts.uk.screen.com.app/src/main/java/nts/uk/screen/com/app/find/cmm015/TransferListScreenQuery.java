package nts.uk.screen.com.app.find.cmm015;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.adapter.jobtitle.JobTitleInfoAdapter;
import nts.uk.ctx.at.shared.dom.adapter.jobtitle.JobTitleInfoImport;
import nts.uk.ctx.at.shared.dom.adapter.jobtitle.SequenceMasterImport;
import nts.uk.ctx.at.shared.dom.adapter.workplace.config.info.JobTitleExport;
import nts.uk.ctx.at.shared.dom.adapter.workplace.config.info.JobTitleHistoryExport;
import nts.uk.ctx.at.shared.dom.adapter.workplace.config.info.WorkplaceConfigInfoAdapter;
import nts.uk.ctx.bs.employee.app.query.employee.EmpsChangeHistoryQuery;
import nts.uk.ctx.bs.employee.app.query.employee.EmpsChangeHistoryQueryProcessor;
import nts.uk.ctx.bs.employee.dom.workplace.master.service.WorkplaceExportService;
import nts.uk.ctx.bs.employee.dom.workplace.master.service.WorkplaceInforParam;
import nts.uk.query.pub.employee.EmployeeInformationExport;
import nts.uk.query.pub.employee.EmployeeInformationPub;
import nts.uk.query.pub.employee.EmployeeInformationQueryDto;
import nts.uk.shr.com.context.AppContexts;

/**
 * «ScreenQuery»
 * UKDesign.UniversalK.共通.CMM_マスタメンテナンス.CMM015_職場異動の登録.A:職場異動の登録.メニュー別OCD.異動一覧を作成する.異動一覧を作成する
 * @author NWS-DungDV
 *
 */
@Stateless
public class TransferListScreenQuery {

	@Inject
	private EmpsChangeHistoryQueryProcessor empsChangeHistoryQueryProcessor;
	
	@Inject
	private EmployeeInformationPub employeeInformationPub;
	
	@Inject
	private WorkplaceExportService workplaceExportService;
	
	@Inject
	private WorkplaceConfigInfoAdapter configInfoAdapter;
	
	@Inject
	private JobTitleInfoAdapter infoAdapter;
	
	/**
	 * 異動一覧を作成する
	 * @param period 期間
	 */
	public TransferList createList(DatePeriod period) {
		
		// 職場または職位履歴を変更している社員を取得する
		EmpsChangeHistoryQuery empsChangeHistory = empsChangeHistoryQueryProcessor.get(new DatePeriod(period.start().addDays(-1), period.end()));
		
		// A＝List<期間付き職場履歴項目>：flatmap　$.社員ID
		Stream<String> awhItems = empsChangeHistory.getAwhItems().stream()
				.flatMap(mapper -> Stream.of(mapper.getEmployeeId()));
		
		// B＝List<期間付き職位履歴項目>：flatmap　$.社員ID
		Stream<String> ajthItems = empsChangeHistory.getAjthItems().stream()
				.flatMap(mapper -> Stream.of(mapper.getEmployeeId()));
		
		// 社員名称リスト＝A＋B distinct
		List<String> sids = Stream.concat(awhItems, ajthItems)
				.distinct()
				.collect(Collectors.toList());
		
		// <<Public>> 社員の情報を取得する
		EmployeeInformationQueryDto param = new EmployeeInformationQueryDto(sids, period.start(), true, false, false, false, false, false);
		List<EmployeeInformationExport> empInfors = employeeInformationPub.find(param);
		
		// 職場名称リスト＝List<期間付き職場履歴項目>：flatmap　$．所属職場履歴項目．職場ID distinct
		List<String> wkpList = empsChangeHistory.getAwhItems().stream()
				.flatMap(mapper -> Stream.of(mapper.getWorkplaceId()))
				.distinct()
				.collect(Collectors.toList());
		
		List<WorkplaceInforParam> wkpListInfo = new ArrayList<WorkplaceInforParam>();
		wkpList.forEach(wkpId -> {
			GeneralDate wkpStart = empsChangeHistory.getAwhItems().stream()
					.filter(x -> x.getWorkplaceId().equals(wkpId))
					.map(x -> x.getStartDate())
					.max((x, y) -> x.compareTo(y))
					.get();
			// [No.560]職場IDから職場の情報をすべて取得する
			wkpListInfo.addAll(workplaceExportService.getWorkplaceInforFromWkpIds(AppContexts.user().companyId(), Arrays.asList(wkpId), wkpStart));
		});
		
		// 職位名称リスト＝List<期間付き職位履歴項目>：flatmap　$．所属職位履歴項目．職位ID distinct
		List<String> jtList = empsChangeHistory.getAjthItems().stream()
				.flatMap(mapper -> Stream.of(mapper.getJobTitleId()))
				.distinct()
				.collect(Collectors.toList());
		
		List<JobTitleInfoImport> jtInfor = new ArrayList<JobTitleInfoImport>();
		jtList.forEach(jtId -> {
			GeneralDate jtStart = empsChangeHistory.getAjthItems().stream()
					.filter(x -> x.getJobTitleId().equals(jtId))
					.map(x -> x.getStartDate())
					.max((x, y) -> x.compareTo(y))
					.get();
			// 職位IDから職位を取得する
			jtInfor.addAll(getJobTitleFromIds(AppContexts.user().companyId(), Arrays.asList(jtId), jtStart));
		});
		
		return new TransferList
			(
				empsChangeHistory.getAwhItems(),	// List<期間付き職場履歴項目>
				empsChangeHistory.getAjthItems(),	// List<期間付き職位履歴項目>
				empInfors,							// List<社員ID、社員コード、ビジネスネーム、ビジネスネームカナ>
				wkpListInfo,						// List＜職場ID、職場CD、職場名称、職場表示名、職場総称、職場外部コード、階層コード＞
				jtInfor								// List<職位ID、職位コード、職位名称、序列コード、管理職とする、並び順>
			);
	}
	
	/**
	 *  職位IDから職位を取得する
	 * @param cid 会社ID
	 * @param jtList 職位ID（List）
	 * @param date 基準日
	 * @return List<職位一覧情報>
	 */
	private List<JobTitleInfoImport> getJobTitleFromIds(String companyId, List<String> jtList, GeneralDate date) {
		// ドメインモデル「職位」を取得する Start ↓
		List<JobTitleExport> jobTitleExport = this.configInfoAdapter.findAllById(companyId, jtList, date);
		// End ↑
		
		// ドメインモデル「職位情報」を取得する Start ↓
		List<JobTitleHistoryExport> lstJobHis = new ArrayList<>();
		jobTitleExport.forEach(x -> {
			lstJobHis.addAll(x.getJobTitleHistories());
		});
		List<JobTitleInfoImport> jobTitleInfoImports = new ArrayList<>();
		for (JobTitleHistoryExport jobtitle : lstJobHis) {
			String historyId = jobtitle.getHistoryId();
			List<JobTitleInfoImport> jobTitleInfos = this.infoAdapter.findByJobIds(companyId, jtList, historyId);
			jobTitleInfoImports.addAll(jobTitleInfos);
		}
		// End ↑
		
		if (!jobTitleInfoImports.isEmpty()) {
			// ドメインモデル「序列」をすべて取得する Start ↓
			Map<String, Integer> lstMasterImports = new HashMap<>();
			for (JobTitleInfoImport jobTitleInfo : jobTitleInfoImports) {
				String sequenceCode = jobTitleInfo.getSequenceCode();
				Map<String, Integer> masterImports = this.infoAdapter.findAll(companyId, sequenceCode).stream().collect(
						Collectors.toMap(SequenceMasterImport::getSequenceCode, SequenceMasterImport::getOrder));
				lstMasterImports.putAll(masterImports);
			}
			// End ↑
			
			// 取得したドメインモデル「職位情報」をドメインモデル「序列．並び順」で並び替える
			jobTitleInfoImports.forEach(e -> {
				if (lstMasterImports.containsKey(e.getSequenceCode()) && e.getSequenceCode() != null) {
					e.setOrder(lstMasterImports.get(e.getSequenceCode()));
				} else {
					// If can't find then set order is last
					e.setOrder(999);
				}
			});
			jobTitleInfoImports.sort((e1, e2) -> e1.getOrder() - e2.getOrder());
		}
		
		return jobTitleInfoImports;
	}
}
