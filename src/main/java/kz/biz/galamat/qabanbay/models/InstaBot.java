package kz.biz.galamat.qabanbay.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstaBot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "last_session_id_update")
    private Date lastSessionIdUpdate;

    private Boolean challenged;
}
