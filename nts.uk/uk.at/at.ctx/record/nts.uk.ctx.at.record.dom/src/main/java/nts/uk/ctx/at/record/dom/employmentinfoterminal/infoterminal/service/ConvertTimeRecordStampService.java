package nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.adapter.employmentinfoterminal.infoterminal.EmpDataImport;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.reflectstamp.ReflectStampInDailyRecord;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.EmpInfoTerminal;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.EmpInfoTerminalCode;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.receive.StampReceptionData;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.InfoReflectDestStamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.ReflectDataStampDailyService;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.RegisterStampData;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.StampDataReflectResult;
import nts.uk.ctx.at.shared.dom.adapter.holidaymanagement.CompanyInfo;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;

/**
 * @author ThanhNX
 * 
 *         データタイムレコードを打刻に変換する
 */
public class ConvertTimeRecordStampService {

	private ConvertTimeRecordStampService() {
	};

	// 変換する
	public static Optional<StampDataReflectResult> convertData(Require require,
			EmpInfoTerminalCode empInfoTerCode, ContractCode contractCode, StampReceptionData stampReceptData) {

		Optional<EmpInfoTerminal> empInfoTerOpt = require.getEmpInfoTerminal(empInfoTerCode, contractCode);
		if (!empInfoTerOpt.isPresent())
			return Optional.empty();

		// $就業情報端末.打刻(打刻受信データ)
		val stamp = createStamp(empInfoTerOpt.get(), contractCode, stampReceptData);
		if(!stamp.isPresent()) {
			return Optional.empty();
		}
		
		Optional<AtomTask> stampReflectResult = RegisterStampData.registerStamp(require,
				stamp.get());
		if(!stampReflectResult.isPresent()) {
			return Optional.empty();
		}
		
		//	$社員データ 
		Optional<EmpDataImport> employeeData = getEmployeeData(require, contractCode, stamp.get().getCardNumber());
		
		if (!employeeData.isPresent())
			return Optional
					.of(new StampDataReflectResult(Optional.empty(), stampReflectResult.orElse(AtomTask.none())));
		//＄終了状態
		boolean loginSuccess = login(require, contractCode.v(), employeeData.get());
		if(!loginSuccess)
			return Optional
					.of(new StampDataReflectResult(Optional.empty(), stampReflectResult.orElse(AtomTask.none())));
		
		return createDailyData(require, stamp.get(), stampReflectResult.get());
	}

	//打刻を作成する
	private static Optional<Stamp> createStamp(EmpInfoTerminal empInfoTer,
			ContractCode contractCode, StampReceptionData stampReceptData){
		//＄打刻
		Optional<Stamp> stamp = empInfoTer.getCreateStampInfo().createStamp(contractCode, stampReceptData, empInfoTer.getEmpInfoTerCode());
		return stamp;
	}

	//社員IDを取得する
	private static Optional<String> getEmployeeId(Require require, ContractCode contractCode, StampNumber stampNumber) {
		Optional<StampCard> stampCard = require.getByCardNoAndContractCode(contractCode,
				new StampNumber(stampNumber.v()));
		return stampCard.map(x -> x.getEmployeeId());
	}
	
	// ログイン
	private static boolean login(Require require, String contractCode, EmpDataImport empData) {

		// $UserID
		Optional<String> userId = require.getUserIdFromLoginId(empData.getPersonId());
		if (!userId.isPresent()) {
			return false;
		}
		// $会社コード
		String companyCode = require.getCompanyInfoById(empData.getCompanyId()).getCompanyCode();

		require.loggedInAsEmployee(userId.get(), empData.getPersonId(), contractCode, empData.getCompanyId(),
				companyCode, empData.getEmployeeId(), empData.getEmployeeCode());
		return true;

	}

