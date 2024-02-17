package io.feedpulse.dto.request;

import io.feedpulse.model.Role;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountRequestDTO implements Serializable {

    private Optional<String> email = Optional.empty();
    private Optional<String> password = Optional.empty();
    private Optional<String> referralCode = Optional.empty();


}
