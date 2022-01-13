package nts.uk.ctx.at.shared.infra.repository.dailyattdcal.dailyattendance.calculationsettings.shortwork;

import java.util.Optional;

import javax.ejb.Stateless;

import org.apache.commons.lang3.BooleanUtils;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calculationsettings.shorttimework.CalcOfShortTimeWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calculationsettings.shorttimework.CalcOfShortTimeWorkRepository;
import nts.uk.ctx.at.shared.infra.entity.dailyattdcal.dailyattendance.calculationsettings.shortwork.KsrmtCalcShortTimeWork;

/**
 * リポジトリ実装：短時間勤務の計算
 * @author shuichi_ishida
 */
@Stateless
public class JpaCalcOfShortTimeWorkRepository extends JpaRepository implements CalcOfShortTimeWorkRepository{

	@Override
	public Optional<CalcOfShortTimeWork> find(String companyId) {
		return this.queryProxy().find(companyId, KsrmtCalcShortTimeWork.class)
				.map(c -> toDomain(c));
	}
	
	@Override
	public void addOrUpdate(CalcOfShortTimeWork calcOfShortTimeWork) {
		
		Optional<KsrmtCalcShortTimeWork> entityOpt =
				this.queryProxy().find(calcOfShortTimeWork.getCompanyId(), KsrmtCalcShortTimeWork.class);
		KsrmtCalcShortTimeWork entity = new KsrmtCalcShortTimeWork();
		if (entityOpt.isPresent()){
			entity = entityOpt.get();
		}
		else{
			entity.companyId = calcOfShortTimeWork.getCompanyId();
		}

		entity.calcMethod = BooleanUtils.toBoolean(calcOfShortTimeWork.getCalcMethod().value);
		
		if (entityOpt.isPresent()){
			this.commandProxy().update(entity);
		}
		else{
			this.commandProxy().insert(entity);
		}
	}
	
	/**
	 * ドメイン変換
	 * @param entity エンティティ：短時間勤務の計算
	 * @return ドメイン：短時間勤務の計算
	 */
	private CalcOfShortTimeWork toDomain(KsrmtCalcShortTimeWork entity){
		
		return CalcOfShortTimeWork.createFromJavaType(
				entity.companyId,
				BooleanUtils.toInteger(entity.calcMethod));
	}
}
