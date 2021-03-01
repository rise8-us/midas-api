package mil.af.abms.midas.api.product;

import java.util.Optional;

import mil.af.abms.midas.api.RepositoryInterface;
import mil.af.abms.midas.api.product.dto.ProductDTO;

public interface ProductRepository extends RepositoryInterface<ProductEntity, ProductDTO> {

    Optional<ProductEntity> findByName(String name);

}
