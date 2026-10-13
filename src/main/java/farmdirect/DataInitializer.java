
package farmdirect;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {

            productRepository.save(
                    new Product("Tomatoes", "Fresh farm tomatoes", 30, "🍅")
            );

            productRepository.save(
                    new Product("Potatoes", "Fresh farm potatoes", 25, "🥔")
            );

            productRepository.save(
                    new Product("Carrots", "Fresh and crunchy carrots", 40, "🥕")
            );

            productRepository.save(
                    new Product("Apples", "Fresh apples", 120, "🍎")
            );

            System.out.println("Sample products added to database!");
        }
    }
}

