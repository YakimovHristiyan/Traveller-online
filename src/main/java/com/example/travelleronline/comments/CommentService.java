package com.example.travelleronline.comments;

import com.example.travelleronline.comments.dtos.CommentRequestDTO;
import com.example.travelleronline.comments.dtos.CommentWithParentDTO;
import com.example.travelleronline.general.exceptions.BadRequestException;
import com.example.travelleronline.general.exceptions.UnauthorizedException;
import com.example.travelleronline.posts.Post;
import com.example.travelleronline.reactions.dto.LikesDislikesDTO;
import com.example.travelleronline.reactions.toComment.CommentReaction;
import com.example.travelleronline.users.User;
import com.example.travelleronline.users.dtos.UserIdNamesPhotoDTO;
import com.example.travelleronline.general.MasterService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService extends MasterService {

    private static final int COMMENT_CONTENT_LENGTH_MIN = 5;
    private static final int COMMENT_CONTENT_LENGTH_MAX = 500;

    CommentWithParentDTO createComment(int pid, CommentRequestDTO dto, int uid) {
        validateCommentContent(dto);
        Post post = getPostById(pid);
        User user = getVerifiedUserById(uid);
        Comment comment = new Comment();
        comment.setCreatedAt(LocalDateTime.now());
        comment.setContent(dto.getContent());
        comment.setUser(user);
        comment.setPost(post);
        commentRepository.save(comment);
        return modelMapper.map(comment, CommentWithParentDTO.class);
    }

    void editComment(int cid, CommentRequestDTO dto, int uid) {
        Comment existingComment = getCommentById(cid);
        validateOwnerOfComment(uid, existingComment);
        validateCommentContent(dto);
        existingComment.setContent(dto.getContent());
        commentRepository.save(existingComment);
    }

    void deleteComment(int cid, int uid) {
        Comment comment = getCommentById(cid);
        Post post = comment.getPost();
        if(comment.getUser().getUserId() == uid || post.getOwner().getUserId() == uid) {
            commentRepository.deleteById(cid);
        }
        else {
            throw new BadRequestException("You must be post's owner or comment's owner " +
                    "in order to delete this comment.");
        }
    }

    void deleteAllComments(int pid, int uid) {
        Post post = getPostById(pid);
        if(post.getOwner().getUserId() != uid) {
            throw new UnauthorizedException("You must be post's owner in order to delete all comments.");
        }
        commentRepository.deleteAll();
    }

    CommentWithParentDTO respondToComment(int cid, int uid, CommentRequestDTO dto) {
        Comment comment = getCommentById(cid);
        if (comment.getParent() != null) {
            throw new BadRequestException("Comment of a comment cannot be commented.");
        }
        Post post = comment.getPost();
        User user = getVerifiedUserById(uid);
        Comment response = new Comment();
        response.setPost(post);
        response.setCreatedAt(LocalDateTime.now());
        response.setContent(dto.getContent());
        response.setUser(user);
        response.setParent(comment);
        commentRepository.save(response);
        return modelMapper.map(response, CommentWithParentDTO.class);
    }

    LikesDislikesDTO reactTo(int uid, int cid, String reaction) {
        User user = getVerifiedUserById(uid);
        Comment comment = getCommentById(cid);
        CommentReaction commentReaction = new CommentReaction();
        commentReaction.setUser(user);
        commentReaction.setComment(comment);
        switch (reaction) {
            case "like" -> commentReaction.setLike(true);
            case "dislike" -> commentReaction.setLike(false);
            default -> throw new BadRequestException("Unknown value for parameter \"reaction\".");
        }
        updateCommentReaction(user, comment, commentReaction);
        return getLikesAndDislikes(comment);
    }

    List<UserIdNamesPhotoDTO> getUsersWhoReacted(int cid, String reaction) {
        Comment comment = getCommentById(cid);
        switch (reaction) {
            case "like" -> {
                return comment.getCommentReactions().stream()
                        .filter(cr -> cr.isLike())
                        .map(cr -> modelMapper.map(cr.getUser(), UserIdNamesPhotoDTO.class))
                        .collect(Collectors.toList());
            }
            case "dislike" -> {
                return comment.getCommentReactions().stream()
                        .filter(cr -> !cr.isLike())
                        .map(cr -> modelMapper.map(cr.getUser(), UserIdNamesPhotoDTO.class))
                        .collect(Collectors.toList());
            }
            default -> throw new BadRequestException("Unknown value for parameter \"reaction\".");
        }
    }

    private void updateCommentReaction(User user, Comment comment, CommentReaction commentReaction) {
        List<CommentReaction> reactionsSameCommentAndUser =
                commentReactRepo.findAllByUserAndComment(user, comment);
        if (reactionsSameCommentAndUser.size() == 0) {
            commentReactRepo.save(commentReaction);
        }
        else {
            CommentReaction oldCommentReaction = reactionsSameCommentAndUser.get(0);
            commentReactRepo.delete(oldCommentReaction);
            if (oldCommentReaction.isLike() != commentReaction.isLike()) {
                commentReactRepo.save(commentReaction);
            }
        }
    }

    private LikesDislikesDTO getLikesAndDislikes(Comment comment) {
        LikesDislikesDTO dto = new LikesDislikesDTO();
        int likes = comment.getCommentReactions().stream()
                .filter(pr -> pr.isLike()).toList()
                .size();
        dto.setLikes(likes);
        int dislikes = comment.getCommentReactions().size() - likes;
        dto.setDislikes(dislikes);
        return dto;
    }

    private void validateOwnerOfComment(int uid, Comment comment) {
        if(comment.getUser().getUserId() != uid) {
            throw new UnauthorizedException("Only the owner of the comment can edit the comment.");
        }
    }

    private void validateCommentContent(CommentRequestDTO dto) {
        if (dto.getContent().length() < COMMENT_CONTENT_LENGTH_MIN ||
            dto.getContent().length() > COMMENT_CONTENT_LENGTH_MAX) {
            throw new BadRequestException("Comment size must be between " + COMMENT_CONTENT_LENGTH_MIN +
                    " and " + COMMENT_CONTENT_LENGTH_MAX + " letters.");
        }
    }

}