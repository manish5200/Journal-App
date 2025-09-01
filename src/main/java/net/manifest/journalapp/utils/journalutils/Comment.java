package net.manifest.journalapp.utils.journalutils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @NonNull
    private ObjectId userId;
    @NonNull
    private String username;
    @NonNull
    private String text;
    private LocalDateTime createdAt;

}
