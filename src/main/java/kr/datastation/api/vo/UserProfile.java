package kr.datastation.api.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    private Long id;
    private String username;
    private String name;
    private Timestamp joinedAt;
}
