package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.adapter.employmentinfoterminal.infoterminal.EmpDataImport;
import nts.uk.ctx.at.record.dom.dailyresultcreationprocess.creationprocess.creationclass.dailywork.TemporarilyReflectStampDailyAttd;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;

/**
 * DS : 打刻データがいつの日別実績に反映するか
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.打刻管理.打刻.打刻データがいつの日別実績に反映するか
 * 
 * @author tutk
 *
 */
public class ReflectDataStampDailyService {
	/**
	 * [1] 判断する
	 * 
	 * @param require
	 * @param employeeId
	 * @param stamp
	 * @return 反映対象日
	 */
	public static Optional<InfoReflectDestStamp> getJudgment(Require require, 
			Stamp stamp) {
		Stamp stampCheck = stamp.clone();
		Optional<String> employeeId = require.getByCardNoAndContractCode(stamp.getContractCode(), stampCheck.getCardNumber())
				.map(x -> x.getEmployeeId());
		if (!employeeId.isPresent())
			return Optional.empty();
		//会社IDを取得する
		val cidInfo = require.getEmpData(Arrays.asList(employeeId.get()));
		if(cidInfo.isEmpty()) {
			return Optional.empty();
		}
		String cid = cidInfo.get(0).getCompanyId();
		GeneralDate date = stampCheck.getStampDateTime().toDate();
		DatePeriod period = new DatePeriod(date.addDays(-2), date.addDays(1));
		Optional<GeneralDate> reflectDate =  period.stream().filter(c -> TemporarilyReflectStampDailyAttd.createDailyDomAndReflectStamp(require, cid, employeeId.get(), c, stampCheck)
				.map(x -> stampCheck.getImprintReflectionStatus().isReflectedCategory()).orElse(false)).findFirst();
		return reflectDate.map(dateProcess -> new InfoReflectDestStamp(dateProcess, employeeId.get(), cid));
	}

	public static interface Require extends TemporarilyReflectStampDailyAttd.Require{
		// [R-1]打刻カードを取得する
		public Optional<StampCard> getByCardNoAndContractCode(ContractCode contractCode, StampNumber stampNumber);
		
		//  会社IDを取得する
		//GetMngInfoFromEmpIDListAdapter
		List<EmpDataImport> getEmpData(List<String> empIDList);
	}

}
