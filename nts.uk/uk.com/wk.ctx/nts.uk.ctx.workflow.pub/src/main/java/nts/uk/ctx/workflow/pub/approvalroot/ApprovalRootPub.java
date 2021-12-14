package nts.uk.ctx.workflow.pub.approvalroot;

import java.util.List;
import java.util.Map;

import nts.arc.time.calendar.period.DatePeriod;

public interface ApprovalRootPub {
	/**
	 * 就業の承認ルート未登録の社員を取得する
	 * @param cid
	 * @param period
	 * @param lstSid
	 * @return
	 */
	Map<String, List<String>> lstEmplUnregister(String cid, DatePeriod period, List<String> lstSid);
}
