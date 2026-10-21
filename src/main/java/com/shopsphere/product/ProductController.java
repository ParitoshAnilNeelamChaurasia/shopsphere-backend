package com.shopsphere.product;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;
    public ProductController(ProductService service) { this.service = service; }
    @GetMapping public ProductDtos.ProductPage search(@RequestParam(required=false) String search,
                                                      @RequestParam(required=false) String category,
                                                      @RequestParam(required=false, defaultValue="featured") String sort,
                                                      @RequestParam(defaultValue="1") int page,
                                                      @RequestParam(defaultValue="8") int pageSize) {
        return service.search(search, category, sort, page, pageSize);
    }
    @GetMapping("/{id}") public ProductDtos.ProductResponse get(@PathVariable Long id) { return service.getById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ProductDtos.ProductResponse create(@Valid @RequestBody ProductDtos.ProductRequest request) { return service.create(request); }
    @PutMapping("/{id}") public ProductDtos.ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductDtos.ProductRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { service.delete(id); }
}
