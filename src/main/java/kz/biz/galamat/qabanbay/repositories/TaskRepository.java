package kz.biz.galamat.qabanbay.repositories;

import kz.biz.galamat.qabanbay.models.LikePostTask;
import kz.biz.galamat.qabanbay.models.Task;
import kz.biz.galamat.qabanbay.models.TaskState;
import kz.biz.galamat.qabanbay.models.TaskType;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TaskRepository<T extends Task> extends PagingAndSortingRepository<T, Long> {

    List<T> findAllByTaskStateAndTaskType(TaskState state, TaskType taskType);
}
