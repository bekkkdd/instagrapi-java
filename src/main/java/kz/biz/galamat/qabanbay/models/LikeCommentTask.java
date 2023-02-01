package kz.biz.galamat.qabanbay.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeCommentTask extends Task {
    @Column(name = "comment_pk")
    private String commentPK;
    @Column(name = "count")
    private Integer count;
    @Column(name = "completed_count")
    private Integer completedCount;
}
