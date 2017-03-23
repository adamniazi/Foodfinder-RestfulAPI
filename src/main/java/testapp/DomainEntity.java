package testapp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class DomainEntity {
    private List<String> errors = new ArrayList<>();
    public void addError(String error){
        errors.add(error);
    }
}
