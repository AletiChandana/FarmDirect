
package farmdirect;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/farmer/orders")
public class FarmerOrderController {

    private final OrderRepository orderRepository;

    public FarmerOrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public List<Map<String, Object>> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(order -> Map.<String, Object>of(
                        "orderId", order.getId(),
                        "customerName", order.getCustomerName(),
                        "phone", order.getPhone(),
                        "deliveryAddress", order.getDeliveryAddress(),
                        "totalAmount", order.getTotalAmount(),
                        "status", order.getStatus(),
                        "createdAt", order.getCreatedAt(),
                        "items", order.getItems().stream()
                                .map(item -> Map.<String, Object>of(
                                        "productName",
                                        item.getProduct().getName(),
                                        "quantity",
                                        item.getQuantity(),
                                        "unitPrice",
                                        item.getUnitPrice(),
                                        "subtotal",
                                        item.getSubtotal()
                                ))
                                .toList()
                ))
                .toList();
    }
}

