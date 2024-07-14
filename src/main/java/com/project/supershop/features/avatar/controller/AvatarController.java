package com.project.supershop.features.avatar.controller;


import com.project.supershop.common.ResultResponse;
import com.project.supershop.features.avatar.services.AvatarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/avatar")
public class AvatarController {
    private final String FEMALE_DEFAULT_AVATAR = "src/main/java/com/project/supershop/access/images/defaultAvatar/femaleAvatar/female.png";
    private final String MALE_DEFAULT_AVATAR = "src/main/java/com/project/supershop/access/images/defaultAvatar/maleAvatar/male.png";
    private final AvatarService avatarService;

    public AvatarController (AvatarService avatarService){
        this.avatarService = avatarService;
    }

    @GetMapping("/images/{filename:.+")
    public ResponseEntity<ResultResponse<?>> serveAvatar(@PathVariable String fileName){
        try{
            String filePath;

        }
    }

}
