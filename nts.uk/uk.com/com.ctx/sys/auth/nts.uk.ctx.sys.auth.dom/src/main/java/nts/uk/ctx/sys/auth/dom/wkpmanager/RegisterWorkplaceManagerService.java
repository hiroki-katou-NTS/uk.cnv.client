package nts.uk.ctx.sys.auth.dom.wkpmanager;

import java.util.List;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.sys.shared.dom.employee.EmpEnrollPeriodImport;

/**
 * 職場管理者を登録する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.権限管理.職場管理者.職場管理者を登録する
 * @author lan_lt
 *
 */
public class RegisterWorkplaceManagerService {
	
	/**
	 * 追加する
	 * @param require Require
	 * @param workplaceId 職場ID
	 * @param sid 社員ID
	 * @param historyPeriod 履歴期間
	 * @return
	 */
	public static AtomTask add(Require require, String workplaceId, String sid, DatePeriod historyPeriod) {
		
		val registeredWorkplaceManagers  = require.getWorkplaceMangager(workplaceId, sid);
		
		checkError(require, sid, historyPeriod, registeredWorkplaceManagers );	
		
		val workPlaceManager = WorkplaceManager.createNew(workplaceId, sid, historyPeriod);
		
		return AtomTask.of(() -> require.insert(workPlaceManager));
		
	}
	
	/**
	 * 期間を変更する
	 * @param require Require
	 * @param workPlaceManager 職場管理者
	 * @return
	 */
	public static AtomTask changePeriod(Require require, WorkplaceManager workPlaceManager) {
		
		List<WorkplaceManager> registeredWorkplaceManagers  = require.getWorkplaceMangager(workPlaceManager.getWorkplaceId()
				,	workPlaceManager.getEmployeeId()).stream()
				.filter(c -> !c.getWorkplaceManagerId().equals(workPlaceManager.getWorkplaceManagerId()))
				.collect(Collectors.toList());
		
		checkError(require, workPlaceManager.getEmployeeId(), workPlaceManager.getHistoryPeriod(), registeredWorkplaceManagers);
		
		return AtomTask.of(() -> require.update(workPlaceManager));
	}
	
	/**
	 * エラーをチェック
	 * @param require
	 * @param sid 社員ID
	 * @param historyDatePeriod 履歴期間
	 * @param registeredWorkplaceManagers 既に登録される職場管理者リスト
	 */
	private static void checkError(Require require, String sid, DatePeriod historyDatePeriod,
			List<WorkplaceManager> registeredWorkplaceManagers ) {
		if (registeredWorkplaceManagers .stream()
				.anyMatch(c -> c.getHistoryPeriod().compare(historyDatePeriod).isDuplicated())) {
			throw new BusinessException("Msg_619");
		}
		
		List<EmpEnrollPeriodImport> empComHistories = require.getEmployeeCompanyHistory(sid, historyDatePeriod);
		
		if(empComHistories.stream().allMatch(empHist -> !empHist.getDatePeriod().contains(historyDatePeriod))) {
			throw new BusinessException("Msg_2199");
		}
	
	}
	
	public static interface Require{
		/**
		 * 職場管理者を取得する
		 * @param workplaceId 職場ID
		 * @param sid 社員ID
		 * @return
		 */
		List<WorkplaceManager> getWorkplaceMangager(String workplaceId, String sid);
		
		/**
		 * 職場管理者を追加する
		 * @param workplaceManager 職場管理者
		 * @return 
		 */
		void insert(WorkplaceManager workplaceManager);
		
		/**
		 * 職場管理者を登録する
		 * @param workplaceManager 職場管理者
		 */
		void update(WorkplaceManager workplaceManager);
		
		/**
		 * 社員の所属会社履歴を取得する( 社員ID, 期間 )
		 * @param sid 社員ID
		 * @param datePeriod 期間
		 * @return
		 */
		List<EmpEnrollPeriodImport> getEmployeeCompanyHistory(String sid, DatePeriod datePeriod);
	}
	
}
