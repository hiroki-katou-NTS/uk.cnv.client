package nts.uk.ctx.at.shared.infra.repository.scherec.taskmanagement.taskmaster;

import lombok.val;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.primitive.PrimitiveValueBase;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskmaster.TaskingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.*;
import nts.uk.ctx.at.shared.infra.entity.scherec.taskmanagement.taskmaster.KsrmtTaskChild;
import nts.uk.ctx.at.shared.infra.entity.scherec.taskmanagement.taskmaster.KsrmtTaskMaster;
import nts.uk.ctx.at.shared.infra.entity.scherec.taskmanagement.taskmaster.KsrmtTaskMasterPk;
import nts.uk.ctx.at.shared.infra.entity.scherec.taskmanagement.taskmaster.metamodel.KsrmtTaskChildPk_;
import nts.uk.ctx.at.shared.infra.entity.scherec.taskmanagement.taskmaster.metamodel.KsrmtTaskChild_;
import nts.uk.ctx.at.shared.infra.entity.scherec.taskmanagement.taskmaster.metamodel.KsrmtTaskMasterPk_;
import nts.uk.ctx.at.shared.infra.entity.scherec.taskmanagement.taskmaster.metamodel.KsrmtTaskMaster_;
import nts.uk.shr.com.color.ColorCode;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;


@Stateless
public class JpaTaskingRepository extends JpaRepository implements TaskingRepository {

    @Override
    public void insert(Task task) {
        val entityMaster = KsrmtTaskMaster.toEntity(task);
        val entityChilds = KsrmtTaskChild.toEntittys(task);
        this.commandProxy().insert(entityMaster);
        this.getEntityManager().flush();
        this.commandProxy().insertAll(entityChilds);
    }

    @Override
    public void update(Task task) {
        val entityMaster = KsrmtTaskMaster.toEntity(task);
        val entityChilds = KsrmtTaskChild.toEntittys(task);
        this.commandProxy().update(entityMaster);
        this.deleteChildTask(AppContexts.user().companyId(), task.getTaskFrameNo().v(), task.getCode().v());
        this.getEntityManager().flush();
        this.commandProxy().insertAll(entityChilds);
    }

    @Override
    public void delete(String cid, TaskFrameNo taskFrameNo, TaskCode code) {
        this.commandProxy().remove(KsrmtTaskMaster.class, new KsrmtTaskMasterPk(cid, taskFrameNo.v(), code.v()));
        this.deleteChildTask(cid, taskFrameNo.v(), code.v());
    }

    private void deleteChildTask(String cid, int taskFrameNo, String code) {
        EntityManager em = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaDelete<KsrmtTaskChild> childCriteriaDelete = criteriaBuilder.createCriteriaDelete(KsrmtTaskChild.class);
        Root<KsrmtTaskChild> root = childCriteriaDelete.from(KsrmtTaskChild.class);

        List<Predicate> condition = new ArrayList<>();
        condition.add(criteriaBuilder.equal(root.get(KsrmtTaskChild_.pk).get(KsrmtTaskChildPk_.CID), cid));
        condition.add(criteriaBuilder.equal(root.get(KsrmtTaskChild_.pk).get(KsrmtTaskChildPk_.FRAMENO), taskFrameNo));
        condition.add(criteriaBuilder.equal(root.get(KsrmtTaskChild_.pk).get(KsrmtTaskChildPk_.CD), code));
        childCriteriaDelete.where(condition.toArray(new Predicate[]{}));
        em.createQuery(childCriteriaDelete).executeUpdate();
    }

    @Override
    public List<Task> getListTask(String cid) {
        EntityManager em = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<KsrmtTaskMaster> criteriaQuery = criteriaBuilder.createQuery(KsrmtTaskMaster.class);
        Root<KsrmtTaskMaster> root = criteriaQuery.from(KsrmtTaskMaster.class);
        criteriaQuery.select(root);
        Predicate predicate = criteriaBuilder.equal(root.get(KsrmtTaskMaster_.pk).get(KsrmtTaskMasterPk_.CID), cid);
        criteriaQuery.where(predicate);
        TypedQuery<KsrmtTaskMaster> query = em.createQuery(criteriaQuery);
        List<KsrmtTaskMaster> rs = query.getResultList();
        return getListTask(rs);
    }

