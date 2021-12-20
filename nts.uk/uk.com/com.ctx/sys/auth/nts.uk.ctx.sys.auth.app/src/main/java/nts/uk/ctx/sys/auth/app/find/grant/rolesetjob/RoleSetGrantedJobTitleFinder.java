package nts.uk.ctx.sys.auth.app.find.grant.rolesetjob;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.auth.dom.adapter.employee.JobTitleAdapter;
import nts.uk.ctx.sys.auth.dom.employee.dto.SimpleJobTitleImport;
import nts.uk.ctx.sys.auth.dom.grant.rolesetjob.RoleSetGrantedJobTitleRepository;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSetRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.context.LoginUserContext;

/**
 * 
 * @author HungTT
 *
 */
@Stateless
public class RoleSetGrantedJobTitleFinder {

	@Inject
	private RoleSetRepository roleSetRepo;

	@Inject
	private RoleSetGrantedJobTitleRepository roleSetJobRepo;
	
	@Inject
	private JobTitleAdapter jobTitleAdapter;

	public GrantRoleSetJobDto getAllData(GeneralDate refDate) {
		String companyId = AppContexts.user().companyId();
		LoginUserContext user = AppContexts.user();
		if (!user.roles().have().companyAdmin())
			throw new BusinessException("Msg_1103");

		// get Job Title by date, companyId
		List<String> listJobId = jobTitleAdapter.findAll(companyId, refDate).stream().map(item -> item.getPositionId()).collect(Collectors.toList());
		//no result => throw new BusinessException("Msg_712");
		if (listJobId.isEmpty()) {
			throw new BusinessException("Msg_712");
		}
		
		// get Sequence Master return List <position ID, position code, position name, ranking code, sort order>
		List<SimpleJobTitleImport> listJobSimple = jobTitleAdapter.findByIds(companyId, listJobId, refDate);
		//no result => throw new BusinessException("Msg_712");
		if (listJobSimple.isEmpty()) {
			throw new BusinessException("Msg_712");
		}
		listJobSimple.sort((s1, s2) -> s1.getJobTitleCode().compareTo(s2.getJobTitleCode()));
		List<JobTitleDto> listJobTitle = listJobSimple.stream().map(item -> new JobTitleDto(item.getJobTitleId(), item.getJobTitleCode(), item.getJobTitleName())).collect(Collectors.toList());
        
		// get Role Set by companyId, sort ASC
		List<RoleSetDto> listRoleSet = roleSetRepo.findByCompanyId(companyId).stream()
				.map(item -> new RoleSetDto(item.getRoleSetCd().v(), item.getRoleSetName().v())).collect(Collectors.toList());
		if (listRoleSet.isEmpty()){
			throw new BusinessException("Msg_713");
		}
		listRoleSet.sort((rs1, rs2) -> rs1.getCode().compareTo(rs2.getCode()));

		// get Role Set Granted Job Title
		List<RoleSetGrantedJobTitleDetailDto> detailDtoList = roleSetJobRepo.getByCompanyId(companyId).stream()
				.map(detail -> RoleSetGrantedJobTitleDetailDto.fromDomain(detail))
				.collect(Collectors.toList());
		
		RoleSetGrantedJobTitleDto roleSetJob = new RoleSetGrantedJobTitleDto(detailDtoList);

		return new GrantRoleSetJobDto(listRoleSet, roleSetJob, listJobTitle);
	}

}
