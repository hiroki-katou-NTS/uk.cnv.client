/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.dom.roleset.service;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.auth.dom.adapter.employee.employeeinfo.EmpInfoByCidSidImport;
import nts.uk.ctx.sys.auth.dom.adapter.employee.employeeinfo.EmployeeInfoAdapter;
import nts.uk.ctx.sys.auth.dom.adapter.jobtitle.SyJobTitleAdapter;
import nts.uk.ctx.sys.auth.dom.grant.rolesetjob.RoleSetGrantedJobTitle;
import nts.uk.ctx.sys.auth.dom.grant.rolesetjob.RoleSetGrantedJobTitleRepository;
import nts.uk.ctx.sys.auth.dom.grant.rolesetperson.RoleSetGrantedPerson;
import nts.uk.ctx.sys.auth.dom.grant.rolesetperson.RoleSetGrantedPersonRepository;
import nts.uk.ctx.sys.auth.dom.roleset.DefaultRoleSetRepository;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSet;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSetCode;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSetRepository;
import nts.uk.ctx.sys.shared.dom.user.User;
import nts.uk.ctx.sys.shared.dom.user.UserRepository;
import nts.uk.shr.com.context.AppContexts;

/**
* The Class RoleSetServiceImp implement from RoleSetService.
* @author HieuNV
*/

@Stateless
public class RoleSetServiceImp implements RoleSetService{

    @Inject
    private RoleSetRepository roleSetRepository;

    @Inject
    private DefaultRoleSetRepository defaultRoleSetRepository;

    @Inject
    private RoleSetGrantedPersonRepository roleSetGrantedPersonRepository;

    @Inject
    private RoleSetGrantedJobTitleRepository roleSetGrantedJobTitleRepository;
    
    @Inject
    private EmployeeInfoAdapter employeeInfoAdapter;
    
    @Inject
    private UserRepository userRepo;
    
    @Inject
    private SyJobTitleAdapter syJobTitleAdapter;

    /**
     * Get all Role Set - ロールセットをすべて取得する
     * @return
     */
    @Override
    public List<RoleSet> getAllRoleSet() {
        return roleSetRepository.findByCompanyId(AppContexts.user().companyId());
    }

    /**
     * アルゴリズム「新規登録」を実行する - Execute the algorithm "new registration"
     * @param roleSet
     */
    @Override
    public void registerRoleSet(RoleSet roleSet) {
        // validate
        roleSet.validate();

        //check duplicate RoleSetCd - ロールセットコードが重複してはならない
        if (roleSetRepository.isDuplicateRoleSetCd(roleSet.getRoleSetCd().v(), roleSet.getCompanyId())) {
            throw new BusinessException("Msg_3");
        }

        // register to DB - ドメインモデル「ロールセット」を新規登録する
        this.roleSetRepository.insert(roleSet);
    }

    /**
     * アルゴリズム「更新登録」を実行する - Execute algorithm "update registration"
     * @param roleSet
     */
    @Override
    public    void updateRoleSet(RoleSet roleSet){

        // validate
        roleSet.validate();

        // update the Role set to DB - ドメインモデル「ロールセット」を更新登録する
        this.roleSetRepository.update(roleSet);
    }

    /**
     * アルゴリズム「削除」を実行する - Execute algorithm "delete"
     * @param roleSetCd
     */
    @Override
    public void deleteRoleSet(String roleSetCd) {
         //Validate constrains before perform deleting
        Optional<RoleSet> roleSetOpt = roleSetRepository.findByRoleSetCdAndCompanyId(roleSetCd, AppContexts.user().companyId());
        if (!roleSetOpt.isPresent()) {
            return;
        }
        RoleSet roleSetDom = roleSetOpt.get();

        //Confirm preconditions - 事前条件を確認する - ドメインモデル「既定のロールセット」を取得する

        // ロールセット個人別付与で使用されている場合は削除できない
        if (isGrantedForPerson(roleSetDom.getCompanyId(), roleSetCd)) {
            throw new BusinessException("Msg_850");
        }
        // ロールセット職位別付与で使用されている場合は削除できない
        if (isGrantedForPosition(roleSetDom.getCompanyId(), roleSetCd)) {
            throw new BusinessException("Msg_850");
        }

        // ドメインモデル「既定のロールセット」を取得する
        if (isDefault(roleSetDom.getCompanyId(),roleSetCd)) {
            throw new BusinessException("Msg_585");
        }

        // register to DB - ドメインモデル「ロールセット」を新規登録する
        this.roleSetRepository.delete(roleSetDom.getRoleSetCd().v(), roleSetDom.getCompanyId());
    }

