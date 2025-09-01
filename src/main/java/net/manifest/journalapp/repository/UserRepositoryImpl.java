package net.manifest.journalapp.repository;

import net.manifest.journalapp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Finds all users who are eligible for the weekly sentiment analysis email.
     * This checks for a valid email and that the user has enabled the feature.
     * @return A list of eligible User objects.
     */
    public List<User> getUserForSA() {
        Query query = new Query();

        // Criterion 1: User must have a valid, non-empty email address.
        Criteria emailCriteria = Criteria.where("email")
                .exists(true)
                .ne("")
                .regex(EMAIL_REGEX);

        // --- THIS IS THE CORRECTED LINE ---
        // Criterion 2: The 'sentimentAnalysisEnabled' field must be true.
        // We now use the correct field name from the User entity.
        Criteria sentimentCriteria = Criteria.where("sentimentAnalysisEnabled")
                .is(true);

        // Combine the criteria: a user must meet BOTH conditions.
        Criteria finalCriteria = new Criteria().andOperator(emailCriteria, sentimentCriteria);
        query.addCriteria(finalCriteria);

        // Execute the query against the 'users' collection.
        return mongoTemplate.find(query, User.class);
    }
}
