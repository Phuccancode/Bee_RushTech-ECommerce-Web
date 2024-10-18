package com.project.Bee_RushTech.controllers;

import com.project.Bee_RushTech.dtos.ProductDTO;
import jakarta.validation.Valid;
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
public class ProductController {
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(@Valid @ModelAttribute ProductDTO productDTO,
//                                                @RequestPart("file") MultipartFile file,
                                           BindingResult result){
        try{
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
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
                //Lưu vào bảng product_images
//                {
//                    "name": "Macbook Air 15 inch 2024",
//                        "price": 812,
//                        "thumbnail": "",
//                        "description": "This is a test product",
//                        "category_id": 1
//                }
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
    public ResponseEntity<String> getProducts(
//            @RequestParam("page") int page,
//            @RequestParam("limit") int limit
    ){
        return ResponseEntity.ok("Get Product here");
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getProductById(@PathVariable("id") long productId){
        return ResponseEntity.ok("Product with Id " + productId);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable long id){
        //giong voi ResponseEntity.ok()
        //return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully");
        //Dung cai binh thuong hay hon
        return ResponseEntity.ok("Product deleted with id " + id);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable long id
            , @ModelAttribute ProductDTO productDTO){


        return ResponseEntity.ok("Product with id "+id+" is updated");
    }
}
