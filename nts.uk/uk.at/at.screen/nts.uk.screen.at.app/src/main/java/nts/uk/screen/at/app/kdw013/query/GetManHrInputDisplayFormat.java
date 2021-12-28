package nts.uk.screen.at.app.kdw013.query;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.jobmanagement.displayformat.ManHrInputDisplayFormat;
import nts.uk.ctx.at.record.dom.jobmanagement.displayformat.ManHrInputDisplayFormatRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author sonnlb
 *
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務実績.作業管理.工数入力表示フォーマット.App.表示フォーマットを取得する
 * 
 */
@Stateless
public class GetManHrInputDisplayFormat {

	@Inject
	private ManHrInputDisplayFormatRepository repo;

	/**
	 * 取得する
	 * @return Optional<工数入力表示フォーマットDto>
	 */
	public Optional<ManHrInputDisplayFormat> get() {
		// 1 get(ログイン会社ID)
		return this.repo.get(AppContexts.user().companyId());
	}
}
