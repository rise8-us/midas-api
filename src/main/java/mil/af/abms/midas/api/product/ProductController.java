package mil.af.abms.midas.api.product;

import javax.validation.Valid;

import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.dtos.SprintProductMetricsDTO;
import mil.af.abms.midas.api.product.dto.CreateProductDTO;
import mil.af.abms.midas.api.product.dto.ProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductDTO;
import mil.af.abms.midas.config.security.annotations.HasProductAccess;

@RestController
@RequestMapping("/api/products")
public class ProductController extends AbstractCRUDController<Product, ProductDTO, ProductService> {

    @Autowired
    public ProductController(ProductService service) {
        super(service);
    }

    @PostMapping
    public ProductDTO create(@Valid @RequestBody CreateProductDTO createProductDTO) {
        return service.create(createProductDTO).toDto();
    }

    @HasProductAccess
    @PutMapping("/{id}")
    public ProductDTO updateById(@Valid @RequestBody UpdateProductDTO updateProductDTO, @PathVariable Long id) {
        return service.updateById(id, updateProductDTO).toDto();
    }

    @HasProductAccess
    @PutMapping("/{id}/archive")
    public ProductDTO updateIsArchivedById(@RequestBody IsArchivedDTO isArchivedDTO, @PathVariable Long id) {
        return service.updateIsArchivedById(id, isArchivedDTO).toDto();
    }

    @GetMapping("/{id}/sprint-metrics/{startDate}")
    public TreeMap<LocalDate, List<SprintProductMetricsDTO>> getSprintMetrics(@PathVariable Long id, @PathVariable String startDate, @RequestParam(defaultValue = "14") Integer duration, @RequestParam(defaultValue = "10") Integer sprints) {
        return service.getSprintMetrics(id, LocalDate.parse(startDate), duration, sprints);
    }

}
