package com.example.travelleronline.posts;

import com.example.travelleronline.categories.Category;
import com.example.travelleronline.users.User;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @Column
    private String title;
    @Column
    private LocalDateTime dateOfUpload;
    @Column
    private String clipUri;
    @Column
    private String description;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category categoryId;
    @Column
    private float locationLatitude;
    @Column
    private float locationLongitude;


    //TODO pictures, likes/unlikes, dislikes/undislikes,

}
