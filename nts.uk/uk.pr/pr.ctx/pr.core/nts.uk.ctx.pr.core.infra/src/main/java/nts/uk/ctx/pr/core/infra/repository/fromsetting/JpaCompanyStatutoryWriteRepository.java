package nts.uk.ctx.pr.core.infra.repository.fromsetting;

import java.util.Optional;
import java.util.List;

import javax.ejb.Stateless;

import nts.uk.ctx.pr.core.dom.fromsetting.Code;
import nts.uk.ctx.pr.core.dom.fromsetting.CompanyStatutoryWrite;
import nts.uk.ctx.pr.core.dom.fromsetting.CompanyStatutoryWriteRepository;
import nts.uk.ctx.pr.core.infra.entity.fromsetting.QpbmtComStatutoryWrite;
import nts.uk.ctx.pr.core.infra.entity.fromsetting.QpbmtComStatutoryWritePk;
import nts.arc.layer.infra.data.JpaRepository;

@Stateless
public class JpaCompanyStatutoryWriteRepository extends JpaRepository implements CompanyStatutoryWriteRepository
{

    private static final String SELECT_ALL_QUERY_STRING = "SELECT f FROM QpbmtComStatutoryWrite f";
    private static final String SELECT_BY_KEY_STRING = SELECT_ALL_QUERY_STRING + " WHERE  f.comStatutoryWritePk.cid =:cid AND  f.comStatutoryWritePk.code =:code ";
    private static final String SELECT_BY_CID = SELECT_ALL_QUERY_STRING + " WHERE  f.comStatutoryWritePk.cid =:cid order by f.comStatutoryWritePk.code ASC";


    @Override
    public List<CompanyStatutoryWrite> getAllCompanyStatutoryWrite(){
        return this.queryProxy().query(SELECT_ALL_QUERY_STRING, QpbmtComStatutoryWrite.class)
                .getList(item -> item.toDomain());
    }

    @Override
    public Optional<CompanyStatutoryWrite> getCompanyStatutoryWriteById(String cid, String code){
        return this.queryProxy().query(SELECT_BY_KEY_STRING, QpbmtComStatutoryWrite.class)
        .setParameter("cid", cid)
        .setParameter("code", new Code(code).v())
        .getSingle(c->c.toDomain());
    }

    @Override
    public void add(CompanyStatutoryWrite domain){
        this.commandProxy().insert(QpbmtComStatutoryWrite.toEntity(domain));
    }

    @Override
    public void update(CompanyStatutoryWrite domain){
        this.commandProxy().update(QpbmtComStatutoryWrite.toEntity(domain));
    }

    @Override
    public void remove(String cid, String code){
        this.commandProxy().remove(QpbmtComStatutoryWrite.class, new QpbmtComStatutoryWritePk(cid, code));
    }

    @Override
    public List<CompanyStatutoryWrite> getByCid(String cid) {
        return this.queryProxy().query(SELECT_BY_CID, QpbmtComStatutoryWrite.class)
                .setParameter("cid",cid)
                .getList(item -> item.toDomain());
    }

}
