package nts.uk.ctx.at.schedule.pub.schedule.workschedule.employeeinfo;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 社員ランク情報Export
 * @author HieuLT
 *
 */
@Getter
@AllArgsConstructor
public class EmpRankInfoExport {
	
	 /**社員ID **/
	private  String empId;
	/** ランクコード **/
	private  String rankCode;
	
	/**ランク記号 **/
	private  String rankSymbol;

}
