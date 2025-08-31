package net.manifest.journalapp.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@Tag(name="Public APIs",description = "API health-check" )
public class PublicController {

    //HEALTH CHECK OF API
    @GetMapping("/health-check")
    @Operation(summary = "Health Check of our apis of the app")
        public ResponseEntity<String> healthCheck(){
        return  new ResponseEntity<>("OK🆗",HttpStatus.OK);
    }

}
