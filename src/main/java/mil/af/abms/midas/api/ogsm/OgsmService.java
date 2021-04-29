package mil.af.abms.midas.api.ogsm;

import javax.transaction.Transactional;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.assertion.AssertionService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.ogsm.dto.CreateOgsmDTO;
import mil.af.abms.midas.api.ogsm.dto.OgsmDTO;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.user.UserService;

@Service
public class OgsmService extends AbstractCRUDService<Ogsm, OgsmDTO, OgsmRepository> {

    private UserService userService;
    private ProductService productService;
    private AssertionService assertionService;

    @Autowired
    public OgsmService(OgsmRepository repository) {
        super(repository, Ogsm.class, OgsmDTO.class);
    }

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }
    @Autowired
    void setProductService(ProductService productService) { this.productService = productService; }
    @Autowired
    void setAssertionService(AssertionService assertionService) { this.assertionService = assertionService; }

    @Transactional
    public Ogsm create(CreateOgsmDTO createOgsmDTO) {
        Ogsm newOgsm = Builder.build(Ogsm.class)
                .with(t -> t.setCreatedBy(userService.getUserBySecContext()))
                .with(o -> o.setProduct(productService.getObject(createOgsmDTO.getProductId())))
                .get();
        Ogsm savedOgsm = repository.save(newOgsm);

        savedOgsm.setAssertions(
                createOgsmDTO.getAssertionDTOs().stream().map(a -> {
                        a.setOgsmId(savedOgsm.getId());
                        return assertionService.create(a);
                }).collect(Collectors.toSet())
        );

        return savedOgsm;
    }


}
