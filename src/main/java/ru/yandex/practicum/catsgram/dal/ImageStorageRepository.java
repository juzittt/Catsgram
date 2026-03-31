package ru.yandex.practicum.catsgram.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.model.Image;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class ImageStorageRepository extends BaseRepository<Image> {
    private static final String FIND_BY_POST_ID = "SELECT * FROM image_storage WHERE post_id = ?";
    private static final String FIND_BY_ID = "SELECT * FROM image_storage WHERE id = ?";
    private static final String INSERT_QUERY = """
        INSERT INTO image_storage (post_id, original_file_name, file_path, upload_date)
        VALUES (?, ?, ?, ?) RETURNING id
        """;

    public ImageStorageRepository(JdbcTemplate jdbc, RowMapper<Image> mapper) {
        super(jdbc, mapper);
    }

    public List<Image> findByPostId(Long postId) {
        return findMany(FIND_BY_POST_ID, postId);
    }

    public Optional<Image> findById(Long id) {
        return findOne(FIND_BY_ID, id);
    }

    public Image save(Image image) {
        long id = insert(
                INSERT_QUERY,
                image.getPostId(),
                image.getOriginalFileName(),
                image.getFilePath(),
                Timestamp.from(java.time.Instant.now())
        );
        image.setId(id);
        return image;
    }
}