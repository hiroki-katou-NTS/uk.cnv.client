/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nts.uk.ctx.at.shared.infra.entity.statutory.worktime;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author NWS_THANHNC_PC
 */
@Entity
@Table(name = "KSHST_EMP_FLEX_SET")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "KshstEmpFlexSet.findAll", query = "SELECT k FROM KshstEmpFlexSet k"),
    @NamedQuery(name = "KshstEmpFlexSet.findByInsDate", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.insDate = :insDate"),
    @NamedQuery(name = "KshstEmpFlexSet.findByInsCcd", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.insCcd = :insCcd"),
    @NamedQuery(name = "KshstEmpFlexSet.findByInsScd", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.insScd = :insScd"),
    @NamedQuery(name = "KshstEmpFlexSet.findByInsPg", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.insPg = :insPg"),
    @NamedQuery(name = "KshstEmpFlexSet.findByUpdDate", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.updDate = :updDate"),
    @NamedQuery(name = "KshstEmpFlexSet.findByUpdCcd", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.updCcd = :updCcd"),
    @NamedQuery(name = "KshstEmpFlexSet.findByUpdScd", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.updScd = :updScd"),
    @NamedQuery(name = "KshstEmpFlexSet.findByUpdPg", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.updPg = :updPg"),
    @NamedQuery(name = "KshstEmpFlexSet.findByExclusVer", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.exclusVer = :exclusVer"),
    @NamedQuery(name = "KshstEmpFlexSet.findByCid", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.kshstEmpFlexSetPK.cid = :cid"),
    @NamedQuery(name = "KshstEmpFlexSet.findByEmpCd", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.kshstEmpFlexSetPK.empCd = :empCd"),
    @NamedQuery(name = "KshstEmpFlexSet.findByYear", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.kshstEmpFlexSetPK.year = :year"),
    @NamedQuery(name = "KshstEmpFlexSet.findByStatJanTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.statJanTime = :statJanTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findByStatFebTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.statFebTime = :statFebTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findByStatMarTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.statMarTime = :statMarTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findByStatAprTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.statAprTime = :statAprTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findByStatMayTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.statMayTime = :statMayTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findByStatJunTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.statJunTime = :statJunTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findByStatJulTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.statJulTime = :statJulTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findByStatAugTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.statAugTime = :statAugTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findByStatSepTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.statSepTime = :statSepTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findByStatOctTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.statOctTime = :statOctTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findByStatNovTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.statNovTime = :statNovTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findByStatDecTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.statDecTime = :statDecTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findBySpecJanTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.specJanTime = :specJanTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findBySpecFebTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.specFebTime = :specFebTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findBySpecMarTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.specMarTime = :specMarTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findBySpecAprTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.specAprTime = :specAprTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findBySpecMayTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.specMayTime = :specMayTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findBySpecJunTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.specJunTime = :specJunTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findBySpecJulTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.specJulTime = :specJulTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findBySpecAugTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.specAugTime = :specAugTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findBySpecSepTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.specSepTime = :specSepTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findBySpecOctTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.specOctTime = :specOctTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findBySpecNovTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.specNovTime = :specNovTime"),
    @NamedQuery(name = "KshstEmpFlexSet.findBySpecDecTime", query = "SELECT k FROM KshstEmpFlexSet k WHERE k.specDecTime = :specDecTime")})
public class KshstEmpFlexSet implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected KshstEmpFlexSetPK kshstEmpFlexSetPK;
    @Column(name = "INS_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date insDate;
    @Size(max = 4)
    @Column(name = "INS_CCD")
    private String insCcd;
    @Size(max = 30)
    @Column(name = "INS_SCD")
    private String insScd;
    @Size(max = 14)
    @Column(name = "INS_PG")
    private String insPg;
    @Column(name = "UPD_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updDate;
    @Size(max = 4)
    @Column(name = "UPD_CCD")
    private String updCcd;
    @Size(max = 30)
    @Column(name = "UPD_SCD")
    private String updScd;
    @Size(max = 14)
    @Column(name = "UPD_PG")
    private String updPg;
    @Basic(optional = false)
    @NotNull
    @Column(name = "EXCLUS_VER")
    private int exclusVer;
    @Basic(optional = false)
    @NotNull
    @Column(name = "STAT_JAN_TIME")
    private int statJanTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "STAT_FEB_TIME")
    private int statFebTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "STAT_MAR_TIME")
    private int statMarTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "STAT_APR_TIME")
    private int statAprTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "STAT_MAY_TIME")
    private int statMayTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "STAT_JUN_TIME")
    private int statJunTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "STAT_JUL_TIME")
    private int statJulTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "STAT_AUG_TIME")
    private int statAugTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "STAT_SEP_TIME")
    private int statSepTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "STAT_OCT_TIME")
    private int statOctTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "STAT_NOV_TIME")
    private int statNovTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "STAT_DEC_TIME")
    private int statDecTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SPEC_JAN_TIME")
    private int specJanTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SPEC_FEB_TIME")
    private int specFebTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SPEC_MAR_TIME")
    private int specMarTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SPEC_APR_TIME")
    private int specAprTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SPEC_MAY_TIME")
    private int specMayTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SPEC_JUN_TIME")
    private int specJunTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SPEC_JUL_TIME")
    private int specJulTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SPEC_AUG_TIME")
    private int specAugTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SPEC_SEP_TIME")
    private int specSepTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SPEC_OCT_TIME")
    private int specOctTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SPEC_NOV_TIME")
    private int specNovTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SPEC_DEC_TIME")
    private int specDecTime;

    public KshstEmpFlexSet() {
    }

    public KshstEmpFlexSet(KshstEmpFlexSetPK kshstEmpFlexSetPK) {
        this.kshstEmpFlexSetPK = kshstEmpFlexSetPK;
    }

    public KshstEmpFlexSet(KshstEmpFlexSetPK kshstEmpFlexSetPK, int exclusVer, int statJanTime, int statFebTime, int statMarTime, int statAprTime, int statMayTime, int statJunTime, int statJulTime, int statAugTime, int statSepTime, int statOctTime, int statNovTime, int statDecTime, int specJanTime, int specFebTime, int specMarTime, int specAprTime, int specMayTime, int specJunTime, int specJulTime, int specAugTime, int specSepTime, int specOctTime, int specNovTime, int specDecTime) {
        this.kshstEmpFlexSetPK = kshstEmpFlexSetPK;
        this.exclusVer = exclusVer;
        this.statJanTime = statJanTime;
        this.statFebTime = statFebTime;
        this.statMarTime = statMarTime;
        this.statAprTime = statAprTime;
        this.statMayTime = statMayTime;
        this.statJunTime = statJunTime;
        this.statJulTime = statJulTime;
        this.statAugTime = statAugTime;
        this.statSepTime = statSepTime;
        this.statOctTime = statOctTime;
        this.statNovTime = statNovTime;
        this.statDecTime = statDecTime;
        this.specJanTime = specJanTime;
        this.specFebTime = specFebTime;
        this.specMarTime = specMarTime;
        this.specAprTime = specAprTime;
        this.specMayTime = specMayTime;
        this.specJunTime = specJunTime;
        this.specJulTime = specJulTime;
        this.specAugTime = specAugTime;
        this.specSepTime = specSepTime;
        this.specOctTime = specOctTime;
        this.specNovTime = specNovTime;
        this.specDecTime = specDecTime;
    }

    public KshstEmpFlexSet(String cid, String empCd, short year) {
        this.kshstEmpFlexSetPK = new KshstEmpFlexSetPK(cid, empCd, year);
    }

    public KshstEmpFlexSetPK getKshstEmpFlexSetPK() {
        return kshstEmpFlexSetPK;
    }

    public void setKshstEmpFlexSetPK(KshstEmpFlexSetPK kshstEmpFlexSetPK) {
        this.kshstEmpFlexSetPK = kshstEmpFlexSetPK;
    }

    public Date getInsDate() {
        return insDate;
    }

    public void setInsDate(Date insDate) {
        this.insDate = insDate;
    }

    public String getInsCcd() {
        return insCcd;
    }

    public void setInsCcd(String insCcd) {
        this.insCcd = insCcd;
    }

    public String getInsScd() {
        return insScd;
    }

    public void setInsScd(String insScd) {
        this.insScd = insScd;
    }

    public String getInsPg() {
        return insPg;
    }

    public void setInsPg(String insPg) {
        this.insPg = insPg;
    }

    public Date getUpdDate() {
        return updDate;
    }

    public void setUpdDate(Date updDate) {
        this.updDate = updDate;
    }

    public String getUpdCcd() {
        return updCcd;
    }

    public void setUpdCcd(String updCcd) {
        this.updCcd = updCcd;
    }

    public String getUpdScd() {
        return updScd;
    }

    public void setUpdScd(String updScd) {
        this.updScd = updScd;
    }

    public String getUpdPg() {
        return updPg;
    }

    public void setUpdPg(String updPg) {
        this.updPg = updPg;
    }

    public int getExclusVer() {
        return exclusVer;
    }

    public void setExclusVer(int exclusVer) {
        this.exclusVer = exclusVer;
    }

    public int getStatJanTime() {
        return statJanTime;
    }

    public void setStatJanTime(int statJanTime) {
        this.statJanTime = statJanTime;
    }

    public int getStatFebTime() {
        return statFebTime;
    }

    public void setStatFebTime(int statFebTime) {
        this.statFebTime = statFebTime;
    }

    public int getStatMarTime() {
        return statMarTime;
    }

    public void setStatMarTime(int statMarTime) {
        this.statMarTime = statMarTime;
    }

    public int getStatAprTime() {
        return statAprTime;
    }

    public void setStatAprTime(int statAprTime) {
        this.statAprTime = statAprTime;
    }

    public int getStatMayTime() {
        return statMayTime;
    }

    public void setStatMayTime(int statMayTime) {
        this.statMayTime = statMayTime;
    }

    public int getStatJunTime() {
        return statJunTime;
    }

    public void setStatJunTime(int statJunTime) {
        this.statJunTime = statJunTime;
    }

    public int getStatJulTime() {
        return statJulTime;
    }

    public void setStatJulTime(int statJulTime) {
        this.statJulTime = statJulTime;
    }

    public int getStatAugTime() {
        return statAugTime;
    }

    public void setStatAugTime(int statAugTime) {
        this.statAugTime = statAugTime;
    }

    public int getStatSepTime() {
        return statSepTime;
    }

    public void setStatSepTime(int statSepTime) {
        this.statSepTime = statSepTime;
    }

    public int getStatOctTime() {
        return statOctTime;
    }

    public void setStatOctTime(int statOctTime) {
        this.statOctTime = statOctTime;
    }

    public int getStatNovTime() {
        return statNovTime;
    }

    public void setStatNovTime(int statNovTime) {
        this.statNovTime = statNovTime;
    }

    public int getStatDecTime() {
        return statDecTime;
    }

    public void setStatDecTime(int statDecTime) {
        this.statDecTime = statDecTime;
    }

    public int getSpecJanTime() {
        return specJanTime;
    }

    public void setSpecJanTime(int specJanTime) {
        this.specJanTime = specJanTime;
    }

    public int getSpecFebTime() {
        return specFebTime;
    }

    public void setSpecFebTime(int specFebTime) {
        this.specFebTime = specFebTime;
    }

    public int getSpecMarTime() {
        return specMarTime;
    }

    public void setSpecMarTime(int specMarTime) {
        this.specMarTime = specMarTime;
    }

    public int getSpecAprTime() {
        return specAprTime;
    }

    public void setSpecAprTime(int specAprTime) {
        this.specAprTime = specAprTime;
    }

    public int getSpecMayTime() {
        return specMayTime;
    }

    public void setSpecMayTime(int specMayTime) {
        this.specMayTime = specMayTime;
    }

    public int getSpecJunTime() {
        return specJunTime;
    }

    public void setSpecJunTime(int specJunTime) {
        this.specJunTime = specJunTime;
    }

    public int getSpecJulTime() {
        return specJulTime;
    }

    public void setSpecJulTime(int specJulTime) {
        this.specJulTime = specJulTime;
    }

    public int getSpecAugTime() {
        return specAugTime;
    }

    public void setSpecAugTime(int specAugTime) {
        this.specAugTime = specAugTime;
    }

    public int getSpecSepTime() {
        return specSepTime;
    }

    public void setSpecSepTime(int specSepTime) {
        this.specSepTime = specSepTime;
    }

    public int getSpecOctTime() {
        return specOctTime;
    }

    public void setSpecOctTime(int specOctTime) {
        this.specOctTime = specOctTime;
    }

    public int getSpecNovTime() {
        return specNovTime;
    }

    public void setSpecNovTime(int specNovTime) {
        this.specNovTime = specNovTime;
    }

    public int getSpecDecTime() {
        return specDecTime;
    }

    public void setSpecDecTime(int specDecTime) {
        this.specDecTime = specDecTime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (kshstEmpFlexSetPK != null ? kshstEmpFlexSetPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof KshstEmpFlexSet)) {
            return false;
        }
        KshstEmpFlexSet other = (KshstEmpFlexSet) object;
        if ((this.kshstEmpFlexSetPK == null && other.kshstEmpFlexSetPK != null) || (this.kshstEmpFlexSetPK != null && !this.kshstEmpFlexSetPK.equals(other.kshstEmpFlexSetPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.KshstEmpFlexSet[ kshstEmpFlexSetPK=" + kshstEmpFlexSetPK + " ]";
    }
    
}
