package ITMFactory.Workbench.Controller;

import ITMFactory.Workbench.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductDao extends JpaRepository<Product, Integer> {

    @Query(value = "select * from product where product_sessionid = ?1 order by product_count DESC limit 1",
            nativeQuery = true)
    List<Product> GetProductProducedbysessionid(int worksessionid);


    @Query(value = "Select * from product " +
            "inner join worksession on product.product_sessionID = worksession.sessionid " +
            "inner join worker on worker.rfid = worksession.rfid where Worker_name like %?1 " +
            "order by product_count desc limit 1",nativeQuery = true)
    List<Product> GetProductProducedbyWorker(String name);

    @Query(value = "Select product_count from product " +
            "inner join worksession on product.product_sessionID = worksession.sessionid " +
            "inner join worker on worker.rfid = worksession.rfid where Worker_name like %?1 " +
            "order by product_count desc limit 1",nativeQuery = true)
    int GetProductProducedbyWorkerr(String name);

}