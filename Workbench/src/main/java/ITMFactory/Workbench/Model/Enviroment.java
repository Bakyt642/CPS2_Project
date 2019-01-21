package ITMFactory.Workbench.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name = "Enviroment")
public class Enviroment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false,length = 255)
    private String SensorName;

    @Column(nullable = false,length = 255)
    private float Sensor_Value;

    @Column(length = 255)
    private String Time;

    @Column(length = 255)
    private String Date;

    @ManyToOne
    @JoinColumn(name = "SessionID")
    private Worksession worksession;

    public Enviroment(){}

    public Enviroment(int id, String SensorName, float Sensor_Value, String Time, String Date, Worksession worksession){
        this.id = id;
        this.SensorName = SensorName;
        this.Sensor_Value = Sensor_Value;
        this.Time = Time;
        this.Date = Date;
        this.worksession = worksession;
    }

    @JsonIgnore
    public int getid(){
        return id;
    }

    public void setid(int id){
        this.id = id;
    }


    public String getSensorName(){
        return SensorName;
    }

    public void setSensorName(String SensorName){
        this.SensorName = SensorName;
    }

    public float getValue(){
        return Sensor_Value;
    }

    public void setValue(float Sensor_Value){
        this.Sensor_Value = Sensor_Value;
    }

    public String getTime(){
        return Time;
    }

    public void setTime(String Time){
        this.Time = Time;
    }

    public String getDate(){
        return Date;
    }

    public void setDate(String Date){
        this.Date = Date;
    }

    public long getWorksession(){
        return worksession.getSessionID();
    }
}