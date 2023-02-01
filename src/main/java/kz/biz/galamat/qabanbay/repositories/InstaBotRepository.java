package kz.biz.galamat.qabanbay.repositories;

import kz.biz.galamat.qabanbay.models.InstaBot;
import kz.biz.galamat.qabanbay.models.LikePostTask;
import kz.biz.galamat.qabanbay.models.Task;
import kz.biz.galamat.qabanbay.models.TaskState;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface InstaBotRepository extends PagingAndSortingRepository<InstaBot, Long> {

    List<InstaBot> findAllByChallenged(Boolean challenged);
    InstaBot findByUsername(String username);
    InstaBot findByUsernameAndPassword(String username, String password);
}
