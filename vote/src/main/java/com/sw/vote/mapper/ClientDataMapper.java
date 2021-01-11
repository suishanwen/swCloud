package com.sw.vote.mapper;

import com.sw.vote.model.entity.ClientData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
@Mapper
public interface ClientDataMapper extends tk.mybatis.mapper.common.Mapper<ClientData> {


    @Select("SELECT\n" +
            " detail\n" +
            "FROM\n" +
            " client_data\n" +
            "WHERE\n" +
            " id = #{id}\n" +
            "AND date = #{date}")
    String selectDetail(@Param("id") String id, @Param("date") String date);

    @Select("SELECT\n" +
            " date,reward,ip,location\n" +
            "FROM\n" +
            " client_data\n" +
            "WHERE\n" +
            " id = #{id}\n" +
            "AND DATE_FORMAT(date,'%Y-%m')=#{date} order by date asc")
    List<ClientData> selectDetails(@Param("id") String id, @Param("date") String date);

    @Select("SELECT\n" +
            " user_id,\n" +
            " date,\n" +
            " sum( reward ) reward \n" +
            "FROM\n" +
            "  client_data \n" +
            "WHERE\n" +
            " DATE_FORMAT( date, '%Y-%m' )= #{date} \n" +
            "GROUP BY\n" +
            " user_id,\n" +
            " date")
    List<ClientData> selectMonthDetails(@Param("date") String date);

    @Select("SELECT\n" +
            " t.id,\n" +
            " t.count,\n" +
            " t.reward,\n" +
            " t.avg,\n" +
            " ext.area,\n" +
            " cd.user_id,\n" +
            " cd.sort_no \n" +
            "FROM\n" +
            " (\n" +
            " SELECT\n" +
            "  id,\n" +
            "  count( 1 ) count,\n" +
            "  sum( reward ) reward, \n" +
            "  sum( reward )/count( 1 ) avg \n" +
            " FROM\n" +
            "  client_data \n" +
            " WHERE\n" +
            "  date > DATE_SUB( CURDATE(), INTERVAL ${day} DAY ) \n" +
            " GROUP BY\n" +
            "  id \n" +
            " ) t\n" +
            " INNER JOIN client_direct cd ON cd.id = t.id \n" +
            " INNER JOIN client_direct_ext ext ON cd.id = ext.id \n" +
            "ORDER BY\n" +
            " t.avg ${order} \n" +
            " LIMIT ${limit}")
    List<Map> selectEliminatedDetails(@Param("day") int day, @Param("limit") int limit,@Param("order") String order);

    @Select("SELECT\n" +
            " a.sort_no,\n" +
            " b.id,\n" +
            " b.location,\n" +
            " b.reward\n" +
            "FROM\n" +
            " client_direct a\n" +
            "INNER JOIN client_data b ON a.id = b.id\n" +
            "AND b.date = #{date}\n" +
            "WHERE\n" +
            " a.user_id = #{userId}\n" +
            "ORDER BY\n" +
            " a.sort_no ASC")
    List<ClientData> selectDataByUserId(@Param("userId") String userId, @Param("date") String date);

    @Select("SELECT\n" +
            " user_id,\n" +
            " sort_no,\n" +
            " id,\n" +
            " location,\n" +
            " reward\n" +
            "FROM\n" +
            " client_data \n" +
            "AND date = #{date}\n" +
            "ORDER BY\n" +
            " sort_no ASC")
    List<ClientData> selectAllData(@Param("date") String date);

    @Select("SELECT\n" +
            " location \n" +
            "FROM\n" +
            " client_data \n" +
            "WHERE\n" +
            " id = #{id} \n" +
            "ORDER BY\n" +
            " date DESC \n" +
            " LIMIT 1")
    String selectLocationById(@Param("id") String id);

}

