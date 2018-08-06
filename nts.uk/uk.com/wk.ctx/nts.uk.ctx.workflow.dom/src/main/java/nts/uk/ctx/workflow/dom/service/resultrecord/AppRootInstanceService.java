package nts.uk.ctx.workflow.dom.service.resultrecord;

import java.util.List;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApprovalRootState;
import nts.uk.ctx.workflow.dom.resultrecord.AppRootDynamic;
import nts.uk.ctx.workflow.dom.resultrecord.AppRootInstance;
import nts.uk.ctx.workflow.dom.resultrecord.RecordRootType;
import nts.uk.ctx.workflow.dom.service.output.ApprovalRootStateStatus;
import nts.uk.shr.com.time.calendar.period.DatePeriod;
/**
 * 中間データ版
 * @author Doan Duy Hung
 *
 */
public interface AppRootInstanceService {
	
	/**
	 * [No.113](中間データ版)承認対象者と期間から承認状況を取得する
	 * @param employeeIDLst 対象者社員ID
	 * @param period 期間
	 * @param rootType ルート種類（日別確認／月別確認）
	 * @return 承認ルートの状況
	 */
	public List<ApprovalRootStateStatus> getAppRootStatusByEmpsPeriod(List<String> employeeIDLst, DatePeriod period, RecordRootType rootType);
	
	/**
	 * 対象者と期間から承認ルート中間データを取得する
	 * @param employeeIDLst
	 * @param period
	 * @param rootType
	 * @return
	 */
	public List<AppRootDynamicPeriod> getAppRootDynamicByEmpPeriod(List<String> employeeIDLst, DatePeriod period, RecordRootType rootType);
	
	/**
	 * 対象日の承認ルート中間データを取得する
	 * @param date
	 * @param appRootDynamicLst
	 * @return
	 */
	public AppRootDynamic getAppRootDynamicByDate(GeneralDate date, List<AppRootDynamic> appRootDynamicLst);
	
	/**
	 * 対象日の就業実績確認状態を取得する
	 * @param companyID
	 * @param employeeID
	 * @param date
	 * @param rootType
	 * @return
	 */
	public AppRootInstance getAppRootInstanceByDate(String companyID, String employeeID, GeneralDate date, RecordRootType rootType);
	
	/**
	 * 中間データから承認ルートインスタンスに変換する
	 * @param appRootDynamic
	 * @param appRootInstance
	 * @return
	 */
	public ApprovalRootState convertFromAppRootDynamic(AppRootDynamic appRootDynamic, AppRootInstance appRootInstance);
	
}
