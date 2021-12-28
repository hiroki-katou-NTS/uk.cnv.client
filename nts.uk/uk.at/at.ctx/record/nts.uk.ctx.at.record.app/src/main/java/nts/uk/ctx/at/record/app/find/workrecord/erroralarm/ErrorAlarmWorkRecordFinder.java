/**
 * 4:55:11 PM Jul 21, 2017
 */
package nts.uk.ctx.at.record.app.find.workrecord.erroralarm;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErAlApplication;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErAlApplicationRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmConditionRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecord;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecordRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.ErrorAlarmCondition;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author hungnm
 *
 */
@Stateless
public class ErrorAlarmWorkRecordFinder {

	@Inject
	private ErrorAlarmWorkRecordRepository repository;

	// エラー発生時に呼び出す申請一覧
	@Inject
	private ErAlApplicationRepository erAlApplicationRepository;

	@Inject
	private ErrorAlarmConditionRepository errorAlarmConditionRepository;

	/**
	 * 
	 * @param type = 0 => user setting, type = 1 => system setting
	 * @return
	 */
	public ErrorAlarmDto getListErrorAlarmWorkRecord(int type) {
		ErrorAlarmDto errorAlarmDto = new ErrorAlarmDto();
		List<ErrorAlarmWorkRecord> lstErrorAlarm = repository
				.getListErrorAlarmWorkRecord(AppContexts.user().companyId(), type);
		if (type == 0) {
			lstErrorAlarm = lstErrorAlarm.stream().filter(eral -> eral.getCode().v().startsWith("U"))
					.collect(Collectors.toList());
		}
		if (type == 1) {
			lstErrorAlarm = lstErrorAlarm.stream().filter(eral -> eral.getCode().v().startsWith("S"))
					.collect(Collectors.toList());
			if (!lstErrorAlarm.isEmpty()) {
				for (ErrorAlarmWorkRecord e : lstErrorAlarm) {
					Optional<ErrorAlarmCondition> errorAlarmCondition = this.errorAlarmConditionRepository
																			.findConditionByErrorAlamCheckId(e.getErrorAlarmCheckID());
					if (errorAlarmCondition.isPresent())
						e.setErrorAlarmCondition(errorAlarmCondition.get());
				}
			}
		}
		List<ErrorAlarmWorkRecordDto> lstDto = lstErrorAlarm.stream()
				.map(eral -> {
					if(eral.getFixedAtr() && eral.getErrorAlarmCondition() == null)
						return new ErrorAlarmWorkRecordDto(eral.getCompanyId(),
								eral.getCode().v().substring(1),
								eral.getName().v(),
								eral.getFixedAtr() ? 1 : 0,
								eral.getUseAtr() ? 1 : 0,
								eral.getRemarkCancelErrorInput().value,
								eral.getRemarkColumnNo(),
								eral.getTypeAtr().value,
								"",
								eral.getMessage().getBoldAtr() ? 1 : 0,
								eral.getMessage().getMessageColor().v(),
								eral.getCancelableAtr() ? 1 : 0,
								eral.getErrorDisplayItem() != null ? eral.getErrorDisplayItem().intValue() : null,
								0,
								eral.getLstApplication(),
								0,
								0,
								0,
								false);
					return ErrorAlarmWorkRecordDto.fromDomain(eral, eral.getErrorAlarmCondition());
				}).collect(Collectors.toList());
		errorAlarmDto.setErrorAlarmWorkRecordList(lstDto);
		errorAlarmDto.setOotsukaOption(getOotsukaOptionInfo());
		errorAlarmDto.setApplicationList(getApplicationName());
		return errorAlarmDto;
	}

	public List<ErrorAlarmWorkRecordDto> findByListErrorAlamCheckId(List<String> listEralCheckId) {
		List<ErrorAlarmWorkRecord> lstErrorAlarm = repository.findByListErrorAlamCheckId(listEralCheckId);
		List<ErrorAlarmCondition> lstCondition = repository.findConditionByListErrorAlamCheckId(listEralCheckId);
		List<ErrorAlarmWorkRecordDto> lstDto = lstErrorAlarm.stream().map(eral -> {
			for (ErrorAlarmCondition errorAlarmCondition : lstCondition) {
				if (errorAlarmCondition.getErrorAlarmCheckID().equals(eral.getErrorAlarmCheckID())) {
					return ErrorAlarmWorkRecordDto.fromDomain(eral, errorAlarmCondition);
				}
			}
			return null;
		}).collect(Collectors.toList());
		return lstDto;
	}

	/**
	 * UKDesign.UniversalK.就業.KDW_日別実績.KDW007_勤務実績のエラーアラーム設定.エラー/アラームの条件設定.ユースケース.日別.起動する
	 * (khởi động).起動する..大塚オプション情報を取得する
	 */

	public boolean getOotsukaOptionInfo() {
		return AppContexts.optionLicense().customize().ootsuka();
	}

	/**
	 * UKDesign.UniversalK.就業.KDW_日別実績.KDW007_勤務実績のエラーアラーム設定.エラー/アラームの条件設定.アルゴリズム.日別.申請の名称を取得する.ログインしている会社をもとにドメインモデル「エラー発生時に呼び出す申請一覧」を取得する
	 */
	public List<Integer> getApplicationName() {
		String companyId = AppContexts.user().companyId();
		String errorAlarmCode = null;
		Optional<ErAlApplication> errApp = this.erAlApplicationRepository.getAllErAlAppByEralCode(companyId,
				errorAlarmCode);
		if (!errApp.isPresent())
			return Collections.emptyList();
		ErAlApplication errAppVal = errApp.get();
		return errAppVal.getAppType();
	}

}
