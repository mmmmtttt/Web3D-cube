package com.company.project.dao;
import com.company.project.core.Mapper;
import com.company.project.dto.Contributor;
import com.company.project.dto.Correct;
import com.company.project.dto.RecordDTO;
import com.company.project.model.Record;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RecordMapper extends Mapper<Record> {
    public List<RecordDTO> getRecords(String username);
    public List<Correct> getCorrect(String username);
    public Integer getAllTrial(String username);
    //出现多个参数时，需要加上@Param注解，不然会报错
    public Boolean findRecord(@Param("username")String username, @Param("roomid")Integer roomid, @Param("checkpoint")Integer checkpoint);
    public void addRecord(Record record);
    public void updateFailure(@Param("userId")Integer userId, @Param("roomId")Integer roomId, @Param("checkpoint")Integer checkpoint);
    public void updateSuccess(@Param("userId")Integer userId, @Param("roomId")Integer roomId, @Param("checkpoint")Integer checkpoint);
    public List<Contributor> getContributors(Integer roomId);
    public void welcomeVictory(Integer roomId);
}
