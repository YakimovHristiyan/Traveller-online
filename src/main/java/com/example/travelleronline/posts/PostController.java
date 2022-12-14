package com.example.travelleronline.posts;

import com.example.travelleronline.posts.dtos.PostEditDTO;
import com.example.travelleronline.posts.dtos.PostFilterDTO;
import com.example.travelleronline.posts.dtos.PostDTO;
import com.example.travelleronline.reactions.dto.LikesDislikesDTO;
import com.example.travelleronline.users.dtos.UserIdNamesPhotoDTO;
import com.example.travelleronline.posts.dtos.PostCreationDTO;
import com.example.travelleronline.general.MasterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class PostController extends MasterController {

    @Autowired
    private PostService postService;

    @PostMapping("/posts")
    public PostCreationDTO createPost(@RequestBody PostCreationDTO dto, HttpSession session) {
        int uid = getUserId(session);
        return postService.createPost(dto, uid);
    }

    @GetMapping("/posts/{pid}")
    public PostDTO getPostById(@PathVariable int pid) {
        return postService.getPost(pid);
    }

    @GetMapping("/posts/categories")
    public List<PostFilterDTO> getPostsByCategory(@RequestParam String category,
                                                  @RequestParam int pageNumber,
                                                  @RequestParam int rowsNumber) {
        return postService.getPostsByCategory(category, pageNumber, rowsNumber);
    }

    @PutMapping("/posts/{pid}")
    public void editPost(@PathVariable int pid, @RequestBody PostEditDTO dto,
                         HttpSession session) {
        int uid = getUserId(session);
        postService.editPost(pid, dto, uid);
    }

    @DeleteMapping("/posts/{pid}")
    public void deletePostById(@PathVariable int pid, HttpSession session) {
        int uid = getUserId(session);
        postService.deletePostById(pid, uid);
    }

    // removes tag after second visit
    @PostMapping("/posts/{pid}/tag/{uid}")
    public void tagUserToPost(@PathVariable int pid, @PathVariable int uid,
                              HttpSession session) {
        int sessionUserId = getUserId(session);
        postService.tagUserToPost(pid, uid, sessionUserId);
    }

    // removes old reaction after following visit
    @PutMapping("/posts/{pid}/react")
    public LikesDislikesDTO reactTo(@PathVariable int pid,
                                    @RequestParam("reaction") String reaction,
                                    HttpSession session) {
        int uid = getUserId(session);
        return postService.reactTo(uid, pid, reaction);
    }

    @GetMapping("/posts/{pid}/users")
    public List<UserIdNamesPhotoDTO> getUsersWhoReacted(@PathVariable int pid,
                                                        @RequestParam("reaction") String reaction) {
        return postService.getUsersWhoReacted(pid, reaction);
    }

    @GetMapping("/posts/filter")
    public List<PostFilterDTO> filterPosts(@RequestParam String searchBy,
                                           @RequestParam String value, @RequestParam String orderBy,
                                           @RequestParam int pageNumber, @RequestParam int rowsNumber) {
        return postService.filterPosts(searchBy, value, orderBy, pageNumber, rowsNumber);
    }

    // News Feed
    @GetMapping
    public List<PostFilterDTO> showNewsFeed(HttpSession session,
                                      @RequestParam int pageNumber,
                                      @RequestParam int rowsNumber) {
        return postService.showNewsFeed(getUserId(session), pageNumber, rowsNumber);
    }

    // Profile Page
    @GetMapping("/users/{uid}/posts")
    public List<PostFilterDTO> getAllPostsOfUser(@PathVariable int uid,
                                            @RequestParam int pageNumber,
                                            @RequestParam int rowsNumber) {
        return postService.getPostsOfUser(uid, pageNumber, rowsNumber);
    }

}