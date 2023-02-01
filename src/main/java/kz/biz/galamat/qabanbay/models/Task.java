package kz.biz.galamat.qabanbay.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Enumerated(EnumType.STRING)
        @Column(length = 20, name = "task_type")
        private TaskType taskType;

        @Enumerated(EnumType.STRING)
        @Column(length = 20, name = "task_state")
        private TaskState taskState;

        @Column(name="UPDATE_TS")
        private Date dateMaj;

        @PreUpdate
        void onPersist() {
                this.setDateMaj(new Timestamp((new Date()).getTime()));
        }
}
