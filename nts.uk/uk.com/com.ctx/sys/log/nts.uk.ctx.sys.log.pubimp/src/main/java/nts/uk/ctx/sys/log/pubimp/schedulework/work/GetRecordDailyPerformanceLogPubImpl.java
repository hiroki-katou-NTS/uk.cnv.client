package nts.uk.ctx.sys.log.pubimp.schedulework.work;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.log.dom.datacorrectionlog.DataCorrectionLogRepository;
import nts.uk.ctx.sys.log.dom.logbasicinfo.LogBasicInfoRepository;
import nts.uk.ctx.sys.log.dom.service.GetRecordDailyPerformanceLog;
import nts.uk.ctx.sys.log.pub.schedulework.CorrectRecordDailyResultExport;
import nts.uk.ctx.sys.log.pub.schedulework.record.GetRecordDailyPerformanceLogPub;
import nts.uk.shr.com.security.audittrail.basic.LogBasicInformation;
import nts.uk.shr.com.security.audittrail.correction.content.DataCorrectionLog;
import nts.uk.shr.com.security.audittrail.correction.content.TargetDataType;

@Stateless
public class GetRecordDailyPerformanceLogPubImpl implements GetRecordDailyPerformanceLogPub {

	@Inject
	private DataCorrectionLogRepository dataCorrectionLogRepo;

	@Inject
	private LogBasicInfoRepository logBasicInfoRepo;

	@Override
	public List<CorrectRecordDailyResultExport> getBySpecifyItemId(String sid, GeneralDate targetDate, Integer itemId) {

		RequireImpl impl = new RequireImpl(dataCorrectionLogRepo, logBasicInfoRepo);
		return GetRecordDailyPerformanceLog.getBySpecifyItemId(impl, sid, targetDate, itemId).stream()
				.map(x -> new CorrectRecordDailyResultExport(x.getSid(), x.getTargetDate(), x.getItemId(),
						x.getCorrectTime()))
				.collect(Collectors.toList());
	}

	@AllArgsConstructor
	public class RequireImpl implements GetRecordDailyPerformanceLog.Require {

		private final DataCorrectionLogRepository dataCorrectionLogRepo;

		private final LogBasicInfoRepository logBasicInfoRepo;

		@Override
		public List<DataCorrectionLog> getInfoLog(String sid, GeneralDate targetDate, Integer itemId,
				TargetDataType type) {
			return dataCorrectionLogRepo.getInfoLog(sid, targetDate, itemId, type);
		}

		@Override
		public List<LogBasicInformation> getLogBasicInfo(List<String> operationId) {
			return logBasicInfoRepo.getLogBasicInfo(operationId);
		}

	}
}
