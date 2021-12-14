package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance;

import java.util.ArrayList;
import java.util.List;

import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.RecordRemainCreateInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.CreateRemainInformation;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.SubsTransferProcessMode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * @author thanh_nx
 *
 *         <<Public>> 残数作成元の勤務予定・実績を取得する
 */
public class CreateWorkRecordScheduleRemain {

	//残数作成元情報を作成する
	public static List<RecordRemainCreateInfor> createRemain(Require require, String cid,
			List<IntegrationOfDaily> domainDailys, SubsTransferProcessMode processMode) {

		List<RecordRemainCreateInfor> lstResult = new ArrayList<>();
		// 残数作成元情報Listを作成する
		domainDailys.forEach(domainDaily -> {
			// 残数作成元情報を作成する
			lstResult.add(CreateRemainInformation.create(require, cid, domainDaily, processMode));
		});

		return lstResult;
	}

	public static interface Require extends CreateRemainInformation.Require {

	}
}
