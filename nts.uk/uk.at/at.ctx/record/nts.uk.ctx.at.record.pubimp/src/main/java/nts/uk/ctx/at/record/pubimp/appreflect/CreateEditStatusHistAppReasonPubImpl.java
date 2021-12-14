package nts.uk.ctx.at.record.pubimp.appreflect;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.applicationcancel.CreateEditStatusHistAppReason;
import nts.uk.ctx.at.record.dom.editstate.EditStateOfDailyPerformance;
import nts.uk.ctx.at.record.dom.editstate.repository.EditStateOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.pub.appreflect.CreateEditStatusHistAppReasonPub;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.ApplicationReflectHistory;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.ApplicationReflectHistoryRepo;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class CreateEditStatusHistAppReasonPubImpl implements CreateEditStatusHistAppReasonPub {

	@Inject
	private EditStateOfDailyPerformanceRepository editStateOfDailyRepo;
	
	@Inject
	private ApplicationReflectHistoryRepo applicationReflectHistoryRepo;

	@Override
	public void process(String employeeId, GeneralDate date, String appId, ScheduleRecordClassifi classification,
			Map<Integer, String> mapValue, GeneralDateTime reflectTime) {
		RequireImpl impl = new RequireImpl(editStateOfDailyRepo);
		CreateEditStatusHistAppReason.process(impl, employeeId, date, appId, classification, mapValue, reflectTime);

	}

	@AllArgsConstructor
	public class RequireImpl implements CreateEditStatusHistAppReason.Require {

		private final EditStateOfDailyPerformanceRepository editStateOfDailyRepo;

		@Override
		public List<EditStateOfDailyPerformance> findByKey(String employeeId, GeneralDate ymd) {
			return editStateOfDailyRepo.findByKey(employeeId, ymd);
		}

		@Override
		public void addAndUpdate(List<EditStateOfDailyPerformance> editStates) {
			editStateOfDailyRepo.addAndUpdate(editStates);

		}

		@Override
		public void insertAppReflectHist(ApplicationReflectHistory hist) {
			applicationReflectHistoryRepo.insertAppReflectHist(AppContexts.user().companyId(), hist);
		}

	}
}
