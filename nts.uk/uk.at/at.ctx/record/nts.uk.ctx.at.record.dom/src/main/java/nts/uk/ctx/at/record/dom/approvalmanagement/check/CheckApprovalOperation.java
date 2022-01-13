package nts.uk.ctx.at.record.dom.approvalmanagement.check;

import java.util.Optional;

import javax.ejb.Stateless;

import nts.uk.ctx.at.record.dom.approvalmanagement.ApprovalProcessingUseSetting;
import nts.uk.ctx.at.record.dom.approvalmanagement.enums.ConfirmationOfManagerOrYouself;

@Stateless
public class CheckApprovalOperation {
	
	public Optional<ConfirmationOfManagerOrYouself> checkApproval(ApprovalProcessingUseSetting approvalSetting) {
		if (approvalSetting.getUseDayApproverConfirm() && approvalSetting.getSupervisorConfirmErrorAtr() != null) {
			if (approvalSetting.getSupervisorConfirmErrorAtr().value == ConfirmationOfManagerOrYouself.CAN_CHECK.value) {
				return Optional.of(ConfirmationOfManagerOrYouself.CAN_CHECK);
			}
			return Optional.of(ConfirmationOfManagerOrYouself.CAN_NOT_CHECK);
		}
		return Optional.empty();
//			} else {
//				return Optional.of(ConfirmationOfManagerOrYouself.CAN_NOT_REGISTER);
//			}
//		} else {
//			return Optional.empty();
//		}
	}
	
//	public Optional<ConfirmOfManagerOrYouself> checkOld(FunctionalRestriction identityProcessUseSet) {
//		if (identityProcessUseSet.getUseSupervisorConfirm()
//				&& identityProcessUseSet.getSupervisorConfirmError() != null) {
//			if (identityProcessUseSet.getSupervisorConfirmError() == ConfirmOfManagerOrYouself.CAN_CHECK_WHEN_ERROR) {
//				return Optional.of(ConfirmOfManagerOrYouself.CAN_CHECK_WHEN_ERROR);
//			} else
//				return Optional.of(ConfirmOfManagerOrYouself.CANNOT_CHECKED_WHEN_ERROR);
//		} else {
//			return Optional.empty();
//		}
//	}
}
