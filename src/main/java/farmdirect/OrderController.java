
package farmdirect;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderController(
            OrderRepository orderRepository,
            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> placeOrder(
            @RequestBody OrderRequest request) {

        if (request.customerName == null
                || request.customerName.isBlank()
                || request.phone == null
                || request.phone.isBlank()
                || request.deliveryAddress == null
                || request.deliveryAddress.isBlank()
                || request.items == null
                || request.items.isEmpty()) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Enter your details and add products to the cart."
                    ));
        }

        try {
            CustomerOrder order = new CustomerOrder();
            order.setCustomerName(request.customerName.trim());
            order.setPhone(request.phone.trim());
            order.setDeliveryAddress(
                    request.deliveryAddress.trim());

            double total = 0;

            for (OrderRequest.ItemRequest requestedItem
                    : request.items) {

                if (requestedItem.productId == null
                        || requestedItem.quantity <= 0
                        || requestedItem.quantity > 1000) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Invalid product or quantity.");
                }

                Product product = productRepository
                        .findById(requestedItem.productId)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "A product is unavailable."));

                if (!Double.isFinite(product.getPrice())
                        || product.getPrice() < 0) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "A product has an invalid price.");
                }

                if (product.getStock() < requestedItem.quantity) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Not enough stock available for "
                                    + product.getName()
                                    + ". Available stock: "
                                    + product.getStock() + " kg.");
                }

                double subtotal =
                        product.getPrice() * requestedItem.quantity;

                OrderItem item = new OrderItem();
                item.setProduct(product);
                item.setQuantity(requestedItem.quantity);
                item.setUnitPrice(product.getPrice());
                item.setSubtotal(subtotal);

                order.addItem(item);

                product.setStock(
                        product.getStock() - requestedItem.quantity);
                productRepository.save(product);

                total += subtotal;
            }

            order.setTotalAmount(total);

            CustomerOrder savedOrder =
                    orderRepository.save(order);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Order placed successfully!",
                            "orderId", savedOrder.getId(),
                            "totalAmount", savedOrder.getTotalAmount(),
                            "status", savedOrder.getStatus()
                    ));

        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (Exception exception) {
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "message",
                            "Could not place the order. Please try again."
                    ));
        }
    }
}
