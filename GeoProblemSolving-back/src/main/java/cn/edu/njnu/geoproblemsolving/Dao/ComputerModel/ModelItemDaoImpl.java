package cn.edu.njnu.geoproblemsolving.Dao.ComputerModel;

import cn.edu.njnu.geoproblemsolving.Entity.ModelItem.ModelItemEntity;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.List;

@Component
public class ModelItemDaoImpl implements ModelItemDao {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public ModelItemDaoImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Object readComputableModel(String oid) {
        Query query = Query.query(Criteria.where("oid").is(oid));
        if (mongoTemplate.find(query, ModelItemEntity.class).isEmpty()) {
            return "None";
        } else {
            List<ModelItemEntity> modelEntity = mongoTemplate.find(query, ModelItemEntity.class);
            return modelEntity;
        }
    }

    @Override
    public String updateComputableModel(JSONObject obj){
            ModelItemEntity modelItemEntity = JSONObject.toJavaObject(obj,ModelItemEntity.class);
            mongoTemplate.save(modelItemEntity,"CModel");//save: 若新增数据的主键已经存在，则会对当前已经存在的数据进行修改操作。
            return "Suc";
    }
}
