package nts.uk.ctx.at.record.dom.standardtime.repository;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.record.dom.standardtime.AgreementTimeOfWorkPlace;
import nts.uk.ctx.at.record.dom.standardtime.BasicAgreementSetting;
import nts.uk.ctx.at.record.dom.standardtime.enums.LaborSystemtAtr;

@Stateless
public class AgreementTimeOfWorkPlaceDomainServiceImp implements AgreementTimeOfWorkPlaceDomainService {

	@Inject
	private BasicAgreementSettingRepository basicAgreementSettingRepository;

	@Inject
	private AgreementTimeOfWorkPlaceRepository agreementTimeOfWorkPlaceRepository;

	@Override
	public List<String> add(AgreementTimeOfWorkPlace agreementTimeOfWorkPlace,
			BasicAgreementSetting basicAgreementSetting) {

		List<String> errors = new ArrayList<>();
		if (checkLimitTimeAndErrorTime(basicAgreementSetting)) {
			/**
			 * パラメータ parameters {0}：#KMK008_66 {1}：#KMK008_68
			 */
			errors.add("Msg_59, #KMK008_66, #KMK008_68");
			// throw new BusinessException("Msg_59","#KMK008_66", "#KMK008_68");
		}

		if (checkAlarmTimeAndErrorTime(basicAgreementSetting)) {
			/**
			 * パラメータ parameters {0}：#KMK008_67 {1}：#KMK008_66
			 * 
			 */
			errors.add("Msg_59, #KMK008_67, #KMK008_66");
		}

		this.basicAgreementSettingRepository.add(basicAgreementSetting);

		this.agreementTimeOfWorkPlaceRepository.add(agreementTimeOfWorkPlace);

		return errors;
	}

	@Override
	public List<String> update(BasicAgreementSetting basicAgreementSetting) {
		
		List<String> errors = new ArrayList<>();
		if (checkLimitTimeAndErrorTime(basicAgreementSetting)) {
			/**
			 * パラメータ parameters {0}：#KMK008_66 {1}：#KMK008_68
			 */
			errors.add("Msg_59, #KMK008_66, #KMK008_68");
			// throw new BusinessException("Msg_59","#KMK008_66", "#KMK008_68");
		}

		if (checkAlarmTimeAndErrorTime(basicAgreementSetting)) {
			/**
			 * パラメータ parameters {0}：#KMK008_67 {1}：#KMK008_66
			 * 
			 */
			errors.add("Msg_59, #KMK008_67, #KMK008_66");
		}
		
		this.basicAgreementSettingRepository.update2(basicAgreementSetting);
		
		return errors;
	}

	@Override
	public void remove(String basicSettingId, String workPlaceId, int laborSystemAtr) {
		this.basicAgreementSettingRepository.remove(basicSettingId);
		
		this.agreementTimeOfWorkPlaceRepository.remove(workPlaceId,
				EnumAdaptor.valueOf(laborSystemAtr, LaborSystemtAtr.class));
	}

	private boolean checkLimitTimeAndErrorTime(BasicAgreementSetting setting) {
		if (setting.getErrorWeek().v().compareTo(setting.getLimitWeek().v()) > 0
				|| setting.getErrorTwoWeeks().v().compareTo(setting.getLimitTwoWeeks().v()) > 0
				|| setting.getErrorFourWeeks().v().compareTo(setting.getLimitFourWeeks().v()) > 0
				|| setting.getErrorOneMonth().v().compareTo(setting.getLimitOneMonth().v()) > 0
				|| setting.getErrorTwoMonths().v().compareTo(setting.getLimitTwoMonths().v()) > 0
				|| setting.getErrorThreeMonths().v().compareTo(setting.getLimitThreeMonths().v()) > 0
				|| setting.getErrorOneYear().v().compareTo(setting.getLimitOneYear().v()) > 0) {
			return true;
		}
		return false;
	}

	private boolean checkAlarmTimeAndErrorTime(BasicAgreementSetting setting) {
		if (setting.getAlarmWeek().v().compareTo(setting.getErrorWeek().v()) > 0
				|| setting.getAlarmTwoWeeks().v().compareTo(setting.getErrorTwoWeeks().v()) > 0
				|| setting.getAlarmFourWeeks().v().compareTo(setting.getErrorFourWeeks().v()) > 0
				|| setting.getAlarmOneMonth().v().compareTo(setting.getErrorOneMonth().v()) > 0
				|| setting.getAlarmTwoMonths().v().compareTo(setting.getErrorTwoMonths().v()) > 0
				|| setting.getAlarmThreeMonths().v().compareTo(setting.getErrorThreeMonths().v()) > 0
				|| setting.getAlarmOneYear().v().compareTo(setting.getErrorOneYear().v()) > 0) {
			return true;
		}
		return false;
	}

}