    @Override
    public List<Task> getListTask(String cid, TaskFrameNo taskFrameNo) {
        EntityManager em = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<KsrmtTaskMaster> criteriaQuery = criteriaBuilder.createQuery(KsrmtTaskMaster.class);
        Root<KsrmtTaskMaster> root = criteriaQuery.from(KsrmtTaskMaster.class);
        criteriaQuery.select(root);
        List<Predicate> conditions = new ArrayList<>();
        conditions.add(criteriaBuilder.equal(root.get(KsrmtTaskMaster_.pk).get(KsrmtTaskMasterPk_.CID), cid));
        conditions.add(criteriaBuilder.equal(root.get(KsrmtTaskMaster_.pk).get(KsrmtTaskMasterPk_.FRAMENO), taskFrameNo.v()));
        criteriaQuery.where(conditions.toArray(new Predicate[]{}));
        TypedQuery<KsrmtTaskMaster> query = em.createQuery(criteriaQuery);
        List<KsrmtTaskMaster> rs = query.getResultList();
        return getListTask(rs);
    }

    @Override
    public Optional<Task> getOptionalTask(String cid, TaskFrameNo taskFrameNo, TaskCode code) {
        val entityOpt = this.queryProxy().find(new KsrmtTaskMasterPk(
                cid,
                taskFrameNo.v(),
                code.v()
        ), KsrmtTaskMaster.class);
        return entityOpt.map(this::getListTask);
    }

    @Override
    public List<Task> getListTask(String cid, List<TaskFrameNo> taskFrameNos) {
        EntityManager em = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<KsrmtTaskMaster> criteriaQuery = criteriaBuilder.createQuery(KsrmtTaskMaster.class);
        Root<KsrmtTaskMaster> root = criteriaQuery.from(KsrmtTaskMaster.class);
        criteriaQuery.select(root);

        List<KsrmtTaskMaster> result = new ArrayList<>();

        CollectionUtil.split(taskFrameNos, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, frameNos -> {
            // Predicate where clause
            List<Predicate> conditions = new ArrayList<>();
            List<Integer> integerList = frameNos.stream().map(PrimitiveValueBase::v).collect(Collectors.toList());
            conditions.add(criteriaBuilder.equal(root.get(KsrmtTaskMaster_.pk).get(KsrmtTaskMasterPk_.CID), cid));
            conditions.add(root.get(KsrmtTaskMaster_.pk).get(KsrmtTaskMasterPk_.FRAMENO).in(integerList));
            criteriaQuery.where(conditions.toArray(new Predicate[]{}));
            TypedQuery<KsrmtTaskMaster> query = em.createQuery(criteriaQuery);
            result.addAll(query.getResultList());
        });
        return getListTask(result);
    }

    @Override
    public List<Task> getListTask(String cid, GeneralDate referenceDate) {
        EntityManager em = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<KsrmtTaskMaster> criteriaQuery = criteriaBuilder.createQuery(KsrmtTaskMaster.class);
        Root<KsrmtTaskMaster> root = criteriaQuery.from(KsrmtTaskMaster.class);
        criteriaQuery.select(root);
        List<Predicate> conditions = new ArrayList<>();
        conditions.add(criteriaBuilder.equal(root.get(KsrmtTaskMaster_.pk).get(KsrmtTaskMasterPk_.CID), cid));
        conditions.add(criteriaBuilder.lessThanOrEqualTo(root.get(KsrmtTaskMaster_.EXPSTARTDATE), referenceDate));
        conditions.add(criteriaBuilder.greaterThanOrEqualTo(root.get(KsrmtTaskMaster_.EXPENDDATE), referenceDate));
        criteriaQuery.where(conditions.toArray(new Predicate[]{}));
        TypedQuery<KsrmtTaskMaster> query = em.createQuery(criteriaQuery);
        List<KsrmtTaskMaster> rs = query.getResultList();
        return getListTask(rs);
    }

