package nts.uk.ctx.at.record.dom.stamp.card.stampcard;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import nts.arc.task.tran.AtomTask;
import nts.uk.ctx.at.record.dom.adapter.employee.EmployeeDataMngInfoImport;
import nts.uk.ctx.at.record.dom.stamp.card.stamcardedit.StampCardEditing;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampMeans;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.RegisterStampData;
import nts.uk.ctx.at.shared.dom.adapter.holidaymanagement.CompanyImport622;

/**
 * @author sonnlb
 * 
 *         打刻カード番号を自動作成する
 *         UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.打刻管理.打刻カード.打刻カード番号を自動作成する
 */
public class AutoCreateStampCardNumberService {

	/**
	 * [1] 作成する
	 * 
	 * @param 社員ID
	 *            employeeID
	 * @param 打刻手段
	 *            stampMeans
	 * @return
	 * @return 打刻カード作成結果 Optional<打刻カード作成結果>
	 */

	public static Optional<StampCardCreateResult> create(Require require, String employeeID, StampMeans stampMeans) {

		// if not 打刻手段.打刻カードを自動作成するか()
		if (!stampMeans.checkAutoCreateStamp()) {
			return Optional.empty();
		}

		// $打刻カード = [prv-1] 打刻カードを作成する(require, 社員ID)
		Optional<StampCard> stampCard = createStampCard(require, employeeID);

		// if $打刻カード.isEmpty
		if (!stampCard.isPresent()) {
			return Optional.empty();
		}

		// $AtomTask = AtomTask: require.打刻カードを追加する($打刻カード)

		Optional<AtomTask> atomTask = Optional.of(AtomTask.of(() -> {
			require.add(stampCard.get());
		}));

		return Optional.ofNullable(new StampCardCreateResult(stampCard.get().getStampNumber().v(), atomTask));
	}

	/**
	 * [prv-1] 打刻カードを作成する
	 * 
	 * @param require
	 * @param employeeID
	 * @return
	 */
	private static Optional<StampCard> createStampCard(Require require, String employeeID) {
		// $社員データ管理情報 = require.社員情報を取得する(社員ID)
		List<EmployeeDataMngInfoImport> empInfos = require.findBySidNotDel(Arrays.asList(employeeID));

		// if $社員データ管理情報.isEmpty
		if (empInfos.isEmpty()) {
			return Optional.empty();
		}

		EmployeeDataMngInfoImport empInfo = empInfos.get(0);

		// $会社情報 = require.会社情報を取得する($社員データ管理情報.会社ID)

		Optional<CompanyImport622> companyInfoOpt = require.getCompanyNotAbolitionByCid(empInfo.getCompanyId());

		// if $会社情報.isEmpty

		if (!companyInfoOpt.isPresent()) {
			return Optional.empty();
		}
		CompanyImport622 companyInfo = companyInfoOpt.get();

		// $打刻カード番号 = [prv-2] 打刻カード番号を作成する(require, $会社情報.契約コード, $会社情報.会社ID,
		// $会社情報.会社コード, $社員データ管理情報.社員コード)
		Optional<String> stampCardNumberOpt = createStampCardNumber(require, companyInfo.getContractCd(),
				companyInfo.getCompanyId(), companyInfo.getCompanyCode(), empInfo.getEmployeeCode());

		// if $打刻カード番号.isEmpty
		if (!stampCardNumberOpt.isPresent()) {
			return Optional.empty();
		}

		return Optional.of(new StampCard(companyInfo.getContractCd(), stampCardNumberOpt.get(), employeeID));
	}

	/**
	 * [prv-2] 打刻カード番号を作成する
	 */
	private static Optional<String> createStampCardNumber(Require require, String contractCode, String companyId,
			String companyCode, String employeeCode) {
		// $打刻カード編集 = require.打刻カード編集を取得する(会社ID)
		Optional<StampCardEditing> stampCardEditingOpt = require.get(companyId);

		// if $打刻カード編集.isEmpty
		if (!stampCardEditingOpt.isPresent()) {
			return Optional.empty();
		}

		// $打刻カード番号 = $打刻カード編集.打刻カードを作成する(会社コード, 社員コード)
		Optional<String> stampCardNumberOpt = stampCardEditingOpt.get().createStampCard(companyCode, employeeCode);

		// if $打刻カード番号.isEmpty

		if (!stampCardNumberOpt.isPresent()) {
			return Optional.empty();
		}
		// $登録済カード = require.打刻カードを取得する(契約コード, $打刻カード番号)

		Optional<StampCard> StampCardOpt = require.getByCardNoAndContractCode(stampCardNumberOpt.get(), contractCode);

		// if not $登録済カード.isEmpty
		if (StampCardOpt.isPresent()) {
			return Optional.empty();
		}
		return Optional.ofNullable(stampCardNumberOpt.get());
	}

	public static interface Require extends RegisterStampData.Require {
		// [R-1] 社員情報を取得する
		List<EmployeeDataMngInfoImport> findBySidNotDel(List<String> sid);

		// [R-2] 会社情報を取得する
		Optional<CompanyImport622> getCompanyNotAbolitionByCid(String cid);

		// [R-3] 打刻カード編集を取得する
		Optional<StampCardEditing> get(String companyId);

		// [R-4] 打刻カードを取得する
		public Optional<StampCard> getByCardNoAndContractCode(String stampNumber, String contractCode);

		// [R-5] 打刻カードを追加する
		void add(StampCard domain);
	}
}
