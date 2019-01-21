package ITMFactory.Workbench.Model;
import javax.persistence.*;

@Entity
@Table(name = "Product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idProduct;

    @Column(nullable = false,length = 255)
    private int ProductCount;

    @ManyToOne
    @JoinColumn(name = "ProductSessionID")
    private Worksession worksession;

    public Product(){}

    public Product(int idProduct, int ProductCount){
        this.idProduct = idProduct;
        this.ProductCount = ProductCount;
        //this.worksession = worksession;
    }

    public int getidProduct(){
        return idProduct;
    }

    public void setIdProduct(){
        this.idProduct = idProduct;
    }
/*
    public Worksession getworksession(){
        return worksession;
    }
    public void setworksession(Worksession worksession){
        this.worksession = worksession;
    }
*/
    public int getProductCount(){
        return ProductCount;
    }

    public void setProductCount(int ProductCount){
        this.ProductCount = ProductCount;
    }

}