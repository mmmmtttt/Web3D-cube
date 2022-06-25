package com.company.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "room_id")
    private Integer roomId;

    private Integer checkpoint;

    private String tag;

    private Integer trial;

    @Column(name = "has_succeeded")
    private String hasSucceeded;

    private Integer deprecate;

    public Record(Integer userId,Integer roomId,Integer checkpoint,String tag,Integer trial,String hasSucceeded,Integer deprecate){
        this.checkpoint = checkpoint;
        this.deprecate = deprecate;
        this.tag = tag;
        this.userId = userId;
        this.roomId = roomId;
        this.trial = trial;
        this.hasSucceeded = hasSucceeded;
    }

}
