package ITMFactory.Workbench.Controller;

import ITMFactory.Workbench.Model.Enviroment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EnviromentDao extends JpaRepository<Enviroment,Integer> {

    @Query(value = "select * from enviroment where sessionid = ?1",nativeQuery = true)
    List<Enviroment> GetEnviromentStatusbasedonSessionID(int sessionid);

    @Query(value = "select * from enviroment where sessionid =(select sessionid from worksession order by sessionid desc limit 1)",nativeQuery = true)
    List<Enviroment> GetlastEnviromentStatus();

    @Query(value = "select sensor_value from enviroment where sensor_name like %?1 order by sessionid desc limit 1",nativeQuery = true)
    Float GetSensorValueNow(String SensorName);
}

