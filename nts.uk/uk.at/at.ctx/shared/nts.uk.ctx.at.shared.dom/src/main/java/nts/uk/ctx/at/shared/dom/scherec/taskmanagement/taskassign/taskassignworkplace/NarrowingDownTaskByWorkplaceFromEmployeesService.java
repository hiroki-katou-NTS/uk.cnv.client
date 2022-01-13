package nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskassign.taskassignworkplace;


import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;

/**
 * @name: 社員から職場別作業の絞込を取得する
 * @path UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).作業管理.作業絞込.職場別作業の絞込.社員から職場別作業の絞込を取得する
 * @author ThanhPV
 */
@Stateless
public class NarrowingDownTaskByWorkplaceFromEmployeesService {
   
//■Public
	/**
     * @name: [1] 取得する
     * @Description 説明:指定社員の基準日時点の所属職場の職場別作業の絞込を取得する。ない場合、上位職場の設定を参照する	
     * @input require
     * @input companyID 会社ID
     * @input employeeID 社員ID
     * @input date 年月日
     * @input taskFrameNo 作業枠NO
     * @output Optional<NarrowingDownTaskByWorkplace> 職場別作業の絞込
     */
    public static Optional<NarrowingDownTaskByWorkplace> get(Require require, String companyID, String employeeID, GeneralDate date,TaskFrameNo taskFrameNo) {
    	//	$職場リスト = require.職場を取得する(会社ID,社員ID,基準日)
    	List<String> listWpkIds = require.findWpkIdsBySid(employeeID, date);
		//$職場リスト :				※注意：$職場リストを順番にループすること										
    	for (String wpkId : listWpkIds) {
    		//$職場別作業の絞込 = require.職場別作業の絞込を取得する($)
    		Optional<NarrowingDownTaskByWorkplace> narrowingDownTaskByWorkplace = require.getNarrowingDownTaskByWorkplace(wpkId, taskFrameNo);
    		//if $職場別作業の絞込.isPresent()																		
    			//return $職場別作業の絞込
    		if(narrowingDownTaskByWorkplace.isPresent())
    			return narrowingDownTaskByWorkplace;
		}
    	//	return Optional.Empty
    	return Optional.empty();
    }
//■Require
    public interface Require {
         // [R-1] 職場を取得する
         // アルゴリズム.所属職場を含む上位職場を取得(会社ID,社員ID,基準日)
    	List<String> findWpkIdsBySid(String employeeID, GeneralDate date);
         // [R-2] 職場別作業の絞込を取得する
         // 職場別作業の絞込Repository.Get(職場ID,作業枠NO)
    	Optional<NarrowingDownTaskByWorkplace> getNarrowingDownTaskByWorkplace(String workPlaceId, TaskFrameNo taskFrameNo);
    }
}