    @Override
    public List<Task> getListTask(String cid, GeneralDate referenceDate, List<TaskFrameNo> taskFrameNos) {
        EntityManager em = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<KsrmtTaskMaster> criteriaQuery = criteriaBuilder.createQuery(KsrmtTaskMaster.class);
        Root<KsrmtTaskMaster> root = criteriaQuery.from(KsrmtTaskMaster.class);
        criteriaQuery.select(root);

        List<KsrmtTaskMaster> result = new ArrayList<>();
        CollectionUtil.split(taskFrameNos, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, frameNos -> {
            // Predicate where clause
            List<Predicate> conditions = new ArrayList<>();
            conditions.add(criteriaBuilder.equal(root.get(KsrmtTaskMaster_.pk).get(KsrmtTaskMasterPk_.CID), cid));
            conditions.add(criteriaBuilder.lessThanOrEqualTo(root.get(KsrmtTaskMaster_.EXPSTARTDATE), referenceDate));
            conditions.add(criteriaBuilder.greaterThanOrEqualTo(root.get(KsrmtTaskMaster_.EXPENDDATE), referenceDate));

            List<Integer> integerList = frameNos.stream().map(PrimitiveValueBase::v).collect(Collectors.toList());
            conditions.add(root.get(KsrmtTaskMaster_.pk).get(KsrmtTaskMasterPk_.FRAMENO).in(integerList));
            criteriaQuery.where(conditions.toArray(new Predicate[]{}));
            TypedQuery<KsrmtTaskMaster> query = em.createQuery(criteriaQuery);
            result.addAll(query.getResultList());

        });
        return getListTask(result);
    }

    @Override
    public List<Task> getListTask(String cid, GeneralDate referenceDate, TaskFrameNo taskFrameNo, List<TaskCode> codes) {
        EntityManager em = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<KsrmtTaskMaster> criteriaQuery = criteriaBuilder.createQuery(KsrmtTaskMaster.class);
        Root<KsrmtTaskMaster> root = criteriaQuery.from(KsrmtTaskMaster.class);
        criteriaQuery.select(root);
        List<KsrmtTaskMaster> result = new ArrayList<>();
        CollectionUtil.split(codes, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, code -> {
            // Predicate where clause
            List<Predicate> conditions = new ArrayList<>();
            conditions.add(criteriaBuilder.equal(root.get(KsrmtTaskMaster_.pk).get(KsrmtTaskMasterPk_.CID), cid));
            conditions.add(criteriaBuilder.lessThanOrEqualTo(root.get(KsrmtTaskMaster_.EXPSTARTDATE), referenceDate));
            conditions.add(criteriaBuilder.greaterThanOrEqualTo(root.get(KsrmtTaskMaster_.EXPENDDATE), referenceDate));

            List<String> stringListCodes = code.stream().map(PrimitiveValueBase::v).collect(Collectors.toList());
            conditions.add(criteriaBuilder.equal(root.get(KsrmtTaskMaster_.pk).get(KsrmtTaskMasterPk_.FRAMENO), taskFrameNo.v()));
            conditions.add(root.get(KsrmtTaskMaster_.pk).get(KsrmtTaskMasterPk_.CD).in(stringListCodes));
            criteriaQuery.where(conditions.toArray(new Predicate[]{}));
            TypedQuery<KsrmtTaskMaster> query = em.createQuery(criteriaQuery);
            result.addAll(query.getResultList());

        });
        return getListTask(result);
    }

    @Override
    public List<Task> getListChildTask(String cid, TaskFrameNo taskFrameNo, TaskCode code) {
        Optional<Task> optionalTask = this.getOptionalTask(cid, taskFrameNo, code);
        if (!optionalTask.isPresent() || optionalTask.get().getChildTaskList().isEmpty())
            return Collections.emptyList();
        val listCode = optionalTask.get().getChildTaskList();
        return this.getListTask(cid, new TaskFrameNo(taskFrameNo.v() + 1), listCode);

    }

