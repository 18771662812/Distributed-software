package org.example.shop.Controller;

import org.example.shop.common.Result;
import org.example.shop.entity.Product;
import org.example.shop.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 获取商品详情（带缓存）
     */
    @GetMapping("/{id}")
    public Result<Product> getProductDetail(@PathVariable Long id) {
        Product product = productService.getProductDetail(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }

    /**
     * 获取所有商品
     */
    @GetMapping("/list")
    public Result<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return Result.success(products);
    }

    /**
     * 创建商品
     */
    @PostMapping
    public Result<String> createProduct(@RequestBody Product product) {
        boolean success = productService.createProduct(product);
        if (success) {
            return Result.success("商品创建成功");
        }
        return Result.error("商品创建失败");
    }

    /**
     * 更新商品
     */
    @PutMapping("/{id}")
    public Result<String> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        boolean success = productService.updateProduct(product);
        if (success) {
            return Result.success("商品更新成功");
        }
        return Result.error("商品更新失败");
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteProduct(@PathVariable Long id) {
        boolean success = productService.deleteProduct(id);
        if (success) {
            return Result.success("商品删除成功");
        }
        return Result.error("商品删除失败");
    }
}
