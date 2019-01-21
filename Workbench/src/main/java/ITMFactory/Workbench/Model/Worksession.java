package ITMFactory.Workbench.Model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "worksession")
public class Worksession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long SessionID;

    @ManyToOne
    @JoinColumn(name = "RFID")
    private Worker worker;

    @OneToMany(mappedBy = "worksession")
    private Set<Enviroment> Enviroments;

    @OneToMany(mappedBy = "worksession")
    private Set<Product> products;

    public Worksession(){}

    public Worksession(long SessionID, Worker worker, Set<Enviroment> Enviroments, Set<Product> products ) {
        this.SessionID = SessionID;
        this.worker = worker;
        this.Enviroments = Enviroments;
        this.products = products;
    }

    public long getSessionID() {
        return SessionID;
    }

    public Worker getworker() {
        return worker;
    }

    public void setworker(Worker worker) {
        this.worker = worker;
    }
/*
    public Set<Enviroment> getEnviroments(){
        return Enviroments;
    }

    public void setEnviroment(Set<Enviroment> Enviroments){
        this.Enviroments = Enviroments;
    }

    public Set<Product> getproducts(){
        return products;
    }

    public void setproducts(Set<Product> products){
        this.products = products;
    }
*/
}
