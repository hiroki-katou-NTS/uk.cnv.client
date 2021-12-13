package nts.uk.ctx.bs.employee.dom.classification.algorithm;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.bs.employee.dom.classification.Classification;
import nts.uk.ctx.bs.employee.dom.classification.ClassificationCode;
import nts.uk.ctx.bs.employee.dom.classification.ClassificationRepository;

/**
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".基幹.社員.分類.分類マスタ.アルゴリズム.分類コード（List）から分類を取得する
 * @author HieuLt
 *
 */
/**分類コード（List）から分類を取得する  **/
@Stateless
public class GetClassificationbyCode {
	
	@Inject
	private ClassificationRepository repo;
	
	public List<ClassificationCode> getByCode(String companyId , List<String> lstClassificationCd){
		
		//ドメインモデル「分類」を取得する(get domain model 「分類」)
		List<Classification> data = repo.getClassificationByCodes(companyId, lstClassificationCd);
		List<ClassificationCode> result = data.stream().map(c -> c.getClassificationCode()).collect(Collectors.toList());
		return result;
	}
}
