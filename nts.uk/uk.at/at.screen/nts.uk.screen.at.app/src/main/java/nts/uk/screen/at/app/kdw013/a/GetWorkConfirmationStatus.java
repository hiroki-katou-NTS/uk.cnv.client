package nts.uk.screen.at.app.kdw013.a;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation.ConfirmationWorkResults;
import nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation.ConfirmationWorkResultsRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation.Confirmer;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeBasicInfoImport;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.作業確認状況を取得する
 * 
 * @author tutt
 *
 */
@Stateless
public class GetWorkConfirmationStatus {

	@Inject
	private ConfirmationWorkResultsRepository confirmRepo;

	@Inject
	private EmpEmployeeAdapter empEmployeeAdapter;

	/**
	 * 
	 * @param sid  社員ID
	 * @param date 年月日
	 * @return List＜確認者>
	 */
	public List<ConfirmerDto> get(String targetSID, GeneralDate targetYMD) {
		List<ConfirmerDto> confirmerDtos = new ArrayList<>();

		// 1: get(社員ID,年月日)
		Optional<ConfirmationWorkResults> confirmResultOpt = confirmRepo.get(targetSID, targetYMD);

		if (confirmResultOpt.isPresent()) {

			// 2: <call>()
			// [No.600]社員ID（List）から社員コードと表示名を取得（削除社員考慮）

			// 社員一覧 = 取得した「作業実績の確認．確認者一覧」
			List<Confirmer> confirmers = confirmResultOpt.get().getConfirmers();
			List<String> confirmIds = confirmers.stream().map(m -> m.getConfirmSID()).collect(Collectors.toList());

			// 基準日
			GeneralDate baseDate = GeneralDate.today();

			// 期間．開始日 = INPUT「基準日」
			// 期間．終了日 = INPUT「基準日」
			DatePeriod datePeriod = new DatePeriod(baseDate, baseDate);

			List<EmployeeBasicInfoImport> lstEmployeeInfo = empEmployeeAdapter.getEmpInfoLstBySids(confirmIds,
					datePeriod, true, true);

			for (Confirmer c : confirmers) {
				for (EmployeeBasicInfoImport e : lstEmployeeInfo) {

					if (c.getConfirmSID().equals(e.getSid())) {
						ConfirmerDto confirmer = new ConfirmerDto(c.getConfirmSID(), e.getEmployeeCode(),
								e.getEmployeeName(), c.getConfirmDateTime());
						confirmerDtos.add(confirmer);
					}
				}
			}
		}

		return confirmerDtos;
	}
}
