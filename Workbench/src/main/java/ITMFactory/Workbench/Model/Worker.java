package ITMFactory.Workbench.Model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "worker")
public class Worker {

    @Id
    private String RFID;

    @Column(nullable = false,length = 255)
    private String Worker_name;

    @Column(length = 255)
    private int age;

    @OneToMany(mappedBy = "worker")
    private Set<Worksession> Worksessions;

    public Worker(){}

    public Worker(String RFID, String Worker_name, int age, Set<Worksession> Worksessions){
        this.age = age;
        this.RFID = RFID;
        this.Worker_name = Worker_name;
        this.Worksessions = Worksessions;
    }

    public String getRFID(){
        return RFID;
    }

    public void setage(int age){this.age = age;}

    public int getage(){
        return age;
    }

    public void setRFID(String RFID){this.RFID = RFID;}

    public void setWorkerName(String Worker_name){this.Worker_name = Worker_name;}

    public String getWorkerName(){
        return Worker_name;
    }

/*
        public Set<Worksession> getworksession(){
            return Worksessions;
        }
        public void setworksession(Set<Worksession> Worksession){
            this.Worksessions = Worksession;
        }
*/
}

