package uk.ac.isc.textview;

class PhaseEditData implements Cloneable {

    private final Integer phaseId;
    private String type;
    private Boolean fix;
    private Boolean nondef;
    private Integer timeShift;
    private Boolean deleteAmp;
    private String phaseBreak;
    private Integer putHypocentre;

    public PhaseEditData(Integer phaseId, 
            String type, 
            Boolean fix, 
            Boolean nondef,
            Integer timeShift,
            Boolean deleteAmp,
            String phaseBreak,
            Integer putHypocentre) {
        this.phaseId = phaseId;
        this.type = type;
        this.fix = fix;
        this.nondef = nondef;
        this.timeShift = timeShift;
        this.deleteAmp = deleteAmp;
        this.phaseBreak = phaseBreak;
        this.putHypocentre = putHypocentre;
    }

    public Integer getPhaseId() {
        return phaseId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getFix() {
        return fix;
    }

    public void setFix(Boolean fix) {
        this.fix = fix;
    }

    public Boolean getNondef() {
        return nondef;
    }

    public void setNondef(Boolean nondef) {
        this.nondef = nondef;
    }

    public Integer getTimeShift() {
        return timeShift;
    }

    public void setTimeShift(Integer timeShift) {
        this.timeShift = timeShift;
    }

    public Boolean getDeleteAmp() {
        return deleteAmp;
    }

    public void setDeleteAmp(Boolean deleteAmp) {
        this.deleteAmp = deleteAmp;
    }

    public String getPhaseBreak() {
        return phaseBreak;
    }

    public void setPhaseBreak(String phaseBreak) {
        this.phaseBreak = phaseBreak;
    }

    public Integer getPutHypocentre() {
        return putHypocentre;
    }

    public void setPutHypocentre(Integer putValue) {
        this.putHypocentre = putValue;
    }

    @Override
    public Object clone() {

        try {
            PhaseEditData clone = (PhaseEditData) super.clone();
            clone.setType(this.type);
            clone.setFix(this.fix);
            clone.setNondef(this.nondef);
            clone.setTimeShift(this.timeShift);
            clone.setDeleteAmp(this.deleteAmp);
            clone.setPhaseBreak(this.phaseBreak);
            clone.setPutHypocentre(this.putHypocentre);
            return clone;
        } catch (CloneNotSupportedException e) {
            return e;
        }
    }
}
