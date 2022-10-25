package com.example.travelleronline.comments;

import com.example.travelleronline.posts.Post;
import com.example.travelleronline.reactions.toPost.PostReaction;
import com.example.travelleronline.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Integer> {
}