	//日別実績を処理する
	private static Optional<StampDataReflectResult> createDailyData(Require require, Stamp stamp, AtomTask atomTask) {

		// $反映対象日 = [prv-3] いつの日別実績に反映するか(require, 社員ID, 打刻)
		Optional<InfoReflectDestStamp> infoReflectDestStamp = ReflectDataStampDailyService.getJudgment(require, 
				stamp);
		if (infoReflectDestStamp.isPresent()) {
			//チェック日が当月以降かどうかを確認する
			if(!checkInClosurePeriod(require, infoReflectDestStamp.get().getSid(), infoReflectDestStamp.get().getDate())) {
				return Optional.of(new StampDataReflectResult(Optional.empty(), atomTask));
			}
			
			Optional<StampDataReflectResult> stampDataResult = ReflectStampInDailyRecord.reflect(require, stamp);
			if(!stampDataResult.isPresent()) {
				return Optional.of(new StampDataReflectResult(Optional.of(infoReflectDestStamp.get().getDate()), atomTask));
			}
			List<AtomTask> taskLst = new ArrayList<>();
			taskLst.add(atomTask);
			taskLst.add(stampDataResult.get().getAtomTask());
			return stampDataResult.map(x -> new StampDataReflectResult(x.getReflectDate(), AtomTask.bundle(taskLst)));
		}
		return Optional.of(new StampDataReflectResult(Optional.empty(), atomTask));
	}
	
    //チェック日が当月以降かどうかを確認する
	private static boolean checkInClosurePeriod(Require require, String sid, GeneralDate date) {
		DatePeriod period = ClosureService.findClosurePeriod(require, new CacheCarrier(), sid, date);
		
		return period == null ? false : date.afterOrEquals(period.start());
	}
	
	// 社員データを取得
	private static Optional<EmpDataImport> getEmployeeData(Require require, ContractCode contractCode, StampNumber stampNumber){

		//$社員ID
		Optional<String> employeeId = getEmployeeId(require, contractCode, stampNumber);

		//$管理情報
		Optional<EmpDataImport> empData = employeeId
				.flatMap(sid -> require.getEmpData(Arrays.asList(sid)).stream().findFirst());
		
		if (!employeeId.isPresent() || !empData.isPresent()) {
			return Optional.empty();
		}
		
		return empData;
		
	}
	
	public static interface Require extends RegisterStampData.Require, ReflectStampInDailyRecord.Require,
			ReflectDataStampDailyService.Require, ClosureService.RequireM3 {

		// [R-1]就業情報端末を取得する
		public Optional<EmpInfoTerminal> getEmpInfoTerminal(EmpInfoTerminalCode empInfoTerCode,
				ContractCode contractCode);

		// [R-2]打刻記録を取得する
//		public Optional<StampRecord> getStampRecord(ContractCode contractCode, StampNumber stampNumber,
//				GeneralDateTime dateTime);

		// [R-3]打刻カードを取得する
		public Optional<StampCard> getByCardNoAndContractCode(ContractCode contractCode, StampNumber stampNumber);

//		// [R-4]タイムレコードのﾘｸｴｽﾄ設定を取得する
//		public Optional<TimeRecordReqSetting> getTimeRecordReqSetting(EmpInfoTerminalCode empInfoTerCode,
//				ContractCode contractCode);

		// [R-6] 就業担当者の社員ID（List）を取得する
		public List<String> getListEmpID(String companyID, GeneralDate referenceDate);
		
		// [R-8] 社員IDListから管理情報を取得する
		//GetMngInfoFromEmpIDListAdapter
		List<EmpDataImport> getEmpData(List<String> empIDList); 
		//[R-8]会社IDを取得する
		//CompanyAdapter
		CompanyInfo getCompanyInfoById(String companyId);
		//UserIDを取得する
		//	IGetInfoForLogin
		public Optional<String> getUserIdFromLoginId(String perId);
		
		// 紐従業員の役割でログインする
		//LoginUserContextManager
		void loggedInAsEmployee(
				String userId,
				String personId,
				String contractCode,
				String companyId,
				String companyCode,
				String employeeId,
				String employeeCode);
		
		//ログアウト
		void loggedOut();
		
	}
}
