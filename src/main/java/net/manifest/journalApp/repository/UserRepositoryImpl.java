package net.manifest.journalApp.repository;


import net.manifest.journalApp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.schema.JsonSchemaObject;


import java.util.List;

public class UserRepositoryImpl {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";


      @Autowired
      private MongoTemplate mongoTemplate;

      public List<User> getUserForSA(){
        Query query = new Query();
          Criteria emailCriteria = Criteria.where("email")
                                            .exists(true)
                                            .ne("")
                                            .regex(EMAIL_REGEX);
          Criteria sentimentcriteria = Criteria.where("sentimentAnalysis")
                                            .is(true);
                                           // .type(JsonSchemaObject.Type.BsonType.BOOLEAN);
          Criteria criteria =new Criteria();
          Criteria finalCriteria = criteria.andOperator(emailCriteria, sentimentcriteria);

          query.addCriteria(finalCriteria);
          List<User> users = mongoTemplate.find(query, User.class);
        return users;

    }

    //Scheduler to send message on every Sunday 9.00 AM

}
