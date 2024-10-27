package com.project.bee_rushtech.controllers;

import com.project.bee_rushtech.dtos.ProductDTO;
import com.project.bee_rushtech.models.Product;
import com.project.bee_rushtech.services.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(@Valid @ModelAttribute ProductDTO productDTO,
                                           BindingResult result){
        try{
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Product newProduct = productService.createProduct(productDTO);
            // file is optional
            List<MultipartFile> files = productDTO.getFiles();
            files = files == null ? new ArrayList<MultipartFile>(): files;
            for(MultipartFile file:files){
                if(file.getSize() == 0) continue;
                //check the size and format of file
                if(file.getSize() >10*1024*1024){
//                throw new ResponseStatusException(
//                        HttpStatus.PAYLOAD_TOO_LARGE,"File is too large! Max size is 10MB");
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File is too large! Max size is 10MB");
                }
                //Get the format of file
                String contentType = file.getContentType();
                if(contentType == null || !contentType.startsWith("image/")){
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("File must be an image");

                }
                String filename = storeFile(file);
            }
            return ResponseEntity.ok("Product created successfully");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    private String storeFile(MultipartFile file) throws IOException {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        //Add UUID in forward of file name to make it unique
        String uniqueFilename = UUID.randomUUID().toString() +" "+ filename;
        //Path to folder storing file
        java.nio.file.Path uploadDir = Paths.get("upload");
        //Check and create folder if it doesnt exist
        if(!Files.exists(uploadDir)){
            Files.createDirectories(uploadDir);
        }
        //path to destination file
        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }
    //http://localhost:9090/api/v1/products?page=1&limit=10
    @GetMapping("")
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ){

        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") long productId){
        try{
            return ResponseEntity.ok(productService.getProductById(productId));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable long id){
        //giong voi ResponseEntity.ok()
        //return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully");
        //Dung cai binh thuong hay hon
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted with id " + id);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable long id
            ,@Valid @ModelAttribute ProductDTO productDTO){
        try{
            productService.updateProduct(id, productDTO);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Product with id "+id+" is updated");
    }
}