    /**
     * Check setting default of Role set
     * @return
     */
    private boolean isDefault(String companyId,String roleSetCd ) {
        val defaultRoleSet = defaultRoleSetRepository.find(companyId);
        return defaultRoleSet.map(defaultRoleSet1 -> defaultRoleSet1.getRoleSetCd().v().equals(roleSetCd)).orElse(false);

    }

    /**
     * Check if this Role Set is granted for member
     * @return
     */
    private boolean isGrantedForPerson(String companyId, String roleSetCd) {
        /**check from CAS014 */
        return roleSetGrantedPersonRepository.checkRoleSetCdExist(roleSetCd, companyId);
    }

    /**
     * Check if this Role Set is granted for Position (manager)
     * @return
     */
    private boolean isGrantedForPosition(String companyId, String roleSetCd) {
        /** check from CAS014 */
        return roleSetGrantedJobTitleRepository.checkRoleSetCdExist(companyId, new RoleSetCode(roleSetCd));
    }

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.auth.dom.roleset.service.RoleSetService#getRoleSetFromUserId(java.lang.String, nts.arc.time.GeneralDate)
	 */
	@Override
	public Optional<RoleSet> getRoleSetFromUserId(String userId, GeneralDate baseDate) {
    	return getRoleSetFromUserId(userId, baseDate, AppContexts.user().companyId());
	}
	
	@Override
	public Optional<RoleSet> getRoleSetFromUserId(String userId, GeneralDate baseDate, String companyId) {
		User user = userRepo.getByUserID(userId).get();
		
		if (user.getAssociatedPersonID() == null || !user.getAssociatedPersonID().isPresent())
			return Optional.empty();
//			throw new RuntimeException("取得失敗");
		
		Optional<EmpInfoByCidSidImport> optImportEmployee = employeeInfoAdapter.getEmpInfoBySidCid(user.getAssociatedPersonID().get(), companyId);
		if (!optImportEmployee.isPresent())
			return Optional.empty();
//			throw new RuntimeException("取得失敗");
		
		// Get RoleSet granted for Person
		EmpInfoByCidSidImport importEmployee = optImportEmployee.get();
		// Update EAP No.2709
		Optional<RoleSetGrantedPerson> optRoleSetGrantedPerson = roleSetGrantedPersonRepository.getByEmployeeDate(importEmployee.getSid(), baseDate);
		if (optRoleSetGrantedPerson.isPresent())
			return roleSetRepository.findByRoleSetCdAndCompanyId(optRoleSetGrantedPerson.get().getRoleSetCd().v(), companyId);
		
		// Get RoleSet granted for JobTitle
		val optSysJobTitle = syJobTitleAdapter.gerBySidAndBaseDate(importEmployee.getSid(), baseDate);
		if (optSysJobTitle.isPresent()) {
			
			Optional<RoleSetGrantedJobTitle> roleSetGrantedJobTitle = roleSetGrantedJobTitleRepository.getByJobTitleId(companyId, optSysJobTitle.get().jobTitleId);
			
	    	if (roleSetGrantedJobTitle.isPresent()) {
	    		return roleSetRepository.findByRoleSetCdAndCompanyId(roleSetGrantedJobTitle.get().getRoleSetCd().v(), companyId);
	    	}
		}
		
    	// Get Default RoleSet
        val defaultOpt  = defaultRoleSetRepository.findByCompanyId(companyId);
    	String defaultRoleSetCD = defaultOpt.isPresent()? defaultOpt.get().getRoleSetCd().v() : null;
    	return roleSetRepository.findByRoleSetCdAndCompanyId(defaultRoleSetCD, companyId);
	}
}
