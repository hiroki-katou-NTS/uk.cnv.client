package nts.uk.ctx.at.schedule.infra.repository.schedule.workschedules.displaysetting;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.primitive.PrimitiveValueBase;
import nts.uk.ctx.at.schedule.dom.schedule.setting.displaycontrol.PersonSymbolQualify;
import nts.uk.ctx.at.schedule.dom.workschedule.displaysetting.ConditionATRWorkSchedule;
import nts.uk.ctx.at.schedule.dom.workschedule.displaysetting.DisplayControlPersonalCondition;
import nts.uk.ctx.at.schedule.dom.workschedule.displaysetting.DisplayControlPersonalConditionRepo;
import nts.uk.ctx.at.schedule.dom.workschedule.displaysetting.PersonInforDisplayControl;
import nts.uk.ctx.at.schedule.dom.workschedule.displaysetting.QualificationCD;
import nts.uk.ctx.at.schedule.dom.workschedule.displaysetting.WorkscheQualifi;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.displaysetting.KscmtSyacndDispCtl;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.displaysetting.KscmtSyacndDispCtlPK;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.displaysetting.KscmtSyacndDispCtlQua;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 
 * 個人条件の表示制御 Repository
 *
 */

@Stateless
public class JpaDisplayControlPersonalConditionRepository extends JpaRepository
		implements DisplayControlPersonalConditionRepo {

	private static final String DELETE_QUA = "delete from KscmtSyacndDispCtlQua a WHERE a.pk.cid = :cid ";
	private static final String DELETE_DISPCTL = "delete from KscmtSyacndDispCtl a WHERE a.pk.cid = :cid ";
	private static final String query = "SELECT a FROM KscmtSyacndDispCtl a WHERE a.pk.cid = :cid AND a.pk.cndAtr IN :cndAtr";
	private static final String DELETE = "delete from KscmtSyacndDispCtlQua a "
			+ " WHERE a.pk.cid = :cid AND a.pk.qualification IN :qualification ";
	private static final String QUERY_QUALIFICATION = "SELECT a.pk.qualification FROM KscmtSyacndDispCtlQua a WHERE a.pk.cid = :cid ";
	private static final String GET_DISPCTL = "SELECT a FROM KscmtSyacndDispCtl a WHERE a.pk.cid = :cid ";
	private static final String GET_QUA = "SELECT a FROM KscmtSyacndDispCtlQua a WHERE a.pk.cid = :cid ";

	// [1] Insert(個人条件の表示制御)
	@Override
	public void insert(DisplayControlPersonalCondition condition) {
		List<KscmtSyacndDispCtl> dispCtls = KscmtSyacndDispCtl.toEntities(condition);
		if (!dispCtls.isEmpty()) {
			this.commandProxy().insertAll(dispCtls);
		}
		List<KscmtSyacndDispCtlQua> dispCtlQuas = KscmtSyacndDispCtlQua.toEntities(condition);
		if (!dispCtlQuas.isEmpty()) {
			this.commandProxy().insertAll(dispCtlQuas);
		}

	}

	// [2] Update(個人条件の表示制御)
	@Override
	public void update(DisplayControlPersonalCondition condition) {
		List<KscmtSyacndDispCtl> dispCtls = this.queryProxy().query(GET_DISPCTL, KscmtSyacndDispCtl.class)
				.setParameter("cid", condition.getCompanyID()).getList();
		dispCtls.forEach(i -> {
			if (condition.getListConditionDisplayControl().stream().anyMatch(e -> i.pk.cndAtr == e.getConditionATR().value)) {
				condition.getListConditionDisplayControl().stream()
						.filter(e -> i.pk.cndAtr == e.getConditionATR().value)
						.findFirst().ifPresent(d -> {
					i.dispAtr = d.getDisplayCategory().value;
					i.syname = condition.getOtpWorkscheQualifi().isPresent()
							? condition.getOtpWorkscheQualifi().get().getQualificationMark().v()
							: null;
				});
			} else {
				i.dispAtr = NotUseAtr.NOT_USE.value;
			}
		});
		condition.getListConditionDisplayControl().stream()
				.filter(d -> dispCtls.stream().noneMatch(e -> e.pk.cndAtr == d.getConditionATR().value))
				.forEach(d -> {
					String qualificationMark = condition.getOtpWorkscheQualifi().isPresent()
							? condition.getOtpWorkscheQualifi().get().getQualificationMark().v() : null;
					KscmtSyacndDispCtl entity = new KscmtSyacndDispCtl(
							new KscmtSyacndDispCtlPK(condition.getCompanyID(), d.getConditionATR().value),
							d.getDisplayCategory().value,
							qualificationMark
					);
					this.commandProxy().insert(entity);
				});

		if (condition.getOtpWorkscheQualifi().isPresent() && !condition.getOtpWorkscheQualifi().get().getListQualificationCD().isEmpty()) {
			List<String> qualificationCD = condition.getOtpWorkscheQualifi().get().getListQualificationCD().stream()
					.map(PrimitiveValueBase::v).collect(Collectors.toList());
			List<String> dispCtlQuas = this.queryProxy().query(QUERY_QUALIFICATION, String.class)
					.setParameter("cid", condition.getCompanyID()).getList();

			List<String> listToDel = dispCtlQuas.stream().filter(e -> !qualificationCD.contains(e))
					.collect(Collectors.toList());
			if (!listToDel.isEmpty()) {
				this.queryProxy().query(DELETE).setParameter("cid", condition.getCompanyID())
						.setParameter("qualification", listToDel);
			}

			List<String> listToInsert = qualificationCD.stream().filter(e -> !dispCtlQuas.contains(e))
					.collect(Collectors.toList());
			listToInsert.forEach(code -> {
				this.commandProxy().insert(KscmtSyacndDispCtlQua.toEntity(condition, code));
			});
		} else {
			this.queryProxy().query(DELETE_QUA).setParameter("cid", condition.getCompanyID());
		}

	}

	// [3] Delete(会社ID)
	@Override
	public void delete(String companyId) {
		this.queryProxy().query(DELETE_DISPCTL).setParameter("cid", companyId);
		this.queryProxy().query(DELETE_QUA).setParameter("cid", companyId);

	}

	// [4] get
	@Override
	public Optional<DisplayControlPersonalCondition> get(String companyId) {
		List<KscmtSyacndDispCtl> dispCtls = this.queryProxy().query(GET_DISPCTL, KscmtSyacndDispCtl.class)
				.setParameter("cid", companyId).getList();
		List<KscmtSyacndDispCtlQua> dispCtlQuas = this.queryProxy().query(GET_QUA, KscmtSyacndDispCtlQua.class)
				.setParameter("cid", companyId).getList();
		if (dispCtls.isEmpty() && dispCtlQuas.isEmpty()) {
			return Optional.empty();
		}
		List<PersonInforDisplayControl> listConditionDisplayControl = dispCtls.stream()
				.map(i -> new PersonInforDisplayControl(ConditionATRWorkSchedule.valueOf(i.pk.cndAtr),
						NotUseAtr.valueOf(i.dispAtr)))
				.collect(Collectors.toList());
		Optional<WorkscheQualifi> otpWorkscheQualifi = Optional.empty();
		if (!dispCtlQuas.isEmpty()) {
			otpWorkscheQualifi = Optional.of(new WorkscheQualifi(
					dispCtls.isEmpty() ? null : new PersonSymbolQualify(dispCtls.get(0).syname), dispCtlQuas.stream()
							.map(i -> new QualificationCD(i.pk.qualification)).collect(Collectors.toList())));
		}
		DisplayControlPersonalCondition condition = new DisplayControlPersonalCondition(companyId,
				listConditionDisplayControl, otpWorkscheQualifi);
		return Optional.of(condition);
	}

}
