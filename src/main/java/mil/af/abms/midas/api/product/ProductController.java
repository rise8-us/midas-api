package mil.af.abms.midas.api.product;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.product.dto.ArchiveProductDTO;
import mil.af.abms.midas.api.product.dto.CreateProductDTO;
import mil.af.abms.midas.api.product.dto.ProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductJourneyMapDTO;

@RestController
@RequestMapping("/api/products")
public class ProductController extends AbstractCRUDController<Product, ProductDTO, ProductService> {

    @Autowired
    public ProductController(ProductService service) { super(service); }

    @PostMapping
    public ProductDTO create(@Valid @RequestBody CreateProductDTO createProductDTO) {
        return service.create(createProductDTO).toDto();
    }

    @PutMapping("/{id}")
    public ProductDTO updateById(@Valid @RequestBody UpdateProductDTO updateProductDTO, @PathVariable Long id) {
        return service.updateById(id, updateProductDTO).toDto();
    }

    @PutMapping("/{id}/journeymap")
    public ProductDTO updateProductJourneyMapById(@RequestBody UpdateProductJourneyMapDTO updateProductJourneyMapDTO, @PathVariable Long id) {
        return service.updateJourneyMapById(id, updateProductJourneyMapDTO).toDto();
    }
    @PutMapping("/{id}/archive")
    public ProductDTO archiveById(@RequestBody ArchiveProductDTO archiveProductDTO, @PathVariable Long id) {
        return service.archive(id, archiveProductDTO).toDto();
    }
}
