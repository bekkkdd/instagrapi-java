package kz.biz.galamat.qabanbay.models;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikePostTask extends Task {
    @Column(name = "post_code")
    private String postCode;
    @Column(name = "count")
    private Integer count;
    @Column(name = "completed_count")
    private Integer completedCount;
}
