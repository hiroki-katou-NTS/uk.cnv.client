package nts.uk.ctx.sys.log.dom.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.shr.com.security.audittrail.basic.LogBasicInformation;
import nts.uk.shr.com.security.audittrail.correction.content.DataCorrectionLog;
import nts.uk.shr.com.security.audittrail.correction.content.TargetDataType;

/**
 * @author thanh_nx
 *
 *         日別実績の修正記録を取得する
 */
public class GetRecordDailyPerformanceLog {
	
	// [1] 項目IDを指定して取得する
	public static List<CorrectRecordDailyResult> getBySpecifyItemId(Require require, String sid, GeneralDate targetDate,
			Integer itemId) {
		List<DataCorrectionLog> infoLogs = require.getInfoLog(sid, targetDate, itemId, TargetDataType.DAILY_RECORD);
		if (infoLogs.isEmpty())
			return new ArrayList<>();
		Map<String, GeneralDateTime> basicInfoMap = require
				.getLogBasicInfo(infoLogs.stream().map(x -> x.getOperationId()).collect(Collectors.toList())).stream()
				.collect(Collectors.toMap(x -> x.getOperationId(), x -> x.getModifiedDateTime()));
		return infoLogs.stream().map(x -> {
			return new CorrectRecordDailyResult(sid, targetDate, itemId, basicInfoMap.get(x.getOperationId()));
		}).collect(Collectors.toList());
	}

	public static interface Require{
		
		//[R-1] 項目IDを指定してデータ修正記録を取得する	
		List<DataCorrectionLog> getInfoLog(String sid, GeneralDate targetDate, Integer itemId, TargetDataType type);
		
		//[R-2] ログ基本情報を取得する	
		List<LogBasicInformation> getLogBasicInfo(List<String> operationId);
	}
}
