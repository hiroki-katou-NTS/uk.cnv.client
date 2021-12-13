/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nts.uk.ctx.at.record.infra.entity.daily.shortwork;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
//import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.BooleanUtils;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.actualworkinghours.AttendanceTimeOfDailyPerformance;
//import nts.uk.ctx.at.record.infra.entity.daily.actualworktime.KrcdtDayAttendanceTime;
import nts.uk.ctx.at.record.infra.entity.daily.time.KrcdtDayTimeAtd;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 *
 * @author NWS_THANHNC_PC
 */
@Entity
@Table(name = "KRCDT_DAY_TS_SHORTTIME")
@XmlRootElement
@NamedQueries({
//    @NamedQuery(name = "KrcdtDaiShortWorkTime.findAll", query = "SELECT k FROM KrcdtDaiShortWorkTime k"),
//    @NamedQuery(name = "KrcdtDaiShortWorkTime.findByInsDate", query = "SELECT k FROM KrcdtDaiShortWorkTime k WHERE k.insDate = :insDate"),
//    @NamedQuery(name = "KrcdtDaiShortWorkTime.findByInsCcd", query = "SELECT k FROM KrcdtDaiShortWorkTime k WHERE k.insCcd = :insCcd"),
//    @NamedQuery(name = "KrcdtDaiShortWorkTime.findByInsScd", query = "SELECT k FROM KrcdtDaiShortWorkTime k WHERE k.insScd = :insScd"),
//    @NamedQuery(name = "KrcdtDaiShortWorkTime.findByInsPg", query = "SELECT k FROM KrcdtDaiShortWorkTime k WHERE k.insPg = :insPg"),
//    @NamedQuery(name = "KrcdtDaiShortWorkTime.findByUpdDate", query = "SELECT k FROM KrcdtDaiShortWorkTime k WHERE k.updDate = :updDate"),
//    @NamedQuery(name = "KrcdtDaiShortWorkTime.findByUpdCcd", query = "SELECT k FROM KrcdtDaiShortWorkTime k WHERE k.updCcd = :updCcd"),
//    @NamedQuery(name = "KrcdtDaiShortWorkTime.findByUpdScd", query = "SELECT k FROM KrcdtDaiShortWorkTime k WHERE k.updScd = :updScd"),
//    @NamedQuery(name = "KrcdtDaiShortWorkTime.findByUpdPg", query = "SELECT k FROM KrcdtDaiShortWorkTime k WHERE k.updPg = :updPg"),
//    @NamedQuery(name = "KrcdtDaiShortWorkTime.findByExclusVer", query = "SELECT k FROM KrcdtDaiShortWorkTime k WHERE k.exclusVer = :exclusVer"),
//    @NamedQuery(name = "KrcdtDaiShortWorkTime.findBySid", query = "SELECT k FROM KrcdtDaiShortWorkTime k WHERE k.krcdtDaiShortWorkTimePK.sid = :sid"),
//    @NamedQuery(name = "KrcdtDaiShortWorkTime.findByYmd", query = "SELECT k FROM KrcdtDaiShortWorkTime k WHERE k.krcdtDaiShortWorkTimePK.ymd = :ymd"),
//    @NamedQuery(name = "KrcdtDaiShortWorkTime.findByShortWorkTimeFrameNo", query = "SELECT k FROM KrcdtDaiShortWorkTime k WHERE k.krcdtDaiShortWorkTimePK.shortWorkTimeFrameNo = :shortWorkTimeFrameNo"),
//    @NamedQuery(name = "KrcdtDaiShortWorkTime.findByStartTime", query = "SELECT k FROM KrcdtDaiShortWorkTime k WHERE k.startTime = :startTime"),
//    @NamedQuery(name = "KrcdtDaiShortWorkTime.findByEndTime", query = "SELECT k FROM KrcdtDaiShortWorkTime k WHERE k.endTime = :endTime"),
//    @NamedQuery(name = "KrcdtDaiShortWorkTime.findByChildCareAtr", query = "SELECT k FROM KrcdtDaiShortWorkTime k WHERE k.childCareAtr = :childCareAtr"),
//    @NamedQuery(name = "KrcdtDaiShortWorkTime.findByTime", query = "SELECT k FROM KrcdtDaiShortWorkTime k WHERE k.time = :time")
	})
public class KrcdtDaiShortWorkTime extends ContractUkJpaEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    public KrcdtDaiShortWorkTimePK krcdtDaiShortWorkTimePK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "START_TIME")
    public int startTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "END_TIME")
    public int endTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CHILD_CARE_ATR")
    public boolean childCareAtr;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns(value = {
			@JoinColumn(name = "SID", referencedColumnName = "SID", insertable = false, updatable = false),
			@JoinColumn(name = "YMD", referencedColumnName = "YMD", insertable = false, updatable = false) })
	public KrcdtDayTimeAtd krcdtDayTime;
    

    public KrcdtDaiShortWorkTime() {
    }

    public KrcdtDaiShortWorkTime(KrcdtDaiShortWorkTimePK krcdtDaiShortWorkTimePK) {
        this.krcdtDaiShortWorkTimePK = krcdtDaiShortWorkTimePK;
    }

    public KrcdtDaiShortWorkTime(KrcdtDaiShortWorkTimePK krcdtDaiShortWorkTimePK, int startTime, int endTime, int childCareAtr) {
        this.krcdtDaiShortWorkTimePK = krcdtDaiShortWorkTimePK;
        this.startTime = startTime;
        this.endTime = endTime;
        this.childCareAtr = BooleanUtils.toBoolean(childCareAtr);
    }

    public KrcdtDaiShortWorkTime(String sid, GeneralDate ymd, int shortWorkTimeFrameNo) {
        this.krcdtDaiShortWorkTimePK = new KrcdtDaiShortWorkTimePK(sid, ymd, shortWorkTimeFrameNo);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (krcdtDaiShortWorkTimePK != null ? krcdtDaiShortWorkTimePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof KrcdtDaiShortWorkTime)) {
            return false;
        }
        KrcdtDaiShortWorkTime other = (KrcdtDaiShortWorkTime) object;
        if ((this.krcdtDaiShortWorkTimePK == null && other.krcdtDaiShortWorkTimePK != null) || (this.krcdtDaiShortWorkTimePK != null && !this.krcdtDaiShortWorkTimePK.equals(other.krcdtDaiShortWorkTimePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.KrcdtDaiShortWorkTime[ krcdtDaiShortWorkTimePK=" + krcdtDaiShortWorkTimePK + " ]";
    }

	@Override
	protected Object getKey() {
		return krcdtDaiShortWorkTimePK;
	}

	public void setData(AttendanceTimeOfDailyPerformance attendanceTime,int frameNo) {
		
	}
    
}
