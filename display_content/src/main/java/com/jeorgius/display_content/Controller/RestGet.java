package com.jeorgius.display_content.Controller;

import com.jeorgius.display_content.Controller.Logic.FullInfoDisplay;
import com.jeorgius.display_content.Controller.RestEntities.Display.FullGallery;
import com.jeorgius.display_content.Controller.RestEntities.Display.FullNews;
import com.jeorgius.display_content.Controller.RestEntities.Display.FullStoreItem;
import com.jeorgius.display_content.Controller.RestEntities.Display.FullUser;
import com.jeorgius.lib.entities.GalleryItem;
import com.jeorgius.lib.entities.NewsItem;
import com.jeorgius.lib.entities.Store.StoreItem;
import com.jeorgius.lib.entities.Users.User;
import com.jeorgius.lib.repos.GalleryRepo;
import com.jeorgius.lib.repos.NewsRepo;
import com.jeorgius.lib.repos.StoreRepo;
import com.jeorgius.lib.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RestGet {

    private NewsRepo newsRepo;
    private GalleryRepo galleryRepo;
    private UserRepo userRepo;
    private StoreRepo storeRepo;
    private FullInfoDisplay fullInfoDisplay;

    public RestGet() {}

    @Autowired
    public RestGet(NewsRepo newsRepo,
                   GalleryRepo galleryRepo,
                   UserRepo userRepo,
                   StoreRepo storeRepo,
                   FullInfoDisplay fullInfoDisplay) {
        this.newsRepo = newsRepo;
        this.galleryRepo = galleryRepo;
        this.userRepo = userRepo;
        this.storeRepo = storeRepo;
        this.fullInfoDisplay = fullInfoDisplay;
    }


    @GetMapping("/news")
    public List<NewsItem> renderNewsSection() {
        return newsRepo.findAll();
    }

    @GetMapping("/news/{newsItem}")
    public FullNews renderNewsItem(@PathVariable("newsItem") String itemId) {
        return fullInfoDisplay.renderFullNews(itemId);
    }

    @GetMapping("/gallery")
    public List<GalleryItem> renderGallerySection() {
        return galleryRepo.findAll();
    }

    @GetMapping("/gallery/{galleryItem}")
    public FullGallery renderGalleryItem(@PathVariable("galleryItem") String itemId) {
        return fullInfoDisplay.renderFullGalleryItem(itemId);
    }

    @GetMapping("/users")
    public List<User> renderUserList() {
        return userRepo.findAll();
    }

    @PutMapping("/users/{userId}")
    public FullUser renderUser(@PathVariable("userId") String userId) {
        return fullInfoDisplay.renderFullUser(userId);
    }

    @GetMapping("/store")
    public List<StoreItem> renderStore() {
        return storeRepo.findAll();
    }

    @GetMapping("/store/{itemId}")
    public FullStoreItem renderItem(@PathVariable("itemId") String itemId) {
        return fullInfoDisplay.renderStoreItem(itemId);
    }

}

