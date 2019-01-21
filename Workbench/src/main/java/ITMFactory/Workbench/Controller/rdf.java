package ITMFactory.Workbench.Controller;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.StringWriter;
import java.io.Writer;

@Controller
@RequestMapping("rdf")
public class rdf {

    @Autowired
    EnviromentDao Ed;

    @Autowired
    ProductDao Pd;

    @GetMapping(value = "/turtle_plain", produces = {"text/plain"})
    public @ResponseBody
    String rdf(){
        Writer out = new StringWriter();
        GenerateRDF().write(out,"turtle");
        return out.toString();
    }

    @GetMapping(value = "turtle", produces = {"text/turtle"})
    public @ResponseBody
    String turtle(){
        return GenerateRDF().toString();
    }

    private Model GenerateRDF(){
        final String sosa = "http://www.w3.org/ns/sosa/";
        final String db = "http://dbpedia.org/resource/";
        final String dbo = "http://dbpedia.org/ontology/";
        final String wiki = "https://www.wikidata.org/wiki/";
        final String owl = "http://www.w3.org/2002/07/owl#";
        final String xsd = org.apache.jena.vocabulary.XSD.getURI();
        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix("sosa",sosa);
        model.setNsPrefix("db",db);
        model.setNsPrefix("dbo",dbo);
        model.setNsPrefix("wiki",wiki);
        model.setNsPrefix("rdf", RDF.getURI());
        model.setNsPrefix("foaf", FOAF.getURI());
        model.setNsPrefix("owl",owl);
        model.setNsPrefix("xsd", xsd);
        Resource r = model.createProperty(sosa,"Sensor");
        Property observes = model.createProperty(sosa, "observes");
        Resource hum = model.createProperty(db,"Humidity");
        Resource temp = model.createProperty(db,"Temperature");
        Resource lum = model.createProperty(db,"Light");
        Property rfid = model.createProperty(db,"Radio-frequency_identification");
        Resource product = model.createProperty(wiki,"Q2424752");
        Property quantity = model.createProperty(db,"Quantity");
        Resource worker = model.createProperty(wiki,"Q327055");
        Property abstractt = model.createProperty(dbo,"abstract");
        Property produceBy = model.createProperty(dbo,"producedBy");
        Property rdfvalue = model.createProperty(RDF.getURI(), "value");
        Property rdftype = model.createProperty(RDF.getURI(), "type");
        Property workername = model.createProperty(FOAF.getURI(),"name");
        Property person = model.createProperty(FOAF.getURI(),"Person");
        Property thing = model.createProperty(owl,"thing");
        Literal Humidityvalue = model.createTypedLiteral(Ed.GetSensorValueNow("hum"),XSD.decimal.getURI());
        Literal Tempvalue = model.createTypedLiteral(Ed.GetSensorValueNow("temp"),XSD.decimal.getURI());
        Literal lumvalue = model.createTypedLiteral(Ed.GetSensorValueNow("lum"),
                XSD.decimal.getURI());
        Literal abstracttemp = model.createTypedLiteral("A temperature is an objective comparative measure of hot or cold"
                ,XSD.xstring.getURI());
        Literal literalnameofworker = model.createTypedLiteral("alex",XSD.xstring.getURI());
        Literal literalofRFID = model.createTypedLiteral("8400071725",XSD.xstring.getURI());
        Literal literalQ = model.createTypedLiteral(Pd.GetProductProducedbyWorkerr("alex"),XSD.integer.getURI());
        model.add(r, observes, hum);
        model.add(r, observes, temp);
        model.add(r, observes, lum);
        model.add(r,observes,product);
        model.add(product, produceBy, worker);
        model.add(worker, rdftype, person);
        model.add(product, quantity, literalQ);
        model.add(worker,workername, literalnameofworker);
        model.add(worker, rfid, literalofRFID);
        model.add(hum, rdfvalue, Humidityvalue);
        model.add(temp, rdfvalue, Tempvalue);
        model.add(lum, rdfvalue, lumvalue);
        model.add(temp, abstractt, abstracttemp);
        model.add(product,rdftype,thing);
        return model;
    }
}