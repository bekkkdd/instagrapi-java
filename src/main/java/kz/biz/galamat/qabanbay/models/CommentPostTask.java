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
public class CommentPostTask extends Task {
    @Column(name = "post_code")
    private String postCode;
    @Column(name = "text_with_separator")
    private String textWithSeparator;
    @Column(name = "completed_count")
    private Integer completedCount;
}
