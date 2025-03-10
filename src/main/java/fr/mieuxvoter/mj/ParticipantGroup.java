package fr.mieuxvoter.mj;

import java.math.BigInteger;

public class ParticipantGroup {

    public enum Type {
        Median,
        Contestation,
        Adhesion,
    }

    protected BigInteger size;
    protected Integer grade;
    protected Type type;

    public ParticipantGroup(
            BigInteger size,
            Integer grade,
            Type type
    ) {
        this.size = size;
        this.grade = grade;
        this.type = type;
    }

    public BigInteger getSize() {
        return size;
    }

    public Integer getGrade() {
        return grade;
    }

    public Type getType() {
        return type;
    }
}
