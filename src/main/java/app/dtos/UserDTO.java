package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import java.util.HashSet;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class UserDTO {
    Set<String> roles = new HashSet();
    private String username;
    private String password;

    public UserDTO(String username, Set<String> roles) {
        this.username = username;
        this.roles = roles;
    }
}
