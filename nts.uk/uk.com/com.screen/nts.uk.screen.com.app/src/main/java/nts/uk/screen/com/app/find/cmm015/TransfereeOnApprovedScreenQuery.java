package nts.uk.screen.com.app.find.cmm015;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.dom.workplace.master.service.WorkplaceExportService;
import nts.uk.ctx.bs.employee.dom.workplace.master.service.WorkplaceInforParam;
import nts.uk.ctx.workflow.app.query.approvermanagement.ApprovalRouteSpStatusQuery;
import nts.uk.ctx.workflow.app.query.approvermanagement.ApprovalRouteSpStatusQueryProcessor;
import nts.uk.query.pub.employee.EmployeeInformationExport;
import nts.uk.query.pub.employee.EmployeeInformationPub;
import nts.uk.query.pub.employee.EmployeeInformationQueryDto;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.共通.CMM_マスタメンテナンス.CMM015_職場異動の登録.A:職場異動の登録.メニュー別OCD.異動者が承認ルートにいるか確認する.異動者が承認ルートにいるか確認する
 * @author NWS-DungDV
 *
 */
@Stateless
public class TransfereeOnApprovedScreenQuery {
	
	@Inject
	private ApprovalRouteSpStatusQueryProcessor approvalRouteSpStatusQueryProcessor;
	
	@Inject
	private EmployeeInformationPub employeeInformationPub;
	
	@Inject
	private WorkplaceExportService workplaceExportService;

	/**
	 * 異動者が承認ルートにいるか確認する
	 * @param sids List<社員ID>
	 * @param basedate 基準日
	 */
	public TransfereeOnApproved check(List<String> sids, GeneralDate basedate) {
		// List<承認ルート状況>
		List<ApprovalRouteSpStatusQuery> approvalRoutes = new ArrayList<ApprovalRouteSpStatusQuery>();
		// Loop 社員ID　in　List<社員ID>
		sids.forEach(sid -> {
			// 異動者の承認ルート指定状況を取得する
			ApprovalRouteSpStatusQuery item = approvalRouteSpStatusQueryProcessor.get(sid, basedate);
			approvalRoutes.add(item);
		});
		
		// A = List<承認ルート状況>：flatMap $.指定者
		Stream<String> empListStream = approvalRoutes.stream().flatMap(mapper -> Stream.of(mapper.getSid()));
	
		// B = List<承認ルート状況>：flatMap $.承認対象社員リスト
		Stream<String> appoveEmpListStream = approvalRoutes.stream()
				.flatMap(mapper -> Stream.of(mapper.getApproveEmpList()))
				.flatMap(mapper -> mapper.stream());
		
		// C = List<承認ルート状況>：flatMap $.指定者の承認者リスト
		Stream<String> approverListStream = approvalRoutes.stream()
				.flatMap(mapper -> Stream.of(mapper.getApproverList()))
				.flatMap(mapper -> mapper.stream());
		
		// 社員情報リスト＝ A + B + C distinct
		List<String> empInforList = Stream
				.concat(Stream.concat(empListStream, appoveEmpListStream), approverListStream)
				.distinct()
				.collect(Collectors.toList());
		
		// <<Public>> 社員の情報を取得する: output List<社員情報>
		EmployeeInformationQueryDto param = new EmployeeInformationQueryDto(empInforList, basedate, false, false, false, false, false, false);
		List<EmployeeInformationExport> empInfors = employeeInformationPub.find(param);
		
		// 職場情報リスト＝List<承認ルート状況>flatmap　$．承認職場リスト．職場ID distinct
		List<String> wkpInforList = approvalRoutes
				.stream()
				.flatMap(mapper -> Stream.of(mapper.getWorkplaceList()))
				.flatMap(mapper -> mapper.stream())
				.distinct()
				.collect(Collectors.toList());
		
		// [No.560]職場IDから職場の情報をすべて取得する: output List<職場情報一覧>
		List<WorkplaceInforParam> wkpListInfo = workplaceExportService.getWorkplaceInforFromWkpIds(AppContexts.user().companyId(), wkpInforList, basedate);
		
		return new TransfereeOnApproved(approvalRoutes, empInfors, wkpListInfo);
		
	}
}
