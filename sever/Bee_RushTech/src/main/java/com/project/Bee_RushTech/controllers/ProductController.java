package com.project.Bee_RushTech.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/product")
public class ProductController {
    @GetMapping("")
    ResponseEntity<String> getProducts(){
        return ResponseEntity.ok("Call API success");
    }
    @GetMapping("/{id}")
    ResponseEntity<String> getProductById(@PathVariable("id") String productId){
        return ResponseEntity.ok("Product with Id " + productId);
    }
}
