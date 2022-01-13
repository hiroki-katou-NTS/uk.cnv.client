package nts.uk.ctx.at.shared.dom.scherec.taskmanagement.domainservice;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskassign.taskassignemployee.TaskAssignEmployee;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskassign.taskassignworkplace.NarrowingDownTaskByWorkplace;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskassign.taskassignworkplace.NarrowingDownTaskByWorkplaceFromEmployeesService;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameUsageSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;

/**
 * @name: 社員が利用できる作業を取得する
 * @path UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).作業管理.作業絞込.社員が利用できる作業を取得する
 * @author ThanhPV
 */
@Stateless
public class GetWorkAvailableToEmployeesService {
   
//■Public
	/**
     * @name [1] 取得する
     * @Description 説明:指定社員の基準日時点で利用できる作業一覧を取得する
     * @input require
     * @input companyID 会社ID
     * @input employeeID 社員ID
     * @input date 基準日
     * @input taskFrameNo 作業枠NO
     * @input taskCodes 上位枠作業コード
     * @output List<TaskCode> List<作業>
     */
    public static List<Task> get(Require require, String companyID, String employeeID, GeneralDate date, TaskFrameNo taskFrameNo, Optional<TaskCode> taskCodes) {
    	//	$作業枠利用設定 = require.作業枠利用設定を取得する()	
    	TaskFrameUsageSetting taskFrameUsageSetting = require.getTask();
    	
    	//$作業枠設定 = $作業枠利用設定.枠設定:																		
    		//filter $.作業枠NO = 作業枠NO																	
    		//filter $.利用区分 == する
    	List<TaskFrameSetting> frameSettingList = taskFrameUsageSetting.getFrameSettingList().stream()
    		.filter(c->c.getTaskFrameNo().v() == taskFrameNo.v())
    		.filter(c->c.getUseAtr().isUse())
    		.collect(Collectors.toList());

    	//if $作業枠設定.isEmpty
    	if(frameSettingList.isEmpty()) 
    		//return Collections.emptyList()
    		return new ArrayList<Task>();
    	
    	//$絞込作業 = [prv-1] 絞込設定の作業を取得する(require, 会社ID, 社員ID, 基準日, 作業枠NO)
    	List<TaskCode> listTaskCode = getTaskCode(require, companyID, employeeID, date, taskFrameNo);
    	
    	List<TaskCode> childTaskList = new ArrayList<TaskCode>();
    	//if 作業枠NO <> 1 AND 上位枠作業コード.isPresent
    	if(taskFrameNo.v() != 1 && taskCodes.isPresent()) {
    		//$親作業 = require.親作業を取得する(作業枠NO-1, 上位枠作業コード)
			Optional<Task> task = require.getOptionalTask(new TaskFrameNo(taskFrameNo.v() -1 ), taskCodes.get());
    		//if not $親作業.isEmpty
    		if(task.isPresent())
    			//$子作業 = $親作業.子作業一覧	
    			childTaskList = task.get().getChildTaskList();
    	}
    	
    	//if $子作業.isEmpty AND $絞込作業.isEmpty
    	if(childTaskList.isEmpty() && listTaskCode.isEmpty())
    		//return require.全ての作業を取得する(基準日, 作業枠NO)
    		return require.getTask(date, Arrays.asList(taskFrameNo));
    	
    	List<TaskCode> childTaskListfilter = new ArrayList<TaskCode>();
    	//if 子作業.isPresent AND $絞込作業.isPresent
    	if(!childTaskList.isEmpty() && !listTaskCode.isEmpty()) {
    		//$利用可能作業 = $子作業：filter $絞込作業.contains($)
    		childTaskListfilter = childTaskList.stream().filter(o->listTaskCode.contains(o)).collect(Collectors.toList());
    	}else {
    		//$利用可能作業 = $子作業.追加($絞込作業)
    		childTaskList.addAll(listTaskCode);
    		childTaskListfilter = childTaskList;
    	}
    		
    		
    	//	return require.利用可能作業を取得する(基準日, 作業枠NO, $利用可能作業)	
    	return require.getListTask(date, taskFrameNo, childTaskListfilter);
    }
//■Private
    /**
     * @name [prv-1] 絞込設定の作業を取得する
     * @Description 説明:利用できる全ての作業を取得する	
     * @input require
     * @input companyID 会社ID
     * @input employeeID 社員ID
     * @input date 基準日
     * @input taskFrameNo 作業枠NO
     * @output List<TaskCode> List<作業コード>
     */
    private static List<TaskCode> getTaskCode(Require require, String companyID, String employeeID, GeneralDate date, TaskFrameNo taskFrameNo) {
    	List<TaskCode> result = new ArrayList<TaskCode>();
    	//$職場別作業の絞込 = 社員から職場別作業の絞込を取得する#取得する(require, 会社ID, 社員ID, 基準日, 作業枠NO)		
    	Optional<NarrowingDownTaskByWorkplace> narrowingDownTaskByWorkplace = NarrowingDownTaskByWorkplaceFromEmployeesService.get(require, companyID, employeeID, date, taskFrameNo);
    	//if $職場別作業の絞込.isPresent()																			
    	if(narrowingDownTaskByWorkplace.isPresent())
    		//$作業CDリスト = $職場別作業の絞込.作業一覧
    		result = narrowingDownTaskByWorkplace.get().getTaskCodeList();
    	//$社員別作業の絞込 = require.社員別作業の絞込を取得する(社員ID, 作業枠NO)	
    	List<TaskAssignEmployee> taskAssignEmployee = require.getTaskAssignEmployee(employeeID, taskFrameNo);
    	//$社員別作業コード = $社員別作業の絞込: map $.作業コード	
    	List<TaskCode> taskCodeByEmployee = taskAssignEmployee.stream().map(c->c.getTaskCode()).collect(Collectors.toList());
    	//return $作業CDリスト.add($社員別作業コード)																
    	result.addAll(taskCodeByEmployee);
    		//distinct	
    	return result.stream().distinct().collect(Collectors.toList());
    }
//■Require
    public interface Require extends NarrowingDownTaskByWorkplaceFromEmployeesService.Require {
         // [R-1] 作業枠利用設定を取得する
         // 作業枠利用設定Repository.Get(会社ID)	
        TaskFrameUsageSetting getTask();
         // [R-2] 全ての作業を取得する
         // 作業Repository.Get(会社ID,基準日,作業枠リスト)
        List<Task> getTask(GeneralDate date, List<TaskFrameNo> TaskFrameNo);
         // [R-3] 利用可能作業を取得する
         // 作業Repository.Get(会社ID,基準日,作業枠NO,作業コードリスト)
        List<Task> getListTask(GeneralDate referenceDate, TaskFrameNo taskFrameNo, List<TaskCode> codes);
         // [R-4] 親作業を取得する
         // 作業Repository.Get(会社ID,作業枠NO,コード)
        Optional<Task> getOptionalTask(TaskFrameNo taskFrameNo, TaskCode codes);
         // [R-5] 社員別作業の絞込を取得する
         // 個人割り当て作業Repository.Get(社員ID,作業枠NO)
        List<TaskAssignEmployee> getTaskAssignEmployee(String employeeId, TaskFrameNo taskFrameNo);
    }
}
