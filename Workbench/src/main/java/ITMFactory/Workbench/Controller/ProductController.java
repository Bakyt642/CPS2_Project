package ITMFactory.Workbench.Controller;
import ITMFactory.Workbench.Model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@RestController
@RequestMapping("/api/itm/product")
@CrossOrigin
public class ProductController {
    @Autowired
    private ProductDao p;

    @PersistenceContext
    private EntityManager em;

    //Get the quantity of product produced in a specific work session
    //http://localhost:8080/api/itm/product?worksessionid=1
    @RequestMapping(params = "worksessionid", method = RequestMethod.GET, produces = "application/json")
    public List<Product> GetProductCountbySessionID(@RequestParam("worksessionid") int worksessionid) {
        return p.GetProductProducedbysessionid(worksessionid);
    }

    //Get product produced by worker
        //http://localhost:8080/api/itm/product?workername=morad
    @RequestMapping(params = "workername",method = RequestMethod.GET, produces = "application/json")
    public List<Product> GetProductQuantitybyWorkerName(@RequestParam("workername") String workername){
        return p.GetProductProducedbyWorker(workername);
    }
}

