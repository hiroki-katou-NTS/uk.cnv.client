package nts.uk.ctx.at.shared.app.command.remainingnumber.annualeave;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.basicinfo.AnnLeaEmpBasicInfoRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.basicinfo.AnnualLeaveEmpBasicInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.AnnLeaMaxDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.AnnualLeaveMaxData;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.pereg.app.command.MyCustomizeException;
import nts.uk.shr.pereg.app.command.PeregUpdateListCommandHandler;
@Stateless
public class UpdateAnnuaLeaveListCommandHandler extends CommandHandlerWithResult<List<UpdateAnnuaLeaveCommand>, List<MyCustomizeException>>
implements PeregUpdateListCommandHandler<UpdateAnnuaLeaveCommand>{
	@Inject
	private AnnLeaEmpBasicInfoRepository annLeaBasicInfoRepo;

	@Inject
	private AnnLeaMaxDataRepository maxDataRepo;
	@Override
	public String targetCategoryCd() {
		return "CS00024";
	}

	@Override
	public Class<?> commandClass() {
		return UpdateAnnuaLeaveCommand.class;
	}

	@Override
	protected List<MyCustomizeException> handle(CommandHandlerContext<List<UpdateAnnuaLeaveCommand>> context) {
		String cid = AppContexts.user().companyId();
		List<UpdateAnnuaLeaveCommand> cmd = context.getCommand();
		List<AnnualLeaveMaxData>  insertMax = new ArrayList<>();
		List<AnnualLeaveMaxData>  updateMax = new ArrayList<>();
		List<AnnualLeaveEmpBasicInfo>  insertEmpBasic = new ArrayList<>();
		List<AnnualLeaveEmpBasicInfo>  updateEmpBasic = new ArrayList<>();
		// sidsPidsMap
		List<String> sids = cmd.stream().map(c -> c.getEmployeeId()).collect(Collectors.toList());
		List<AnnualLeaveMaxData> aLeaveMaxData = maxDataRepo.getAll(sids);
		List<AnnualLeaveEmpBasicInfo> aLeaveEmpBasicInfo =  annLeaBasicInfoRepo.getAll(cid, sids);
		
		cmd.stream().forEach(c ->{
			Optional<AnnualLeaveEmpBasicInfo> basicOpt = aLeaveEmpBasicInfo.stream().filter(item -> item.getEmployeeId().equals(c.getEmployeeId())).findFirst();
			AnnualLeaveEmpBasicInfo basicInfo = AnnualLeaveEmpBasicInfo.createFromJavaType(c.getEmployeeId(),
					c.getWorkingDaysPerYear(), c.getWorkingDayBeforeIntro(), c.getGrantTable(), c.getStandardDate());
			if(basicOpt.isPresent()) {
				updateEmpBasic.add(basicInfo);
				
			}else {
				insertEmpBasic.add(basicInfo);
			}
			
			Optional<AnnualLeaveMaxData> maxOpt = aLeaveMaxData.stream().filter(item -> item.getEmployeeId().equals(c.getEmployeeId())).findFirst();

			if(maxOpt.isPresent()) {
				AnnualLeaveMaxData maxData = maxOpt.get();
				maxData.updateData(c.getMaxTimes(), c.getUsedTimes(), c.getMaxMinutes(), c.getUsedMinutes());
				updateMax.add(maxData);
				
			}else {
				AnnualLeaveMaxData maxData = AnnualLeaveMaxData.createFromJavaType(c.getEmployeeId(), c.getMaxTimes(),
						c.getUsedTimes(), c.getMaxMinutes(), c.getUsedMinutes());
				insertMax.add(maxData);
			}
		});
		
		if(!insertEmpBasic.isEmpty()) {
			annLeaBasicInfoRepo.addAll(insertEmpBasic);
		}
		
		if(!updateEmpBasic.isEmpty()) {
			annLeaBasicInfoRepo.updateAll(updateEmpBasic);
		}
		
		if(!insertMax.isEmpty()) {
			maxDataRepo.addAll(insertMax);
		}
		
		if(!updateMax.isEmpty()) {
			maxDataRepo.updateAll(updateMax);
		}
		
		return new ArrayList<MyCustomizeException>();
	}

}
