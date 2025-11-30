package com.subscription.core.graphql;

import com.subscription.core.entity.Product;
import com.subscription.core.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Objects;

/**
 * 🎓 STEP 3: GraphQL Query Resolvers
 * 
 * Think of this as a Controller, but for GraphQL!
 * 
 * KEY CONCEPTS:
 * 1. @Controller = This is a Spring component
 * 2. @QueryMapping = Maps to "query { ... }" in schema
 * 3. Method name MUST match schema field name
 * 
 * COMPARISON TO REST:
 * 
 * REST:
 * @GetMapping("/products/{id}")
 * public Product getProduct(@PathVariable String id)
 * 
 * GraphQL:
 * 
 * @QueryMapping
 *               public Product product(@Argument String id)
 * 
 *               Both do the same thing, but GraphQL is more flexible!
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ProductGraphQLController {

    private final ProductRepository productRepository;

    /**
     * 🎯 Query Resolver: product(id: ID!)
     * 
     * Handles queries like:
     * {
     * product(id: "prod-123") {
     * name
     * price
     * }
     * }
     * 
     * HOW IT WORKS:
     * 1. GraphQL sees "product" in the query
     * 2. Calls this method with id argument
     * 3. We return a Product object
     * 4. GraphQL extracts ONLY the fields client requested (name, price)
     * 
     * LEARNING: Notice we return the FULL Product entity!
     * GraphQL automatically filters it based on what client asked for.
     * This is the MAGIC - no need for different DTOs!
     */
    @QueryMapping // Maps to: product(id: ID!) in schema
    public Product product(@Argument String id) {
        log.info("[GraphQL] Fetching product by ID: {}", id);

        // Same as REST - just fetch from database
        return productRepository.findById(id).orElse(null);

        // If product not found, GraphQL returns null in response:
        // { "data": { "product": null } }
    }

    /**
     * 🎯 Query Resolver: products
     * 
     * Handles queries like:
     * {
     * products {
     * name
     * price
     * brand
     * }
     * }
     * 
     * WHY IS THIS BETTER THAN REST?
     * 
     * REST: GET /api/products
     * - Always returns ALL 15 fields for EVERY product
     * - 100 products = 100 × 15 fields = 1500 fields sent
     * 
     * GraphQL:
     * - Client asks for only 3 fields (name, price, brand)
     * - 100 products = 100 × 3 fields = 300 fields sent
     * - 80% less data!
     * 
     * Same database query, but less network payload!
     */
    @QueryMapping // Maps to: products in schema
    public List<Product> products() {
        log.info("[GraphQL] Fetching all products");

        // Fetch all products from database
        List<Product> allProducts = productRepository.findAll();

        // We return ALL products with ALL fields
        // But GraphQL will only send what client requested!
        return allProducts;

        // Example response if client asked { products { name price } }:
        // {
        // "data": {
        // "products": [
        // { "name": "Milk", "price": 60.0 },
        // { "name": "Bread", "price": 40.0 }
        // ]
        // }
        // }
    }

    // ═══════════════════════════════════════════════════════
    // 🎓 STEP 4: NESTED FIELD RESOLVERS
    // ═══════════════════════════════════════════════════════

    // These resolvers handle NESTED fields (objects within objects)
    //
    // IMPORTANT: These are called ONLY IF the client requests the field!
    //
    // Example:
    // Query 1: { product(id: "123") { name price } }
    // → category() resolver NOT CALLED
    //
    // Query 2: { product(id: "123") { name price category { name } } }
    // → category() resolver IS CALLED

    private final com.subscription.core.repository.CategoryRepository categoryRepository;
    private final com.subscription.core.repository.DiscountTypeRepository discountTypeRepository;

    /**
     * 🔗 Field Resolver: Product.category
     * 
     * This resolver is called ONLY when client requests the "category" field.
     * 
     * HOW IT WORKS:
     * 1. GraphQL calls product() and gets a Product object
     * 2. If query includes "category { ... }", GraphQL calls this method
     * 3. We receive the Product as a parameter
     * 4. We fetch the Category using product.getCategoryId()
     * 5. GraphQL adds category to the response
     * 
     * WHY @SchemaMapping?
     * - @QueryMapping = Root query (product, products)
     * - @SchemaMapping = Nested field (Product.category)
     * 
     * COMPARISON TO REST:
     * 
     * REST:
     * - You MUST fetch category in product() method
     * - OR create a separate endpoint /products/{id}/with-category
     * - Client gets category whether they need it or not
     * 
     * GraphQL:
     * - Fetch category ONLY if requested
     * - No separate endpoint needed
     * - Client gets exactly what they ask for
     */
    @SchemaMapping(typeName = "Product", field = "category")
    public com.subscription.core.entity.Category category(Product product) {
        // product = The parent Product object

        // Check if product has a category
        if (Objects.isNull(product.getCategoryId())) {
            log.debug("[GraphQL] Product {} has no category", product.getProductId());
            return null; // GraphQL will return category: null
        }

        log.info("[GraphQL] Fetching category {} for product {}",
                product.getCategoryId(), product.getProductId());

        // Fetch category from database
        return categoryRepository.findById(product.getCategoryId()).orElse(null);

        // 🎯 KEY LEARNING:
        // This query runs ONCE per product!
        // If you fetch 10 products, this runs 10 times = N+1 problem
        // We'll fix this with DataLoader in the next step!
    }

    /**
     * 🔗 Field Resolver: Product.discount
     * 
     * Same concept as category(), but for DiscountType.
     * 
     * EXAMPLE CLIENT QUERY:
     * {
     * product(id: "123") {
     * name
     * price
     * discount {
     * discountName
     * value
     * }
     * }
     * }
     * 
     * RESPONSE:
     * {
     * "data": {
     * "product": {
     * "name": "Fresh Milk",
     * "price": 60.0,
     * "discount": {
     * "discountName": "Diwali Sale",
     * "value": 15.0
     * }
     * }
     * }
     * }
     * 
     * Notice: ALL this data (product + category + discount)
     * came from ONE HTTP request to /graphql!
     */
    @SchemaMapping(typeName = "Product", field = "discount")
    public com.subscription.core.entity.DiscountType discount(Product product) {
        if (Objects.isNull(product.getDiscountTypeId())) {
            log.debug("[GraphQL] Product {} has no discount", product.getProductId());
            return null;
        }

        log.info("[GraphQL] Fetching discount {} for product {}",
                product.getDiscountTypeId(), product.getProductId());

        return discountTypeRepository.findById(product.getDiscountTypeId()).orElse(null);
    }

    // 🎓 WHAT HAVE WE LEARNED?
    //
    // 1. @QueryMapping = Entry point (product, products)
    // 2. @SchemaMapping = Nested field (Product.category, Product.discount)
    // 3. Nested resolvers run ONLY if client requests the field
    // 4. Same Product entity, different responses based on query
    //
    // NEXT PROBLEM:
    // If you fetch 100 products with categories:
    // - 1 query for products
    // - 100 queries for categories (one per product)
    // = 101 queries total (N+1 problem!)
    //
    // SOLUTION: DataLoader (batching)
    // We'll implement this in the next step!
}