    @Override
    public List<Task> getListTask(String cid, TaskFrameNo taskFrameNo, List<TaskCode> codes) {
        EntityManager em = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<KsrmtTaskMaster> criteriaQuery = criteriaBuilder.createQuery(KsrmtTaskMaster.class);
        Root<KsrmtTaskMaster> root = criteriaQuery.from(KsrmtTaskMaster.class);
        criteriaQuery.select(root);
        List<KsrmtTaskMaster> result = new ArrayList<>();
        CollectionUtil.split(codes, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, code -> {
            // Predicate where clause
            List<Predicate> conditions = new ArrayList<>();
            conditions.add(criteriaBuilder.equal(root.get(KsrmtTaskMaster_.pk).get(KsrmtTaskMasterPk_.CID), cid));
            List<String> stringListCodes = code.stream().map(PrimitiveValueBase::v).collect(Collectors.toList());
            conditions.add(criteriaBuilder.equal(root.get(KsrmtTaskMaster_.pk).get(KsrmtTaskMasterPk_.FRAMENO), taskFrameNo.v()));
            conditions.add(root.get(KsrmtTaskMaster_.pk).get(KsrmtTaskMasterPk_.CD).in(stringListCodes));
            criteriaQuery.where(conditions.toArray(new Predicate[]{}));
            TypedQuery<KsrmtTaskMaster> query = em.createQuery(criteriaQuery);
            result.addAll(query.getResultList());

        });
        return getListTask(result);
    }

    @Override
    public boolean checkExit(String cid, TaskFrameNo taskFrameNo, TaskCode code) {
        val rs = this.getOptionalTask(cid, taskFrameNo, code);
        return rs.isPresent();
    }

    @Override
    public List<Task> getListTask(String cid, Integer taskFrameNo, List<String> codes) {
//        if (codes.isEmpty()) return new ArrayList<>();
        EntityManager em = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<KsrmtTaskMaster> criteriaQuery = criteriaBuilder.createQuery(KsrmtTaskMaster.class);
        Root<KsrmtTaskMaster> root = criteriaQuery.from(KsrmtTaskMaster.class);
        criteriaQuery.select(root);
        List<KsrmtTaskMaster> result = new ArrayList<>();
        CollectionUtil.split(codes, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, code -> {
            // Predicate where clause
            List<Predicate> conditions = new ArrayList<>();
            conditions.add(criteriaBuilder.equal(root.get(KsrmtTaskMaster_.pk).get(KsrmtTaskMasterPk_.CID), cid));
            List<String> stringListCodes = new ArrayList<>(code);
            conditions.add(criteriaBuilder.equal(root.get(KsrmtTaskMaster_.pk).get(KsrmtTaskMasterPk_.FRAMENO), taskFrameNo));
            conditions.add(root.get(KsrmtTaskMaster_.pk).get(KsrmtTaskMasterPk_.CD).in(stringListCodes));
            criteriaQuery.where(conditions.toArray(new Predicate[]{}));
            TypedQuery<KsrmtTaskMaster> query = em.createQuery(criteriaQuery);
            result.addAll(query.getResultList());

        });
        return getListTask(result);
    }

    private List<Task> getListTask(List<KsrmtTaskMaster> masterList) {
        return masterList.stream().map(this::getListTask).sorted(Comparator.comparing(Task::getCode)).collect(Collectors.toList());
    }

    private Task getListTask(KsrmtTaskMaster e) {

        return new Task(
                new TaskCode(e.getPk().CD),
                new TaskFrameNo(e.getPk().FRAMENO),
                new ExternalCooperationInfo(
                        e.EXTCD1 != null ? Optional.of(new TaskExternalCode(e.EXTCD1)) : Optional.empty(),
                        e.EXTCD2 != null ? Optional.of(new TaskExternalCode(e.EXTCD2)) : Optional.empty(),
                        e.EXTCD3 != null ? Optional.of(new TaskExternalCode(e.EXTCD3)) : Optional.empty(),
                        e.EXTCD4 != null ? Optional.of(new TaskExternalCode(e.EXTCD4)) : Optional.empty(),
                        e.EXTCD5 != null ? Optional.of(new TaskExternalCode(e.EXTCD5)) : Optional.empty()
                ),
                e.getKsrmtTaskChildren().stream().map(i -> new TaskCode(i.pk.getCHILDCD())).collect(Collectors.toList()),
                new DatePeriod(e.EXPSTARTDATE, e.EXPENDDATE),
                new TaskDisplayInfo(
                        new TaskName(e.NAME),
                        new TaskAbName(e.ABNAME),
                        e.COLOR != null ? Optional.of(new ColorCode(e.COLOR)) : Optional.empty(),
                        e.NOTE != null ? Optional.of(new TaskNote(e.NOTE)) : Optional.empty()

                )

        );
    }


}
