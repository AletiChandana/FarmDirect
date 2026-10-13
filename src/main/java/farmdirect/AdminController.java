
package farmdirect;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AppUserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public AdminController(
            AppUserRepository userRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    // Dashboard statistics
    @GetMapping("/dashboard")
    public Map<String, Long> getDashboardStats() {
        return Map.of(
                "users", userRepository.count(),
                "products", productRepository.count(),
                "orders", orderRepository.count()
        );
    }

    // View all users
    @GetMapping("/users")
    public List<Map<String, Object>> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> Map.<String, Object>of(
                        "id", user.getId(),
                        "name", user.getName(),
                        "email", user.getEmail(),
                        "role", user.getRole()
                ))
                .toList();
    }

    // View all products
    @GetMapping("/products")
    public List<Map<String, Object>> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> Map.<String, Object>of(
                        "id", product.getId(),
                        "name", product.getName() == null ? "" : product.getName(),
                        "description", product.getDescription() == null ? "" : product.getDescription(),
                        "price", product.getPrice(),
                        "stock", product.getStock(),
                        "emoji", product.getEmoji() == null ? "" : product.getEmoji()
                ))
                .toList();
    }
}
