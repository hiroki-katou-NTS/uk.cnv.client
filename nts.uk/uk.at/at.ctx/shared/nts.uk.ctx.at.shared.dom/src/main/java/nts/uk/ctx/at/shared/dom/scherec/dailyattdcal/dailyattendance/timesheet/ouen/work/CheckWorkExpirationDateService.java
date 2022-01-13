package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work;


import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameName;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameUsageSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;

/**
 * @name: 作業の有効期限をチェックする
 * @path UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).作業管理.作業マスタ.作業の有効期限をチェックする
 * @author ThanhPV
 */
@Stateless
public class CheckWorkExpirationDateService {
   
//■Public
	/**
     * @name: [1] 確認する
     * @Description 基準日に指定の作業コードを利用できるか確認する
     * @input require
     * @input date 年月日	
     * @input taskFrameNo 作業枠NO
     * @input code 作業コード
     */
    public static void check(Require require, GeneralDate date, TaskFrameNo taskFrameNo, Optional<WorkCode> code) {
    	//if 作業コード.isEmpty return	
    	if(!code.isPresent()) return;
    	//$エラー = true
    	boolean error = true;
    	//$作業 = require.作業を取得する(作業枠NO,作業コード)
    	Optional<Task> task = require.getTask(taskFrameNo, code.get());
    	//if $作業.isPresent AND $作業.有効期限内か(年月日)	$エラー = false
    	if(task.isPresent() && task.get().checkExpirationDate(date)) {
    		error = false;
    	}
    	//if $エラー
        if(error){
        	//$作業枠利用設定= require.作業枠利用設定を取得する()
        	TaskFrameUsageSetting taskFrameUsageSetting = require.getTaskFrameUsageSetting();
        	//$作業名 = 作業枠利用設定.枠設定:																
        		//filter $.作業枠NO = 作業枠NO														
        		//map $.作業枠名
        	Optional<TaskFrameName> taskFrameName = taskFrameUsageSetting.getFrameSettingList().stream()
        		.filter(c->c.getTaskFrameNo().v() == taskFrameNo.v()).map(c->c.getTaskFrameName()).findAny();
        	//BusinessException: Msg_2080	{0}:年月日　{1):$作業名　{2}:作業コード	
            throw new BusinessException("Msg_2080", date.toString(), taskFrameName.map(n->n.v()).orElse(""), code.map(c->c.v()).orElse(""));
        }

    }
//■Require
    public interface Require {
         // [R-1] 作業を取得する
         // 作業Repository.Get(会社ID,作業枠NO,コード)
        Optional<Task> getTask(TaskFrameNo taskFrameNo, WorkCode code);
         // [R-2] 作業枠利用設定を取得する
         // 作業枠利用設定Repository.Get(会社ID)
        TaskFrameUsageSetting getTaskFrameUsageSetting();
    }
}
