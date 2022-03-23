package telran.students.dto;

import javax.validation.constraints.NotNull;

public class QueryDto {

    @NotNull
    public QueryTypeDto type;
    @NotNull
    public String query;

}
