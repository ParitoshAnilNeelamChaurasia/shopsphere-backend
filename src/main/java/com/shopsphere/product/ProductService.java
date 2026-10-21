package com.shopsphere.product;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ProductService {
    private final ProductRepository repository;
    public ProductService(ProductRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public ProductDtos.ProductPage search(String search, String category, String sort, int page, int pageSize) {
        int safePageSize = Math.min(Math.max(pageSize, 1), 50);
        int requestedPage = Math.max(page, 1);
        String normalizedSearch = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        String normalizedCategory = category == null ? "" : category.trim();
        List<Product> products = new ArrayList<>(repository.findAll());
//        products.removeIf(p -> (!normalizedSearch.isBlank() && !(p.getName()+" "+p.getBrand()+" "+p.getCategory()).toLowerCase(Locale.ROOT).contains(normalizedSearch))
//                || (!normalizedCategory.isBlank() && !p.getCategory().equalsIgnoreCase(normalizedCategory)));
        products.removeIf(p ->
                (!normalizedSearch.isBlank()
                        && !((
                        p.getName() + " " +
                                p.getBrand() + " " +
                                p.getCategory() + " " +
                                (p.getDescription() == null ? "" : p.getDescription())
                ).toLowerCase(Locale.ROOT).contains(normalizedSearch)))
                        || (!normalizedCategory.isBlank()
                        && !p.getCategory().equalsIgnoreCase(normalizedCategory))
        );
        Comparator<Product> comparator = Comparator.comparing(Product::getId);
        if ("price-low".equalsIgnoreCase(sort)) comparator = Comparator.comparing(Product::getPrice);
        else if ("price-high".equalsIgnoreCase(sort)) comparator = Comparator.comparing(Product::getPrice).reversed();
        else if ("rating".equalsIgnoreCase(sort)) comparator = Comparator.comparing(Product::getRating).reversed();
        else if ("newest".equalsIgnoreCase(sort)) comparator = Comparator.comparing(Product::getId).reversed();
        products.sort(comparator);

        long total = products.size();
        int totalPages = Math.max(1, (int) Math.ceil(total / (double) safePageSize));
        int actualPage = Math.min(requestedPage, totalPages);
        int start = (actualPage - 1) * safePageSize;
        List<ProductDtos.ProductResponse> content = products.stream().skip(start).limit(safePageSize).map(this::toResponse).toList();
        return new ProductDtos.ProductPage(content, total, totalPages, actualPage, safePageSize);
    }

    @Transactional(readOnly = true)
    public ProductDtos.ProductResponse getById(Long id) { return toResponse(repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found."))); }

    @Transactional
    public ProductDtos.ProductResponse create(ProductDtos.ProductRequest request) { return toResponse(repository.save(fromRequest(new Product(), request, null))); }

    @Transactional
    public ProductDtos.ProductResponse update(Long id, ProductDtos.ProductRequest request) {
        Product product = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found."));
        return toResponse(repository.save(fromRequest(product, request, id)));
    }

    @Transactional
    public void delete(Long id) { if (!repository.existsById(id)) throw new IllegalArgumentException("Product not found."); repository.deleteById(id); }

    public Product getEntity(Long id) { return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found.")); }

    private Product fromRequest(Product p, ProductDtos.ProductRequest r, Long id) {
        p.setName(r.name().trim()); p.setBrand(r.brand().trim()); p.setCategory(r.category().trim()); p.setPrice(r.price()); p.setOriginalPrice(r.originalPrice());
        p.setStock(r.stock()); p.setImage(r.image().trim()); p.setDescription(r.description()); p.setBadge(r.badge());
        String slugBase = r.name().trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        p.setSlug(id == null ? uniqueSlug(slugBase) : slugBase + "-" + id);
        p.setImages(new ArrayList<>(r.images() == null || r.images().isEmpty() ? List.of(r.image()) : r.images()));
        p.setSpecifications(r.specifications() == null ? new java.util.LinkedHashMap<>() : new java.util.LinkedHashMap<>(r.specifications()));
        if (id == null) { p.setRating(4.5); p.setReviews(0); }
        return p;
    }
    private String uniqueSlug(String base) { String slug = base; int n = 2; while (repository.existsBySlugIgnoreCase(slug)) slug = base + "-" + n++; return slug; }

    public ProductDtos.ProductResponse toResponse(Product p) {
        return new ProductDtos.ProductResponse(p.getId(), p.getName(), p.getSlug(), p.getBrand(), p.getCategory(), p.getPrice(), p.getOriginalPrice(), p.getRating(), p.getReviews(), p.getStock(), p.getImage(),
                List.copyOf(p.getImages()), p.getDescription(), p.getBadge(), Map.copyOf(p.getSpecifications()));
    }
}
