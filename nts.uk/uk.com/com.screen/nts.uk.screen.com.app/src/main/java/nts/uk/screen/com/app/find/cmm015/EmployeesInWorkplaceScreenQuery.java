package nts.uk.screen.com.app.find.cmm015;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.adapter.workplace.EmployeeInfoImported;
import nts.uk.ctx.at.record.dom.adapter.workplace.SyWorkplaceAdapter;
import nts.uk.ctx.workflow.dom.adapter.bs.EmployeeAdapter;
import nts.uk.ctx.workflow.dom.adapter.bs.dto.StatusOfEmploymentImport;
import nts.uk.ctx.workflow.dom.service.CollectApprovalRootService;
import nts.uk.query.pub.employee.EmployeeInformationExport;
import nts.uk.query.pub.employee.EmployeeInformationPub;
import nts.uk.query.pub.employee.EmployeeInformationQueryDto;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.共通.CMM_マスタメンテナンス.CMM015_職場異動の登録.A:職場異動の登録.メニュー別OCD.職場の所属社員一覧を取得する.職場の所属社員一覧を取得する
 * @author NWS-DungDV
 *
 */
@Stateless
public class EmployeesInWorkplaceScreenQuery {

	@Inject
    private SyWorkplaceAdapter syWorkplaceAdapter;
	
	@Inject
	private EmployeeAdapter employeeAdapter;
	
	@Inject
	private EmployeeInformationPub employeeInformationPub;
	
	@Inject
	private CollectApprovalRootService collectApprovalRootService;
	
	/**
	 * 職場の所属社員一覧を取得する
	 * @param wkpId 職場ID
	 * @param referDate 基準日
	 * @param incumbent 在職者
	 * @param closed 休業者
	 * @param leave 休職者
	 * @param retiree 退職者
	 * @return
	 */
	public List<EmployeesInWorkplace> get(String wkpId, GeneralDate referDate, Boolean incumbent, Boolean closed, Boolean leave, Boolean retiree) {
		List<String> wkpIds = new ArrayList<String>();
		wkpIds.add(wkpId);
		//1: <call> [No.597]職場の所属社員を取得する: Output 社員一覧
        List<EmployeeInfoImported> empInfos = syWorkplaceAdapter.getLstEmpByWorkplaceIdsAndPeriod(wkpIds, new DatePeriod(referDate, referDate));
        
        List<String> sids = new ArrayList<String>();
        // Loop 社員ID　in　List<社員ID>
        empInfos.forEach(e -> {
        	//2: <call>在職状態を取得
      		StatusOfEmploymentImport sttEmp = employeeAdapter.getStatusOfEmployment(e.getSid(), referDate);
      		
      		sids.add(sttEmp.getEmployeeId());
        });
        
        EmployeeInformationQueryDto param = EmployeeInformationQueryDto.builder()
        		.employeeIds(sids)
        		.referenceDate(referDate)
        		.toGetClassification(false)
        		.toGetPosition(true)
        		.toGetWorkplace(false)
        		.toGetDepartment(false)
        		.toGetEmployment(false)
        		.build();
        // 3: <call> <<Public>> 社員の情報を取得する: Output List＜社員情報＞
        List<EmployeeInformationExport> empInfors = employeeInformationPub.find(param);
        
        // 職位IDリスト
        List<String> jtIds = empInfors.stream()
        		.flatMap(e -> Stream.of(e.getPosition()))
        		.map(e -> e.getPositionId())
        		.distinct()
        		.collect(Collectors.toList());
        
        Map<String, Integer> orders = new HashMap<String, Integer>();
        
        // Loop 　職位ID　in　職位IDリスト
        jtIds.forEach(jobID -> {
        	
        	// 4: <call> 職位IDから序列の並び順を取得
        	Optional<Integer> order = collectApprovalRootService.getDisOrderFromJobID(jobID, AppContexts.user().companyId(), referDate);
        	
        	orders.put(jobID, order.orElse(null));
        });
        
        return empInfors.stream()
        		.map(e -> new EmployeesInWorkplace(
        				e.getEmployeeId(),
        				e.getEmployeeCode(),
        				e.getBusinessName(),
        				e.getPosition().getPositionId(),
        				e.getPosition().getPositionName(),
        				orders.get(e.getPosition().getPositionId())
				))
        		.collect(Collectors.toList());
        
	}
	
}
