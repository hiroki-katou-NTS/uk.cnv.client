package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;

/**
 * DS : 社員の打刻一覧を取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.打刻管理.打刻.社員の打刻一覧を取得する
 * 
 * @author tutk
 *
 */
@Stateless
public class GetListStampEmployeeService {

	/**
	 * [1] 取得する
	 * 
	 * @param require
	 * @param employeeId
	 * @param date
	 * @return
	 */
	public static Optional<EmployeeStampInfo> get(Require require, String employeeId, GeneralDate date) {
		// $社員の打刻データ = 社員の打刻データを取得する#取得する(require, 社員ID, 年月日)
		Optional<StampDataOfEmployees> stampDataOfEmployees = GetEmpStampDataService.get(require, employeeId, date);
		// if $社員の打刻データ.isEmpty return Optional.empty

		if (!stampDataOfEmployees.isPresent()) {
			return Optional.empty();
		}
		// $打刻一覧 = $社員の打刻データ.打刻リスト：表示する打刻情報#新規作成($)
		List<StampInfoDisp> listStampInfoDisp = stampDataOfEmployees.get().getListStamp().stream()
				.map(stamp -> new StampInfoDisp(stamp)).collect(Collectors.toList());
		// return 社員の打刻情報#社員の打刻情報(社員ID、年月日、$打刻一覧)
		return Optional.of(new EmployeeStampInfo(employeeId, date, listStampInfoDisp));
	}

	public static interface Require extends GetEmpStampDataService.Require {
		/**
		 * [R-1] 社員の打刻カード情報を取得する StampCardRepository
		 * 
		 * @param sid
		 * @return
		 */
		List<StampCard> getListStampCard(String sid);

		/**
		 * [R-3] 打刻を取得する StampDakokuRepository
		 * 
		 * @param stampNumbers
		 * @param stampDateTime
		 * @return
		 */
		List<Stamp> getStamp(List<StampNumber> stampNumbers, GeneralDate stampDateTime);

	}

}
