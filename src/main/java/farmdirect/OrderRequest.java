
package farmdirect;

import java.util.List;

public class OrderRequest {

    public String customerName;
    public String phone;
    public String deliveryAddress;
    public List<ItemRequest> items;

    public static class ItemRequest {
        public Long productId;
        public int quantity;
    }
}

