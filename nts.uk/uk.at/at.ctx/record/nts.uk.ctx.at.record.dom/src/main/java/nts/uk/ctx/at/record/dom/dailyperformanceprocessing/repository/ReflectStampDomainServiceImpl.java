package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.EmbossingExecutionFlag;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.createdailyresults.CreateDailyResults;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.errorcheck.CalculationErrorCheckService;
import nts.uk.ctx.at.record.dom.dailyresultcreationprocess.creationprocess.creationclass.dailywork.TemporarilyReflectStampDailyAttd;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.ErrMessageResource;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ChangeDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ICorrectionAttendanceRule;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.OutputTimeReflectForWorkinfo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.TimeReflectFromWorkinfo;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageContent;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrorMessageInfo;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionContent;
import nts.uk.shr.com.i18n.TextResource;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ReflectStampDomainServiceImpl implements ReflectStampDomainService {


	@Inject
	private StampDomainService stampDomainService;

	@Inject
	private TimeReflectFromWorkinfo timeReflectFromWorkinfo;
	
	@Inject
	private DailyRecordConverter dailyRecordConverter;
	
	@Inject
	private CreateDailyResults createDailyResults;
	
	@Inject
	private CalculationErrorCheckService calculationErrorCheckService;
	
	@Inject
	private TemporarilyReflectStampDailyAttd temporarilyReflectStampDailyAttd;
	
	@Inject
	private ICorrectionAttendanceRule iCorrectionAttendanceRule;
	
	@Override
	public OutputAcquireReflectEmbossingNew acquireReflectEmbossingNew(String companyID, String employeeID,
			GeneralDate processingDate, ExecutionTypeDaily executionType, EmbossingExecutionFlag flag,
			IntegrationOfDaily integrationOfDaily, ChangeDailyAttendance changeDailyAtt,CacheCarrier carrier) {
		List<ErrorMessageInfo> listErrorMessageInfo = new ArrayList<>();
		
		//勤務情報から打刻反映時間帯を取得する
		OutputTimeReflectForWorkinfo outputTimeReflectForWorkinfo = timeReflectFromWorkinfo.get(companyID, employeeID, processingDate,
				integrationOfDaily.getWorkInformation());
		
		if(!outputTimeReflectForWorkinfo.getError().isEmpty()) {
			return new OutputAcquireReflectEmbossingNew(listErrorMessageInfo, new ArrayList<>(), integrationOfDaily, changeDailyAtt);
		}
		
		switch(outputTimeReflectForWorkinfo.getEndStatus()) {
		case NO_WORK_TIME:
			listErrorMessageInfo.add(new ErrorMessageInfo(companyID, employeeID, processingDate, ExecutionContent.DAILY_CREATION,
					new ErrMessageResource("022"), new ErrMessageContent(TextResource.localize("Msg_591"))));
			break;
		case NO_WORK_TYPE :
			listErrorMessageInfo.add(new ErrorMessageInfo(companyID, employeeID, processingDate, ExecutionContent.DAILY_CREATION,
					new ErrMessageResource("023"), new ErrMessageContent(TextResource.localize("Msg_590"))));
			break;
		case NO_HOLIDAY_SETTING:
			listErrorMessageInfo.add(new ErrorMessageInfo(companyID, employeeID, processingDate, ExecutionContent.DAILY_CREATION,
					new ErrMessageResource("023"), new ErrMessageContent(TextResource.localize("Msg_1678"))));
			break;
		case NO_WORK_CONDITION:
			listErrorMessageInfo.add(new ErrorMessageInfo(companyID, employeeID, processingDate, ExecutionContent.DAILY_CREATION,
					new ErrMessageResource("023"), new ErrMessageContent(TextResource.localize("Msg_430"))));
			break;
		default: //NORMAL
		}
		if(!listErrorMessageInfo.isEmpty()) {
			return new OutputAcquireReflectEmbossingNew(listErrorMessageInfo, new ArrayList<>(), integrationOfDaily, changeDailyAtt);
		}
		
		if(outputTimeReflectForWorkinfo.getStampReflectRangeOutput().getStampRange().getStart() == null ||
				outputTimeReflectForWorkinfo.getStampReflectRangeOutput().getStampRange().getEnd() == null) {
			listErrorMessageInfo.add(new ErrorMessageInfo(companyID, employeeID, processingDate, ExecutionContent.DAILY_CREATION,
					new ErrMessageResource("022"), new ErrMessageContent(TextResource.localize("Msg_591"))));
			return new OutputAcquireReflectEmbossingNew(listErrorMessageInfo, new ArrayList<>(), integrationOfDaily, changeDailyAtt);
		}
		
		//打刻を取得する
		List<Stamp> lstStamp = this.stampDomainService.handleDataNew(outputTimeReflectForWorkinfo.getStampReflectRangeOutput(),
                processingDate, employeeID, companyID,flag, carrier);
		if (!lstStamp.isEmpty()) {

			// 日別実績のコンバーターを作成する
			// 日別実績のデータをコンバーターに入れる
			DailyRecordToAttendanceItemConverter converter = dailyRecordConverter.createDailyConverter()
					.setData(integrationOfDaily).completed();

			// 「打刻反映管理」を取得する
			for (Stamp stamp : lstStamp) {
				// 対象日に反映できるか
				if (stamp.getImprintReflectionStatus().canReflectedOnTargetDate(processingDate)) {
					// 打刻を反映する
					List<ErrorMessageInfo> listE = temporarilyReflectStampDailyAttd.reflectStamp(companyID, stamp,
							outputTimeReflectForWorkinfo.getStampReflectRangeOutput(), integrationOfDaily,
							changeDailyAtt);
					// do thuật toán スケジュール管理しない場合勤務情報を更新 có thể tạo ra nhiều lỗi giống nhau, nên
					// cần bỏ những lỗi giống nhau trong 1 ngày.
					listE = listE.stream().distinct().collect(Collectors.toList());
					listErrorMessageInfo.addAll(listE);
				}
			}
			// 手修正がある勤怠項目ID一覧を取得する
			List<Integer> attendanceItemIdList = integrationOfDaily.getEditState().stream()
					.filter(c -> c.getEditStateSetting() != EditStateSetting.REFLECT_APPLICATION)
					.map(editState -> editState.getAttendanceItemId()).distinct().collect(Collectors.toList());
			List<ItemValue> listItemValue = converter.convert(attendanceItemIdList);
			// 手修正項目のデータを元に戻す
			if (!attendanceItemIdList.isEmpty()) {
				integrationOfDaily = createDailyResults.restoreData(converter, integrationOfDaily, listItemValue);
			}
		}
		//勤怠ルールの補正処理
		integrationOfDaily = iCorrectionAttendanceRule.process(integrationOfDaily, changeDailyAtt);
		// エラーチェック
		integrationOfDaily = calculationErrorCheckService.errorCheck(companyID, employeeID, processingDate, integrationOfDaily, true);
		
		return new OutputAcquireReflectEmbossingNew(listErrorMessageInfo,lstStamp,integrationOfDaily, changeDailyAtt);
	}
	
	
}
