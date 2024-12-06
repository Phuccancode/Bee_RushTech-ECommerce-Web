//package com.project.bee_rushtech.controllers;
//
//import com.project.bee_rushtech.utils.annotation.IgnoreRestResponse;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.UrlResource;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.nio.file.Paths;
//
//@RestController
//@RequestMapping("${api.prefix}/images")
//@IgnoreRestResponse
//public class ImageController {
//    @GetMapping("/{imageName}")
//    public ResponseEntity<Resource> viewImage(@PathVariable String imageName) {
//        try {
//            java.nio.file.Path imagePath = Paths.get("upload/"+imageName);
//            UrlResource resource = new UrlResource(imagePath.toUri());
//
//            if (resource.exists()) {
//                return ResponseEntity.ok()
//                        .contentType(MediaType.IMAGE_JPEG)
//                        .body(resource);
//            } else {
//                return ResponseEntity.ok()
//                        .contentType(MediaType.IMAGE_JPEG)
//                        .body(new UrlResource(Paths.get("upload/notfound.jpeg").toUri()));
//                //return ResponseEntity.notFound().build();
//            }
//        } catch (Exception e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//}
