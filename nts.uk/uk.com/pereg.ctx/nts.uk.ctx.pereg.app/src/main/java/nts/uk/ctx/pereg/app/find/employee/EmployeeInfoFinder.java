package nts.uk.ctx.pereg.app.find.employee;

import java.math.BigDecimal;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import nts.arc.error.BusinessException;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.stamp.card.stamcardedit.StampCardEditing;
import nts.uk.ctx.at.record.dom.stamp.card.stamcardedit.StampCardEditingRepo;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardRepository;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfo;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfoRepository;
import nts.uk.ctx.bs.employee.dom.setting.code.EmployeeCESetting;
import nts.uk.ctx.bs.employee.dom.setting.code.IEmployeeCESettingRepository;
import nts.uk.ctx.pereg.dom.usesetting.UserSetting;
import nts.uk.ctx.pereg.dom.usesetting.UserSettingRepository;
import nts.uk.ctx.sys.shared.dom.user.UserRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class EmployeeInfoFinder {

	@Inject
	private EmployeeDataMngInfoRepository empDataRepo;

	@Inject
	private EmployeeDataMngInfoRepository employeeRepository;

	@Inject
	private UserRepository userRepo;

	@Inject
	private StampCardRepository stampCardRepo;

	@Inject
	private UserSettingRepository userSettingRepo;

	@Inject
	private StampCardEditingRepo stamCardEditRepo;

	@Inject
	private IEmployeeCESettingRepository empCESettingRepo;

	private static final String JP_SPACE = "　";

	public String generateEmplCode(String startLetters) {
		String companyId = AppContexts.user().companyId();

		Optional<EmployeeCESetting> _employeeCESetting = empCESettingRepo.getByComId(companyId);
		if (!_employeeCESetting.isPresent()) {
			return "";
		}
		int employeeCodeLength = _employeeCESetting.get().getDigitNumb().v();

		Optional<String> lastEmployeeCode = employeeRepository.findLastEml(companyId, startLetters, employeeCodeLength);

		if (!lastEmployeeCode.isPresent()) {
			throw new BusinessException("Msg_505");
		}
		String result = generateCode(lastEmployeeCode.get());
		Optional<EmployeeDataMngInfo> emp = employeeRepository.findByEmployeCD(result, companyId);
		if (emp.isPresent()) {
			throw new BusinessException("Msg_505");
		}
		return result;
	}

	public String generateCardNo(String startLetters) {
		String contractCode = AppContexts.user().contractCode();
		String companyId = AppContexts.user().companyId();
		startLetters = startLetters == null ? "" : startLetters;
		StampCardEditing _stamCardEdit = stamCardEditRepo.get(companyId);
		if (_stamCardEdit == null) {
			return "";
		}
		int maxLengthCardNo = _stamCardEdit.getDigitsNumber().v();
		Optional<String> lastCardNo = stampCardRepo.getLastCardNo(contractCode, startLetters, maxLengthCardNo);
		if (!lastCardNo.isPresent()) {
			throw new BusinessException("Msg_505");
		}
		String result =  generateCode(lastCardNo.get());
		Optional<StampCard> cardNo = stampCardRepo.getByCardNoAndContractCode(result, contractCode);
		if (cardNo.isPresent()) {
			throw new BusinessException("Msg_505");
		}
		return result;
	}

	public String initEmplCode() {
		String employeeId = AppContexts.user().employeeId();

		Optional<UserSetting> _userSetting = userSettingRepo.getUserSetting(employeeId);

		if (!_userSetting.isPresent()) {
			return "";
		}
		UserSetting userSetting = _userSetting.get();

		switch (userSetting.getEmpCodeValType()) {
		case INIT_DESIGNATION:
			return generateEmplCode(userSetting.getEmpCodeLetter().v());
		case MAXVALUE:
			return generateEmplCode("");
		case BLANK:
			return "";
		}

		return "";
	}

	public String initCardNo(String newEmployeeCode) {
		String employeeId = AppContexts.user().employeeId();
		String companyCode = AppContexts.user().companyCode();
		Optional<UserSetting> _userSetting = userSettingRepo.getUserSetting(employeeId);
		if (!_userSetting.isPresent()) {
			return "";
		}
		UserSetting userSetting = _userSetting.get();
		switch (userSetting.getCardNoValType()) {
		case BLANK:
			return "";
		case SAME_AS_EMP_CODE:
			return newEmployeeCode;
		case CPC_AND_EMPC:
			return companyCode + newEmployeeCode;
		case MAXVALUE:
			return generateCardNo("");
		case INIT_DESIGNATION:
			return generateCardNo(userSetting.getCardNoLetter().v());
		}
		return "";
	}

	/**
	 * validate EmpInfo EA修正履歴 - No1159 EA修正履歴 - No1160 EA修正履歴 - No1161 EA修正履歴 -
	 * No1162
	 */
	public void validateEmpInfo(EmpInfoDto empInfo) {
		Boolean isDuplicateEmpCode = !CollectionUtil.isEmpty(this.empDataRepo
				.getEmployeeNotDeleteInCompany(AppContexts.user().companyId(), empInfo.getEmployeeCode()));
		if (isDuplicateEmpCode) {
			throw new BusinessException("Msg_345");
		}

		// check duplicate cardNo
		if (empInfo.cardNo != null && empInfo.cardNo != "") {
			String contractCode = AppContexts.user().contractCode();
			Optional<StampCard> stampCard = this.stampCardRepo.getByCardNoAndContractCode(empInfo.cardNo, contractCode);
			if (stampCard.isPresent()) {
				throw new BusinessException("Msg_346");
			}
		}

		// Check duplicate loginID
		String contractCode = AppContexts.user().contractCode();
		Boolean isDuplicateLoginId = this.userRepo.getByContractAndLoginId(contractCode, empInfo.getLoginId()).isPresent();
		if (isDuplicateLoginId) {

			throw new BusinessException("Msg_757");

		}

		String employeeName = empInfo.getEmployeeName();
		if (employeeName.startsWith(JP_SPACE) || employeeName.endsWith(JP_SPACE) || !employeeName.contains(JP_SPACE)) {
			throw new BusinessException("Msg_924");
		}

	}

	private String generateCode(String value) {
		int mixLength = value.length();
		boolean spaceAtBefore = checkSpaceAtBefore(value);
		boolean spaceAtAfter = checkSpaceAtAfter(value);
		String returnValue = null;
		value = value.trim();
		if (StringUtils.isNumeric(value)) {
			returnValue = doWithNumeric(value);

		} else {
			returnValue = doWithCharacter(value);
		}
		return restoreSpace(returnValue, mixLength, spaceAtBefore, spaceAtAfter);

	}

	private boolean checkSpaceAtBefore(String value) {
		if(value.charAt(0) == ' ') {
			return true;
		}
		return false;
	}

	private boolean checkSpaceAtAfter(String value) {
		if(value.charAt(value.length() -1) == ' ') {
			return true;
		}
		return false;
	}

	private String restoreSpace(String value, int mixLengt, boolean spaceAtBefore, boolean spaceAtAfter) {
		if (value.length() >= mixLengt) {
			return value;
		}
		if (spaceAtBefore){
			while(value.length() < mixLengt) {
				value = " " + value;
			}
		} else if (spaceAtAfter) {
			while(value.length() < mixLengt) {
				value += " ";
			}
		}
		return value;
	}

	private String doWithNumeric(String value) {
		int length = value.length();
		// value is all 999...
		if (value.matches("[9]+")) {
			String standard = "A";
			while (standard.length() < length - 1) {
				standard += "0";
			}
			standard += "1";
			return standard;
		}

		BigDecimal numbericValue = new BigDecimal(value);
		numbericValue = numbericValue.add(new BigDecimal(1));

		String upperValue = numbericValue.toString();

		while (upperValue.length() < value.length()) {
			upperValue = "0" + upperValue;
		}
		return upperValue;

	}

	private String doWithCharacter(String value) {
		// value is all z or Z
		if (value.matches("[zZ]+")) {
			throw new BusinessException("Msg_505");
		}

		char increaseLetter = ' ';
		char[] valueAsArray = value.toCharArray();
		char[] resultArr = new char[valueAsArray.length];
		boolean canContinue = true;
		boolean beforeLast = false;
		for (int i = valueAsArray.length - 1; i >= 0; i--) {
			char currentChar = valueAsArray[i];
			if (canContinue) {
				canContinue = false;
				switch (currentChar) {
				case ' ':
					increaseLetter = ' ';
					canContinue = true;
					break;
				case '9':
					increaseLetter = '0';
					beforeLast = true;
					canContinue = true;
					break;
				// nếu ký tự tăng là z
				case 'z':
					increaseLetter = 'a';
					beforeLast = true;
					canContinue = true;
					break;
				// nếu ký tự tăng là Z
				case 'Z':
					increaseLetter = 'A';
					beforeLast = true;
					canContinue = true;
					break;
				default:
					increaseLetter = (char) ((int) currentChar + 1);
					break;
				}

				if (i != 0) {
					if (beforeLast && increaseLetter == ' ') {
						increaseLetter = '1';
						beforeLast = false;
						canContinue = false;
					}
					resultArr[i] = increaseLetter;
				} else {
					if (canContinue) {
						resultArr[i] = increaseLetter;
						for (int j = resultArr.length - 2; j > 0; j--) {
							char c = resultArr[j - 1];
							resultArr[j] = c;
						}
						if (beforeLast)
							resultArr[0] = '1';
					} else
						resultArr[i] = increaseLetter;
				}
			} else {
				resultArr[i] = valueAsArray[i];
			}

		}
		return String.valueOf(resultArr);
	}

}