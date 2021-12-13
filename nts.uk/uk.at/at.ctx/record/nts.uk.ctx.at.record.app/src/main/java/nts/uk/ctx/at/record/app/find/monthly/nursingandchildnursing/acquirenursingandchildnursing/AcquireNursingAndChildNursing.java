package nts.uk.ctx.at.record.app.find.monthly.nursingandchildnursing.acquirenursingandchildnursing;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.info.CareLeaveRemainingInfoRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.info.ChildCareLeaveRemInfoRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.info.NursingCareLeaveRemainingInfo;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingCategory;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingLeaveSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingLeaveSettingRepository;

/**
 * 子の看護・介護管理区分を取得する
 * UKDesign.UniversalK.就業.KDL_ダイアログ.残数確認ダイアログ共通.子の看護・介護.アルゴリズム.子の看護・介護管理区分を取得する
 * 
 * @author tutk
 *
 */
@Stateless
public class AcquireNursingAndChildNursing {

	@Inject
	private ChildCareLeaveRemInfoRepository childCareLeaveRemInfoRepository;

	@Inject
	private CareLeaveRemainingInfoRepository careLeaveRemainingInfoRepository;

	@Inject
	private NursingLeaveSettingRepository nursingLeaveSettingRepository;

	/**
	 * @param companyId       会社ID
	 * @param employeeId      社員ID
	 * @param nursingCategory 介護看護区分
	 */
	public AcquireNursingAndChildNursingDto get(String companyId, String employeeId, NursingCategory nursingCategory) {

		// ドメインモデル「子の看護休暇基本情報」を取得する
		Optional<NursingCareLeaveRemainingInfo> nursingCareLeaveRemainingInfo = Optional.empty();
		if (nursingCategory.equals(NursingCategory.Nursing)) {
			nursingCareLeaveRemainingInfo = careLeaveRemainingInfoRepository.getCareByEmpId(employeeId)
					.map(mapper -> (NursingCareLeaveRemainingInfo) mapper);
		} else if (nursingCategory.equals(NursingCategory.ChildNursing)) {
			nursingCareLeaveRemainingInfo = childCareLeaveRemInfoRepository.getChildCareByEmpId(employeeId)
					.map(mapper -> (NursingCareLeaveRemainingInfo) mapper);
		}
		// 取得した子の看護休暇基本情報．使用区分をチェック
		if (!nursingCareLeaveRemainingInfo.isPresent() || !nursingCareLeaveRemainingInfo.get().isUseClassification()) {
			return new AcquireNursingAndChildNursingDto(Optional.empty(), false,nursingCareLeaveRemainingInfo);
		}
		// ドメインモデル「介護看護休暇設定」を取得する
		NursingLeaveSetting nursingLeaveSetting = nursingLeaveSettingRepository
				.findByCompanyIdAndNursingCategory(companyId, nursingCategory.value);

		// 介護看護休暇設定 ＝ 取得した介護看護休暇設定 をセットする
		// 管理区分 ＝ 取得した介護看護休暇設定．管理区分
		boolean manageType = (nursingLeaveSetting.getManageType() == ManageDistinct.YES ? true : false);
		return new AcquireNursingAndChildNursingDto(Optional.of(nursingLeaveSetting), manageType,nursingCareLeaveRemainingInfo);
	}
}
