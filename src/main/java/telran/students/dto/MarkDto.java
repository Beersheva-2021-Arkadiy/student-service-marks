package telran.students.dto;

public class MarkDto {

    public int stid;
    public int suid;
    public int value;

    public MarkDto() {
    }

    public MarkDto(int stid, int suid, int value) {
        this.stid = stid;
        this.suid = suid;
        this.value = value;
    }
}
