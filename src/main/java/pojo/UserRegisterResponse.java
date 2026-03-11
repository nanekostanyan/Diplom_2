package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterResponse {
    private Boolean success;
    private String accessToken;
    private String refreshToken;
    private User user;
}
