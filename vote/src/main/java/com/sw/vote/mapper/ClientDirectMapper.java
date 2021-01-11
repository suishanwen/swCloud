package com.sw.vote.mapper;

import com.sw.vote.aspect.LogCost;
import com.sw.vote.model.entity.ClientDirect;
import com.sw.vote.model.entity.ClientDirectExt;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface ClientDirectMapper extends tk.mybatis.mapper.common.Mapper<ClientDirect> {
    @LogCost(name = "同步数据-数据库")
    void batchUpdate(@Param("list") List<ClientDirect> list);


    @Select("<script> \n" +
            "SELECT\n" +
            " cd.id,\n" +
            " ext.adsl_user,\n" +
            " ext.adsl_pwd \n" +
            "FROM\n" +
            " client_direct cd\n" +
            " LEFT JOIN client_direct_ext ext ON cd.id = ext.id " +
            "WHERE \n" +
            "    cd.user_id = #{userId}\n" +
            "AND cd.sort_no = #{sortNo}\n" +
            "</script>")
    ClientDirectExt selectExtByUser(@Param("userId") String userId, @Param("sortNo") int sortNo);

    @Select("SELECT\n" +
            " count( id ) \n" +
            "FROM\n" +
            " client_direct \n" +
            "WHERE\n" +
            " user_id = #{userId};")
    int selectUserCount(@Param("userId") String userId);


    @Select("<script> \n" +
            "SELECT direct\n" +
            "FROM client_direct\n" +
            "WHERE \n" +
            "    id = #{id}\n" +
            "</script>")
    String selectDirectById(@Param("id") String id);

    @Select("SELECT\n" +
            " count(DISTINCT version)\n" +
            "FROM\n" +
            " client_direct")
    int checkVersion();

    @Select("SELECT\n" +
            " max(version) version\n" +
            "FROM\n" +
            " client_direct")
    String selectLatestVersion();

    @Update("UPDATE client_direct\n" +
            "SET direct = 'TASK_SYS_UPDATE'\n" +
            "WHERE\n" +
            " version <> #{version}")
    void updateLatestVersion(@Param("version") String version);

    @Insert("<script> \n" +
            "INSERT INTO\n" +
            " client_direct(id,user_id,sort_no)\n" +
            "values (#{id},#{userId},#{sortNo}) \n" +
            "</script>")
    void insertClient(@Param("id") String id, @Param("userId") String userId, @Param("sortNo") int sortNo);


    @Update("<script> \n" +
            "UPDATE \n" +
            "    client_direct\n" +
            "SET  version= #{version}\n" +
            "WHERE \n" +
            "    id = #{id}\n" +
            "</script>")
    void setVersion(@Param("id") String id, @Param("version") String version);

    @SuppressWarnings("unused")
    @Update("<script> \n" +
            "UPDATE \n" +
            "    client_direct\n" +
            "SET  instance= #{instance}\n" +
            "WHERE \n" +
            "    id = #{id}\n" +
            "</script>")
    void setInstance(@Param("id") String id, @Param("instance") String instance);

    @Update("<script> \n" +
            "UPDATE \n" +
            "    client_direct\n" +
            "SET  direct=''\n" +
            "WHERE \n" +
            "    id = #{id}\n" +
            "AND direct = #{direct}\n" +
            "</script>")
    void confirmDirect(@Param("id") String id, @Param("direct") String direct);


    @Select("<script> \n" +
            "SELECT\n" +
            "   id,\n" +
            "   sort_no,\n" +
            "   instance,\n" +
            "   project_name,\n" +
            "   success,\n" +
            "   reward,\n" +
            "   update_time,\n" +
            "   direct,\n" +
            "   version\n" +
            "FROM\n" +
            "    client_direct\n" +
            "WHERE \n" +
            "    user_id = #{userId}\n" +
            "ORDER BY\n" +
            "    sort_no\n" +
            "</script>")
    List<ClientDirect> selectByUserId(@Param("userId") String userId);

    @Select("<script> \n" +
            "SELECT\n" +
            "   *\n" +
            "FROM\n" +
            "    client_direct\n" +
            "ORDER BY\n" +
            "    user_id,sort_no\n" +
            "</script>")
    List<ClientDirect> selectAllCient();


    @Update("<script> \n" +
            "UPDATE\n" +
            "    client_direct\n" +
            "SET direct = #{direct}\n" +
            "WHERE \n" +
            "    id = #{id}\n" +
            "</script>")
    void updateDirect(@Param("id") String id, @Param("direct") String direct);


    @Update("<script> \n" +
            "UPDATE\n" +
            "    client_direct\n" +
            "SET direct = #{direct}\n" +
            "</script>")
    void updateDirectAll( @Param("direct") String direct);


    @Select("SELECT\n" +
            " count( id ) \n" +
            "FROM\n" +
            " client_direct \n" +
            "WHERE\n" +
            " update_time >= CURRENT_TIMESTAMP - INTERVAL 15 MINUTE;")
    int selectActive();

    @Select("SELECT concat(用户id,'-',序号) 用户,机器名,区域,开通时间,过期时间 FROM vm_info")
    List<LinkedHashMap<String,Object>> selectVmInfo();
}

