package nts.uk.ctx.at.record.dom.monthly.agreement.approver;

import lombok.val;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

import javax.ejb.Stateless;
import java.util.Optional;

/**
 * 会社の承認者履歴を変更する
 * @author khai.dh
 */
@Stateless
public class CompanyApproverHistoryChangeDomainService {

	/**
	 * [1] 変更する
	 * 会社別の承認者（36協定）の履歴を変更して、直前の履歴の終了日を変更する
	 */
	public AtomTask changeApproverHistory(
			Require require,
			GeneralDate startDateBeforeChange,
			Approver36AgrByCompany histToChange) {

		return AtomTask.of(() -> {
			require.changeHistory(histToChange);

			val optPrevHist = require.getPrevHistory(startDateBeforeChange.addDays(-1));
			if (optPrevHist.isPresent()) {
				val prevHist = optPrevHist.get();
				val newEndDate = histToChange.getPeriod().start().addDays(-1);
				val periodWithNewEndDate = new DatePeriod(prevHist.getPeriod().start(), newEndDate);
				prevHist.setPeriod(periodWithNewEndDate);
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
	}
}
