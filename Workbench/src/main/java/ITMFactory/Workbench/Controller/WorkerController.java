package ITMFactory.Workbench.Controller;
import ITMFactory.Workbench.Model.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@RestController
@RequestMapping("/api/itm/worker")
@CrossOrigin
public class WorkerController {


    @Autowired
    private WorkerDao W;

    @PersistenceContext
    private EntityManager em;

    //get all worker information
    //http://localhost:8080/api/itm/worker?all
    @RequestMapping(params = "all",method = RequestMethod.GET, produces = "application/json")
    public List<Worker> GetWorkers() {
       // return W.findAll();
        return W.getallworker();
    }

    //get worker by his name
    //http://localhost:8080/api/itm/worker?name=morad
    @RequestMapping(params = "name", method = RequestMethod.GET, produces = "application/json")
    public List<Worker> GetWorkerByName(@RequestParam("name") String Worker_Name) {
        String Qsql = "SELECT c FROM Worker c WHERE LOWER(c.Worker_name) =:name";
        return em.createQuery(Qsql, Worker.class).setParameter("name", Worker_Name).getResultList();
    }

    // get worker information by his RFID
    //http://localhost:8080/api/itm/worker?rfid=aaa
    @RequestMapping(params = "rfid", method = RequestMethod.GET, produces = "application/json")
    public List<Worker> GetWorkerByRFID(@RequestParam("rfid") String Worker_rfid) {
        return W.GetWorkerByRFID(Worker_rfid);
    }

    /*
    @PostMapping("addworker")
    public ResponseEntity<Object> Sendvalues(@RequestBody worker s){
        Sensor saveValue = w.save(s);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(saveValue.getId()).toUri();
        return ResponseEntity.created(location).build();
}
     */
}