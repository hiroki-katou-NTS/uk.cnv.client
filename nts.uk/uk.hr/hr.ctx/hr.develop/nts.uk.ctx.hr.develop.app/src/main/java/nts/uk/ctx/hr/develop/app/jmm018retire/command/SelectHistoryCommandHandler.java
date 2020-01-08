package nts.uk.ctx.hr.develop.app.jmm018retire.command;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.uk.ctx.hr.develop.app.jmm018retire.jmm0018bcfinder;
import nts.uk.ctx.hr.develop.app.jmm018retire.dto.MandatoryRetirementDto;
import nts.uk.ctx.hr.develop.app.jmm018retire.dto.RelateMasterDto;
import nts.uk.ctx.hr.develop.app.jmm018retire.dto.StartDto;

@Stateless
public class SelectHistoryCommandHandler{
	
	@Inject
	private jmm0018bcfinder finder;
	
	public StartDto selcectHistory(StartDto startdto, String hist, boolean isLatestHis) {
		if(startdto.getRelateMaster() == null) {
			throw new BusinessException("MsgJ_JMM018_16");
		}else {
			// アルゴリズム [就業規則の取得] を実行する (THực hiện thuật toán  [Lấy Quy tắc làm việc/Labor regulations] )
			MandatoryRetirementDto madatory = finder.getLaborRegulation(hist);
			if(madatory == null && isLatestHis == false) {
				throw new BusinessException("MsgJ_JMM018_19");
			}else if(madatory == null && isLatestHis == true) {
				throw new BusinessException("MsgJ_JMM018_18");
			}else {
				RelateMasterDto relate = finder.getRelateMaster();
				return new StartDto(relate, madatory);
			}
		}
	}
}
