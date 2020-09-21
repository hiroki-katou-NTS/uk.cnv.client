package nts.uk.ctx.at.record.dom.monthly.agreement.approver;

import lombok.val;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

import javax.ejb.Stateless;
import java.util.Optional;

/**
 * 会社の承認者履歴を削除する
 * @author khai.dh
 */
@Stateless
public class CompanyApproverHistoryDeleteDomainService {

	/**
	 * 会社別の承認者（36協定）の履歴を削除して、直前の履歴の終了日を変更する
	 */
	public AtomTask deleteApproverHistory(Require require, Approver36AgrByCompany histToDel){

		return AtomTask.of(() -> {
			require.deleteHistory(histToDel);

			val prevEndDate = histToDel.getPeriod().end().addDays(-1);
			val optPrevHist = require.getPrevHistory(prevEndDate);
			if (optPrevHist.isPresent()) {
				val prevHist = optPrevHist.get();
				prevHist.setPeriod(new DatePeriod(prevHist.getPeriod().start(), GeneralDate.max()));
				require.changeHistory(prevHist);
			}
		});
	}

	public static interface Require {
		/**
		 * [R-1] 直前の履歴を取得する Get previous history
		 */
		Optional<Approver36AgrByCompany> getPrevHistory(GeneralDate endDate);

		/**
		 * [R-2] 履歴を変更する Change history
		 */
		void changeHistory(Approver36AgrByCompany hist);

		/**
		 * [R-3] 履歴を削除する Delete history
		 */
		void deleteHistory(Approver36AgrByCompany hist);
	}
}
